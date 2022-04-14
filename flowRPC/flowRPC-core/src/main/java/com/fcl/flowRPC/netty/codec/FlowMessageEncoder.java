package com.fcl.flowRPC.netty.codec;

import com.fcl.flowRPC.constant.MessageConst;
import com.fcl.flowRPC.pojo.RPCHeader;
import com.fcl.flowRPC.pojo.RPCMessage;
import com.fcl.flowRPC.serialize.ProtostuffUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class FlowMessageEncoder extends MessageToByteEncoder<RPCMessage> {

    @Override
    protected void encode(ChannelHandlerContext ctx, RPCMessage msg, ByteBuf out) throws Exception {

        if (msg == null || msg.getHeader() == null) throw new Exception("待编码消息不能为空");

        ByteBuf sendBuf = Unpooled.buffer();

        // 编码消息头
        RPCHeader header = msg.getHeader();
        sendBuf.writeInt(0);
        sendBuf.writeByte(header.getMsgType());
        sendBuf.writeByte(header.getSerializeType());

        // 编码消息体
        if (header.getSerializeType() == MessageConst.SERIALIZE_PROTOSTUFF) {
            ProtostuffUtil.encode(msg.getBody(), sendBuf);
        } else { // 不存在对应序列化方式，则不进行序列化
            sendBuf.writeByte(0);
        }

        // 最后设置length
        sendBuf.setInt(0, sendBuf.readableBytes());
        header.setLength(sendBuf.readableBytes());

        out.writeBytes(sendBuf);
    }
}
