package com.php25.common.core.net;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.util.List;

/**
 * @author penghuiping
 * @date 2022/7/15 20:29
 */
public class NioTcpServer {
    private final List<ChannelHandler> channelHandlers;
    private Integer serverPort = 8080;
    private Integer bossThreadNumbers = 1;
    private Integer workThreadNumbers = Runtime.getRuntime().availableProcessors();


    public NioTcpServer(List<ChannelHandler> channelHandlers) {
        this.channelHandlers = channelHandlers;
    }

    public NioTcpServer(Integer serverPort, List<ChannelHandler> channelHandlers) {
        this.serverPort = serverPort;
        this.channelHandlers = channelHandlers;
    }

    public NioTcpServer(Integer port, List<ChannelHandler> channelHandlers, Integer bossThreadNumbers, Integer workThreadNumbers) {
        if (null == channelHandlers || channelHandlers.isEmpty()) {
            throw new IllegalArgumentException("至少需要有一个ChannelHandler");
        }
        this.channelHandlers = channelHandlers;

        if (null != serverPort) {
            this.serverPort = port;
        }

        if (null != bossThreadNumbers) {
            this.bossThreadNumbers = bossThreadNumbers;
        }
        if (null != workThreadNumbers) {
            this.workThreadNumbers = workThreadNumbers;
        }
    }

    public void start() throws InterruptedException {
        NioEventLoopGroup boss = new NioEventLoopGroup(this.bossThreadNumbers);
        NioEventLoopGroup group = new NioEventLoopGroup(this.workThreadNumbers);
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(boss, group);
            b.channel(NioServerSocketChannel.class);
            b.localAddress(this.serverPort);
            b.option(ChannelOption.SO_KEEPALIVE, true);
            b.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
            b.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
            b.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel channel) {
                    ChannelPipeline channelPipeline = channel.pipeline();
                    for (ChannelHandler channelHandler : NioTcpServer.this.channelHandlers) {
                        channelPipeline.addLast(channelHandler);
                    }
                }
            });
            ChannelFuture f = b.bind().sync();
            f.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully();
            boss.shutdownGracefully();
        }
    }
}
