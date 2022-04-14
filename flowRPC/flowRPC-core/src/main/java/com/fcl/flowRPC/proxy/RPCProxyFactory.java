package com.fcl.flowRPC.proxy;

import com.fcl.flowRPC.constant.MessageConst;
import com.fcl.flowRPC.netty.RPCClient;
import com.fcl.flowRPC.pojo.RPCRequest;
import com.fcl.flowRPC.pojo.RPCResponse;
import com.fcl.flowRPC.util.IdUtil;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class RPCProxyFactory {

    private final RPCClient rpcClient;

    public RPCProxyFactory(RPCClient rpcClient) {
        this.rpcClient = rpcClient;
    }

    public Object getRPCProxy(Class<?> rpcInterface, String serviceName) {
        RPCProxyInvocationHandler rpcInvocationHandler = new RPCProxyInvocationHandler(rpcClient, serviceName);
        return Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[]{rpcInterface}, rpcInvocationHandler);
    }

    static class RPCProxyInvocationHandler implements InvocationHandler {

        private final RPCClient rpcClient;
        private final String serviceName;

        public RPCProxyInvocationHandler(RPCClient rpcClient, String serviceName) {
            this.rpcClient = rpcClient;
            this.serviceName = serviceName;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

            // 构造RPC请求体
            RPCRequest request = new RPCRequest();
            request.setRequestId(IdUtil.getId());
            request.setClassName(method.getDeclaringClass().getName());
            request.setMethodName(method.getName());
            request.setParamTypes(method.getParameterTypes());
            request.setParams(args);

            // 通过RPCClient发送RPC请求，获得结果
            RPCResponse response = rpcClient.sendRPCRequest(serviceName, request);

            // 如果RPC调用出现异常
            if (response.getCode() == MessageConst.RESPONSE_CODE_ERROR) {
                throw new Exception(response.getMsg());
            }

            return response.getData();
        }

    }
}
