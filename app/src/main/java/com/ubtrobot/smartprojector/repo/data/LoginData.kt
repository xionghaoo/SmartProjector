package com.ubtrobot.smartprojector.repo.data

class LoginData {
    var token: String? = null
    var expireAt: Long = 0
    var refreshToken: String? = null

    /**
     * userId : 838074
     * userName : null
     * userEmail : null
     * userPhone : null
     * userGender : null
     * userImage : null
     * countryCode : null
     * nickName : null
     * userBirthday : null
     * countryName : null
     * emailVerify : null
     * pwdCreateType : null
     * userExtraPhone : null
     * userExtraEmail : null
     * roleId : null
     * roleName : null
     * accountType : null
     * isOversea : null
     * schoolAccountUserInfo : null
     */
    var user: User? = null

    class User {
        var userId = 0
        var userName: String? = null
        var userEmail: String? = null
        var userPhone: String? = null
//        var userGender: Any? = null
        var userImage: String? = null
//        var countryCode: Any? = null
//        var nickName: Any? = null
//        var userBirthday: Any? = null
//        var countryName: Any? = null
//        var emailVerify: Any? = null
//        var pwdCreateType: Any? = null
//        var userExtraPhone: Any? = null
//        var userExtraEmail: Any? = null
//        var roleId: Any? = null
//        var roleName: Any? = null
//        var accountType: Any? = null
//        var isOversea: Any? = null
//        var schoolAccountUserInfo: Any? = null
    }
}