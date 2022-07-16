package com.php25.common.core.net;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @author penghuiping
 * @date 2022/7/15 20:29
 */
public class NioTcpServer {
    private final PipelineHandlerConfig pipelineHandlerConfig;
    private Integer serverPort = 8080;
    private Integer bossThreadNumbers = 1;
    private Integer workThreadNumbers = Runtime.getRuntime().availableProcessors();


    public NioTcpServer(PipelineHandlerConfig pipelineHandlerConfig) {
        this.pipelineHandlerConfig = pipelineHandlerConfig;
    }

    public NioTcpServer(Integer serverPort, PipelineHandlerConfig pipelineHandlerConfig) {
        this.serverPort = serverPort;
        this.pipelineHandlerConfig = pipelineHandlerConfig;
    }

    public NioTcpServer(Integer port, PipelineHandlerConfig pipelineHandlerConfig, Integer bossThreadNumbers, Integer workThreadNumbers) {
        if (null == pipelineHandlerConfig) {
            throw new IllegalArgumentException("需要配置pipelineHandlerConfig");
        }
        this.pipelineHandlerConfig = pipelineHandlerConfig;

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
            b.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
            b.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
            b.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel channel) {
                    ChannelPipeline channelPipeline = channel.pipeline();
                    NioTcpServer.this.pipelineHandlerConfig.config(channelPipeline);
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
