package com.ubtedu.bridge;

import android.util.Log;

import androidx.test.runner.AndroidJUnit4;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.mockito.Mockito.*;

/**
 * @Author qinicy
 * @Date 2019/3/29
 **/
@RunWith(AndroidJUnit4.class)
public class MockTest {

    @Mock
    private BridgeDispatcher mDispatcher = mock(BridgeDispatcher.class);

    @Test
    public void test() {
        Assert.assertNotNull(mDispatcher);
        when(mDispatcher.call("xxx")).thenReturn("mock");
        String msg = mDispatcher.call("xxx");
        Log.d(BridgeTest.TAG,"mock msg:"+msg);
    }
}
