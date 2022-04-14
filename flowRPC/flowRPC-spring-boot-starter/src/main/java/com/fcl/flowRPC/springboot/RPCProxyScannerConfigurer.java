package com.fcl.flowRPC.springboot;

import com.fcl.flowRPC.annotation.ServiceProvider;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.core.env.Environment;

public class RPCProxyScannerConfigurer implements BeanDefinitionRegistryPostProcessor {

    private String basePackage;

    @Autowired
    public void setBasePackage(Environment env) {
        this.basePackage = env.getProperty("flowRPC.basePackage");
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry) throws BeansException {
        RPCProxyScanner scanner = new RPCProxyScanner(beanDefinitionRegistry);
        scanner.setAnnotationClass(ServiceProvider.class);
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {

    }
}
