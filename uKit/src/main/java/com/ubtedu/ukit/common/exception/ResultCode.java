/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.common.exception;

/**
 * @Author qinicy
 * @Date 2018/11/6
 **/
public interface ResultCode {
    int OK = 0;

    int ACTION_SAVE_FAIL = 10;
    int OSS_LOGIN_TOKEN_EXPIRED = 101;
    int OSS_EMPTY_API_RESULT = 201;

    //===========文件上传============
    int UPLOAD_FAIL_CASE_INVALID_FILE = 501;

    //   int OSS_SERVICE_EXCEPTION = 501;
//   int OSS_USER_NOT_LOGIN = 502;
    int OSS_INVALID_ACTION_INFO = 503;
    int OSS_UPDATE_ACTION_FAIL = 504;
    int OSS_UPDATE_FAIL_CASE_INVALID_FILE = 505;
    int OSS_UPLOAD_FAIL_CASE_DUPLICATE = 506;
    //===========文件下载============
    int OSS_DOWN_WRITE_FILE_FAIL = 601;


    //===========文件下载============
    int SEND_FILE_FAIL = 701;


    //===========服务器API返回=======
    /*==============新增code==================*/ // 20180528
    // 错误参数
    int ERROR_PARAMS = 1000;

    // (2002,"用户或密码错误"),
    int ACCOUNT_PASSWD_ERROR = 2002;


    // (2003,"用户已经存在"),
    int USER_EXISTS = 2003;

    // (2004,"用户不存在"),
    int USER_NOT_FOUND = 2004;

    // (2005,"验证码错误"),
    int CAPTCHA_ERROR = 2005;

    // (2006,"账号错误"),
    int ACCOUNT_ERROR = 2006;

    // (2007,"操作超时"),
    int OPERATION_TIMEOUT = 2007;

    // (2008,"绑定关系已经存在"),
    int BIND_EXISTS = 2008;

    // (2009,"绑定关系不存在"),
    int BIND_NOT_FOUND = 2009;

    // (2010,"邮箱格式不正确"),
    int EMAIL_FORMAT_ERROR = 2010;

    // (2011,"手机号码格式不正确"),
    int PHONE_FORMAT_ERROR = 2011;

    // (2012,"新密码不能为空"),
    int NEW_PASSWD_ERROR = 2012;

    // (2013,"非法的app登录"),
    int ILLEGAL_LOGIN = 2013;

    // (2014,"密码错误"),
    int PASSWD_ERROR = 2014;

    // (2015,"发送内容不能为空"),
    int CONTENT_EMPTY = 2015;

    // (2016,"发送短信的项目不能为空"),
    int MSG_CONTENT_EMPTY = 2016;

    // (2017,"发送失败"),
    int SEND_FAIL = 2017;

    // (2018,"邮箱已经被注册"),
    int EMAIL_EXISTS = 2018;

    // (2019,"非法邮箱"),
    int ILLEGAL_EMAIL = 2019;

    // (2020,"邮箱不支持短信登录"),
    int EMAIL_NO_SUPPORT_MSG_LOGIN = 2020;

    // (2021,"同一帐号一分钟只能发送一次短信"),
    int ACCOUNT_MSG_OVER_COUNT = 2021;

    // (2022,"邮箱已验证"),
    int EMAIL_VERIFIED = 2022;

    // (2023,"邮箱已被其他帐号绑定"),
    int EMAIL_ALREADY_BIND = 2023;

    // (2024,"同一帐号一小时内最多只能发送3次短信"),
    int ACCOUNT_MSG_OVER_COUNT_PER_HOUR = 2024;

    // (2025,"同一帐号24小时内最多只能发送10次短信"),
    int ACCOUNT_MSG_OVER_COUNT_24 = 2025;

    // (2026,"验证码已过期"),
    int CAPTCHA_OVERDUE = 2026;

    // (2027,"一个邮箱一分钟内只能发送一次邮件"),
    int EMAIL_CAPTCHA_OVER_COUNT = 2027;

    // (2028,"手机已经被绑定"),
    int PHONE_BINDED = 2028;

    // (2029,"新密码与旧密码不能相同"),
    int PASSWORD_RESET_SAME = 2029;

    // (2030,"新密码与旧密码不能相同"),
    int PASSWORD_ORIGINAL_INCORRECT = 2030;



    //EDU账号登录密码错误
    int EDU_ACCOUNT_PASSWD_ERROR = 5001;
    //用户被冻结，请联系管理员
    int EDU_ACCOUNT_FROZEN = 5002;
    //EDU账号修改密码原密码错误
    int EDU_ACCOUNT_MODIFY_ORIGIN_PASSWD_ERROR = 5051;

    /**
     * 会话过期，异地登陆
     * Unauthorized error
     */
    int SERVER_LOGIN_TOKEN_EXPIRED = 401;
    /**
     * 拒绝访问
     */
    int SERVER_FORBIDDEN = 402;
    /**
     * Not Found
     */
    int SERVER_PAGE_NOT_FOUND = 404;
    /**
     * 自定义的错误码，服务器返回失败，请稍后重试
     */
    int SERVER_NO_RESULT = 100404;


}
