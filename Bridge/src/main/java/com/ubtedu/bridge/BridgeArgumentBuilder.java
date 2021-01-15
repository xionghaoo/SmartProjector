/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.bridge;

import java.util.ArrayList;
import java.util.List;

/**
 * 构建{@link IBridge#call(String, Object[], OnCallback)}第二个参数的构建器
 * @Author qinicy
 * @Date 2019/3/21
 **/
public class BridgeArgumentBuilder {
    private List<Object> mArguments;

    public BridgeArgumentBuilder() {
        mArguments = new ArrayList<>();
    }

    /**
     * 添加参数，需要保持和目标API形参顺序一致，可接受任何可序列化为json的类型
     * @param arg
     * @return
     */
    public BridgeArgumentBuilder add(Object arg){
        if (arg != null){
            mArguments.add(arg);
        }
        return this;
    }

    /**
     * 构建参数数组
     * @return 参数数组
     */
    public Object[] build(){
        return mArguments.toArray();
    }
}
