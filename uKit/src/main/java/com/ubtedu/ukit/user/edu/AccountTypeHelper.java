package com.ubtedu.ukit.user.edu;

import android.content.Context;
import android.text.TextUtils;
import android.text.method.KeyListener;
import android.widget.EditText;

import com.google.gson.Gson;
import com.ubtedu.alpha1x.utils.SharedPreferenceUtils;
import com.ubtedu.alpha1x.utils.StringUtil;
import com.ubtedu.ukit.R;
import com.ubtedu.ukit.common.view.KeyFactory;
import com.ubtedu.ukit.user.UserConsts;
import com.ubtedu.ukit.user.vo.LoginAccountInfo;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @Author qinicy
 * @Date 2019/11/15
 **/
public class AccountTypeHelper {
    private static final KeyListener PHONE_DIGITS = KeyFactory.createPhoneDigitsKeyListener();
    private static final KeyListener EMAIL_DIGITS = KeyFactory.createEmailDigitsKeyListener();
    private static final KeyListener EDU_ACCOUNT_DIGITS = KeyFactory.createEduAccountDigitsKeyListener();
    private static final String EMAIL_SYMBOL = "@";
    private String mLastPhoneNumber;
    private String mLastEmailAddress;
    private String mLastEduAccount;
    private static StringBuilder sAccountBuilder;

    public String getLastAccount(@LoginAccountInfo.LoginAccountType int type) {
        if (type == LoginAccountInfo.LOGIN_TYPE_PHONE) {
            return mLastPhoneNumber;
        } else if (type == LoginAccountInfo.LOGIN_TYPE_EMAIL) {
            return mLastEmailAddress;
        } else if (type == LoginAccountInfo.LOGIN_TYPE_EDU) {
            return mLastEduAccount;
        }
        return "";
    }

    public void recordAccount(@LoginAccountInfo.LoginAccountType int type, String account) {
        if (type == LoginAccountInfo.LOGIN_TYPE_PHONE) {
            mLastPhoneNumber = account;
        } else if (type == LoginAccountInfo.LOGIN_TYPE_EMAIL) {
            mLastEmailAddress = account;
        } else if (type == LoginAccountInfo.LOGIN_TYPE_EDU) {
            mLastEduAccount = account;
        }
    }

    public static int getAccountMaxLength(@LoginAccountInfo.LoginAccountType int type) {
        int l = UserConsts.ACCOUNT_PHONE_MAX_LENGTH;
        if (type == LoginAccountInfo.LOGIN_TYPE_PHONE) {
            l = UserConsts.ACCOUNT_PHONE_MAX_LENGTH;
        } else if (type == LoginAccountInfo.LOGIN_TYPE_EMAIL) {
            l = UserConsts.ACCOUNT_EMAIL_MAX_LENGTH;
        } else if (type == LoginAccountInfo.LOGIN_TYPE_EDU) {
            l = UserConsts.ACCOUNT_EDU_MAX_LENGTH;
        }
        return l;
    }

    public static boolean isAccountValid(@LoginAccountInfo.LoginAccountType int type, String account) {
        boolean accountValid;
        if (type == LoginAccountInfo.LOGIN_TYPE_PHONE) {
            accountValid = !TextUtils.isEmpty(account) && StringUtil.isNumeric(account);
        } else if (type == LoginAccountInfo.LOGIN_TYPE_EMAIL) {
            accountValid = !TextUtils.isEmpty(account) && StringUtil.isEmail(account);
        } else {
            accountValid = !TextUtils.isEmpty(account);
        }
        return accountValid;
    }

    public static int getAccountLoginHintStringId(@LoginAccountInfo.LoginAccountType int type) {
        if (type == LoginAccountInfo.LOGIN_TYPE_PHONE) {
            return R.string.account_login_hint_input_account_phone;
        } else if (type == LoginAccountInfo.LOGIN_TYPE_EMAIL) {
            return R.string.account_login_hint_input_account_email;
        } else if (type == LoginAccountInfo.LOGIN_TYPE_EDU) {
            return R.string.account_login_hint_input_account_edu;
        }
        return R.string.account_login_hint_input_account_phone;

    }

    public static LoginAccountInfo getLoginAccountHistory(Context context) {
        String gsonStr = SharedPreferenceUtils.getInstance(context).getStringValue(UserConsts.SP_LAST_LOGIN_ACCOUNT_INFO);
        LoginAccountInfo accountInfo = null;
        if (gsonStr != null) {
            accountInfo = new Gson().fromJson(gsonStr, LoginAccountInfo.class);
            if (accountInfo != null) {
                try {
                    JSONObject object = new JSONObject(gsonStr);
                    if (object.has("isPhone")) {
                        boolean isPhone = object.getBoolean("isPhone");
                        if (isPhone) {
                            accountInfo.setType(LoginAccountInfo.LOGIN_TYPE_PHONE);
                        } else {
                            accountInfo.setType(LoginAccountInfo.LOGIN_TYPE_EMAIL);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        if (accountInfo == null) {
            accountInfo = new LoginAccountInfo();
        }

        return accountInfo;
    }

    public static KeyListener getEditTextKeyListener(@LoginAccountInfo.LoginAccountType int type) {
        if (type == LoginAccountInfo.LOGIN_TYPE_PHONE) {
            return PHONE_DIGITS;
        } else if (type == LoginAccountInfo.LOGIN_TYPE_EMAIL) {
            return EMAIL_DIGITS;
        } else if (type == LoginAccountInfo.LOGIN_TYPE_EDU) {
            return EDU_ACCOUNT_DIGITS;
        }
        return PHONE_DIGITS;
    }

    public static int getAccountIconId(@LoginAccountInfo.LoginAccountType int type) {
        if (type == LoginAccountInfo.LOGIN_TYPE_PHONE) {
            return R.drawable.email_phone_icon;
        } else if (type == LoginAccountInfo.LOGIN_TYPE_EMAIL) {
            return R.drawable.phone_email_icon;
        } else if (type == LoginAccountInfo.LOGIN_TYPE_EDU) {
            return R.drawable.login_input_schoolaccount_icon;
        }
        return R.drawable.email_phone_icon;
    }

    /**
     * 处理@字符:
     * 1.@不能在开头或结尾位置
     * 2.不能包含一个以上@
     */
    public static void handleEmailSymbol(EditText editText, LoginAccountInfo accountInfo) {
        if (sAccountBuilder == null) {
            sAccountBuilder = new StringBuilder();
        }

        if (sAccountBuilder.length() > 0) {
            sAccountBuilder.delete(0, sAccountBuilder.length());
        }
        sAccountBuilder.append(accountInfo.account);

        int sourceLength = sAccountBuilder.length();
        if (sourceLength > 0) {
            //去掉开头的@
            if (sAccountBuilder.indexOf(EMAIL_SYMBOL) == 0) {
                sAccountBuilder.deleteCharAt(0);
            }
            //如果有两个@，去掉第二个
            int lastIndex = sAccountBuilder.lastIndexOf(EMAIL_SYMBOL);
            //检测是否有多个@，有的话需要去掉
            if (sAccountBuilder.indexOf(EMAIL_SYMBOL) != lastIndex) {
                sAccountBuilder.deleteCharAt(lastIndex);
            }
        }
        //如果长度不一致，说明有删除@的操作，更新输入框
        if (sourceLength != sAccountBuilder.length()) {
            String newAccount = sAccountBuilder.toString();
            editText.setText(newAccount);
            editText.setSelection(newAccount.length());
            accountInfo.account = newAccount;
        }
    }
}
