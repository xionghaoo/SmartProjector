package com.ubtedu.ukit.common.utils;

import android.text.TextUtils;

import com.ubtedu.base.net.rxretrofit.subscriber.SimpleRxSubscriber;
import com.ubtedu.bridge.BridgeObject;
import com.ubtedu.bridge.BridgeResult;
import com.ubtedu.bridge.BridgeResultCode;
import com.ubtedu.ukit.common.files.FileHelper;
import com.ubtedu.ukit.project.blockly.BlocklyPresenter;
import com.ubtedu.ukit.project.bridge.BridgeCommunicator;
import com.ubtedu.ukit.project.bridge.functions.BlocklyFunctions;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author naOKi
 * @Date 2019/10/28
 **/
public class MicroPythonUtils {

    private static final Pattern importPattern = Pattern.compile("^\\s*import\\s+([^#]+)");
    private static final Pattern subscribeIdPattern = Pattern.compile("^\\s*#\\s*SubscribeId\\s*=\\s*([^\\s]+)");

    private static final String END_LINE_OF_FILE = "scripter.hold()";
    private static final String NEW_LINE = "\n";
    private static final String RETURN = "\r";

    private static final String NEW_LINEx3 = "\n\n\n";
    private static final String NEW_LINEx2 = "\n\n";

    private static final String IMPORT_PREFIX = "import ";

    private static String COMMON_PART = null;
    private static String BLOCKLY_VERSION = null;

    private MicroPythonUtils() {}

    private static String readSdcardBlocklyVersionCode() {
        return FileHelper.readTxtFile(BlocklyPresenter.SDCARD_BLOCKLY_VERSION);
    }

    public static boolean hasCommonPart() {
        if(BLOCKLY_VERSION == null) {
            COMMON_PART = null;
            return false;
        }
        String sdcardBlocklyVersion = readSdcardBlocklyVersionCode();
        if(!TextUtils.equals(BLOCKLY_VERSION, sdcardBlocklyVersion)) {
            COMMON_PART = null;
            BLOCKLY_VERSION = null;
            return false;
        }
        return COMMON_PART != null;
    }

    public static String conversionPython(HashSet<String> importList, String microPythonContent) {
        return conversionPython(null, importList, microPythonContent, null);
    }

    public static String conversionPython(String id, HashSet<String> importList, String microPythonContent, HashMap<String, String> scriptKeyMap) {
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader stringReader = new BufferedReader(new StringReader(microPythonContent));
            String line;
            sb.append("\n");
            while((line = stringReader.readLine()) != null) {
                if(!TextUtils.isEmpty(line)) {
                    Matcher matcher;
                    matcher = importPattern.matcher(line);
                    if (matcher.find()) {
                        String imports = matcher.group(1);
                        String[] importItems = imports.split(",");
                        for (String importItem : importItems) {
                            importItem = importItem.trim();
                            if(!TextUtils.isEmpty(importItem)) {
                                importList.add(importItem);
                            }
                        }
                        //line = "#" + line;
                        line = "";
                    } else if(!TextUtils.isEmpty(id) && scriptKeyMap != null) {
                        matcher = subscribeIdPattern.matcher(line);
                        if (matcher.find()) {
                            String subscribeId = matcher.group(1);
                            subscribeId = subscribeId.trim();
                            if(!TextUtils.isEmpty(subscribeId)) {
                                scriptKeyMap.put(id, subscribeId);
                            }
                            line = "";
                        }
                    }
                }
                sb.append(line).append("\n");
            }
        } catch (Throwable e) {
            //do nothing
        }
        return sb.toString();
    }

    public static String mergedMicroPythonContent(HashSet<String> importList, String microPythonContent) {
        if(COMMON_PART == null) {
            return null;
        }
        StringBuilder result = new StringBuilder();
        result.append(NEW_LINE);
        for(String importItem : importList) {
            result.append(IMPORT_PREFIX).append(importItem).append(NEW_LINE);
        }
        result.append(NEW_LINEx2);
        result.append(COMMON_PART);
        result.append(NEW_LINEx2);
        result.append(microPythonContent);
        result.append(NEW_LINEx2);
        result.append(END_LINE_OF_FILE);
        result.append(NEW_LINE);
        String output = result.toString();
        output = output.replace(RETURN, NEW_LINE);
        while(output.contains(NEW_LINEx3)) {
            output = output.replace(NEW_LINEx3, NEW_LINEx2);
        }
        if(TextUtils.isEmpty(output.trim())) {
            return null;
        }
        return output;
    }

    public static boolean writeMicroPythonFile(String filePath, HashSet<String> importList, String microPythonContent) {
        if(COMMON_PART == null) {
            return false;
        }
        StringBuilder result = new StringBuilder();
        result.append(NEW_LINE);
        for(String importItem : importList) {
            result.append(IMPORT_PREFIX).append(importItem).append(NEW_LINE);
        }
        result.append(NEW_LINEx2);
        result.append(COMMON_PART);
        result.append(NEW_LINEx2);
        result.append(microPythonContent);
        result.append(NEW_LINEx2);
        result.append(END_LINE_OF_FILE);
        result.append(NEW_LINE);
        String output = result.toString();
        output = output.replace(RETURN, NEW_LINE);
        while(output.contains(NEW_LINEx3)) {
            output = output.replace(NEW_LINEx3, NEW_LINEx2);
        }
        if(TextUtils.isEmpty(output.trim())) {
            return false;
        }
        return FileHelper.writeTxtFile(filePath, output);
    }

    public static void loadCommonPart(Runnable runnable) {
        if(!BridgeCommunicator.getInstance().getBlocklyBridge(false).isCommunicable()) {
            return;
        }
        BridgeCommunicator.getInstance().getBlocklyBridge(false).call(BlocklyFunctions.getCommenPy, null)
            .subscribe(new SimpleRxSubscriber<BridgeResult>() {
                @Override
                public void onNext(BridgeResult result) {
                    super.onNext(result);
                    if (result.code != BridgeResultCode.SUCCESS) {
                        return;
                    }
                    try {
                        BridgeObject jsonObject = (BridgeObject) result.data;
                        COMMON_PART = jsonObject.getString("content");
                        BLOCKLY_VERSION = readSdcardBlocklyVersionCode();
                        if (runnable != null) {
                            runnable.run();
                        }
                    } catch (Throwable e) {
                        // do nothing
                    }
                }
            });
    }

    public static void xmlToPython(String subscribeId,String xml, Xml2PythonCallback callback) {
        if(!BridgeCommunicator.getInstance().getBlocklyBridge(false).isCommunicable()) {
            if (callback != null) {
                callback.onCallback(false, xml, null);
            }
            return;
        }
        Object[] args = new Object[]{subscribeId, xml};
        BridgeCommunicator.getInstance().getBlocklyBridge(false).call(BlocklyFunctions.xmlToPython, args)
                .subscribe(new SimpleRxSubscriber<BridgeResult>() {
                    @Override
                    public void onNext(BridgeResult result) {
                        super.onNext(result);
                        if (result.code != BridgeResultCode.SUCCESS) {
                            if (callback != null) {
                                callback.onCallback(false, xml, null);
                            }
                            return;
                        }
                        try {
                            BridgeObject jsonObject = (BridgeObject) result.data;
                            String content = jsonObject.getString("content");
                            if (callback != null) {
                                callback.onCallback(true, xml, content);
                            }
                        } catch (Throwable e) {
                            if (callback != null) {
                                callback.onCallback(false, xml, null);
                            }
                        }
                    }
                });
    }

    public interface Xml2PythonCallback {
        void onCallback(boolean isSuccess, String xml, String content);
    }

}
