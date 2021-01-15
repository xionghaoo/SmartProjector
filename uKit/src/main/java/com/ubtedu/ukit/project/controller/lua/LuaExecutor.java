package com.ubtedu.ukit.project.controller.lua;

import android.text.TextUtils;

import com.ubtedu.ukit.project.controller.utils.RcLogUtils;

import org.keplerproject.luajava.LuaState;
import org.keplerproject.luajava.LuaStateFactory;

public class LuaExecutor {

    private final static int SOURCE_FILE = 1;
    private final static int SOURCE_STRING = 2;

    private LuaExecutor() {}

    private Source src;

    private String methodName = "run";
    private Object[] params;

    private LuaThread luaThread = null;

    private boolean isEditDone = false;
    private boolean isTerminate = false;
    
    private LuaExecutorCallback luaExecutorCallback;

    private class Source {
        private int type;
        private String text;
        public Source(int type, String text) {
            this.type = type;
            this.text = text;
        }
    }

    public static LuaExecutor newLuaExecutor() {
        return new LuaExecutor();
    }

    public LuaExecutor loadFromString(String text) {
        if(!isEditDone) {
            src = new Source(SOURCE_STRING, text);
        }
        return this;
    }

    public LuaExecutor loadFromFile(String text) {
        if(!isEditDone) {
            src = new Source(SOURCE_FILE, text);
        }
        return this;
    }

    public LuaExecutor setMethodName(String methodName) {
        if(!isEditDone) {
            this.methodName = methodName;
        }
        return this;
    }

    public LuaExecutor setParams(Object... params) {
        if(!isEditDone) {
            this.params = params;
        }
        return this;
    }

    public void terminate() {
        if(!isEditDone) {
            return;
        }
//        RcLogUtils.e("Terminate: %s", toString());
        isTerminate = true;
        luaExecutorCallback = null;
        synchronized (this) {
            if (luaThread != null) {
                luaThread.terminate();
            }
        }
    }

    public boolean editDone() {
        if(src == null || (src.type != SOURCE_FILE && src.type != SOURCE_STRING) || TextUtils.isEmpty(src.text)) {
            return false;
        }
        isEditDone = true;
        return true;
    }

    public boolean execute() {
        if(!isEditDone) {
            return false;
        }
        synchronized (this) {
            if(luaThread != null) {
                return false;
            }
            luaThread = new LuaThread(this, luaExecutorCallback, false);
            luaThread.start();
            return true;
        }
    }
    
    public LuaExecutorCallback getLuaExecutorCallback() {
        return luaExecutorCallback;
    }
    
    public LuaExecutor setLuaExecutorCallback(LuaExecutorCallback luaExecutorCallback) {
        this.luaExecutorCallback = luaExecutorCallback;
        return this;
    }
    
    public interface LuaExecutorCallback {
        void onLuaExecuteBegin(LuaExecutor le);
        void onLuaExecuteError(LuaExecutor le, Throwable e);
        void onLuaExecuteEnd(LuaExecutor le);
    }

    private class LuaThread extends Thread {
        private LuaExecutor le;

        private LuaState lua;
        private LuaExecutorCallback luaExecutorCallback;
        private LuaInterface.RunningFlagJavaFunction runningFlagJavaFunction;

        private boolean runAgain;

        public LuaThread(LuaExecutor le, LuaExecutorCallback luaExecutorCallback, boolean runAgain) {
            super("LuaThread");
            this.le = le;
            this.luaExecutorCallback = luaExecutorCallback;
            this.runAgain = runAgain;
        }

        public void terminate() {
            if(runningFlagJavaFunction != null) {
                runningFlagJavaFunction.setRunningState(false);
                runningFlagJavaFunction = null;
            }
        }

        private boolean isStackOverflow(LuaState lua) {
            int top = lua.getTop();
            if(top < 1) {
                return false;
            }
            int type = lua.type(top);
            if (type != LuaState.LUA_TSTRING) {
                return false;
            }
            String value = lua.toString(top);
            return value.contains("stack overflow");
        }

        private void runProgram() throws Exception {
            try {
                lua = LuaStateFactory.newLuaState();
//                RcLogUtils.e("Lua Start: %s, %s", le.toString(), lua.toString());
                lua.openLibs();
                if (src.type == SOURCE_FILE) {
                    lua.LdoFile(src.text);
                } else if (src.type == SOURCE_STRING) {
                    lua.LdoString(src.text);
                }
                runningFlagJavaFunction = LuaInterface.initFunctions(lua, new LuaInterface.ILuaRunProgramAgainInterface() {
                    @Override
                    public void onRunProgramAgain() throws Exception {
                        runProgramAgain();
                    }
                });
                runningFlagJavaFunction.setRunningState(!isTerminate);
                lua.getGlobal(methodName);
                if (params != null && params.length > 0) {
                    for (Object param : params) {
                        lua.pushObjectValue(param);
                    }
                }
//              RcLogUtils.e("Begin: %s", lua.dumpStack());
                int result = lua.pcall(params != null ? params.length : 0, 0, 0);
//              RcLogUtils.e("End: %s", lua.dumpStack());
                RcLogUtils.e("result: %d", result);
                if(result != 0 && isStackOverflow(lua)) {
                    throw new StackOverflowError("lua stack overflow");
                }
            } finally {
                if(lua != null) {
//                    RcLogUtils.e("Lua End: %s, %s", le.toString(), lua.toString());
                    if (!lua.isClosed()) {
                        lua.close();
                    }
                    lua = null;
                }
            }
        }

        private void runProgramAgain() throws Exception {
            LuaExecutorCallback luaExecutorCallback = this.luaExecutorCallback;
            this.luaExecutorCallback = null;
            terminate();
            synchronized (LuaExecutor.this) {
                luaThread = new LuaThread(le, luaExecutorCallback, true);
                luaThread.start();
            }
        }

        @Override
        public void run() {
            if(!runAgain && luaExecutorCallback != null) {
                luaExecutorCallback.onLuaExecuteBegin(le);
            }
            try {
                runProgram();
            } catch (Throwable e) {
                RcLogUtils.e(e);
                if(luaExecutorCallback != null) {
                    luaExecutorCallback.onLuaExecuteError(le, e);
                }
            }
            if(luaExecutorCallback != null) {
                luaExecutorCallback.onLuaExecuteEnd(le);
            }
        }
    }

}
