package com.ubtedu.base.net.socket.tcp.client.helper.stickpackage;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by qinicy on 2017/7/27.
 */

public class FileStickPackageHelper implements AbsStickPackageHelper {
    @Override
    public byte[] execute(InputStream inStream) {
        ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
        byte[] buff = new byte[100]; //buff用于存放循环读取的临时数据
        int rc = 0;
        try {
            while ((rc = inStream.read(buff, 0, 100)) > 0) {
                swapStream.write(buff, 0, rc);
            }
        } catch (IOException e) {
            Log.d("","content:IOException");
            e.printStackTrace();
            return null;
        }
        byte[] in_b = swapStream.toByteArray();
        Log.d("","content:"+in_b.length);
        if (in_b == null){
            return null;
        }
        if (in_b.length == 0){
            return null;
        }
        return in_b;
    }
}
