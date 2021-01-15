package com.ubtedu.ukit.api;


import com.ubtedu.ukit.application.ServerConfig;

/**
 * Created by qinicy on 2017/6/19.
 */

public class Urls {

    /**
     * Base url:{@link ServerConfig#sAccountServer}
     */
    interface GDPRUrl {
        /**
         * 网络服务器地址
         */
        String WebBaseAddress = "/user-service-rest/v2/";
        /**
         * 根据产品ID获得产品的用户协议和隐私声明
         */
        String PactUrl = WebBaseAddress + "gdpr/pactUrl";
        /**
         * 根据产品ID获得协议的H5的URL地址
         */
        String PactInfo = WebBaseAddress + "gdpr/pactInfo";
        /**
         * 保存用户同意协议信息
         */
        String SaveUserPact = WebBaseAddress + "gdpr/saveUserPact";
        /**
         * 获取用户最新同意协议信息
         */
        String UserPactInfo = WebBaseAddress + "gdpr/userPactInfo";

    }

    /**
     * Base url:{@link ServerConfig#sAccountServer}
     */
    public interface UserUrl {
        /**
         * 网络服务器地址
         */
        String WebBaseAddress = "/user-service-rest/v2/";


        String UbtInterfaceUrl = WebBaseAddress + "user";

        /**
         * 删除用户（调试阶段用）Delete、修改用户信息Patch
         */
        String UserUrl = UbtInterfaceUrl;

        /**
         * 绑定账号, Patch
         */
        String PatchAccountBindUrl = UbtInterfaceUrl + "/account/bind";

        /**
         * 获取验证码, Get
         * account, 账户
         * accountType, 类型 0 手机， 1 邮箱
         * purpose， 作用 1：用户注册或者绑定手机/邮箱，2：找回密码 3：短信验证码登录
         * zhFlag，是否做国内手机校验（0：否；1：是；默认0）
         */
        String GetCaptchaUrl = UbtInterfaceUrl + "/captcha";

        /**
         * 获取验证码校验结果, Get
         */
        String GetCaptchaVerifyUrl = UbtInterfaceUrl + "/captcha/check";
        /**
         * 获取验证码校验结果, Get
         */
        String GetResetPasswordCaptchaVerifyUrl = UbtInterfaceUrl + "/captcha/verify-result";

        /**
         * 校验账户是否存在, Get
         */
        String GetUserCheckUrl = UbtInterfaceUrl + "/check";

        /**
         * 获取用户信息, Get
         */
        String GetUserInfoUrl = UbtInterfaceUrl + "/info";

        /**
         * 用户登录, Put
         */
        String PutUserLoginUrl = UbtInterfaceUrl + "/login";

        /**
         * 用户手机登录，Put
         */
        String PutUserLoginCellphoneUrl = UbtInterfaceUrl + "/login/cellphone";

        /**
         * 第三方登录校验, Put
         */
        String PutUserLoginThirdUrl = UbtInterfaceUrl + "/login/third";

        /**
         * 退出登录，Delete
         */
        String DeleteUserLogoutUrl = UbtInterfaceUrl + "/logout";

        /**
         * 修改密码，Patch
         */
        String PatchModifyUserPasswordUrl = UbtInterfaceUrl + "/password";

        /**
         * 重置密码，Patch
         */
        String PatchResetUserPasswordUrl = UbtInterfaceUrl + "/password/reset";

        /**
         * 验证密码，Get
         */
        String GetUserPasswordVerifyUrl = UbtInterfaceUrl + "/password/verification";

        /**
         * 用户注册，Post
         */
        String PostRegisterUrl = UbtInterfaceUrl + "/register";

        /**
         * 邮箱校验后注册，POST
         */
        String PostRegisterEmailVerifyUrl = UbtInterfaceUrl + "/register/emailverify";

        /**
         * 无需验证码的邮箱注册，Post
         */
        String PostRegisterEmailUrl = UbtInterfaceUrl + "/register/noverify";

        /**
         * 刷新token，PUT
         */
        String PutRefreshUserTokenUrl = UbtInterfaceUrl + "/token/refresh";

        /**
         * 通过账号名获取旧的服务器中的用户数据
         */
        String GetOldUserByAccount = "edu/user";

        /**
         * 意见反馈
         */
        String FeedBack = "/v1/course/product/feedback";
        /**
         * 登录的imei号
         */
        String UserIMEI = WebBaseAddress + "device/imei";
        /**
         * 校验更换设备IMEI
         */
        String CheckIMEI = WebBaseAddress + "device/imei/check";

    }

    /**
     * https://test79.ubtrobot.com/v1/ubt-ems-rest/swagger-ui.html#
     */
    public interface EduUserUrl{
        String BASE_URL = "v1/ubt-ems-rest";
        String COMMON_LOGIN_URL = BASE_URL+"/common-user/login";
        String EDU_LOGIN_URL = BASE_URL+"/app/login";
        String UPDATE_PASSWORD_URL = BASE_URL+"/app/updatePwd";
        String REFRESH_TOKEN_URL = BASE_URL+"/app/refresh";
        String LOGOUT_URL = BASE_URL+"/app/logout";
    }

    /**
     * Base url:{@link ServerConfig#sAccountServer}
     */
    public interface FileUploadApiUrl {

        /**
         * 文件上传地址
         */
        String PostFileUploadUrl = "/user-service-rest/v2/common/file/upload";

        /**
         * 获取七牛云上传的token
         */
        String GetQiNiuTokenUrl = "/v2/file-service-rest/files/up-token";

    }

    /**
     * Base url:{@link ServerConfig#sAPIServer}
     * 阿里云或者7牛云上传下载参数获取
     */
    interface FileControllerUrl {
        String UpSignature = "/v2/file-service-rest/file/up-signature";
    }


    /**
     * Base url:{@link ServerConfig#sAPIServer}
     */
    interface UserDataUrl {
        String userDatabaseUrl = "/v1/course/";
        String userProject = userDatabaseUrl + "user/project";
        String userMedia = userDatabaseUrl + "user/media";
        String userData = userDatabaseUrl + "user/data";
    }
}
