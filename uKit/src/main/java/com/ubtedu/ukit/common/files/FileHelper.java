/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.common.files;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;

import androidx.annotation.IntDef;

import com.ubtedu.alpha1x.utils.AppUtil;
import com.ubtedu.alpha1x.utils.LogUtil;
import com.ubtedu.alpha1x.utils.ZipUtil2;
import com.ubtedu.base.storage.StorageUtils;
import com.ubtedu.base.storage.core.ExternalStorage;
import com.ubtedu.base.storage.core.Storage;
import com.ubtedu.deviceconnect.libs.utils.URoIoUtils;
import com.ubtedu.ukit.user.UserManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.schedulers.Schedulers;

/**
 * @Author qinicy
 * @Date 2018/11/13
 **/
public class FileHelper {
    public static final int STORAGE_TYPE_EXT_DATA = 0;
    public static final int STORAGE_TYPE_EXT_CACHE = 1;
    public static final int STORAGE_TYPE_INT_DATA = 2;
    public static final int STORAGE_TYPE_INT_CACHE = 3;

    public static final String USERS = "users" + File.separator;
    public static final String PUBLIC = "public";
    public static final String DOWNLOAD_PATH = "download";
    public static final String CACHE_DIR = "cache";

    public static final String ZIP_CACHE_PATH = "zip_cache";
    public static final String UKT_CACHE_PATH = "ukt_cache";
    public static final String ZIP_FILE_SUFFIX = ".zip";
    public static final String UKT_FILE_SUFFIX = ".ukt";
    public static final String IMPORT_CACHE_PATH = "import_cache";
    public static final String BLOCKLY_PATH = "blockly";
    public static final String UNITY3D_PATH = "unity3d";
    public static final String WORKSPACE_PATH = "workspace";
    public static final String PROJECTS_PATH = "projects";
    public static final String MEDIA_PATH = "media";
    private static final String IMAGE_CACHE = "image";
    private static final String OTA = "ota";


    private static ExternalStorage mExternalStorage;
    private static final String UBTEDU_DIR = "UBTEDU";

    private static String sFileRootPath;
    private static String sCacheRootPath;
    private static Context sAppcationContext;


    public static void init(Context context) {
        sAppcationContext = context.getApplicationContext();
        mExternalStorage = StorageUtils.getExternalStorage();
        if (!mExternalStorage.isWritable()) {
            LogUtil.e("ExternalStorage unwritable");
        }
        File dataDir = mExternalStorage.getFilesDirectory(context, null);
        File cacheDir = mExternalStorage.getCacheDirectory(context);
        if (dataDir != null) {
            sFileRootPath = dataDir.getPath();
        } else {
            String appName = AppUtil.getAppName(context);
            if (appName != null) {
                sFileRootPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + appName;
            } else {
                sFileRootPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + UBTEDU_DIR;
            }
        }

        if (cacheDir != null) {
            sCacheRootPath = cacheDir.getPath();
        } else {
            sCacheRootPath = sFileRootPath + File.separator + CACHE_DIR;
        }

        mExternalStorage.setRootPath(sFileRootPath);
    }

    public static Storage getStorage(@StorageType int type) {
        switch (type) {
            case STORAGE_TYPE_EXT_DATA:
                mExternalStorage.setRootPath(sFileRootPath);
                return mExternalStorage;
            case STORAGE_TYPE_EXT_CACHE:
                mExternalStorage.setRootPath(sCacheRootPath);
                return mExternalStorage;
            default:
                break;
        }
        return mExternalStorage;
    }

    public static String getFileRootPath() {
        return sFileRootPath;
    }

    public static String getPublicPath() {
        return sFileRootPath + File.separator + PUBLIC;
    }

    public static String getWorkspacePath() {
        return sFileRootPath + File.separator + WORKSPACE_PATH;
    }

    public static String getProjectsPath(String userId) {
        return getUserPath(userId) + PROJECTS_PATH;
    }

    public static String getOtaPath() {
        return join(getPublicPath(), OTA);
    }

    public static String getMediaPath(String userId) {
        return getUserPath(userId) + File.separator + MEDIA_PATH;
    }

    public static String getCacheRootPath() {
        return sCacheRootPath;
    }


