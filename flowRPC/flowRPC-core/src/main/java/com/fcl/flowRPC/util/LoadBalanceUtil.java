package com.fcl.flowRPC.util;

import io.netty.channel.Channel;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class LoadBalanceUtil {

    public static final AtomicInteger atm = new AtomicInteger(0);

    public static Channel roundRobin(List<Channel> channelList) {
        int size = channelList.size();
        int index = (atm.addAndGet(1) + size) % size;
        return channelList.get(index);
    }
}
