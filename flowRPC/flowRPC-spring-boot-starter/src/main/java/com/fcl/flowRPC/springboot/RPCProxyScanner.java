package com.fcl.flowRPC.springboot;

import com.fcl.flowRPC.proxy.RPCProxyFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;

import java.lang.annotation.Annotation;

public class RPCProxyScanner extends ClassPathBeanDefinitionScanner {

    private RPCProxyFactory rpcProxyFactory;

    private Class<? extends Annotation> annotationClass;

    public RPCProxyScanner(BeanDefinitionRegistry registry) {
        super(registry);
    }

    public void setAnnotationClass(Class<? extends Annotation> annotationClass) {
        this.annotationClass = annotationClass;
    }


}
