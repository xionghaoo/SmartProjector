package com.ubtedu.ukit.demo;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ubtedu.alpha1x.core.base.activity.UbtBaseActivity;
import com.ubtedu.alpha1x.core.mvp.ViewModelFactory;
import com.ubtedu.ukit.R;
import com.ubtedu.ukit.home.HomeActivity;

public class DemoActivity extends UbtBaseActivity<DemoContracts.Presenter, DemoContracts.UI> {
    private TextView mTextView;
    private Button mRequestBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);
        mTextView = findViewById(R.id.user_tv);
        mRequestBtn = findViewById(R.id.request_btn);
        bindSafeClickListener(mRequestBtn);
    }
    @Override
    public void onClick(View v, boolean isSafeClick) {
        super.onClick(v, isSafeClick);
        if (v == mRequestBtn){
//            getPresenter().requestUserInfo();
            HomeActivity.open(this);
        }
    }

    @Override
    protected ViewModelFactory getViewModelFactory() {
        return new DemoViewModelFactory();
    }
    @Override
    protected DemoContracts.Presenter createPresenter() {
        return new DemoPresenter();
    }
    @Override
    protected DemoContracts.UI createUIView() {
        return new MainUI();
    }
    class MainUI extends DemoContracts.UI{
        @Override
        void onUpdateUserInfo(User user) {
            mTextView.setText(user.toString());
        }
        @Override
        void onLoading(String msg) {
            mTextView.setText(msg);
        }
        @Override
        void onError(String msg) {
            mTextView.setText(msg);
        }
    }
}
