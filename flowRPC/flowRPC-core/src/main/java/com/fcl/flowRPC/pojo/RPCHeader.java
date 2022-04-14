package com.fcl.flowRPC.pojo;

public class RPCHeader {

    private int length;
    private byte msgType;
    private byte serializeType;

    public int getLength() {
        return length;
    }

    public RPCHeader setLength(int length) {
        this.length = length;
        return this;
    }

    public byte getMsgType() {
        return msgType;
    }

    public RPCHeader setMsgType(byte msgType) {
        this.msgType = msgType;
        return this;
    }

    public byte getSerializeType() {
        return serializeType;
    }

    public RPCHeader setSerializeType(byte serializeType) {
        this.serializeType = serializeType;
        return this;
    }
}
