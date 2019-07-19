package com.ww;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.LoaderClassPath;
import javassist.NotFoundException;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

public class MyAgent {
    /**
     *
     * @param args
     * @param instrumentation
     */
    public static void premain(String args, Instrumentation instrumentation){
        System.out.println("hello java agent"+args);

        instrumentation.addTransformer(new ClassFileTransformer() {
            // any !!! class before load will  call  this methord  return byte code
            public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
                if(className==null ){
                    return null;
                }
                if(!className.replace("/",".").equals("com.ww.Test")   ){
                    return null;
                }

                //Bytecode factory
                ClassPool pool= new ClassPool();
                // orign class byte[]
                pool.insertClassPath(new LoaderClassPath(loader));
                try {
                    CtClass ctclasss = pool.get(className.replace("/", "."));
                    CtMethod method = ctclasss.getDeclaredMethods()[0];
                    method.insertBefore("System.out.println(System.nanoTime());");
                    method.insertAfter("System.out.println(System.nanoTime());");
                    return ctclasss.toBytecode();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        });
    }
}
