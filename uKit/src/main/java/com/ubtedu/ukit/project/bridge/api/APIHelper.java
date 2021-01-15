package com.ubtedu.ukit.project.bridge.api;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;

import com.ubtedu.alpha1x.utils.GsonUtil;
import com.ubtedu.bridge.BridgeBoolean;
import com.ubtedu.bridge.BridgeObject;
import com.ubtedu.bridge.BridgeResult;
import com.ubtedu.bridge.OnCallback;
import com.ubtedu.ukit.R;
import com.ubtedu.ukit.application.UKitApplication;
import com.ubtedu.ukit.common.base.UKitBaseActivity;
import com.ubtedu.ukit.common.dialog.PromptDialogFragment;
import com.ubtedu.ukit.common.dialog.PromptEditDialogFragment;
import com.ubtedu.ukit.project.bridge.BridgeCommunicator;
import com.ubtedu.ukit.project.bridge.arguments.LoadingArguments;
import com.ubtedu.ukit.project.bridge.arguments.PromptArguments;


/**
 * 处理API中的loading，toast，dialog
 *
 * @Author qinicy
 * @Date 2019/6/3
 **/
public class APIHelper {
    private Handler mHandler;

    public APIHelper() {
        mHandler = new android.os.Handler(Looper.getMainLooper());
    }

