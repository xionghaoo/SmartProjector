package com.ubtrobot.smartprojector;

import org.junit.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class JavaASMTest {
    static String dir = "D:\\Projects\\android\\SmartProjector\\app\\src\\test\\java\\com\\ubtrobot\\smartprojector";

//    @Test
//    public void test() {
//        System.out.println(dir);
//    }

    @Test
    public void test_inject() {
        try {
            /**
             * 1、准备待分析的class
             */
            FileInputStream fis = new  FileInputStream
                    (dir + "/InjectTest.class");
/**
 * 2、执行分析与插桩
 */
//class字节码的读取与分析引擎
            ClassReader cr = new  ClassReader(fis);
// 写出器 COMPUTE_FRAMES 自动计算所有的内容，后续操作更简单
            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
//分析，处理结果写入cw EXPAND_FRAMES：栈图以扩展格式进行访问
            cr.accept(new  ClassAdapterVisitor(cw), ClassReader.EXPAND_FRAMES);
/**
 * 3、获得结果并输出
 */
            byte[] newClassBytes = cw.toByteArray();
            File file = new  File(dir + "/");
            file.mkdirs();
            FileOutputStream fos = new  FileOutputStream
                    (dir + "/InjectTest.class");
            fos.write(newClassBytes);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
