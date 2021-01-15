package com.ubtedu.bridge;

/**
 * @Author qinicy
 * @Date 2019/4/2
 **/
public class LoginUI {
    @inject
    private StringHelper mHelper;
    @inject
    private UserManger mUserManger;

    public LoginUI() {

    }

    public void login(String account, String password) {
        if (mHelper.isAccountVilid(account)) {
            mUserManger.login(account, password);
        }
    }


}
