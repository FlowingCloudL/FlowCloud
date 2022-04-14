package com.fcl.flowRPC.netty;

import com.fcl.flowRPC.config.RPCClientConfig;
import com.fcl.flowRPC.constant.MessageConst;
import com.fcl.flowRPC.netty.handler.RPCClientHandler;
import com.fcl.flowRPC.netty.codec.FlowMessageDecoder;
import com.fcl.flowRPC.netty.codec.FlowMessageEncoder;
import com.fcl.flowRPC.pojo.*;
import com.fcl.flowRPC.proxy.RPCProxyFactory;
import com.fcl.flowRPC.util.LoadBalanceUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.SynchronousQueue;

public class RPCClient {

    // ====================================== 线程安全变量 ======================================
    private static final Logger logger = LoggerFactory.getLogger(RPCClient.class);
    private final Bootstrap bootstrap = new Bootstrap();
    private final EventLoopGroup reactorGroup = new NioEventLoopGroup();
    private RPCClientConfig config;
    private final RPCProxyFactory rpcProxyFactory = new RPCProxyFactory(this);

    // ====================================== 线程共享变量 ======================================
    private final Map<String, SynchronousQueue<RPCResponse>> rpcSyncQueueMap = new ConcurrentHashMap<>();
    private final Map<String, CopyOnWriteArrayList<Channel>> serviceChannelMap = new HashMap<>();
    private final Map<SocketAddress, RPCChannel> addressChannelMap = new ConcurrentHashMap<>();

    // ====================================== 实例方法 ======================================
    public static RPCClient build() {
        return new RPCClient();
    }

    public RPCClient config(RPCClientConfig config) {
        this.config = config;
        return this;
    }

    public RPCProxyFactory getRpcProxyFactory() {
        return rpcProxyFactory;
    }

    public void start() throws Exception {
        // 1.初始化配置 config
        initConfig();

        // 2.初始化启动类 bootstrap
        initBootstrap();

        // 3.启用注册中心
        initRegistry();
    }

    private void initConfig() throws Exception {
        if (config == null) throw new Exception("[RPC-Client]配置不能为空！");
    }

