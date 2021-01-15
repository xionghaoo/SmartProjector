/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.common.flavor;

import androidx.annotation.Keep;

/**
 * @Author qinicy
 * @Date 2018/12/17
 **/
@Keep
public enum Channel {
    dev(0),
    /**
     * 官网
     */
    ubtedu(1),
    /**
     * 官网
     */
    googlePlay(2),
    /**
     * 腾讯应用宝
     */
    tencent(3),
    /**
     * 华为市场
     */
    huawei(4),
    /**
     * Chromebook
     */
    chromebook(5),
    /**
     * 联想
     */
    lenovo(6),
    lenovoPad(7),
    /**
     * 豌豆荚
     */
    wandoujia(8 ),
    /**
     * 360市场
     */
    qihoo(9 ),
    /**
     * North America
     */
    NA(10 ),

    other(99);

    private int channelId;




    Channel(int id) {
        this.channelId = id;
    }
    public int getId() {
        return this.channelId;
    }


    public static Channel fromName(String name) {
        if (dev.name().equals(name)) {
            return dev;
        }
        if (ubtedu.name().equals(name)) {
            return ubtedu;
        }
        if (googlePlay.name().equals(name)) {
            return googlePlay;
        }
        if (tencent.name().equals(name)) {
            return tencent;
        }
        if (huawei.name().equals(name)) {
            return huawei;
        }
        if (chromebook.name().equals(name)) {
            return chromebook;
        }
        if (lenovo.name().equals(name)) {
            return lenovo;
        }
        if (wandoujia.name().equals(name)) {
            return wandoujia;
        }
        if (qihoo.name().equals(name)) {
            return qihoo;
        }
        if (NA.name().equals(name)) {
            return NA;
        }
        return other;
    }
}
