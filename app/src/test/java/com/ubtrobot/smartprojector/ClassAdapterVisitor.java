package com.ubtrobot.smartprojector;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class ClassAdapterVisitor extends ClassVisitor {
    public  ClassAdapterVisitor(ClassVisitor cv) {
        super(Opcodes.ASM7, cv);
    }
    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature,
                                     String[] exceptions) {
        System.out.println("方法:" + name + " 签名:" + desc);
        MethodVisitor mv = super.visitMethod(access, name, desc, signature,
                exceptions);
        return  new MethodAdapterVisitor(api,mv, access, name, desc);
    }
}
