package com.fcl.flowRPC.pojo;

import lombok.Data;

@Data
public class RPCHeader {

    private int length;
    private byte msgType;
    private byte serializeType;

}
