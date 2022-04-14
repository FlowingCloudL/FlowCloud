package com.fcl.flowRPC.example.client;

import com.fcl.flowRPC.example.api.UserService;
import com.fcl.flowRPC.netty.RPCClient;
import com.fcl.flowRPC.config.RPCClientConfig;
import com.fcl.flowRPC.proxy.RPCProxyFactory;


public class TestClient {

    public static void main(String[] args) throws Exception {

        RPCClient client = RPCClient.build().config(new RPCClientConfig());
        client.start();

        //生成代理
        RPCProxyFactory factory = client.getRpcProxyFactory();
        UserService service = (UserService) factory.getRPCProxy(UserService.class, "testService");

        //像调用本地接口一样调用远程方法
        System.out.println(service.queryList());
        System.out.println(service.queryById(1));
    }
}
