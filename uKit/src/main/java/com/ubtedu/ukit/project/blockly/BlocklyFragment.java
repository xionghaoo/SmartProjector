/**
 * ©2018, UBTECH Robotics, Inc. All rights reserved (C)
 **/
package com.ubtedu.ukit.project.blockly;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.Nullable;

import com.ubtedu.alpha1x.utils.LogUtil;
import com.ubtedu.bridge.BridgeBoolean;
import com.ubtedu.bridge.BridgeImpl;
import com.ubtedu.ukit.BuildConfig;
import com.ubtedu.ukit.R;
import com.ubtedu.ukit.common.analysis.UBTReporter;
import com.ubtedu.ukit.common.base.UKitBaseFragment;
import com.ubtedu.ukit.project.bridge.BridgeCommunicator;
import com.ubtedu.ukit.project.bridge.functions.BlocklyFunctions;
import com.ubtedu.ukit.project.vo.Blockly;

/**
 * 我的项目
 *
 * @Author qinicy
 * @Date 2018/11/5
 **/
public class BlocklyFragment extends UKitBaseFragment<BlocklyContracts.Presenter, BlocklyContracts.UI> {

    private final static String TAG = "BLOCKLY-WEBVIEW";
    private static final int BLOCKLY_LOADING_TIMEOUT = 40000;
    private WebView mWebView;
    private Blockly mBlockly;
    private boolean isControllerBlockly;
    private OnBlocklyLoadingFinishListener mLoadingFinishListener;
    private Handler mLoadingHandler;

    public static BlocklyFragment newInstance(boolean isControllerBlockly) {
        BlocklyFragment fragment = new BlocklyFragment();
        fragment.isControllerBlockly = isControllerBlockly;
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLoadingHandler = new Handler(Looper.getMainLooper());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_blockly, null);
        mWebView = root.findViewById(R.id.fragment_blockly_webview);
        mWebView.setWebViewClient(new BlocklyWebClient());
        WebView.setWebContentsDebuggingEnabled(BuildConfig.DEBUG);
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.getSettings().setBlockNetworkImage(true);
        mWebView.getSettings().setAllowFileAccess(true);//android 11 默认false
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (newProgress == 100) {
                    // 网页加载完成
                    mWebView.getSettings().setBlockNetworkImage(false);
                }

            }
        });
        BridgeCommunicator.getInstance().getBlocklyBridgeHandler(isControllerBlockly).setWebView(mWebView);
        getPresenter().loadBlockly();
        startLoadingTimeout();
        return root;
    }

    private void startLoadingTimeout() {
        mLoadingHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mLoadingFinishListener != null) {
                    mLoadingFinishListener.onLoadingFinish();
                }
            }
        }, BLOCKLY_LOADING_TIMEOUT);
    }

    private void clearLoadingTimeout() {
        mLoadingHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onVisibilityChangedToUser(boolean isVisibleToUser) {
        super.onVisibilityChangedToUser(isVisibleToUser);
        int isVisible = BridgeBoolean.wrap(isVisibleToUser);
        BridgeCommunicator.getInstance().getBlocklyBridge(isControllerBlockly).call(BlocklyFunctions.onVisibilityChangedToUser, new Object[]{isVisible}, null);

        reportPageEvent(isVisibleToUser);
    }

    private void reportPageEvent(boolean isVisibleToUser) {
        LogUtil.d("isVisibleToUser:"+isVisibleToUser+"  isControllerBlockly:"+isControllerBlockly);
        String name = "blockly";
        if (isControllerBlockly) {
            name = "remote_blockly";
        }
        if (isVisibleToUser) {
            UBTReporter.onPageStart(name);
        } else {
            UBTReporter.onPageEnd(name);
        }
    }

    /**
     * 主动关闭Blockly，释放一些资源
     */
    public void close() {
        if (mWebView != null){
            ViewParent parent = mWebView.getParent();
            if (parent != null) {
                ((ViewGroup) parent).removeView(mWebView);
            }
            mWebView.stopLoading();
            mWebView.removeAllViews();
            mWebView.destroy();
            mWebView = null;
        }
        clearLoadingTimeout();
        BridgeCommunicator.getInstance().getBlocklyBridgeHandler(isControllerBlockly).release();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        clearLoadingTimeout();
    }

    @Override
    public void onDestroy() {
        ((BridgeImpl) BridgeCommunicator.getInstance().getBlocklyBridge(isControllerBlockly)).setBridgeCommunicable(false);
        super.onDestroy();
    }

    public void setBlocklyLoadingFinishListener(OnBlocklyLoadingFinishListener loadingFinishListener) {
        mLoadingFinishListener = loadingFinishListener;
    }

    @Override
    protected BlocklyContracts.Presenter createPresenter() {
        return new BlocklyPresenter();
    }

    @Override
    protected BlocklyContracts.UI createUIView() {
        return new BlocklyUI();
    }

    public Blockly getBlockly() {
        return mBlockly;
    }

    public void setBlockly(Blockly blockly) {
        mBlockly = blockly;
    }


    class BlocklyWebClient extends WebViewClient {
        private long loadPageStartTime;

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            loadPageStartTime = System.currentTimeMillis();
            LogUtil.d(TAG, "加载页面开始 ---> " + loadPageStartTime);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);

            if (mLoadingFinishListener != null) {
                mLoadingFinishListener.onLoadingFinish();
                clearLoadingTimeout();
            }
            LogUtil.d(TAG, "加载页面结束 ---> " + (System.currentTimeMillis() - loadPageStartTime) + "ms");
        }
    }

    class BlocklyUI extends BlocklyContracts.UI {

        @Override
        public void loadUrl(String url) {
            if (mWebView != null) {
                mWebView.loadUrl(url);
            }
        }
    }

    public interface OnBlocklyLoadingFinishListener {
        void onLoadingFinish();
    }
}
