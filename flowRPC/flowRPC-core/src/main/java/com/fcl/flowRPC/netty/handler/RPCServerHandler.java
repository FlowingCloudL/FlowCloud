package com.fcl.flowRPC.netty.handler;

import com.fcl.flowRPC.constant.MessageConst;
import com.fcl.flowRPC.proxy.RPCInvoker;
import com.fcl.flowRPC.pojo.RPCHeader;
import com.fcl.flowRPC.pojo.RPCMessage;
import com.fcl.flowRPC.pojo.RPCRequest;
import com.fcl.flowRPC.pojo.RPCResponse;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@ChannelHandler.Sharable
public class RPCServerHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(RPCServerHandler.class);

    @Autowired
    private RPCInvoker invoker;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        logger.info("收到消息："+msg);

        // 获取RPCRequest
        RPCRequest request = (RPCRequest) ((RPCMessage) msg).getBody();

        // 构造RPCResponse
        RPCResponse response = new RPCResponse();
        response.setRequestId(request.getRequestId());

        // 执行RPC调用
        try {
            Object result = invoker.handler(request);
            response.setCode(MessageConst.RESPONSE_CODE_SUCCESS);
            response.setMsg("调用成功");
            response.setData(result);
        } catch (Throwable e) {
            e.printStackTrace();
            response.setCode(MessageConst.RESPONSE_CODE_ERROR);
            response.setMsg(e.toString());
            response.setData(null);
        }

        // 构造响应消息
        RPCMessage message = new RPCMessage();
        RPCHeader header = new RPCHeader();
        header.setMsgType(MessageConst.TYPE_RPC_RESPONSE);
        header.setSerializeType(MessageConst.SERIALIZE_PROTOSTUFF);
        message.setHeader(header);
        message.setBody(response);
        ctx.writeAndFlush(message);
    }
}
