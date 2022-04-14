package com.fcl.flowRPC.constant;

public class MessageConst {

    public static final byte TYPE_RPC_REQUEST = 0;
    public static final byte TYPE_RPC_RESPONSE = 1;
    public static final byte TYPE_AUTH_REQUEST = 4;
    public static final byte TYPE_AUTH_RESPONSE = 5;
    public static final byte TYPE_HEARTBEAT_REQUEST = 6;
    public static final byte TYPE_HEARTBEAT_RESPONSE = 7;

    public static final byte SERIALIZE_PROTOSTUFF = 1;

    public static final byte RESPONSE_CODE_ERROR = 0;
    public static final byte RESPONSE_CODE_SUCCESS = 1;

}
