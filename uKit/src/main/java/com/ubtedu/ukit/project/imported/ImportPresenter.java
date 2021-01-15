package com.ubtedu.ukit.project.imported;

import android.net.Uri;
import android.text.TextUtils;

import com.ubtedu.alpha1x.utils.GsonUtil;
import com.ubtedu.alpha1x.utils.LogUtil;
import com.ubtedu.base.net.rxretrofit.exception.RxException;
import com.ubtedu.base.net.rxretrofit.subscriber.SimpleRxSubscriber;
import com.ubtedu.ukit.common.files.FileHelper;
import com.ubtedu.ukit.common.utils.UuidUtil;
import com.ubtedu.ukit.project.UserDataSynchronizer;
import com.ubtedu.ukit.project.vo.Project;
import com.ubtedu.ukit.user.UserManager;

import java.io.File;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.functions.Function;

/**
 * @Author hai.li
 * @Date 2020/05/12
 **/
public class ImportPresenter extends ImportContracts.Presenter {

    @Override
    public void doProjectImport(Uri uriFile) {

        if (uriFile != null && !TextUtils.isEmpty(uriFile.getPath()) && uriFile.getPath().endsWith(FileHelper.UKT_FILE_SUFFIX)){
            String fileName = UuidUtil.createUUID();
            String newFilePath = FileHelper.getImportCacheAbsolutePath() + File.separator + fileName + FileHelper.ZIP_FILE_SUFFIX;
            String unzipFileDir = FileHelper.getImportCacheAbsolutePath() + File.separator + fileName;
            boolean copyResult = FileHelper.copyFile(uriFile, newFilePath);
            File newFileZip = new File(newFilePath);
            if(copyResult && newFileZip.exists()){
                FileHelper.unzipFile(newFileZip.getAbsolutePath(), unzipFileDir)
                        .map(new Function<String, Project>() {

                            @Override
                            public Project apply(String outPath) throws Exception {
                                File unzipContentFile = new File(outPath);
                                if(unzipContentFile.exists() && unzipContentFile.isDirectory()){
                                    File[] originalFileDirFiles = unzipContentFile.listFiles();//正常只有一个文件夹
                                    LogUtil.d("originalFileDirFiles = " + originalFileDirFiles.length);
                                    for(File originalFileDirFile : originalFileDirFiles){
                                        String originalFileDirPath = outPath + File.separator + originalFileDirFile.getName();

                                        File originalFileDir = new File(originalFileDirPath);
                                        LogUtil.d("originalFileDir = " + originalFileDir.exists() + " " + originalFileDir.getAbsolutePath());
                                        Project project = UserDataSynchronizer.loadProjectFromFile(originalFileDir);
                                        if(project != null){
                                            if(project.isCompat()){
                                                project.projectId = UuidUtil.createUUID();
                                                project.localModifyTime = System.currentTimeMillis();
                                                project.userId = UserManager.getInstance().getLoginUserId();
                                                project.isModified = true;

                                                //modify manifest file
                                                String json = GsonUtil.get().toJson(project);
                                                String manifest = FileHelper.join(originalFileDirPath, Project.PROJECT_MANIFEST_NAME);
                                                FileHelper.writeTxtFile(manifest, json);

                                                //rename originalFileDir to newFileDir
                                                File newFileDir = new File(originalFileDir.getParentFile().getAbsolutePath() + File.separator + project.projectId);
                                                if(originalFileDir.renameTo(newFileDir)){
                                                    //copy to user project dir
                                                    boolean isCopySuccess = FileHelper.copyDir(newFileDir.getAbsolutePath(), FileHelper.getProjectsPath(project.userId) +File.separator+ project.projectId);
                                                    if(isCopySuccess){
                                                        return project;
                                                    }else {
                                                        //此处处理，拷贝过程中，新目录的缓存文件
                                                        doCleanCacheDir(new File(FileHelper.getProjectsPath(project.userId) +File.separator+ project.projectId));
                                                    }
                                                }else {
                                                    //rename失败，此时的文件都在导入缓存目录，会在页面关闭是统一删除
                                                }
                                            }else {
                                                return project;
                                            }
                                        }
                                    }
                                }
                                return null;
                            }
                        }).subscribe(new SimpleRxSubscriber<Project>(){
                                @Override
                                public void onError(RxException e) {
                                    LogUtil.d("onError = " + e );
                                    getView().onProjectImportSuccess(null);
                                }

                                @Override
                                public void onNext(Project project) {
                                    LogUtil.d("result = " + project );
                                    getView().onProjectImportSuccess(project);
                                }
                        });
            }else {
                getView().onProjectImportSuccess(null);
            }
        }else {
            getView().onProjectImportSuccess(null);
        }
    }

    @Override
    public void doCleanImportCache() {
        doCleanCacheDir(new File(FileHelper.getImportCacheAbsolutePath()));
    }

    @Override
    public void release() {
        doCleanImportCache();
        super.release();
    }

    /**
     * 删除缓存文件
     * @param file
     */
    private void doCleanCacheDir(File file){
        if(file != null && file.exists()){
            Observable.create(new ObservableOnSubscribe<Boolean>() {
                @Override
                public void subscribe(ObservableEmitter<Boolean> emitter) throws Exception {
                    FileHelper.removeDir(file);
                    emitter.onNext(true);
                    emitter.onComplete();
                }
            }).subscribe(new SimpleRxSubscriber<>());
        }
    }

    @Override
    public File getFileForUri(Uri uri) {
        String path = uri.getEncodedPath();
        final int splitIndex = path.indexOf('/', 1);
        // 解析出tag，在此例中tag是my_files
        final String tag = Uri.decode(path.substring(1, splitIndex));
        // path是uri中的<file_name>，path可以只是文件名，也可以是带路径的文件名
        path = Uri.decode(path.substring(splitIndex + 1));
        // 这个tag就是`<files-path name="my_files" path="tempfiles" />`中的属性name

        File root = new File(FileHelper.getSdcardRootPath());
        LogUtil.d("path = " + path + " tag = " + tag + " root = " + root.getAbsolutePath());

        //final File root = mRoots.get(tag);
        // 将路径拼起来，构成实际的文件路径
        //File file = new File(root, path);
        File file = new File(path);
        LogUtil.d("file = " + file.exists());
        if(!file.exists()){
            file = new File(root.getAbsolutePath() + File.separator + path);
        }
        // 略
        return file;
    }

}
