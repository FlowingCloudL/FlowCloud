package com.fcl.flowRPC.netty;

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
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;

@Component
public class RPCServer implements InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(RPCServer.class);

    private static final String LOCAL_IP = "127.0.0.1";

    @Autowired
    private RPCServerHandler rpcServerHandler;

    @Autowired
    private Environment env;

    public void start() {
        int LOCAL_PORT = Integer.parseInt(env.getProperty("server.port"));
        Runnable nettyBootstrap = new Runnable() {
            @Override
            public void run() {
                EventLoopGroup acceptorGroup = new NioEventLoopGroup();
                EventLoopGroup reactorGroup = new NioEventLoopGroup();
                try {
                    ServerBootstrap bootstrap = new ServerBootstrap();
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
                    ChannelFuture future = bootstrap.bind(new InetSocketAddress(LOCAL_IP, LOCAL_PORT)).sync();
                    logger.info("FlowRPC-Server 在 "+LOCAL_IP+":"+LOCAL_PORT+" 启动成功！");
                    future.channel().closeFuture().sync(); // 等待关闭
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    acceptorGroup.shutdownGracefully();
                    reactorGroup.shutdownGracefully();
                }
            }
        };
        new Thread(nettyBootstrap, "FlowRPC-Server").start();
    }

    @Override
    public void afterPropertiesSet() {
        if (env.getProperty("flowRPC.server.enable").equals("true"))
        start();
    }
}