    public static String getImageCacheDirectoryPath() {
        return sFileRootPath + File.separator + IMAGE_CACHE;
    }

    public static String getPublicBlocklyPath() {
        return getPublicPath() + File.separator + BLOCKLY_PATH;
    }

    public static String getUnity3dPath() {
        return getPublicPath() + File.separator + UNITY3D_PATH;
    }

    public static String getUsersPath() {
        return sFileRootPath + File.separator + USERS;
    }

    public static String getCacheUsersPath() {
        return sCacheRootPath + File.separator + USERS;
    }

    public static String getUserPath(String userId) {
        return getUsersPath() + userId + File.separator;
    }

    public static String getProjectsDownloadPath() {
        return getCacheRootPath() + File.separator + DOWNLOAD_PATH + File.separator + PROJECTS_PATH;
    }


    public static String getZipCacheAbsolutePath() {

        return getCacheRootPath() + File.separator + ZIP_CACHE_PATH;
    }

    public static String getUktCacheAbsolutePath() {

        return getCacheRootPath() + File.separator + UKT_CACHE_PATH;
    }

    public static String getImportCacheAbsolutePath() {

        return getCacheRootPath() + File.separator + IMPORT_CACHE_PATH;
    }

    public static String getSdcardRootPath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }


    public static String getSuffix(String file) {
        if (file != null && file.contains(".")) {
            return file.substring(file.lastIndexOf("."));
        }
        return "";
    }

    public static String getNameWithoutSuffix(String filePath) {
        if (filePath != null) {
            File file = new File(filePath);
            String name = file.getName();
            String suffix = getSuffix(filePath);
            if (suffix != null && !"".equals(suffix)) {
                return name.substring(0, name.indexOf(suffix));
            }
        }
        return "";
    }

    public static String removeSuffix(String file) {
        if (file != null) {
            return file.substring(0, file.lastIndexOf("."));
        }
        return "";
    }

    public static String join(String... paths) {
        StringBuilder pathBuilder = new StringBuilder();
        if (paths != null && paths.length > 1) {
            for (int i = 0; i < paths.length; i++) {
                if (i == paths.length - 1) {
                    pathBuilder.append(paths[paths.length - 1]);
                } else {
                    pathBuilder.append(paths[i]).append(File.separator);
                }
            }
        }
        return pathBuilder.toString();
    }

    public static Observable<Boolean> writeFile(final String path, final Object content) {
        return Observable
                .create(new ObservableOnSubscribe<Boolean>() {
                    @Override
                    public void subscribe(ObservableEmitter<Boolean> e) {
                        File file = new File(path);
                        File parent = file.getParentFile();
                        if (parent != null && !parent.exists()) {
                            parent.mkdirs();
                        }
                        try (FileOutputStream fos = new FileOutputStream(file);
                             ObjectOutputStream obs = new ObjectOutputStream(fos)) {
                            obs.writeObject(content);
                            obs.flush();
                            e.onNext(true);
                            e.onComplete();
                        } catch (Exception err) {
                            err.printStackTrace();
                            e.onError(err);
                        }
                    }
                })
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static boolean writeTxtFile(final String path, final String content) {
        return writeTxtFile(path, content, false);
    }

    public static boolean writeTxtFile(final String path, final String content, final boolean append) {
        File file = new File(path);
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }
        if (!TextUtils.isEmpty(content)) {
            try (FileOutputStream fos = new FileOutputStream(file, append)) {

                fos.write(content.getBytes("utf-8"), 0, content.getBytes().length);
                fos.flush();
                fos.close();
                return true;
            } catch (Exception err) {
                err.printStackTrace();
            }
        }

        return false;
    }

    public static Observable<Boolean> asyncWriteTxtFile(final String path, final String content) {
        return Observable
                .create(new ObservableOnSubscribe<Boolean>() {
                    @Override
                    public void subscribe(ObservableEmitter<Boolean> e) {
                        boolean isSuccess = writeTxtFile(path, content);
                        if (!e.isDisposed()) {
                            if (isSuccess) {
                                e.onNext(true);
                                e.onComplete();
                            } else {
                                e.onError(new Exception("Write file fail path:" + path + "  content:" + content));
                            }
                        }
                    }
                })
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static Object readObjectFile(final String path) {
        Object temp = null;
        File file = new File(path);
        if (!file.exists()) {
            LogUtil.e("Error: No found the file: " + path);
            return null;

        } else {
            try (FileInputStream fin = new FileInputStream(file);
                 ObjectInputStream objIns = new ObjectInputStream(fin)) {

                temp = objIns.readObject();
                objIns.close();
                fin.close();
                return temp;
            } catch (Exception err) {
                err.printStackTrace();
                LogUtil.e("Error: " + err.getMessage());
            }
        }
        return null;
    }

    public static Observable<String> asyncReadTxtFile(final String path) {
        return Observable
                .create(new ObservableOnSubscribe<String>() {
                    @Override
                    public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                        String content = readTxtFile(path);
                        if (content != null) {
                            if (!emitter.isDisposed()) {
                                emitter.onNext(content);
                                emitter.onComplete();
                            }
                        } else {
                            if (!emitter.isDisposed()) {
                                emitter.onError(new Exception("Read file fail:" + path));
                            }
                        }
                    }
                })
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static String readTxtFile(final String path) {
        StringBuilder temp = new StringBuilder();
        try {
            String encoding = "utf-8";
            File file = new File(path);
            if (file.isFile() && file.exists()) { //判断文件是否存在
                try (InputStreamReader read = new InputStreamReader(
                        new FileInputStream(file), encoding);//考虑到编码格式
                     BufferedReader bufferedReader = new BufferedReader(read)) {

                    String lineTxt;
                    boolean firstLine = true;
                    while ((lineTxt = bufferedReader.readLine()) != null) {
                        if (firstLine) {
                            firstLine = false;
                        } else {
                            temp.append("\n");
                        }
                        temp.append(lineTxt);
                    }
                    read.close();
                    return temp.toString();
                } catch (Exception e) {
                    e.printStackTrace();

                }

            }
        } catch (Exception err) {
            err.printStackTrace();
            LogUtil.e("Error: " + err.getMessage());
        }
        return null;
    }
    public static Observable<String> zipFiles(final String outPath, final String srcPath) {

        return Observable
                .create(new ObservableOnSubscribe<String>() {
                    @Override
                    public void subscribe(ObservableEmitter<String> e) {
                        LogUtil.d("----- Start zip file -----");
                        boolean success = ZipUtil2.zip(srcPath, outPath);
                        if (!e.isDisposed()) {
                            if (success) {
                                e.onNext(outPath);
                                e.onComplete();
                                LogUtil.d("zipFiles:" + srcPath + " success:" + success);
                            } else {
                                e.onError(new Throwable("Zip action fail!"));
                            }
                        }
                    }
                })
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

    }

    public static Observable<String> unzipFile(final String zipFilePath, final String outPath) {

        return Observable
                .create(new ObservableOnSubscribe<String>() {
                    @Override
                    public void subscribe(ObservableEmitter<String> e) {
                        boolean success = ZipUtil2.unzipFile(zipFilePath, outPath);
                        if (!e.isDisposed()) {
                            if (success) {
                                e.onNext(outPath);
                                e.onComplete();
                            } else {
                                e.onError(new Throwable("Zip action fail!"));
                            }
                        }
                    }
                })
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

    }

    public static boolean copyDir(String sourcePath, String newPath) {
        if (TextUtils.isEmpty(sourcePath) || TextUtils.isEmpty(newPath)) {
            return false;
        }


        File file = new File(sourcePath);
        String[] filePath = file.list();
        File newFile = new File(newPath);
        if (!newFile.exists()) {
            newFile.mkdirs();
        }
        if (filePath != null) {
            for (int i = 0; i < filePath.length; i++) {
                if ((new File(sourcePath + File.separator + filePath[i])).isDirectory()) {
                    copyDir(sourcePath + File.separator + filePath[i], newPath + File.separator + filePath[i]);
                }

                if (new File(sourcePath + File.separator + filePath[i]).isFile()) {
                    boolean success = copyFile(sourcePath + File.separator + filePath[i], newPath + File.separator + filePath[i]);
                    if (!success) {
                        return false;
                    }
                }

            }
        }

        return true;
    }


    public static boolean copyFile(String oldPath, String newPath) {
        if (TextUtils.isEmpty(oldPath) || TextUtils.isEmpty(newPath)) {
            return false;
        }
        File oldFile = new File(oldPath);
        File file = new File(newPath);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        if (!file.exists()) {
            try {
               boolean success =  file.createNewFile();
               if (!success){
                   return false;
               }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try (FileInputStream in = new FileInputStream(oldFile);
             FileOutputStream out = new FileOutputStream(file)) {

            byte[] buffer = new byte[2097152];
            int len;
            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;

    }

    public static boolean copyFile(Uri srcUri, String newPath) {
        if (srcUri == null || TextUtils.isEmpty(srcUri.toString()) || TextUtils.isEmpty(newPath)) {
            return false;
        }
        InputStream in = null;
        FileOutputStream out = null;
        try {
            in = sAppcationContext.getContentResolver().openInputStream(srcUri);
            if(in == null){
                return false;
            }

            File file = new File(newPath);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            if (!file.exists()) {
                boolean success =  file.createNewFile();
                if (!success){
                    return false;
                }
            }

            out = new FileOutputStream(file);
            byte[] buffer = new byte[512 * 1024];
            int len;
            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if(in != null){
                URoIoUtils.close(in);
            }
            if(out != null){
                URoIoUtils.close(out);
            }
        }
        return true;
    }

    public static Observable<Boolean> asyncCopyFile(final String srcFilePath, final String destPath) {
        return Observable
                .create(new ObservableOnSubscribe<Boolean>() {
                    @Override
                    public void subscribe(@NonNull ObservableEmitter<Boolean> emitter) {
                        ExternalStorage storage = (ExternalStorage) FileHelper.getStorage(FileHelper.STORAGE_TYPE_EXT_DATA);
                        if (TextUtils.isEmpty(srcFilePath) || TextUtils.isEmpty(destPath)) {
                            emitter.onError(new Throwable("Copy file fail! srcFilePath is empty!"));
                            return;
                        }
                        File destFile = new File(destPath);
                        boolean success = storage.copy(new File(srcFilePath), destFile.getParent(), destFile.getName(), true);
                        if (success) {
                            emitter.onNext(true);
                            emitter.onComplete();
                        } else {
                            emitter.onError(new Throwable("Copy file fail! srcFilePath = " + srcFilePath));
                        }

                    }
                })
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static Observable<Boolean> asyncCopyDir(final String srcFilePath, final String desFilePath) {
        return Observable
                .create(new ObservableOnSubscribe<Boolean>() {
                    @Override
                    public void subscribe(@NonNull ObservableEmitter<Boolean> emitter) {
                        boolean success = copyDir(srcFilePath, desFilePath);
                        if (success) {
                            emitter.onNext(true);
                            emitter.onComplete();
                        } else {
                            emitter.onError(new Throwable("Copy file fail! srcFilePath = " + srcFilePath));
                        }

                    }
                })
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static Observable<Boolean> copyDir(final String srcFilePath, final String directory, final String fileName) {
        return Observable
                .create(new ObservableOnSubscribe<Boolean>() {
                    @Override
                    public void subscribe(@NonNull ObservableEmitter<Boolean> emitter) {
                        boolean success = copyDir(srcFilePath, directory + File.separator + fileName);
                        if (success) {
                            emitter.onNext(true);
                            emitter.onComplete();
                        } else {
                            emitter.onError(new Throwable("Copy file fail! srcFilePath = " + srcFilePath));
                        }

                    }
                })
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static boolean createFile(final String directory, final String fileName, final byte[] content) {
        ExternalStorage storage = (ExternalStorage) getStorage(FileHelper.STORAGE_TYPE_EXT_DATA);
        return storage.createFile(directory, fileName, content, true);
    }

    public static Observable<Boolean> asyncCreateFile(final String directory, final String fileName, final byte[] content) {
        return Observable
                .create(new ObservableOnSubscribe<Boolean>() {
                    @Override
                    public void subscribe(@NonNull ObservableEmitter<Boolean> emitter) {
                        boolean success = createFile(directory, fileName, content);
                        if (success) {
                            emitter.onNext(true);
                            emitter.onComplete();
                        } else {
                            emitter.onError(new Throwable("create file fail!"));
                        }
                    }
                })
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({STORAGE_TYPE_EXT_DATA, STORAGE_TYPE_EXT_CACHE, STORAGE_TYPE_INT_DATA,
            STORAGE_TYPE_INT_CACHE})
    public @interface StorageType {
    }

    /**
     * 搜索路径
     *
     * @param absRootPath 设置搜索的起始目录，绝对路径
     * @param dirName     设置搜索的目录名称
     * @return 返回搜索到的路径，未搜索到返回null
     */
    public static String searchDirectory(String absRootPath, String dirName) {
        String result = null;
        File rootDir = new File(absRootPath);
        if (!TextUtils.isEmpty(absRootPath) && rootDir.isDirectory() && rootDir.exists()) {
            File[] list = rootDir.listFiles();
            for (File file : list) {
                if (file.isDirectory()) {
                    if (file.getName().equals(dirName)) {
                        // 找到目录
                        return file.getAbsolutePath();

                    } else {
                        // 未找到目录，可能在某个子目录下，继续查找
                        result = searchDirectory(file.getAbsolutePath(), dirName);
                        if (!TextUtils.isEmpty(result)) {
                            // 找到目录，返回路径
                            return result;
                        }
                    }
                }
            }
        }
        return result;
    }

    private static String getUserId() {
        return UserManager.getInstance().getLoginUserId();
    }

    /**
     * 获取文件大小，单位：字节（B）
     */
    public static long getFileSize(String filePath) {
        File file = new File(filePath);
        return file.length();
    }

    /**
     * 获取手机内部存储空间
     */
    public static long getInternalMemorySize() {
        File file = Environment.getDataDirectory();
        StatFs statFs = new StatFs(file.getPath());
        long blockSizeLong = statFs.getBlockSizeLong();
        long blockCountLong = statFs.getBlockCountLong();
        return blockCountLong * blockSizeLong;
    }

    /**
     * 获取手机内部可用存储空间
     *
     * @return 以B为单位的容量
     */
    public static long getAvailableInternalMemorySize() {
        File file = Environment.getDataDirectory();
        StatFs statFs = new StatFs(file.getPath());
        long availableBlocksLong = statFs.getAvailableBlocksLong();
        long blockSizeLong = statFs.getBlockSizeLong();
        return availableBlocksLong * blockSizeLong;
    }


    /**
     * 判断文件名是否合法，符合我们的规范。
     * 1 字母A-Za-z，数字0-9，下划线_ 小圆点. 横杠线- 小括号() 中括号[] 大括号{} 加号+ 感叹号! 邮箱符号@ 百分号% 等于号= 中文和中文标点 空格
     * 2 长度不超过256
     * 3 不以小圆点开头，因为linux下是隐藏文件
     */
    public static boolean isFileNameLegal(String fileName) {
        if (TextUtils.isEmpty(fileName) || fileName.length() > 255) {
            // 文件名为空，或字符长度超过256
            return false;
        }
        String regex =
                "^[A-Za-z0-9_\\s*\\-()\\[\\]{}\\+!@=,\uFF00-\uFFEF\u3000-\u303F\u4E00-\u9FA5]" +
                        "[A-Za-z0-9_\\s*\\-()\\[\\]{}\\+!@=,\uFF00-\uFFEF\u3000-\u303F\u4E00-\u9FA5.]*$";
        return !fileName.startsWith(".") && fileName.matches(regex);
    }

    /**
     * 重命名文件夹
     *
     * @param path    文件夹所在路径
     * @param oldName 文件夹旧名
     * @param newName 文件夹新名
     * @return true修改成功
     */
    public static boolean renameDir(String path, String oldName, String newName) {
        File from = new File(path, oldName);

        if (!from.exists() || !from.isDirectory()) {
            LogUtil.e("Directory does not exist: " + oldName);
            return false;
        }

        File to = new File(path, newName);

        //Rename
        return from.renameTo(to);
    }

    public static boolean removeDir(File file) {

        if (file.isDirectory()) {        //如果是文件夹
            File[] files = file.listFiles();
            for (File file2 : files) {
                removeDir(file2);
            }
        }
        //不管是不是文件夹都删除，当递归完，文件夹都会为空，都会被删除
        return file.delete();
    }
}
