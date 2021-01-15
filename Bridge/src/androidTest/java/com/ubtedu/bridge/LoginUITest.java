package com.ubtedu.bridge;

import org.junit.Test;
import org.mockito.Mockito;

/**
 * @Author qinicy
 * @Date 2019/4/2
 **/
public class LoginUITest {
    @Test
    public void testLogin(){

        LoginUI loginUI = new LoginUI();
        UserManger userManger = new UserManger();
        StringHelper helper = Mockito.mock(StringHelper.class);

        loginUI.login("qinicy","xxxx");



    }
}
