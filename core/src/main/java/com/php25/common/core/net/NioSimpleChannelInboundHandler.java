package com.php25.common.core.net;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

/**
 * @author penghuiping
 * @date 2022/7/15 21:50
 */
public abstract class NioSimpleChannelInboundHandler extends SimpleChannelInboundHandler<ByteBuf> {
    private static final Logger log = LoggerFactory.getLogger(NioSimpleChannelInboundHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        String responseMsg = handleRequestMessage(new String(ByteBufUtil.getBytes(msg), StandardCharsets.UTF_8));
        ByteBuf responseMsgBuffer = ctx.alloc().buffer();
        ByteBufUtil.writeUtf8(responseMsgBuffer, responseMsg);
        ctx.writeAndFlush(responseMsgBuffer);
    }

    /**
     * 处理请求消息
     *
     * @param requestMsg 请求消息
     * @return 返回消息
     * @throws Exception 错误
     */
    protected abstract String handleRequestMessage(String requestMsg) throws Exception;


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("NioTcpServer处理请求发送错误:", cause);
    }
}
