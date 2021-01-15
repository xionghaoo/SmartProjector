/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.common.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ubtedu.base.gson.GExclusionStrategy;
import com.ubtedu.ukit.common.vo.SerializablePOJO;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * @Author qinicy
 * @Date 2018/11/30
 **/
public class SerializedUtil  {
    private Gson mGson;
    public static  <T extends Serializable> T deepClone(T obj){
        T t = null;
        if (obj != null){
            try {
                ByteArrayOutputStream bo = new ByteArrayOutputStream();
                ObjectOutputStream oo = new ObjectOutputStream(bo);
                oo.writeObject(obj);//从流里读出来
                ByteArrayInputStream bi = new ByteArrayInputStream(bo.toByteArray());
                ObjectInputStream oi = new ObjectInputStream(bi);

                t = (T) oi.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return t;
    }




    public String toJson(){
        return getGson().toJson(this);
    }
    private Gson getGson(){
        if (mGson == null){
            GExclusionStrategy strategy = new GExclusionStrategy(true);
            strategy.excludeListOrArray(false);
            strategy.excludeDeclaringClass(SerializablePOJO.class.getName());
            mGson = new GsonBuilder()
                    .addSerializationExclusionStrategy(strategy)
                    .create();
        }
        return mGson;
    }
}