    public BridgeResult toast(final String message) {
        if (!TextUtils.isEmpty(message)) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (getActivity() != null) {
                        getActivity().getUIDelegate().toastShort(message);
                    }
                }
            });
        }
        return BridgeResult.SUCCESS();
    }

    public void prompt(BridgeObject jsonObject, final OnCallback callback) {
        final BridgeResult result = BridgeResult.SUCCESS();
        if (jsonObject != null && getActivity() != null) {
            String json = jsonObject.toString();
            final PromptArguments arguments = GsonUtil.get().toObject(json, PromptArguments.class);
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    int type = arguments.type == null ? PromptDialogFragment.Type.NORMAL :
                            arguments.type.intValue();
                    boolean isCancelable = arguments.isCancelable == null ||
                            arguments.isCancelable.intValue() == BridgeBoolean.TRUE();
                    boolean showPositiveButton = arguments.isShowPositiveButton == null ||
                            arguments.isShowPositiveButton.intValue() == BridgeBoolean.TRUE();
                    boolean showNegativeButton = arguments.isShowNegativeButton == null ||
                            arguments.isShowNegativeButton.intValue() == BridgeBoolean.TRUE();
                    PromptDialogFragment.newBuilder(UKitApplication.getInstance())
                            .type(type)
                            .title(arguments.title)
                            .message(arguments.message)
                            .cancelable(isCancelable)
                            .positiveButtonText(arguments.positiveText)
                            .negativeButtonText(arguments.negativeText)
                            .showPositiveButton(showPositiveButton)
                            .showNegativeButton(showNegativeButton)
                            .onPositiveClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    result.data = BridgeBoolean.TRUE();
                                    callback.onCallback(result);
                                }
                            })
                            .onNegativeClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    result.data = BridgeBoolean.FALSE();
                                    callback.onCallback(result);
                                }
                            })
                            .build()
                            .show(getActivity().getSupportFragmentManager(), "prompt_blockly");
                }
            });
        } else {
            callback.onCallback(BridgeResult.ILLEGAL_ARGUMENTS());
        }
    }

    public void promptEdit(BridgeObject jsonObject, final OnCallback callback) {
        final BridgeResult result = BridgeResult.SUCCESS();
        String title = jsonObject.optString("title");
        String message = jsonObject.optString("message");
        String messageHint = jsonObject.optString("messageHint");

        int maxLength = jsonObject.optInt("maxLength");

        final PromptEditDialogFragment.Builder builder = PromptEditDialogFragment.newBuilder();
        if (!TextUtils.isEmpty(title)) {
            builder.title(title);
        }
        if (!TextUtils.isEmpty(message)) {
            builder.message(message);
        }
        if (!TextUtils.isEmpty(messageHint)) {
            builder.hint(messageHint);
        }
        if (maxLength > 0) {
            builder.maxLength(maxLength);
        }
        if (jsonObject.has("isCancelable")) {
            int isCancelableValue = jsonObject.optInt("isCancelable");
            builder.cancelable(BridgeBoolean.isTrue(isCancelableValue));
        } else {
            builder.cancelable(true);
        }

        builder.onConfirmClickListener(new PromptEditDialogFragment.OnConfirmClickListener() {
            @Override
            public boolean confirm(String text) {
                result.data = text;
                callback.onCallback(result);
                return false;
            }
        });
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                builder.build().show(getActivity().getSupportFragmentManager(), "promptEdit");
            }
        });

    }

    public void interactivePromptEdit(final Number isBlockly, final BridgeObject jsonObject, final OnCallback callback) {
        String title = jsonObject.optString("title");
        String message = jsonObject.optString("message");
        String messageHint = jsonObject.optString("messageHint");
        boolean allowMessageUnchanged = BridgeBoolean.isTrue(jsonObject.optInt("allowMessageUnchanged"));
        int maxLength = jsonObject.optInt("maxLength");

        final PromptEditDialogFragment.Builder builder = PromptEditDialogFragment.newBuilder();
        if (!TextUtils.isEmpty(title)) {
            builder.title(title);
        }
        if (!TextUtils.isEmpty(message)) {
            builder.message(message);
        }
        if (!TextUtils.isEmpty(messageHint)) {
            builder.hint(messageHint);
        }
        if (maxLength > 0) {
            builder.maxLength(maxLength);
        }
        if (jsonObject.has("isCancelable")) {
            int isCancelableValue = jsonObject.optInt("isCancelable");
            builder.cancelable(BridgeBoolean.isTrue(isCancelableValue));
        } else {
            builder.cancelable(true);
        }
        builder.allowMessageUnchanged(allowMessageUnchanged);
        final InteractivePromptConfirmListener confirmListener = new InteractivePromptConfirmListener(BridgeBoolean.isTrue(isBlockly), jsonObject, callback);
        builder.onConfirmClickListener(confirmListener);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                PromptEditDialogFragment fragment = builder.build();
                confirmListener.setDialogFragment(fragment);
                fragment.show(getActivity().getSupportFragmentManager(), "interactivePromptEdit");
            }
        });

    }

    private static class InteractivePromptConfirmListener implements PromptEditDialogFragment.OnConfirmClickListener {
        private PromptEditDialogFragment mDialogFragment;
        private boolean isInteractive;
        private boolean isBlockly;
        private boolean isController;
        private OnCallback mCallback;
        private String mCheckTextFunc;

        public InteractivePromptConfirmListener(boolean isBlockly, BridgeObject args, OnCallback callback) {
            this.isBlockly = isBlockly;
            mCallback = callback;
            if (args != null) {
                mCheckTextFunc = args.optString("checkTextFunc");
                this.isInteractive = mCheckTextFunc != null;
                //Blockly特有
                isController = BridgeBoolean.isTrue(args.optInt("isController"));
            }
        }

        public void setDialogFragment(PromptEditDialogFragment dialogFragment) {
            mDialogFragment = dialogFragment;
        }

        @Override
        public boolean confirm(final String text) {

            OnCallback checkCB = new OnCallback() {
                @Override
                public void onCallback(BridgeResult ret) {
                    boolean valid = true;
                    if (ret.data instanceof Number) {
                        valid = BridgeBoolean.isTrue((Number) ret.data);
                    }
                    if (valid) {
                        BridgeResult result = BridgeResult.SUCCESS();
                        result.data = text;
                        mCallback.onCallback(result);
                        if (mDialogFragment != null) {
                            mDialogFragment.dismiss();
                        }
                    }
                }
            };
            if (isInteractive) {
                Object[] args = new Object[]{text};
                if (isBlockly) {
                    BridgeCommunicator.getInstance().getBlocklyBridge(isController).call(mCheckTextFunc, args, checkCB);
                } else {
                    BridgeCommunicator.getInstance().getUnity3DBridge().call(mCheckTextFunc, args, checkCB);
                }
            } else {
                BridgeResult result = BridgeResult.SUCCESS();
                result.data = BridgeBoolean.TRUE();
                checkCB.onCallback(result);
            }
            return true;
        }
    }

    public BridgeResult showLoading(BridgeObject args) {
        final BridgeResult result = BridgeResult.SUCCESS();
        String json = args.toString();
        final LoadingArguments arguments = GsonUtil.get().toObject(json, LoadingArguments.class);

        if (arguments != null) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    boolean isCancelable = arguments.isCancelable == null ||
                            arguments.isCancelable.intValue() == BridgeBoolean.TRUE();
                    int anim = R.raw.refresh;
                    if (LoadingArguments.ANIM_REFRESH.equals(arguments.anim)) {
//                        anim = R.raw.refresh;
                    }

                    ActivityHelper.showLoading(isCancelable, arguments.message, anim);
                }
            });
            return result;
        } else {
            return BridgeResult.ILLEGAL_ARGUMENTS();
        }
    }

    public BridgeResult hideLoading() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                ActivityHelper.hideLoading();
            }
        });
        return BridgeResult.SUCCESS();
    }

    private UKitBaseActivity getActivity() {
        return ActivityHelper.getResumeActivity();
    }
}
