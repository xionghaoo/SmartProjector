/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.common.view;

/**
 * @Author qinicy
 * @Date 2019/1/25
 **/
public class UKitCharsInputFilter extends BlackCharsInputFilter {

    public UKitCharsInputFilter(int max) {
        super(max);
        initRequirements();
    }

    protected void initRequirements() {
        addFilterChar("<");
        addFilterChar(">");
    }
}
