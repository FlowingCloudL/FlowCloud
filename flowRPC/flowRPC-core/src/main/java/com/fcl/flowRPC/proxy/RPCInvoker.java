package com.fcl.flowRPC.proxy;

import com.fcl.flowRPC.annotation.ServiceConsumer;
import com.fcl.flowRPC.pojo.RPCRequest;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@Component
public class RPCInvoker implements ApplicationContextAware {

    private final Map<String, Object> serviceMap = new HashMap<>();

    public Object handler(RPCRequest request) throws Exception {
        // 获取目标类
        Object serviceBean = serviceMap.get(request.getClassName());
        Class<?> clazz = serviceBean.getClass();
        // 获取目标方法
        Method method = clazz.getMethod(request.getMethodName(), request.getParamTypes());
        // 执行方法
        method.setAccessible(true);
        return method.invoke(serviceBean, request.getParams());
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        // 从IOC容器获取拥有@RPCService的beans
        Map<String, Object> beans = applicationContext.getBeansWithAnnotation(ServiceConsumer.class);
        // 构造serviceMap: key = beanClassName, value = bean
        for (Object bean : beans.values()) {
            for (Class<?> clazz : bean.getClass().getInterfaces()) {
                serviceMap.put(clazz.getName(), bean);
            }
        }
    }
}
