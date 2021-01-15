package com.ubtedu.ukit.project.blockly;

import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @Author qinicy
 * @Date 2019/2/21
 **/
public class BlocklyPresenterTest {
    @Test
    public void tests() {
        System.out.println(versionName2Code("v1.0.0.5"));
        System.out.println(versionName2Code(""));
        System.out.println(versionName2Code("v0.0.01"));
        System.out.println(versionName2Code(null));
    }

    private int versionName2Code(String version) {
        if (version != null) {
            String regEx = "[^0-9]";
            Pattern p = Pattern.compile(regEx);
            Matcher m = p.matcher(version);
            String numStr = m.replaceAll("").trim();
            try {
                return Integer.parseInt(numStr);
            } catch (NumberFormatException e) {
                System.out.println("");
            }
        }
        return 0;
    }
}