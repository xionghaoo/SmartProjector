package com.ubtedu.ukit.project.imported;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.ubtedu.alpha1x.utils.LogUtil;
import com.ubtedu.base.net.rxretrofit.subscriber.SimpleRxSubscriber;
import com.ubtedu.ukit.R;
import com.ubtedu.ukit.application.BasicEventDelegate;
import com.ubtedu.ukit.common.base.UKitBaseActivity;
import com.ubtedu.ukit.home.HomeActivity;
import com.ubtedu.ukit.menu.settings.Settings;
import com.ubtedu.ukit.project.UserDataSynchronizer;
import com.ubtedu.ukit.project.vo.Project;

/**
 * @Author hai.li
 * @Date 2020/05/12
 **/
public class ImportActivity extends UKitBaseActivity<ImportContracts.Presenter, ImportContracts.UI> {

    private TextView mProjectImportTv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.d("");
        setContentView(R.layout.activity_project_import);

        mProjectImportTv = findViewById(R.id.project_import_tv);
        bindSafeClickListener(mProjectImportTv);

        Intent intent = getIntent();
        String action = intent.getAction();
        if (Intent.ACTION_VIEW.equals(action)) {
            Uri uri = intent.getData();
            String path = Uri.decode(uri.getEncodedPath());
            LogUtil.d("uri = " + uri + " path = " + path);
            getPresenter().doProjectImport(uri);
        }else {
            finish();
        }
    }

    @Override
    public void onClick(View v, boolean isSafeClick) {
        super.onClick(v, isSafeClick);
        if (v == mProjectImportTv){
            finish();
        }
    }

    private BasicEventDelegate getBasicEventDelegate() {
        if (getEventDelegate() instanceof BasicEventDelegate) {
            return (BasicEventDelegate) getEventDelegate();
        }
        return null;
    }

    @Override
    protected ImportContracts.Presenter createPresenter() {
        return new ImportPresenter();
    }

    @Override
    protected ImportContracts.UI createUIView() {
        return new ImportUI();
    }

    class ImportUI extends ImportContracts.UI {
        @Override
        public void onProjectImportSuccess(Project project) {

            LogUtil.d("onImportProjectSuccess = " + project);
            String resultTip;
            if(project != null){
                if(!project.isCompat()){
                    resultTip = getString(R.string.project_share_fail_low_version);
                }else if(project.getTargetDevice() == Settings.getTargetDevice()){
                    resultTip = getString(R.string.project_share_save_success);
                }else {
                    resultTip = getString(R.string.project_share_save_success_need_chang_target);
                }
                UserDataSynchronizer.getInstance().sync(true).subscribe(new SimpleRxSubscriber<>());
            }else {
                resultTip = getString(R.string.project_share_save_failed);
            }

            BasicEventDelegate delegate = getBasicEventDelegate();
            if(delegate != null && !delegate.isLauncherComplete()){
                LogUtil.d("open HomeActivity");
                HomeActivity.open(ImportActivity.this, true, resultTip);
            }else {
                getUIView().getUIDelegate().toastShort(resultTip);
            }
            finish();
        }
    }
}
