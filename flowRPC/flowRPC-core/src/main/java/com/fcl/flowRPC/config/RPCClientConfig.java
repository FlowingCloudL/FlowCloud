package com.fcl.flowRPC.config;

public class RPCClientConfig {

//    private Integer channelWorkerThreads = RpcConstant.DEFAULT_THREADS; //channel处理工作线程数，连接数量多时可调大

    private Integer callBackTaskThreads = 200; //回调任务处理线程池，0为不设置
    private Integer callBackTaskQueueSize = 500; //回调任务线程池队列大小

    private Integer connectionTimeoutMillis = 3000; //连接超时时间(ms)
    private Integer requestTimeoutMillis = 1000; //请求超时时间(ms)
    private Integer connectionSizePerNode = 3; //每个节点连接数
    private Integer connectionIdleTimeMillis = 18000; //超过连接空闲时间(ms)未收发数据则关闭连接
    private Integer heartBeatTimeIntervalMillis = 3000; //发送心跳包间隔时间(ms)

//    private CompressType compressType = CompressType.SNAPPY; //压缩算法类型，无需压缩为NONE
//    private SerializeType serializeType = SerializeType.PROTOSTUFF; //序列化类型，默认protostuff
//
//    private LoadBalanceRule loadBalanceRule = LoadBalanceRule.ROUND; //集群负载均衡策略
    private boolean excludeUnAvailableNodesEnable = true; //集群模式下是否排除不可用的节点
    private Integer nodeErrorTimes = 3; //节点连接或请求超时/异常超过设置次数则置为节点不可用
    private Integer nodeHealthCheckTimeIntervalMillis = 1000; //节点健康检查周期(ms),心跳包响应成功则恢复不可用的节点

    private Integer sendBuf = 65535; //tcp发送缓冲区
    private Integer receiveBuf = 65535; //tcp接收缓冲区
    private Integer lowWaterLevel = 1024 * 1024; //netty单个连接低水位
    private Integer highWaterLevel = 10 * 1024 * 1024; //netty单个连接高水位(避免内存溢出)

    private boolean trafficMonitorEnable = false; //是否开启流量控制
    private Long maxReadSpeed = 10 * 1000 * 1000L; //带宽限制，最大读取速度
    private Long maxWriteSpeed = 10 * 1000 * 1000L; //带宽限制，最大写出速度

    public Integer getCallBackTaskThreads() {
        return callBackTaskThreads;
    }

    public RPCClientConfig setCallBackTaskThreads(Integer callBackTaskThreads) {
        this.callBackTaskThreads = callBackTaskThreads;
        return this;
    }

    public Integer getCallBackTaskQueueSize() {
        return callBackTaskQueueSize;
    }

    public RPCClientConfig setCallBackTaskQueueSize(Integer callBackTaskQueueSize) {
        this.callBackTaskQueueSize = callBackTaskQueueSize;
        return this;
    }

    public Integer getConnectionTimeoutMillis() {
        return connectionTimeoutMillis;
    }

    public RPCClientConfig setConnectionTimeoutMillis(Integer connectionTimeoutMillis) {
        this.connectionTimeoutMillis = connectionTimeoutMillis;
        return this;
    }

    public Integer getRequestTimeoutMillis() {
        return requestTimeoutMillis;
    }

    public RPCClientConfig setRequestTimeoutMillis(Integer requestTimeoutMillis) {
        this.requestTimeoutMillis = requestTimeoutMillis;
        return this;
    }

    public Integer getConnectionSizePerNode() {
        return connectionSizePerNode;
    }

    public RPCClientConfig setConnectionSizePerNode(Integer connectionSizePerNode) {
        this.connectionSizePerNode = connectionSizePerNode;
        return this;
    }

    public Integer getConnectionIdleTimeMillis() {
        return connectionIdleTimeMillis;
    }

    public RPCClientConfig setConnectionIdleTimeMillis(Integer connectionIdleTimeMillis) {
        this.connectionIdleTimeMillis = connectionIdleTimeMillis;
        return this;
    }

    public Integer getHeartBeatTimeIntervalMillis() {
        return heartBeatTimeIntervalMillis;
    }

    public RPCClientConfig setHeartBeatTimeIntervalMillis(Integer heartBeatTimeIntervalMillis) {
        this.heartBeatTimeIntervalMillis = heartBeatTimeIntervalMillis;
        return this;
    }

    public boolean isExcludeUnAvailableNodesEnable() {
        return excludeUnAvailableNodesEnable;
    }

    public RPCClientConfig setExcludeUnAvailableNodesEnable(boolean excludeUnAvailableNodesEnable) {
        this.excludeUnAvailableNodesEnable = excludeUnAvailableNodesEnable;
        return this;
    }

    public Integer getNodeErrorTimes() {
        return nodeErrorTimes;
    }

    public RPCClientConfig setNodeErrorTimes(Integer nodeErrorTimes) {
        this.nodeErrorTimes = nodeErrorTimes;
        return this;
    }

    public Integer getNodeHealthCheckTimeIntervalMillis() {
        return nodeHealthCheckTimeIntervalMillis;
    }

    public RPCClientConfig setNodeHealthCheckTimeIntervalMillis(Integer nodeHealthCheckTimeIntervalMillis) {
        this.nodeHealthCheckTimeIntervalMillis = nodeHealthCheckTimeIntervalMillis;
        return this;
    }

    public Integer getSendBuf() {
        return sendBuf;
    }

    public RPCClientConfig setSendBuf(Integer sendBuf) {
        this.sendBuf = sendBuf;
        return this;
    }

    public Integer getReceiveBuf() {
        return receiveBuf;
    }

    public RPCClientConfig setReceiveBuf(Integer receiveBuf) {
        this.receiveBuf = receiveBuf;
        return this;
    }

    public Integer getLowWaterLevel() {
        return lowWaterLevel;
    }

    public RPCClientConfig setLowWaterLevel(Integer lowWaterLevel) {
        this.lowWaterLevel = lowWaterLevel;
        return this;
    }

    public Integer getHighWaterLevel() {
        return highWaterLevel;
    }

    public RPCClientConfig setHighWaterLevel(Integer highWaterLevel) {
        this.highWaterLevel = highWaterLevel;
        return this;
    }

    public boolean isTrafficMonitorEnable() {
        return trafficMonitorEnable;
    }

    public RPCClientConfig setTrafficMonitorEnable(boolean trafficMonitorEnable) {
        this.trafficMonitorEnable = trafficMonitorEnable;
        return this;
    }

    public Long getMaxReadSpeed() {
        return maxReadSpeed;
    }

    public RPCClientConfig setMaxReadSpeed(Long maxReadSpeed) {
        this.maxReadSpeed = maxReadSpeed;
        return this;
    }

    public Long getMaxWriteSpeed() {
        return maxWriteSpeed;
    }

    public RPCClientConfig setMaxWriteSpeed(Long maxWriteSpeed) {
        this.maxWriteSpeed = maxWriteSpeed;
        return this;
    }
}
