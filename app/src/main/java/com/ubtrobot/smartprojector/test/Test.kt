package com.ubtrobot.smartprojector.test

class Test {
    /**
     * token : ifmMZevUZjm7TO23wa9AJjlQ%2BabRR8Njr%2Ba8qgzvIP9KgqJnyTtES376sVHngKO1
     * expireAt : 1623750402975
     * refreshToken : 6e6b350187044bbfaa05abb6c300c9ba
     * user : {"userId":838074,"userName":null,"userEmail":null,"userPhone":null,"userGender":null,"userImage":null,"countryCode":null,"nickName":null,"userBirthday":null,"countryName":null,"emailVerify":null,"pwdCreateType":null,"userExtraPhone":null,"userExtraEmail":null,"roleId":null,"roleName":null,"accountType":null,"isOversea":null,"schoolAccountUserInfo":null}
     */
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
        var userName: Any? = null
        var userEmail: Any? = null
        var userPhone: Any? = null
        var userGender: Any? = null
        var userImage: Any? = null
        var countryCode: Any? = null
        var nickName: Any? = null
        var userBirthday: Any? = null
        var countryName: Any? = null
        var emailVerify: Any? = null
        var pwdCreateType: Any? = null
        var userExtraPhone: Any? = null
        var userExtraEmail: Any? = null
        var roleId: Any? = null
        var roleName: Any? = null
        var accountType: Any? = null
        var isOversea: Any? = null
        var schoolAccountUserInfo: Any? = null
    }
}