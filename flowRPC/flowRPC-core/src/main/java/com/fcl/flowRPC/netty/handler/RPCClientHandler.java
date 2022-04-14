package com.fcl.flowRPC.netty.handler;

import com.fcl.flowRPC.netty.RPCClient;
import com.fcl.flowRPC.constant.MessageConst;
import com.fcl.flowRPC.pojo.RPCMessage;
import com.fcl.flowRPC.pojo.RPCResponse;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ChannelHandler.Sharable
public class RPCClientHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(RPCClientHandler.class);

    private final RPCClient rpcClient;

    public RPCClientHandler(RPCClient rpcClient) {
        this.rpcClient = rpcClient;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        logger.info("连接有效:{}", ctx.channel().remoteAddress());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        Channel channel = ctx.channel();
        logger.info("连接断开:{}", channel.remoteAddress());
        channel.close();
        rpcClient.removeChannel(channel);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        logger.debug("收到消息：{}", msg);
        RPCMessage message = (RPCMessage) msg;
        // 如果是RPC响应消息
        if (message.getHeader().getMsgType() == MessageConst.TYPE_RPC_RESPONSE) {
            RPCResponse response = (RPCResponse) message.getBody();
            rpcClient.setResponse(response);
        }
    }
}
