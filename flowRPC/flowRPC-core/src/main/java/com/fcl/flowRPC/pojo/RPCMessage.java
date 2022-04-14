package com.fcl.flowRPC.pojo;

public class RPCMessage {

    private RPCHeader header;
    private Object body;

    public RPCHeader getHeader() {
        return header;
    }

    public void setHeader(RPCHeader header) {
        this.header = header;
    }

    public Object getBody() {
        return body;
    }

    public void setBody(Object body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "FlowMessage{" +
                "header=" + header +
                ", body=" + body +
                '}';
    }
}
