package com.tycs.jsonrpc4go.rpccli;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.handler.codec.string.StringDecoder;
import org.jboss.netty.handler.codec.string.StringEncoder;

public class RpcClientConn {

    final static Logger     logger = Logger.getLogger("RpcClientConn");

    private RpcClient       rpcClient;

    private ClientBootstrap clientBootstrap;
    private Channel         channel;

    private String          rpcSvrHostname;
    private Integer         rpcSvrPort;

    public RpcClientConn(RpcClient rcs, String hostname, Integer port){
        this.rpcClient = rcs;
        this.rpcSvrHostname = hostname;
        this.rpcSvrPort = port;

        this.startClientBootstrap();
    }

    // stop clientBootstrap and its'channel
    public void stop() {
        this.stopConnection();
        this.clientBootstrap.releaseExternalResources();
    }

    // stop connection
    public void stopConnection() {
        if (clientBootstrap == null) {
            return;
        }
        resetChannel();
    }
    
    // start connection
    public Boolean startConnection() {
        if (clientBootstrap == null) {
            return false;
        }
        resetChannel();

        // create new connection
        final InetSocketAddress monitorAddress = new InetSocketAddress(this.rpcSvrHostname, this.rpcSvrPort);
        ChannelFuture channelFuture = clientBootstrap.connect(monitorAddress);
        this.channel = channelFuture.getChannel();

        channelFuture.addListener(new ChannelFutureListener() {

            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    logger.info("cli connect to svr success," + monitorAddress);
                } else {
                    logger.error("cli connect to svr error," + monitorAddress);
                }
            }
        });

        // wait for 5 seconds
        return channelFuture.awaitUninterruptibly(5000);
    }

    /**
     * start jsonrpc client
     */
    private void startClientBootstrap() {
        ExecutorService executor = Executors.newCachedThreadPool();
        clientBootstrap = new ClientBootstrap(new NioClientSocketChannelFactory(executor, executor));

        clientBootstrap.setPipelineFactory(new ChannelPipelineFactory() {

            public ChannelPipeline getPipeline() {
                ChannelPipeline pipeline = Channels.pipeline();
                pipeline.addLast("decode", new StringDecoder());
                pipeline.addLast("handler", new RpcClientMsgHandler(rpcClient));
                pipeline.addLast("encode", new StringEncoder());
                return pipeline;
            }
        });

        clientBootstrap.setOption("child.tcpNoDelay", true);
        clientBootstrap.setOption("clild.keepAlive", true);
    }

    // close channel
    private void resetChannel() {
        // stop channel
        if (this.channel != null && this.channel.isOpen()) {
            final SocketAddress addr = this.channel.getRemoteAddress();

            this.channel.close();
            ChannelFuture closeFuture = this.channel.getCloseFuture();
            closeFuture.addListener(new ChannelFutureListener() {

                public void operationComplete(ChannelFuture future) throws Exception {
                    if (future.isSuccess()) {
                        logger.info("reset channel from svr, ok," + addr);
                    } else {
                        logger.error("reset channel from svr, error," + addr);
                    }
                }
            });
            closeFuture.awaitUninterruptibly();
        }
        // reset
        this.channel = null;
    }
}
