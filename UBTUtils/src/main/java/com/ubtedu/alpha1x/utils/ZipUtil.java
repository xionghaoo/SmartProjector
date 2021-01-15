package com.ubtedu.alpha1x.utils;


import android.text.TextUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 *
 * 压缩、解压缩工具
 *使用{@link ZipUtil2}
 * @author Bright. Create on 2017/6/12.
 */
@Deprecated
public class ZipUtil {


    /**
     * 压缩文件
     *
     * @param outZipFilePath 目标压缩包名，绝对路径
     * @param srcFilePath    指定被压缩文件、目录绝对路径
     */
    public static boolean zip(String outZipFilePath, String srcFilePath) {
        return zip(outZipFilePath, srcFilePath, null);
    }

    public static boolean zip(String outZipFilePath, String srcFilePath, OnZipListener listener) {
        boolean ret = false;
        if (listener != null) {
            listener.onStartZip();
        }
        if (TextUtils.isEmpty(outZipFilePath)) {
            if (listener != null) {
                listener.onZipError(new NullPointerException("outZipFilePath is empty!"));
            }
            return false;
        }

        String tempOutZipFilePath = outZipFilePath + ".temp";
        File tempFile = new File(tempOutZipFilePath);
        File parent = tempFile.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }

        if (!tempFile.exists()) {
            try {
                tempFile.createNewFile();
            } catch (IOException e) {
                if (listener != null) {
                    listener.onZipError(e);
                }

                e.printStackTrace();
                return false;
            }
        }
        ZipOutputStream zipOutputStream = null;
        BufferedOutputStream bufferedOutputStream = null;
        File inputFile = new File(srcFilePath);
        try {
            zipOutputStream = new ZipOutputStream(new FileOutputStream(tempOutZipFilePath));
            bufferedOutputStream = new BufferedOutputStream(zipOutputStream);
            boolean success = zip(zipOutputStream, inputFile, inputFile.getName(), bufferedOutputStream, null);
            if (!success) {
                return false;
            }
            zipOutputStream.flush();
            File finalzip = new File(tempFile.getParentFile().getAbsolutePath() + File.separator + new File(outZipFilePath).getName());
            tempFile.renameTo(finalzip);
            if (listener != null) {
                listener.onFinishZip();
            }
            ret = true;
        } catch (Exception e) {
            e.printStackTrace();
            if (listener != null) {
                listener.onZipError(e);
            }
            ret = false;
        } finally {
            close(bufferedOutputStream);
            close(zipOutputStream);
        }
        return ret;
    }

    /**
     * 压缩文件
     *
     * @param zipOut    目标压缩文件输出流
     * @param inputFile 指定被压缩文件、目录
     * @param base      被压缩根目录名或根文件名
     * @param bo        BufferedInputStream流
     */
    private static boolean zip(ZipOutputStream zipOut, File inputFile, String base,
                               BufferedOutputStream bo, OnZipListener listener) {
        boolean ret = false;
        FileInputStream fileInputStream = null;
        BufferedInputStream bufferedInputStream = null;
        try {
            if (inputFile.isDirectory()) {
                File[] fiels = inputFile.listFiles();
                if (fiels.length == 0) {
                    zipOut.putNextEntry(new ZipEntry(base + "/")); // 创建zip压缩进入点base
                }
                for (File file : fiels) {
                    boolean success = zip(zipOut, file, base + "/" + file.getName(), bo, null); // 递归遍历子文件夹
                    if (!success) {
                        break;
                    }
                }
                ret = true;
            } else {
                zipOut.putNextEntry(new ZipEntry(base)); // 创建zip压缩进入点base
                fileInputStream = new FileInputStream(inputFile);
                bufferedInputStream = new BufferedInputStream(fileInputStream);
                int b;
                while ((b = bufferedInputStream.read()) != -1) {
                    bo.write(b); // 将字节流写入当前zip目录
                }
                bo.flush();
                ret = true;
            }

        } catch (Exception e) {
            e.printStackTrace();
            if (listener != null) {
                listener.onZipError(e);
            }
        } finally {
            close(bufferedInputStream);
            close(fileInputStream);
        }
        return ret;
    }

    /**
     * 解压缩文件
     *
     * @param zipFileName    压缩文件绝对路径
     * @param outputFileName 目标目录、文件绝对路径
     */
    public static boolean unzip(String zipFileName, String outputFileName) {
        return unzip(zipFileName, outputFileName, null);
    }

    public static boolean unzip(String zipFileName, String outputFileName, OnUnZipListener listener) {
        boolean ret = false;
        ZipInputStream zipInputStream = null;
        BufferedInputStream bufferedInputStream = null;
        ZipEntry entry;
        File fileOut = null;
        FileOutputStream fileOutputStream = null;
        BufferedOutputStream bufferedOutputStream = null;
        try {
            //输入源zip路径
            zipInputStream = new ZipInputStream(new FileInputStream(zipFileName));
            bufferedInputStream = new BufferedInputStream(zipInputStream);
            File zipFile = new File(zipFileName);
            int zipFileSize = zipFile.isFile() ? (int) zipFile.length() : 0;
            int tmpFileSize = 0;
            int progress = 0;
            int lastProgress = 0;
            if (listener != null) {
                listener.onZipping(0);
            }
            entry = zipInputStream.getNextEntry();
            while (entry != null && !entry.isDirectory()) {
                fileOut = new File(outputFileName, entry.getName());
                if (!fileOut.exists()) {
                    (new File(fileOut.getParent())).mkdirs();
                }
                fileOutputStream = new FileOutputStream(fileOut);
                bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
                int len;
                while ((len = bufferedInputStream.read()) != -1) {
                    bufferedOutputStream.write(len);
                    tmpFileSize += len;
                    if (tmpFileSize <= zipFileSize) {
                        progress = (int) ((float) tmpFileSize / (float) zipFileSize * 100f);
                    } else {
                        progress = 99;
                    }
                    if (progress % 20 == 0 && lastProgress < progress) {
                        if (listener != null) {
                            listener.onZipping(progress);
                        }
                    }
                    lastProgress = progress;
                }
                bufferedOutputStream.flush();
            }
            if (listener != null) {
                listener.onZipping(100);
                listener.onFinishUnZip();
            }
            ret = true;
        } catch (Exception e) {
            e.printStackTrace();
            if (listener != null) {
                listener.onUnZipError(e);
            }
            ret = false;
        } finally {
            close(bufferedInputStream);
            close(zipInputStream);
        }
        return ret;
    }

    private static void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public interface OnZipListener {
        void onStartZip();

        void onFinishZip();

        void onZipError(Exception e);
    }

    public interface OnUnZipListener {
        void onStartUnZip();

        void onFinishUnZip();

        void onUnZipError(Exception e);

        void onZipping(int progress);
    }

}
