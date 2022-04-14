package com.fcl.flowRPC.netty.handler;

import com.fcl.flowRPC.constant.MessageConst;
import com.fcl.flowRPC.pojo.RPCHeader;
import com.fcl.flowRPC.pojo.RPCMessage;
import com.fcl.flowRPC.pojo.RPCRequest;
import com.fcl.flowRPC.pojo.RPCResponse;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Map;

@ChannelHandler.Sharable
public class RPCServerHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(RPCServerHandler.class);

    private final Map<String, Object> serviceMap; // 维护【api接口全限定名->接口实现类实例】的映射关系

    public RPCServerHandler(Map<String, Object> serviceMap) {
        this.serviceMap = serviceMap;
    }

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
            Object result = invoke(request);
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

    // 通过反射执行RPC请求
    public Object invoke(RPCRequest request) throws Exception {
        // 获取目标类
        Object serviceBean = serviceMap.get(request.getClassName());
        Class<?> clazz = serviceBean.getClass();
        // 获取目标方法
        Method method = clazz.getMethod(request.getMethodName(), request.getParamTypes());
        // 执行方法
        method.setAccessible(true);
        return method.invoke(serviceBean, request.getParams());
    }
}
