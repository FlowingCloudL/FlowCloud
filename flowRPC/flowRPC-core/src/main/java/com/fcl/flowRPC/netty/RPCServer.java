package com.fcl.flowRPC.netty;

import com.fcl.flowRPC.config.RPCServerConfig;
import com.fcl.flowRPC.netty.codec.FlowMessageDecoder;
import com.fcl.flowRPC.netty.codec.FlowMessageEncoder;
import com.fcl.flowRPC.netty.handler.RPCServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Map;

public class RPCServer {

    private static final Logger logger = LoggerFactory.getLogger(RPCServer.class);

    private RPCServerConfig config;
    private EventLoopGroup acceptorGroup;
    private EventLoopGroup reactorGroup;
    private RPCServerHandler rpcServerHandler;
    private ServerBootstrap bootstrap;
    private Map<String, Object> serviceMap;; // 维护【api接口全限定名->接口实现类实例】的映射关系


    public static RPCServer build() {
        return new RPCServer();
    }

    public RPCServer config(RPCServerConfig config) {
        this.config = config;
        return this;
    }

    public RPCServer serviceMap(Map<String, Object> serviceMap) {
        this.serviceMap = serviceMap;
        return this;
    }

    public void start() throws Exception {
        // 1.初始化配置 config
        initConfig();

        // 2.初始化 rpcServerHandler
        initRPCServerHandler();

        // 3.初始化启动类 bootstrap
        initBootstrap();

        // 4.启动Server
        doRun();
    }

    private void initConfig() throws Exception {
        if (config == null) throw new Exception("[RPC-Server]配置不能为空！");
    }

    private void initBootstrap() {
        acceptorGroup = new NioEventLoopGroup();
        reactorGroup = new NioEventLoopGroup();
        bootstrap = new ServerBootstrap();
        bootstrap.group(acceptorGroup, reactorGroup).channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 100)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast("MessageDecoder", new FlowMessageDecoder());
                        pipeline.addLast("MessageEncoder", new FlowMessageEncoder());
                        pipeline.addLast("service", rpcServerHandler);
                    }
                });
    }

    private void initRPCServerHandler() throws Exception {
        if (serviceMap == null || serviceMap.size() == 0) throw new Exception("[RPC-Server]必须提供至少一个service");
        rpcServerHandler = new RPCServerHandler(serviceMap);
    }

    private void doRun() {
        Runnable nettyBootstrap = new Runnable() {
            @Override
            public void run() {
                try {
                    ChannelFuture future = bootstrap.bind(new InetSocketAddress(config.getPort())).sync();
                    logger.info("FlowRPC-Server 在端口【"+config.getPort()+"】启动成功！");
                    future.channel().closeFuture().sync(); // 等待关闭
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    shutdown();
                }
            }
        };
        new Thread(nettyBootstrap, "FlowRPC-Server").start();
    }

    public void shutdown() {
        acceptorGroup.shutdownGracefully();
        reactorGroup.shutdownGracefully();
    }


    //    @Override
//    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
//        // 从IOC容器获取拥有@RPCService的beans
//        Map<String, Object> beans = applicationContext.getBeansWithAnnotation(ServiceConsumer.class);
//        // 构造serviceMap: key = beanClassName, value = bean
//        for (Object bean : beans.values()) {
//            for (Class<?> clazz : bean.getClass().getInterfaces()) {
//                serviceMap.put(clazz.getName(), bean);
//            }
//        }
//    }
}
