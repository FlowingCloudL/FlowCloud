package com.fcl.flowRPC.netty.codec;

import com.fcl.flowRPC.constant.MessageConst;
import com.fcl.flowRPC.pojo.RPCHeader;
import com.fcl.flowRPC.pojo.RPCMessage;
import com.fcl.flowRPC.pojo.RPCRequest;
import com.fcl.flowRPC.pojo.RPCResponse;
import com.fcl.flowRPC.serialize.ProtostuffUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;


public class FlowMessageDecoder extends LengthFieldBasedFrameDecoder {

    private static final int maxFrameLength = 1024 * 1024;
    private static final int lengthFieldOffset = 0;
    private static final int lengthFieldLength = 4;
    /**
    * 计算方式:
    * <p>
    * 情况1: length为整包长度
    * <p>
    * lengthAdjustment = -lengthFieldEndOffset
    * <p>
    * 情况2: length为消息体长度
    * <p>
    * lengthAdjustment = bodyFieldOffset - lengthFieldEndOffset
    */
    private static final int lengthAdjustment = -4;
    /**
    * 计算方式:
    * <p>
    * initialBytesToStrip = 从头计算要跳过的字节数（即不解析的首部字段的总长度）
    * <p>
    * 例如: 消息 = HDR1(3)+Length(7)+HDR2(5)+Body(24) -> HDR2(5)+Body(25)
    * <p>
    * 则 initialBytesToStrip = 3 + 7 = 10
    */
    private static final int initialBytesToStrip = 0;


    /**
    * <h2>原理:</h2>
    *
    * lengthFieldEndOffset = lengthFieldOffset + lengthFieldLength;
    * <p>
    * frameLength（整包长度） = lengthFieldOffset + length + lengthAdjustment;
    * <p>
    * // 减去不解析的首部长度
    * <p>
    * actualFrameLength（实际整包长度） = frameLengthInt - initialBytesToStrip;
    * <p>
    * // 使 readerIndex += initialBytesToStrip, 实现跳过
    * <p>
    * in.skipBytes(initialBytesToStrip);
    * <p>
    * // 截取出全新的缓冲区，注意：是从旧缓冲区独立出来的
    * <p>
    * frame = extractFrame(ctx, in, readerIndex, actualFrameLength);
    * <p>
    * // 修改旧的缓冲区读索引
    * <p>
    * in.readerIndex(readerIndex + actualFrameLength);
    * <p>
    * return frame;
    */

    public FlowMessageDecoder() {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {

        // 处理半包问题
        ByteBuf frame = (ByteBuf) super.decode(ctx, in);
        if (frame == null) return null; // 半包消息不解码

        RPCMessage message = new RPCMessage();
        RPCHeader header = new RPCHeader();

        // 解码消息头
        header.setLength(frame.readInt());
        header.setMsgType(frame.readByte());
        header.setSerializeType(frame.readByte());
        message.setHeader(header);

        // 解码消息体
        byte msgType = header.getMsgType();
        Class<?> clazz = null;
        if (msgType == MessageConst.TYPE_RPC_REQUEST) clazz = RPCRequest.class;
        else if (msgType == MessageConst.TYPE_RPC_RESPONSE) clazz = RPCResponse.class;

        if (clazz != null) message.setBody(ProtostuffUtil.decode(frame, clazz));

        return message;
    }
}
