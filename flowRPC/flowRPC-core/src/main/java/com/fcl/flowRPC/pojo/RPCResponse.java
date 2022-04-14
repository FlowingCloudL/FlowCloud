package com.fcl.flowRPC.pojo;

public class RPCResponse {

    private String requestId;
    private byte code;
    private String msg;
    private Object data;

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public int getCode() {
        return code;
    }

    public void setCode(byte code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "RPCResponse{" +
                "requestId='" + requestId + '\'' +
                ", code=" + code +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }
}
