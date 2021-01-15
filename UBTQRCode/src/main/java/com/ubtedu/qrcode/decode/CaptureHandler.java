package com.ubtedu.qrcode.decode;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.zxing.Result;
import com.ubtedu.qrcode.camera.CameraManager;
import com.ubtedu.qrcode.contants.QRcodeMessage;
import com.ubtedu.qrcode.view.QRCodeView;

/**
 * This class handles all the messaging which comprises the state machine for capture.
 */
public final class CaptureHandler extends Handler {
    private static final String TAG = CaptureHandler.class.getName();

    private final QRCodeView mActivity;
    private final DecodeThread mDecodeThread;
    private State mState;

    private enum State {
        PREVIEW, SUCCESS, DONE
    }

    public CaptureHandler(QRCodeView activity) {
        this.mActivity = activity;
        mDecodeThread = new DecodeThread(activity);
        mDecodeThread.start();
        mState = State.SUCCESS;
        // Start ourselves capturing previews and decoding.
        restartPreviewAndDecode();
    }

    @Override
    public void handleMessage(Message message) {
        switch (message.what) {
            case QRcodeMessage.auto_focus:
                // Log.d(TAG, "Got auto-focus message");
                // When one auto focus pass finishes, start another. This is the closest thing to
                // continuous AF. It does seem to hunt a bit, but I'm not sure what else to do.
                if (mState == State.PREVIEW) {
                    CameraManager.get().requestAutoFocus(this, QRcodeMessage.auto_focus);
                }
                break;
            case QRcodeMessage.decode_succeeded:
                Log.e(TAG, "Got decode succeeded message");
                mState = State.SUCCESS;
                mActivity.handleDecode((Result) message.obj);
                break;
            case QRcodeMessage.decode_failed:
                // We're decoding as fast as possible, so when one decode fails, start another.
                mState = State.PREVIEW;
                CameraManager.get().requestPreviewFrame(mDecodeThread.getHandler(), QRcodeMessage.decode);
                break;
        }
    }

    public void quitSynchronously() {
        mState = State.DONE;
        CameraManager.get().stopPreview();
        Message quit = Message.obtain(mDecodeThread.getHandler(), QRcodeMessage.quit);
        quit.sendToTarget();
        try {
            mDecodeThread.join();
        } catch (InterruptedException e) {
            // continue
        }

        // Be absolutely sure we don't send any queued up messages
        removeMessages(QRcodeMessage.decode_succeeded);
        removeMessages(QRcodeMessage.decode_failed);
    }

    public void restartPreviewAndDecode() {
        if (mState != State.PREVIEW) {
            CameraManager.get().startPreview();
            mState = State.PREVIEW;
            CameraManager.get().requestPreviewFrame(mDecodeThread.getHandler(), QRcodeMessage.decode);
            CameraManager.get().requestAutoFocus(this, QRcodeMessage.auto_focus);
        }
    }

}