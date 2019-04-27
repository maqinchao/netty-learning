package com.mqc.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class TimeClientHandler extends ChannelHandlerAdapter {

    private ByteBuf firstMessage=null;

    public TimeClientHandler(){
        byte[] bytes="QUERY TIME ORDER".getBytes();
        firstMessage= Unpooled.buffer(bytes.length);
        firstMessage.writeBytes(bytes);
    }

    public void channelActive(ChannelHandlerContext ctx){
        ctx.writeAndFlush(firstMessage);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf byteBuf= (ByteBuf) msg;
        byte[] req=new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(req);
        String body=new String(req,"UTF-8");
        System.out.printf("Now is:"+body);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
