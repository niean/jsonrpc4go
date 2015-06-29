package com.tycs.jsonrpc4go;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.Resource;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.handler.codec.string.StringDecoder;
import org.jboss.netty.handler.codec.string.StringEncoder;
import org.springframework.stereotype.Component;

import com.tycs.jsonrpc4go.service.RpcClientService;

@Component
public class RpcClientConn {

    @Resource
    private RpcClientCfg                 rpcCliCfg;

    private RpcClientService             rpcClientService;

    private static final ExecutorService exec     = Executors.newSingleThreadExecutor();
    private ClientBootstrap              clientBootstrap;

    private Boolean                      isInited = false;

    /**
     * start jsonrpc client
     */
    public void start(RpcClientService rcs) {
        if (isInited){
            return;
        }
        
        exec.execute(new Runnable() {

            public void run() {
                try {
                    setupTcpConn();
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        });
    }

    private void setupTcpConn() {
        ExecutorService executor = Executors.newCachedThreadPool();
        clientBootstrap = new ClientBootstrap(new NioClientSocketChannelFactory(executor, executor));

        clientBootstrap.setPipelineFactory(new ChannelPipelineFactory() {

            public ChannelPipeline getPipeline() {
                ChannelPipeline pipeline = Channels.pipeline();
                pipeline.addLast("decode", new StringDecoder());
                pipeline.addLast("handler", new RpcClientMsgHandler(rpcClientService));
                pipeline.addLast("encode", new StringEncoder());
                return pipeline;
            }
        });

        clientBootstrap.setOption("child.tcpNoDelay", true);
        clientBootstrap.setOption("clild.keepAlive", true);
        createConnection(rpcCliCfg.getSvrHostname(), rpcCliCfg.getSvrPort());
    }

    public void createConnection(String hostname, Integer port) {
        final InetSocketAddress monitorAddress = new InetSocketAddress(hostname, port);
        ChannelFuture channelFuture = clientBootstrap.connect(monitorAddress);

        channelFuture.addListener(new ChannelFutureListener() {

            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    System.out.println("cli connect to svr success," + monitorAddress);
                } else {
                    System.out.println("cli connect to svr error," + monitorAddress);
                }
            }
        });
        // 最多等待5s
        channelFuture.awaitUninterruptibly(5000);
    }

    public RpcClientService getRpcClientService() {
        return rpcClientService;
    }

    public void setRpcClientService(RpcClientService rpcClientService) {
        this.rpcClientService = rpcClientService;
    }
}
