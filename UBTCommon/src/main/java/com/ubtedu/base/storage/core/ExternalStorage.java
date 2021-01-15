package com.ubtedu.base.storage.core;

import android.content.Context;
import android.os.Environment;

import com.ubtedu.base.storage.StorageUtils;
import com.ubtedu.base.storage.StorageUtils.StorageType;

import java.io.File;

/**
 * Storage that responsible for storing on external storage like SD card.<br>
 * This class can be initiated <b>only</b> by {@link StorageUtils} singleton
 * class.
 *
 * @author Roman Kushnarenko - sromku (sromku@gmail.com)
 */
public class ExternalStorage extends AbstractDiskStorage {


    private String mRootPath;

    /**
     * Contractor as friend. Means, only classes from the same package can
     * initiate this class.<br>
     * <br>
     * <p>
     * <b>DO NOT CHANGE</b> to -> public, private or protected
     */
    public ExternalStorage() {
        super();
    }

    /**
     * 设置管理的存储根目录，{@link #createDirectory(String, boolean)}会在该根目录下创建。
     *
     * @param rootPath
     */
    public void setRootPath(String rootPath) {
        mRootPath = rootPath;
        File file = new File(mRootPath);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    public String getRootPath() {
        if (mRootPath != null)
            return mRootPath;
        return buildAbsolutePath();
    }

    @Override
    public StorageType getStorageType() {
        return StorageType.EXTERNAL;
    }

    /**
     * Checks if external storage is available for read and write. <br>
     *
     * @return <code>True</code> if external storage writable, otherwise return
     * <code>False</code>
     */
    public boolean isWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    @Override
    protected String buildAbsolutePath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    /**
     * Build path of folder or file on the external storage location. <br>
     * <b>Note: </b> <li>For directory use regular name</li> <li>For file name
     * use name with .extension like <i>abc.png</i></li><br>
     * <br>
     *
     * @param name The name of the directory
     * @return
     */
    protected String buildPath(String name) {
        String path;
        if (mRootPath != null) {
            path = mRootPath;
        } else {
            path = buildAbsolutePath();
        }
        path = path + File.separator + name;
        return path;
    }

    /**
     * Build folder + file on the external storage location. <br>
     * <b>Note: </b> <li>For directory use regular name</li> <li>For file name
     * use name with .extension like <i>abc.png</i></li><br>
     * <br>
     *
     * @param directoryName The directory name
     * @param fileName      The file name
     * @return
     */
    protected String buildPath(String directoryName, String fileName) {
        String path;
        if (mRootPath != null) {
            path = mRootPath;
        } else {
            path = buildAbsolutePath();
        }
        path = path + File.separator + directoryName + File.separator + fileName;
        return path;
    }

    /**
     * 获取sd卡根目录/data目录
     */
    public File getDataDirectory() {
        return Environment.getDataDirectory();
    }

    /**
     * 获取sd卡中根目录/Android/data/应用包名/files目录
     *
     * @param context Context object
     * @param type    The type of files directory to return.
     *                May be null for the root of the files directory or one of the following
     *                Environment constants for a subdirectory:
     *                DIRECTORY_MUSIC, DIRECTORY_PODCASTS, DIRECTORY_RINGTONES, DIRECTORY_ALARMS,
     *                DIRECTORY_NOTIFICATIONS, DIRECTORY_PICTURES, or DIRECTORY_MOVIES.
     * @return The path of the directory holding application files on external storage.
     * Returns null if external storage is not currently mounted so it could not ensure the path exists;
     * you will need to call this method again when it is available.
     */
    public File getFilesDirectory(Context context, String type) {
        return context.getExternalFilesDir(type);
    }

    /**
     * Returns the absolute path to the directory on the primary external filesystem
     * (that is somewhere on Environment.getExternalStorageDirectory()
     * where the application can place cache files it owns.
     * <p>
     * 获取 sd卡根目录/Android/data/包名/cache/ 目录
     */
    public File getCacheDirectory(Context context) {
        return context.getExternalCacheDir();
    }

}
