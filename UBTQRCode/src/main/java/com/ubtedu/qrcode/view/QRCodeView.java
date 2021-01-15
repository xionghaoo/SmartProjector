package com.ubtedu.qrcode.view;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.zxing.Result;
import com.ubtedu.qrcode.camera.CameraManager;
import com.ubtedu.qrcode.decode.CaptureHandler;
import com.ubtedu.qrcode.decode.DecodeImageCallback;
import com.ubtedu.qrcode.decode.DecodeImageThread;
import com.ubtedu.qrcode.decode.DecodeManager;
import com.ubtedu.qrcode.listener.QRCodeDecodeListener;
import com.ubtedu.qrcodescanner.R;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static android.content.Context.AUDIO_SERVICE;
import static android.content.Context.VIBRATOR_SERVICE;

/**
 * 基于：https://github.com/iluhcm/QrCodeScanner
 * Created by qinicy on 2017/5/27.
 */

public class QRCodeView extends LinearLayout implements SurfaceHolder.Callback {

    public static final int MSG_DECODE_SUCCEED = 1;
    public static final int MSG_DECODE_FAIL = 2;
    private CaptureHandler mCaptureHandler;
    private boolean mHasSurface;
    private QrCodeFinderView mQrCodeFinderView;
    private SurfaceView mSurfaceView;
    private final DecodeManager mDecodeManager = new DecodeManager();
    /**
     * 声音和振动相关参数
     */
    private static final float BEEP_VOLUME = 0.10f;
    private static final long VIBRATE_DURATION = 200L;
    private MediaPlayer mMediaPlayer;
    private boolean mPlayBeep;
    private boolean mVibrate;
    private Executor mQrCodeExecutor;
    private Handler mHandler;


    private QRCodeDecodeListener mQRCodeDecodeListener;
    public QRCodeView(Context context) {
        super(context);
        initView();
        initData();
    }

