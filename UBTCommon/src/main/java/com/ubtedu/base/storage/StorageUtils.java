package com.ubtedu.base.storage;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import com.ubtedu.base.storage.core.ExternalStorage;
import com.ubtedu.base.storage.core.InternalStorage;
import com.ubtedu.base.storage.core.StorageConfiguration;

import java.io.File;
import java.math.BigDecimal;

/**
 * Singleton class that supply all possible storage options.<br>
 * <br>
 * <b>Permissions:</b>
 * <ul>
 * <li>android.permission.WRITE_EXTERNAL_STORAGE</li>
 * <li>android.permission.READ_EXTERNAL_STORAGE</li>
 * </ul>
 *
 * @author Roman Kushnarenko - sromku (sromku@gmail.com)
 */
public class StorageUtils {

    private static InternalStorage mInternalStorage = null;
    private static ExternalStorage mExternalStorage = null;

    private static StorageUtils mInstance = null;
    private static StorageConfiguration mSimpleStorageConfiguration;

    private StorageUtils() {
        // set default configuration
        mSimpleStorageConfiguration = new StorageConfiguration.Builder().build();

        mInternalStorage = new InternalStorage();
        mExternalStorage = new ExternalStorage();
    }

    private static StorageUtils init() {
        if (mInstance == null) {
            synchronized (StorageUtils.class) {
                if (mInstance == null) {
                    mInstance = new StorageUtils();
                }
            }
        }
        return mInstance;
    }

    /**
     * The type of the storage. <br>
     * Possible options:
     * <ul>
     * <li>{@link StorageType#INTERNAL}</li>
     * <li>{@link StorageType#EXTERNAL}</li>
     * </ul>
     *
     * @author sromku
     */
    public enum StorageType {
        INTERNAL,
        EXTERNAL
    }

    /**
     * Get internal storage. The files and folders will be persisted on device
     * memory. The internal storage is good for saving <b>private and secure</b>
     * data.<br>
     * <br>
     * <b>Important:
     * <ul>
     * <li>When the device is low on internal storage space, Android may delete
     * these cache files to recover space.</li>
     * <li>You should always maintain the cache files yourself and stay within a
     * reasonable limit of space consumed, such as 1MB.</li>
     * <li>When the user uninstalls your application, these files are removed.</li>
     * </b>
     * </ul>
     * <i>http://developer.android.com/guide/topics/data/data-storage.html#
     * filesInternal</i>
     *
     * @return {@link InternalStorage}
     */
    public static InternalStorage getInternalStorage(Context context) {
        init();
        mInternalStorage.initActivity(context);
        return mInternalStorage;
    }

    /**
     * Get external storage. <br>
     *
     * @return {@link ExternalStorage}
     */
    public static ExternalStorage getExternalStorage() {
        init();
        return mExternalStorage;
    }

    /**
     * Check whereas the external storage is writable. <br>
     *
     * @return <code>True</code> if external storage writable, otherwise return
     * <code>False</code>
     */
    public static boolean isExternalStorageWritable() {
        init();
        return mExternalStorage.isWritable();
    }

    public static StorageConfiguration getConfiguration() {
        return mSimpleStorageConfiguration;
    }

    /**
     * Set and update the storage configuration
     *
     * @param configuration
     */
    public static void updateConfiguration(StorageConfiguration configuration) {
        if (mInstance == null) {
            throw new RuntimeException("First instantiate the Storage and then you can update the configuration");
        }
        mSimpleStorageConfiguration = configuration;
    }

    /**
     * Set the configuration to default
     */
    public static void resetConfiguration() {
        StorageConfiguration configuration = new StorageConfiguration.Builder().build();
        mSimpleStorageConfiguration = configuration;
    }

    // 获取文件
    //Context.getExternalFilesDir() --> SDCard/Android/data/你的应用的包名/files/ 目录，一般放一些长时间保存的数据
    //Context.getExternalCacheDir() --> SDCard/Android/data/你的应用包名/cache/目录，一般存放临时缓存数据
    public static long getFolderSize(File file) {
        long size = 0;
        try {
            File[] fileList = file.listFiles();
            for (int i = 0; i < fileList.length; i++) {
                // 如果下面还有文件
                if (fileList[i].isDirectory()) {
                    size = size + getFolderSize(fileList[i]);
                } else {
                    size = size + fileList[i].length();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }

    /**
     * 删除指定目录下文件及目录
     *
     * @param filePath       指定目录
     * @param deleteThisPath 是否删除本目录
     * @return
     */
    public static void deleteFolderFile(String filePath, boolean deleteThisPath) {
        if (!TextUtils.isEmpty(filePath)) {
            try {
                File file = new File(filePath);
                if (file.isDirectory()) {// 如果下面还有文件
                    File files[] = file.listFiles();
                    for (int i = 0; i < files.length; i++) {
                        deleteFolderFile(files[i].getAbsolutePath(), true);
                    }
                }
                if (deleteThisPath) {
                    if (!file.isDirectory()) {// 如果是文件，删除
                        file.delete();
                    } else {// 目录
                        if (file.listFiles().length == 0) {// 目录下没有文件或者目录，删除
                            file.delete();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 格式化单位
     *
     * @param size
     * @return
     */
    public static String getFormatSize(double size) {
        double kiloByte = size / 1024;
        if (kiloByte < 1) {
            return size + "Byte";
        }

        double megaByte = kiloByte / 1024;
        if (megaByte < 1) {
            BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "KB";
        }

        double gigaByte = megaByte / 1024;
        if (gigaByte < 1) {
            BigDecimal result2 = new BigDecimal(Double.toString(megaByte));
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "MB";
        }

        double teraBytes = gigaByte / 1024;
        if (teraBytes < 1) {
            BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "GB";
        }
        BigDecimal result4 = new BigDecimal(teraBytes);
        return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString()
                + "TB";
    }

    /***
     * 获取应用外部缓存大小
     */
    public static String getExternalCacheSize(Context context) throws Exception {
        return getFormatSize(getFolderSize(context.getExternalCacheDir()));
    }

    /***
     * 获取应用内部缓存大小
     */
    public static String getInternalCacheSize(Context context) throws Exception {
        return getFormatSize(getFolderSize(context.getCacheDir()));
    }

    /**
     * 清空外部缓存文件
     */
    public static void cleanExternalCache(Context context) {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            deleteFilesByDirectory(context.getExternalCacheDir());
        }
    }

    /**
     * 清空内部缓存文件
     */
    public static void cleanInternalCache(Context context) {
        deleteFilesByDirectory(context.getCacheDir());
    }

    private static void deleteFilesByDirectory(File directory) {
        if (directory != null && directory.exists() && directory.isDirectory()) {
            for (File item : directory.listFiles()) {
                item.deleteOnExit();
            }
        }
    }
}
