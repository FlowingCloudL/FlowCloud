package com.fcl.flowRPC.pojo;

import io.netty.channel.Channel;

public class RPCChannel {

    private String serviceName;
    private Channel channel;

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    @Override
    public String toString() {
        return "RPCChannel{" +
                "serviceName='" + serviceName + '\'' +
                ", channel=" + channel +
                '}';
    }
}
