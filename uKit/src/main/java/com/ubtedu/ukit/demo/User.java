/**
 * ©2018, UBTECH Robotics, Inc. All rights reserved (C)
 **/
package com.ubtedu.ukit.demo;

/**
 *@Author qinicy
 *@Date 2018/9/4
 **/
public class User {
    public String name;
    public String firstName;
    public String lastName;
    public int age;
    public long id;

    public User() {
        id = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return "["+name+","+age+","+id+"]";
    }
}
