package com.php25.common.coresample;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.DefaultFileRegion;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.FileRegion;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestEncoder;
import io.netty.handler.codec.http.HttpResponseDecoder;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.internal.MacAddressUtil;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.channels.FileChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @author penghuiping
 * @date 2020/9/18 14:04
 */
public class NettyTest {

    private static final Logger log = LoggerFactory.getLogger(NettyTest.class);

    @Test
    public void test() throws Exception {
        File file = new File("/tmp/1.txt");
        if (!file.exists()) {
            Files.write(file.toPath(), "hello world,你好!".getBytes(StandardCharsets.UTF_8),
                    StandardOpenOption.CREATE_NEW);
        }
        FileRegion fileRegion = new DefaultFileRegion(file, 0, file.length());

        if (!Files.exists(Paths.get("/tmp/2.txt"))) {
            Files.createFile(Paths.get("/tmp/2.txt"));
        }

        try (
                SeekableByteChannel seekableByteChannel = Files.newByteChannel(Paths.get("/tmp/2.txt"),
                        StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
        ) {
            fileRegion.transferTo(seekableByteChannel, 0);

        } finally {
            fileRegion.release();
        }
    }

    @Test
    public void test2() throws Exception {
        ByteBuf byteBuf = Unpooled.buffer(1024);
        byteBuf.writeBytes("hello world，你好世界!!!".getBytes(StandardCharsets.UTF_8));

        try (
                FileChannel fileChannel = (FileChannel) Files.newByteChannel(Paths.get("/tmp/2.txt"),
                        StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
        ) {
            byteBuf.readBytes(fileChannel, 0, byteBuf.arrayOffset());
            fileChannel.write(byteBuf.nioBuffer());
        }
    }

    @Test
    public void test3() throws Exception {
        log.info("本机的mac地址:{}", MacAddressUtil.formatAddress(MacAddressUtil.bestAvailableMac()));
    }


    @Test
    public void test4() throws Exception {
        //http客户端
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(workerGroup);
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
        bootstrap.option(ChannelOption.TCP_NODELAY, true);
        bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000);

        LinkedBlockingQueue<String> results = new LinkedBlockingQueue<>();
        //设置管道
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                socketChannel.pipeline()
                        .addLast(new HttpRequestEncoder())
                        .addLast(new HttpResponseDecoder())
                        .addLast(new HttpObjectAggregator(66536))
                        .addLast(new SimpleChannelInboundHandler<FullHttpResponse>() {
                            @Override
                            protected void channelRead0(ChannelHandlerContext ctx, FullHttpResponse response) throws Exception {
                                results.offer(response.content().readCharSequence(response.content().readableBytes(), StandardCharsets.UTF_8).toString());
                                ctx.close();
                            }
                        });
            }
        });
        ChannelFuture f = bootstrap.connect("www.baidu.com", 80).sync();
        FullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "/index.html");
        Channel channel = f.channel();
        channel.writeAndFlush(request).sync();
        channel.closeFuture().sync();
        workerGroup.shutdownGracefully().sync();
        String result = results.poll(5, TimeUnit.SECONDS);
        Assertions.assertThat(result).contains("百度一下");
    }
}
