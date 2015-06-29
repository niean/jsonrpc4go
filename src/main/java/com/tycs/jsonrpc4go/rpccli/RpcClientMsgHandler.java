package com.tycs.jsonrpc4go.rpccli;

import org.apache.log4j.Logger;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;

public class RpcClientMsgHandler extends SimpleChannelHandler {

    final static Logger logger = Logger.getLogger("RpcClientMsgHandler");

    private RpcClient   rpcClient;

    public RpcClientMsgHandler(RpcClient rpcClient){
        super();
        this.rpcClient = rpcClient;
    }

    @Override
    public void channelOpen(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        super.channelOpen(ctx, e);
    }

    @Override
    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) {
        rpcClient.setChannel(ctx.getChannel());
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
        logger.debug("recv from svr:" + (String) e.getMessage());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
        logger.fatal(e.getCause());
        e.getCause().printStackTrace();
        ctx.getChannel().close();
    }

    @Override
    public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e) {
        logger.warn("Disconnected from: " + ctx.getChannel().getRemoteAddress());
    }

    @Override
    public void channelClosed(final ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        // 意外断开重连
        logger.error("channelClosed:" + ctx.getName());
        rpcClient.retryConnect();
    }
}