    private void initBootstrap() {
        RPCClientHandler rpcClientHandler = new RPCClientHandler(this);
        bootstrap.group(reactorGroup).channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, config.getConnectionTimeoutMillis())
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast("MessageDecoder", new FlowMessageDecoder());
                        pipeline.addLast("MessageEncoder", new FlowMessageEncoder());
                        pipeline.addLast(rpcClientHandler);
                    }
                });
    }

    private void initRegistry() {
        logger.info("使用测试注册中心");
        Map<String, SocketAddress> serviceAddressMap = new HashMap<>();
        serviceAddressMap.put("testService", new InetSocketAddress("127.0.0.1",10001));
        updateConnection(serviceAddressMap);
    }

    // 连接目标主机并返回对应Channel
    private Channel doConnect(SocketAddress address) throws InterruptedException {
        ChannelFuture future = bootstrap.connect(address).sync();
        logger.info("连接成功：{}", address);
        return future.channel();
    }

    // 关闭时释放资源
    public void destroy() throws InterruptedException {
        reactorGroup.shutdownGracefully().sync();
        logger.info("reactorGroup释放成功");
    }

    // 发送RPC消息
    public RPCResponse sendRPCRequest(String serviceName, RPCRequest request) throws InterruptedException {
        // 从连接管理器获取该服务对应的Channel
        Channel channel = getChannel(serviceName);
        if (channel != null && channel.isActive()) {
            // 构造RPC消息
            RPCMessage message = new RPCMessage();
            // 构造RPC请求头
            RPCHeader header = new RPCHeader();
            header.setMsgType(MessageConst.TYPE_RPC_REQUEST);
            header.setSerializeType(MessageConst.SERIALIZE_PROTOSTUFF);
            message.setHeader(header);
            // 设置request
            message.setBody(request);
            // 构造RPC同步队列，并放入rpcSyncQueueMap中等待响应结果
            SynchronousQueue<RPCResponse> rpcSyncQueue = new SynchronousQueue<>();
            rpcSyncQueueMap.put(request.getRequestId(), rpcSyncQueue);
            // 向Channel发送RPC请求
            channel.writeAndFlush(message);
            // 等待响应触发ChannelRead, 结果被存入rpcSyncQueue中, 这里的take()从阻塞中被唤醒，返回结果
            return rpcSyncQueue.take();
        } else { // 不存在对应channel或者channel不有效，返回错误
            RPCResponse response = new RPCResponse();
            response.setCode(MessageConst.RESPONSE_CODE_ERROR);
            response.setData("服务"+serviceName+"的连接不存在或出现故障！");
            return response;
        }
    }

    private Channel getChannel(String serviceName) {
        List<Channel> channelList = serviceChannelMap.get(serviceName);
        if (channelList == null || channelList.size() == 0) return null;
        return LoadBalanceUtil.roundRobin(channelList);
    }

    synchronized public void updateConnection(Map<String, SocketAddress> serviceAddressMap) {
        logger.info("更新服务节点信息：{}", serviceAddressMap);
        // 如果不存在可用的服务节点
        if (serviceAddressMap == null || serviceAddressMap.size() == 0) {
            logger.info("不存在可用的服务节点！");
            for (RPCChannel rpcChannel : addressChannelMap.values()) {
                rpcChannel.getChannel().close(); // 依次关闭所有通道
            }
            // 清空所有管理的连接
            serviceChannelMap.clear();
            addressChannelMap.clear();
            return;
        }

        // 剔除失效连接
        List<Channel> channelToDelete = new ArrayList<>();
        for (Map.Entry<SocketAddress, RPCChannel> entry : addressChannelMap.entrySet()) {
            if (serviceAddressMap.containsValue(entry.getKey())) {
                channelToDelete.add(entry.getValue().getChannel());
            }
        }
        for (Channel channel : channelToDelete) removeChannel(channel); // 删除连接

        // 建立新连接
        for (Map.Entry<String, SocketAddress> entry : serviceAddressMap.entrySet()) {
            // 如果连接已存在，则跳过
            if (addressChannelMap.containsKey(entry.getValue())) continue;
            // 如果连接不存在，则建立连接
            connectService(entry.getKey(), entry.getValue());
        }
    }

    private void connectService(String serviceName, SocketAddress address) {
        try {
            // 通过RPCClient连接目标服务主机
            Channel channel = doConnect(address);
            // 连接成功, 则将channel添加到连接管理器中
            addChannel(serviceName, channel);
        } catch (InterruptedException e) {
            e.printStackTrace();
            logger.info("连接失败:{}", address);
        }
    }

    private void addChannel(String serviceName, Channel channel) {
        SocketAddress address = channel.remoteAddress();
        logger.info("向连接管理器中添加连接：{}", address);
        // 添加到serviceChannelMap
        CopyOnWriteArrayList<Channel> channelList = serviceChannelMap.getOrDefault(serviceName, new CopyOnWriteArrayList<>());
        channelList.add(channel);
        serviceChannelMap.put(serviceName, channelList);
        // 添加到addressChannelMap
        RPCChannel rpcChannel = new RPCChannel();
        rpcChannel.setServiceName(serviceName);
        rpcChannel.setChannel(channel);
        addressChannelMap.put(address, rpcChannel);
    }

    public void removeChannel(Channel channel) {
        SocketAddress address = channel.remoteAddress();
        RPCChannel rpcChannel = addressChannelMap.get(address); // 获得要移除的rpcChannel
        serviceChannelMap.remove(rpcChannel.getServiceName()); // 从serviceChannelMap中移除
        addressChannelMap.remove(address); // 从addressChannelMap中移
        logger.info("从连接管理器中移除失效连接：{}", address);
    }

    public void setResponse(RPCResponse response) throws InterruptedException {
        // 获取requestId并从syncQueueMap中获取与之对应的syncQueue
        String requestId = response.getRequestId();
        SynchronousQueue<RPCResponse> rpcSyncQueue = rpcSyncQueueMap.get(requestId);
        // 如果存在对应的rpcSyncQueue, 表明RPC调用没有超时
        if (rpcSyncQueue != null) {
            rpcSyncQueue.put(response); // 将RPC响应放入RPC同步队列中（唤醒因take()而阻塞的调用线程的）
            rpcSyncQueueMap.remove(requestId); // 完成同步，则该将同步队列从syncQueueMap中移除
        }
    }

}
