/**
 * ©2018, UBTECH Robotics, Inc. All rights reserved (C)
 **/
package com.ubtedu.bridge;

import android.text.TextUtils;
import android.util.Log;
import android.webkit.JavascriptInterface;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 处理从外部模块过来的{@link IBridgeInterface#call(String)}调用并分配到相应的API
 *
 * @Author qinicy
 * @Date 2018/10/8
 **/
public class BridgeDispatcher implements IBridgeInterface {

    private static final String MSG_IGNORE_RETURN = "This is a async function,please ignore the return data.";
    /**
     * 注册的业务API模块，KEY值是命名空间，VALUE是具体的API定义类对象
     */
    private Map<String, IBridgeAPI> mNamespaceInterfaceObjects;
    /**
     * 注册的业务API，KEY值是具体的API定义类对象，VALUE是这个API定义类对象里定义的所有API信息MAP<API名称，API对应的java method>
     */
    private Map<Object, Map<String, Method>> mObjectInterfaces;
    /**
     * 与外部模块交互的handle对象
     */
    private IBridgeHandler mBridgeHandler;

    public BridgeDispatcher(IBridgeHandler bridgeHandler) {
        mBridgeHandler = bridgeHandler;
        mNamespaceInterfaceObjects = new HashMap<>();
        mObjectInterfaces = new HashMap<>();
    }

    /**
     * @see IBridge#addBridgeAPI(IBridgeAPI, String)
     */
    public void addBridgeAPI(IBridgeAPI api, String namespace) {
        if (namespace == null) {
            namespace = "";
        }
        if (api != null) {
            mNamespaceInterfaceObjects.put(namespace, api);
            mObjectInterfaces.put(api, api.getBridgeAPIs());
        }
    }

    private CallArguments checkCallArgs(String args) {
        try {
            JSONObject object = new JSONObject(args);
            if (object.has(BridgeFieldKeys.KEY_ID) && object.has(BridgeFieldKeys.KEY_FUNC)) {
                CallArguments arguments = new CallArguments();
                arguments.id = object.getInt(BridgeFieldKeys.KEY_ID);
                arguments.func = object.getString(BridgeFieldKeys.KEY_FUNC);
                arguments.callback = object.optString(BridgeFieldKeys.KEY_CALLBACK);
                arguments.args = object.opt(BridgeFieldKeys.KEY_ARGS);
                arguments.async = !TextUtils.isEmpty(arguments.callback);
                if (arguments.id > 0 && !TextUtils.isEmpty(arguments.func)) {
                    return arguments;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }



    /**
     * android ==> other,处理执行结果，并发送到外部bridge模块
     */
    private String handleCallbackResult(CallArguments arguments, BridgeResult result) {
        String json;
        boolean ignoreLog = false;
        if (result != null) {
            json = result.toJson();
            if (result.isAsyncResult) {
                mBridgeHandler.sendCallbackResult(result);
            }
            ignoreLog = MSG_IGNORE_RETURN.equals(result.msg);
        } else {
            json = BridgeResult.UNKNOWN().toJson();
        }

        String argsJson = "[]";
        String func = "";
        if (arguments != null) {
            func = arguments.func;
            if (arguments.args instanceof JSONArray) {
                argsJson = arguments.args.toString();
            }
        }
        if (!ignoreLog) {
            Log.d(BridgeImpl.TAG, "onCallback(android ==> " + mBridgeHandler.getClass().getSimpleName() + "):" + func + argsJson + " --> " + json);
        }

        return json;
    }

    /**
     * other ==> android
     * @see IBridgeInterface#call(String)
     */
    @Override
    public String call(String args) {
        final CallArguments arguments = checkCallArgs(args);
        if (arguments == null) {
            return handleCallbackResult(null, BridgeResult.ILLEGAL_ARGUMENTS());
        }


        String[] nameStr = parseNamespace(arguments.func.trim());
        String namespace = nameStr[0];
        String methodName = nameStr[1];
        IBridgeAPI jsb = mNamespaceInterfaceObjects.get(namespace);

        //接口类不存在
        if (jsb == null) {
            BridgeResult result = BridgeResult.FUN_UNDEFINED().id(arguments.id);
            return handleCallbackResult(arguments, result);
        }

        //方法不存在
        Method method = null;
        Map<String, Method> methodMap = mObjectInterfaces.get(jsb);
        if (methodMap != null) {
            method = methodMap.get(methodName);
        }

        if (method == null) {
            BridgeResult result = BridgeResult.FUN_UNDEFINED().id(arguments.id);
            return handleCallbackResult(arguments, result);
        }


        if (jsb.isJavascriptAPI()) {
            JavascriptInterface annotation = method.getAnnotation(JavascriptInterface.class);
            if (annotation == null) {
                String error = "Method " + methodName + " is not invoked, since  " +
                        "it is not declared with JavascriptInterface annotation! ";
                BridgeResult result = BridgeResult.FUN_UNDEFINED().id(arguments.id).msg(error);
                return handleCallbackResult(arguments, result);
            }
        }

        //执行方法
        BridgeResult result = invokeMethod(arguments, jsb, method);
        result.id = arguments.id;

        return handleCallbackResult(arguments, result);
    }


    private BridgeResult invokeMethod(final CallArguments arguments, IBridgeAPI jsb, Method method) {
        List<Object> funcArguments = parseMethodArguments(arguments);

        BridgeResult result = null;
        try {
            method.setAccessible(true);
            if (arguments.async) {
                APICallback callback = new InternalAPICallback(this, arguments);
                funcArguments.add(callback);
                method.invoke(jsb, funcArguments.toArray());
                result = BridgeResult.SUCCESS();
                result.msg = MSG_IGNORE_RETURN;
            } else {
                Object retData;
                if (funcArguments.size() > 0) {
                    retData = method.invoke(jsb, funcArguments.toArray());
                } else {
                    retData = method.invoke(jsb);
                }

                if (retData instanceof BridgeResult) {
                    result = (BridgeResult) retData;
                } else {
                    result = BridgeResult.SUCCESS().data(retData);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            result = handleExceptionResult(e);
        } finally {
            if (result == null) {
                result = BridgeResult.UNKNOWN();
            }
        }
        result.id = arguments.id;
        result.callback = arguments.callback;
        return result;
    }

    private BridgeResult handleExceptionResult(Exception e) {
        String msg = e.getMessage();
        //API内部抛出的错误，不会直接捕捉到，而是从cause里拿到
        if (e instanceof InvocationTargetException) {
            Throwable cause = e.getCause();
            if (cause instanceof BridgeException) {
                return ((BridgeException) cause).getExceptionResult().msg(cause.getMessage());
            } else {
                msg = cause.getMessage();
                if (msg == null) {
                    msg = cause.toString();
                }
            }
        } else if (e instanceof IllegalArgumentException) {
            return BridgeResult.ILLEGAL_ARGUMENTS().msg(e.getMessage());
        }


        if (msg == null && e.getCause() != null) {
            msg = e.getCause().getMessage();
        }
        if (msg == null) {
            msg = e.toString();
        }
        return BridgeResult.FAIL().msg(msg);
    }

    private List<Object> parseMethodArguments(CallArguments arguments) {
        List<Object> funcArguments = new ArrayList<>();
        if (arguments.args != null) {
            if (arguments.args instanceof JSONArray) {
                JSONArray argsArray = (JSONArray) arguments.args;
                try {
                    for (int i = 0; i < argsArray.length(); i++) {
                        funcArguments.add(argsArray.get(i));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                funcArguments.add(arguments.args);
            }
        }

        List<Object> funcArguments2 = new ArrayList<>();
        try {
            if (funcArguments.size() > 0) {
                for (Object arg : funcArguments) {
                    if (arg instanceof JSONObject) {
                        JSONObject map = (JSONObject) arg;
                        arg = new BridgeObject(map.toString());
                    } else if (arg instanceof JSONArray) {
                        JSONArray list = (JSONArray) arg;
                        arg = new BridgeArray(list.toString());
                    }
                    funcArguments2.add(arg);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        funcArguments = funcArguments2;
        return funcArguments;
    }

    /**
     * other ==> android
     * @see IBridgeInterface#onCallback(String)
     */
    @Override
    public void onCallback(String result) {
        if (result != null) {
            try {
                JSONObject object = new JSONObject(result);
                BridgeResult bridgeResult = new BridgeResult();
                bridgeResult.id = object.optInt(BridgeFieldKeys.KEY_ID);
                bridgeResult.code = object.optInt(BridgeFieldKeys.KEY_CODE);
                bridgeResult.callback = object.optString(BridgeFieldKeys.KEY_CALLBACK);
                bridgeResult.msg = object.optString(BridgeFieldKeys.KEY_MSG);
                bridgeResult.complete = object.optInt(BridgeFieldKeys.KEY_COMPLETE);
                Object data = object.opt(BridgeFieldKeys.KEY_DATA);
                if (data instanceof JSONObject) {
                    JSONObject dataObject = (JSONObject) data;
                    data = new BridgeObject(dataObject.toString());

                } else if (data instanceof JSONArray) {
                    JSONArray array = (JSONArray) data;
                    data = new BridgeArray(array.toString());
                }
                bridgeResult.data = data;

                mBridgeHandler.onCallback(bridgeResult);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    private String[] parseNamespace(String method) {
        int pos = method.lastIndexOf('.');
        String namespace = "";
        if (pos != -1) {
            namespace = method.substring(0, pos);
            method = method.substring(pos + 1);
        }
        return new String[]{namespace, method};
    }

    private static class InternalAPICallback extends APICallback {
        private BridgeDispatcher mDispatcher;
        private IBridgeHandler mHandler;
        private CallArguments mArguments;


        public InternalAPICallback(BridgeDispatcher dispatcher, CallArguments arguments) {
            mDispatcher = dispatcher;
            mArguments = arguments;
        }

        @Override
        public void onCallback(Object data, boolean isComplete) {
            BridgeResult result = BridgeResult
                    .SUCCESS()
                    .id(mArguments.id)
                    .data(data)
                    .callback(mArguments.callback)
                    .complete(isComplete)
                    .asyncResult(true);

            mDispatcher.handleCallbackResult(mArguments, result);
        }

        @Override
        public void onCallback(BridgeResult result) {
            if (result != null) {
                result.id = mArguments.id;
                result.callback = mArguments.callback;
            } else {
                result = BridgeResult.SUCCESS().id(mArguments.id).callback(mArguments.callback);
            }
            result.asyncResult(true);
            mDispatcher.handleCallbackResult(mArguments, result);
        }
    }
}
