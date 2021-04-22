package com.ubtrobot.smartprojector.test;

public class AsmTest {

    private int c = 12;

    public void test() {
        int a = 0;
        setVar(a);
    }

    public void setVar(int b) {
        c = b;
    }
}
