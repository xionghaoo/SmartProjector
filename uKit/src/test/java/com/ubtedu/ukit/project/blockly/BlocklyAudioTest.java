package com.ubtedu.ukit.project.blockly;


import com.ubtedu.alpha1x.utils.TimeUtil;

import org.junit.Test;

/**
 * @Author qinicy
 * @Date 2019/4/19
 **/
public class BlocklyAudioTest {
    @Test
    public void testTime(){
        String s1 = TimeUtil.milliseconds2String(100);
        String s2 = TimeUtil.milliseconds2String(500);
        String s3 = TimeUtil.milliseconds2String(800);
        String s4 = TimeUtil.milliseconds2String(1000);
        String s5 = TimeUtil.milliseconds2String(1400);
        String s6 = TimeUtil.milliseconds2String(1900);
        String s7 = TimeUtil.milliseconds2String(Integer.MAX_VALUE);
        System.out.println(s1);
        System.out.println(s2);
        System.out.println(s3);
        System.out.println(s4);
        System.out.println(s5);
        System.out.println(s6);
        System.out.println(s7);

    }
}