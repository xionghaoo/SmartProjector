package com.ubtedu.ukit.application;

import android.content.Context;

import com.ubtedu.alpha1x.utils.LogUtil;
import com.ubtedu.alpha1x.utils.MD5Util;
import com.ubtedu.alpha1x.utils.StringUtil;

public class PrivacyInfo {
    public final static String PRODUCT_ID;

    static {
        String productId = "";
        try {
//            System.loadLibrary("piUkit2.0");
            init(UKitApplication.getInstance().getApplicationContext());
            productId = getProductId();
        } catch (Exception e) {
            LogUtil.e("PrivacyInfo", e.getMessage());
        } finally {
            PRODUCT_ID = productId;
//            if((UKitApplication.getInstance().getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0) {
//                LogUtil.e("PrivacyInfo", "ProductId:" + PRODUCT_ID);
//                LogUtil.e("PrivacyInfo", "AppId:" + getAppId());
//                LogUtil.e("PrivacyInfo", "AppKey:" + getAppKey());
//                LogUtil.e("PrivacyInfo", "BuglyId:" + getBuglyId());
//            }
        }
    }

    /**
     * 初始化
     */
    private static boolean init(Context context) {
        return true;
    }

    /**
     * 有效校验，校验App包名和签名是否有效
     * @return
     * true: 校验通过
     * false: 校验不通过，与签名不一致或是与授权包名不同
     */
    public static boolean validCheck() {
        return true;
    }

    /**
     * 获取App ID
     * @return 校验通过后返回AppId，校验不通过则返回INVALID_CLIENT
     */
    private static String getAppId() {
        //自行申请
        return "940010010";
    }

    /**
     * 获取App Key
     * @return 校验通过后返回AppKey，校验不通过则返回INVALID_CLIENT
     */
    public static String getAppKey() {
        //自行申请
        return "c3ba293280655a66165dd8cdf4e58217";
    }

    /**
     * 获取Product ID
     * @return 校验通过后返回ProductId，校验不通过则返回INVALID_CLIENT
     */
    private static String getProductId() {
        //自行申请
        return "90001";
    }

    /**
     * 获取Bugly ID
     * @return 校验通过后返回BuglyId，校验不通过则返回INVALID_CLIENT
     */
    private static String getBuglyId() {
        //自行申请
        return "INVALID_CLIENT";
    }

    public static int getUBTAppId() {
        try {
            return Integer.parseInt(getAppId());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private static final String SIGN_PART_SEPARATOR = " ";

    /**
     * 规则：
     * <ol>
     * <li>计算当前时间戳（单位：秒）拼接 AppKey 和 1-10 位大小写字母和数字随机字符和终端设备号 X-UBT-DeviceId 生成字符串的 MD5 值，得到签名段.</li>
     * <li>将签名段与先前的时间戳使用空格连接，同时使用空格连接 randStr 字符串，得到最终 X-UBT-Sign 内容</li>
     * <li>return md5(now + mAppKey + randStr + X-UBT-DeviceId)</li>
     * </ol>
     */
    public static String getUBTSign() {
        long now = System.currentTimeMillis() / 1000;
        String randStr = StringUtil.getRandomChar(10);
        String versionNum = "v2";
        String str = now + getAppKey() + randStr + UKitApplication.getInstance().generateDeviceToken();
        String sign = MD5Util.encodeByMD5(str)
                + SIGN_PART_SEPARATOR + now
                + SIGN_PART_SEPARATOR + randStr
                + SIGN_PART_SEPARATOR + versionNum;
        return sign;
    }

    /**
     * 获取Tencent Bugly App Id
     *
     * @return 校验通过后返回Tencent Bugly App Id，校验不通过则返回INVALID_CLIENT
     */
    public static String getTencentBuglyAppId() {
        return getBuglyId();
    }

}