    public QRCodeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
        initData();
    }

    public QRCodeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
        initData();
        initSurfaceView();
    }

    public void start(){
        SurfaceHolder surfaceHolder = mSurfaceView.getHolder();
        if (mHasSurface) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }

        mPlayBeep = true;
        AudioManager audioService = (AudioManager) getContext().getSystemService(AUDIO_SERVICE);
        if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            mPlayBeep = false;
        }
        initBeepSound();
        mVibrate = true;
    }
    public void stop(){
        if (mCaptureHandler != null) {
            mCaptureHandler.quitSynchronously();
            mCaptureHandler = null;
        }
        CameraManager.get().closeDriver();
    }

    public void setQRCodeDecodeListener(QRCodeDecodeListener QRCodeDecodeListener) {
        mQRCodeDecodeListener = QRCodeDecodeListener;
    }
    public void decodeQRCodeFromImage(String imgPath){
        if (null != mQrCodeExecutor && !TextUtils.isEmpty(imgPath)) {
            mQrCodeExecutor.execute(new DecodeImageThread(imgPath, mDecodeImageCallback));
        }
    }
    private void initSurfaceView() {
        SurfaceHolder surfaceHolder = mSurfaceView.getHolder();
        turnFlashLightOff();
        if (mHasSurface) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }

        mPlayBeep = true;
        AudioManager audioService = (AudioManager) getContext().getSystemService(AUDIO_SERVICE);
        if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            mPlayBeep = false;
        }
        initBeepSound();
        mVibrate = true;
    }


    private void initView() {
        LayoutInflater.from(getContext()).inflate(R.layout.activity_qr_code,this);
        mQrCodeFinderView = (QrCodeFinderView) findViewById(R.id.qr_code_view_finder);
        mSurfaceView = (SurfaceView) findViewById(R.id.qr_code_preview_view);
        mHasSurface = false;


    }

    private void initData() {
        CameraManager.init(getContext());
        mQrCodeExecutor = Executors.newSingleThreadExecutor();
        mHandler = new WeakHandler(this);
    }


    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!mHasSurface) {
            mHasSurface = true;
            initCamera(holder);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mHasSurface = false;
    }
    private boolean hasCameraPermission() {
        PackageManager pm = getContext().getPackageManager();
        return PackageManager.PERMISSION_GRANTED == pm.checkPermission("android.permission.CAMERA", getContext().getPackageName());
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            CameraManager.get().openDriver(surfaceHolder);
        } catch (IOException e) {
            // 基本不会出现相机不存在的情况
            Toast.makeText(getContext(), getContext().getString(R.string.qr_code_camera_not_found), Toast.LENGTH_SHORT).show();
            return;
        } catch (RuntimeException re) {
            re.printStackTrace();
            mDecodeManager.showPermissionDeniedDialog(getContext());
            return;
        }
        mQrCodeFinderView.setVisibility(View.VISIBLE);
        mSurfaceView.setVisibility(View.VISIBLE);
        findViewById(R.id.qr_code_view_background).setVisibility(View.GONE);
        if (mCaptureHandler == null) {
            mCaptureHandler = new CaptureHandler(this);
        }
    }

    private void restartPreview() {
        if (null != mCaptureHandler) {
            mCaptureHandler.restartPreviewAndDecode();
        }
    }
    /**
     * Handler scan result
     *
     * @param result
     */
    public void handleDecode(Result result) {
        playBeepSoundAndVibrate();
        if (null == result) {
            mDecodeManager.showCouldNotReadQrCodeFromScanner(getContext(), new DecodeManager.OnRefreshCameraListener() {
                @Override
                public void refresh() {
                    restartPreview();
                }
            });
        } else {
            String resultString = result.getText();
            handleResult(resultString);
        }
    }
    private void handleResult(String resultString) {
        if (TextUtils.isEmpty(resultString)) {
            if (mQRCodeDecodeListener != null){
                mQRCodeDecodeListener.onFail("Can't decode the qrcode!");
                restartPreview();
            }
        } else {
            if (mQRCodeDecodeListener != null){
                mQRCodeDecodeListener.onDecode(resultString);
                restartPreview();
            }
        }
    }
    public Handler getCaptureHandler() {
        return mCaptureHandler;
    }
    private void openSystemAlbum() {
        Intent intent = new Intent();
        /* 开启Pictures画面Type设定为image */
        intent.setType("image/*");
        /* 使用Intent.ACTION_GET_CONTENT这个Action */
        intent.setAction(Intent.ACTION_GET_CONTENT);
        getContext().startActivity(intent);
    }
    private void initBeepSound() {
        if (mPlayBeep && mMediaPlayer == null) {
            // The volume on STREAM_SYSTEM is not adjustable, and users found it too loud,
            // so we now play on the music stream.
//            setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setOnCompletionListener(mBeepListener);

            AssetFileDescriptor file = getResources().openRawResourceFd(R.raw.beep);
            try {
                mMediaPlayer.setDataSource(file.getFileDescriptor(), file.getStartOffset(), file.getLength());
                file.close();
                mMediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
                mMediaPlayer.prepare();
            } catch (IOException e) {
                mMediaPlayer = null;
            }
        }
    }

    private void playBeepSoundAndVibrate() {
        if (mPlayBeep && mMediaPlayer != null) {
            mMediaPlayer.start();
        }
        if (mVibrate) {
            Vibrator vibrator = (Vibrator) getContext().getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }

    /**
     * When the beep has finished playing, rewind to queue up another one.
     */
    private final MediaPlayer.OnCompletionListener mBeepListener = new MediaPlayer.OnCompletionListener() {
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
        }
    };
    public boolean turnFlashlightOn() {
        return CameraManager.get().setFlashLight(true);
    }

    public boolean turnFlashLightOff() {
        return CameraManager.get().setFlashLight(false);
    }
    private DecodeImageCallback mDecodeImageCallback = new DecodeImageCallback() {
        @Override
        public void decodeSucceed(Result result) {
            mHandler.obtainMessage(MSG_DECODE_SUCCEED, result).sendToTarget();
        }

        @Override
        public void decodeFail(int type, String reason) {
            mHandler.sendEmptyMessage(MSG_DECODE_FAIL);
        }
    };

    private static class WeakHandler extends Handler {
        private WeakReference<QRCodeView> mWeakQrCodeActivity;
        private DecodeManager mDecodeManager = new DecodeManager();

        public WeakHandler(QRCodeView imagePickerActivity) {
            super();
            this.mWeakQrCodeActivity = new WeakReference<>(imagePickerActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            QRCodeView qrCodeActivity = mWeakQrCodeActivity.get();
            switch (msg.what) {
                case MSG_DECODE_SUCCEED:
                    Result result = (Result) msg.obj;
                    if (null == result) {
                        mDecodeManager.showCouldNotReadQrCodeFromPicture(qrCodeActivity.getContext());
                    } else {
                        String resultString = result.getText();
                        handleResult(resultString);
                    }
                    break;
                case MSG_DECODE_FAIL:
                    mDecodeManager.showCouldNotReadQrCodeFromPicture(qrCodeActivity.getContext());
                    break;
            }
            super.handleMessage(msg);
        }

        private void handleResult(String resultString) {
            QRCodeView imagePickerActivity = mWeakQrCodeActivity.get();
            mDecodeManager.showResultDialog(imagePickerActivity.getContext(), resultString, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        }

    }
}
