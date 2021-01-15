package com.ubtedu.alpha1x.utils;

import android.text.TextUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

/**
 * MD5加密
 *
 * @author Bright. Create on 2017/5/5.
 */
public class MD5Util {

    /**
     * MD5加密
     *
     * @param str 原字符串
     * @return 加密后的MD5字符串
     */
    public static String encodeByMD5(String str) {
        StringBuilder strBuilder = null;
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(str.getBytes("UTF-8"));
            byte[] byteArray = messageDigest.digest();
            strBuilder = new StringBuilder();
            for (byte data : byteArray) {
                if (Integer.toHexString(0xFF & data).length() == 1) {
                    strBuilder.append("0").append(Integer.toHexString(0xFF & data));
                } else {
                    strBuilder.append(Integer.toHexString(0xFF & data));
                }
            }
            return strBuilder.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return str;
    }

    /**
     * MD5加密
     *
     * @param filename 文件名
     * @return 加密后的MD5字符串
     */
    public static String encodeFileMd5(String filename) {
        if(TextUtils.isEmpty(filename)) {
            throw new IllegalArgumentException();
        }
        File file = new File(filename);
        if(!file.exists() || !file.canRead() || file.length() == 0) {
            throw new IllegalArgumentException();
        }
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(filename);
            return encodeInputStreamMd5(fis);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
                try {
                    fis.close();
                } catch (Throwable var2) {
                    LogUtil.d(var2.getMessage());
                }
        }
    }
    /**
     * MD5加密
     *
     * @param inputStream 输入流
     * @return 加密后的MD5字符串
     */
    public static String encodeInputStreamMd5(InputStream inputStream) {
        if(inputStream == null) {
            throw new IllegalArgumentException();
        }
        ByteArrayOutputStream baos = null;
        try {
            baos = new ByteArrayOutputStream();
            byte buffer[] = new byte[10240];
            int readLen = 0;
            while((readLen = inputStream.read(buffer, 0, buffer.length)) > 0) {
                baos.write(buffer, 0, readLen);
            }
            return encodeMd5(baos.toByteArray());
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                baos.close();
            } catch (Throwable var2) {
                LogUtil.d(var2.getMessage());
            }
        }
    }

    /**
     * MD5加密
     *
     * @param bytes 原字byte数组
     * @return 加密后的MD5字符串
     */
    public static String encodeMd5(byte bytes[]) {
        if(bytes == null || bytes.length == 0) {
            throw new IllegalArgumentException();
        }
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(bytes);
            StringBuilder sb = new StringBuilder();
            for(byte eachByte : digest) {
                sb.append(String.format(Locale.US,"%02x", eachByte));
            }
            String result = sb.toString();
            return result;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 利用java原生的类实现SHA256加密
     *
     * @param str 加密后的报文
     * @return
     */
    public static String encodeSha256(String str) {
        MessageDigest messageDigest;
        String encodestr = "";
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(str.getBytes("UTF-8"));
            encodestr = byte2Hex(messageDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return encodestr;
    }

    /**
     * 将byte转为16进制
     *
     * @param bytes
     * @return
     */
    private static String byte2Hex(byte[] bytes) {
        StringBuffer stringBuffer = new StringBuffer();
        String temp = null;
        for (int i = 0; i < bytes.length; i++) {
            temp = Integer.toHexString(bytes[i] & 0xFF);
            if (temp.length() == 1) {
                // 1得到一位的进行补0操作
                stringBuffer.append("0");
            }
            stringBuffer.append(temp);
        }
        return stringBuffer.toString();
    }
}
