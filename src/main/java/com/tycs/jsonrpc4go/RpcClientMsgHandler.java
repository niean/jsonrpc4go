package com.tycs.jsonrpc4go;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;

import com.tycs.jsonrpc4go.service.RpcClientService;

public class RpcClientMsgHandler extends SimpleChannelHandler {

    private RpcClientService rpcClientService;

    public RpcClientMsgHandler(RpcClientService rpcClientService){
        super();
        this.rpcClientService = rpcClientService;
    }

    @Override
    public void channelOpen(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        super.channelOpen(ctx, e);
    }

    @Override
    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) {
        rpcClientService.setChannel(ctx.getChannel());
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
        System.out.println("recv from svr:" + (String) e.getMessage());
        //ctx.getChannel().close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
        e.getCause().printStackTrace();
        ctx.getChannel().close();
    }

    @Override
    public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e) {
        System.out.println("Disconnected from: " + ctx.getChannel().getRemoteAddress());
    }

    @Override
    public void channelClosed(final ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        // 意外断开重连
        System.out.println("channelClosed:" + ctx.getName());
        rpcClientService.retryConnect();
    }
}
