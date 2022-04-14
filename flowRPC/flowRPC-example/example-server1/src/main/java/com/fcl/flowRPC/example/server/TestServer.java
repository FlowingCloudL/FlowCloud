package com.fcl.flowRPC.example.server;


import com.fcl.flowRPC.config.RPCServerConfig;
import com.fcl.flowRPC.netty.RPCServer;

import java.util.HashMap;
import java.util.Map;

public class TestServer {

    public static void main(String[] args) throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("com.fcl.flowRPC.example.api.UserService", new UserServiceImpl());
        RPCServer.build().config(new RPCServerConfig()).serviceMap(map).start();
    }


}
