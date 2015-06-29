package com.tycs.jsonrpc4go.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.Resource;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.util.HashedWheelTimer;
import org.jboss.netty.util.Timeout;
import org.jboss.netty.util.Timer;
import org.jboss.netty.util.TimerTask;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.tycs.jsonrpc4go.RpcClientCfg;
import com.tycs.jsonrpc4go.RpcClientConn;
import com.tycs.jsonrpc4go.model.JsonRpcObject;

@Component
public class RpcClientService {

    @Resource
    private RpcClientCfg      rpcCliCfg;

    @Resource
    private RpcClientConn     rpcCliConn;

    private Channel           channel;

    private static AtomicLong counter = new AtomicLong(0);

    // start & stop rpc client connection
    public void startRpcCli() {
        rpcCliConn.start(this);
    }

    public void stopRpcCli() {
        // TODO
    }

    // send to server via json.rpc
    public ChannelFuture sendToSvrAsync(String method, Object params) {
        List<Object> list = new ArrayList<Object>();
        list.add(params);
        JsonRpcObject jro = new JsonRpcObject(method, list, counter.addAndGet(1));
        String jsonStr = JSON.toJSONString(jro);
        System.out.println("send.obj: " + jsonStr);

        try {
            ChannelFuture cf = this.channel.write(jsonStr);
            return cf;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public Boolean sendToSvrSync(String method, Object params, Integer timeoutMs) {
        ChannelFuture cf = sendToSvrAsync(method, params);
        if (cf == null) {
            return false;
        }

        try {
            return cf.awaitUninterruptibly(timeoutMs);// ms
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    // retry after connection closed
    public void retryConnect() {
        Timer timer = new HashedWheelTimer();
        timer.newTimeout(new TimerTask() {

            public void run(Timeout timeout) throws Exception {
                rpcCliConn.createConnection(rpcCliCfg.getSvrHostname(), rpcCliCfg.getSvrPort());
            }
        }, 5, TimeUnit.SECONDS);
    }

    /**
     * Setters and Getters
     * 
     * @return
     */
    public RpcClientCfg getRpcCliCfg() {
        return rpcCliCfg;
    }

    public void setRpcCliCfg(RpcClientCfg rpcCliCfg) {
        this.rpcCliCfg = rpcCliCfg;
    }

    public RpcClientConn getRpcCliConn() {
        return rpcCliConn;
    }

    public void setRpcCliConn(RpcClientConn rpcCliConn) {
        this.rpcCliConn = rpcCliConn;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

}
