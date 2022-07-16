package com.php25.common.core.net.http;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

/**
 * @author penghuiping
 * @date 2022/7/16 17:27
 */
@ChannelHandler.Sharable
public abstract class HttpChannelInBoundHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    private static final Logger log = LoggerFactory.getLogger(HttpChannelInBoundHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        FullHttpResponse response = null;
        HttpVersion httpVersion = request.protocolVersion();
        if (HttpVersion.HTTP_1_1.equals(httpVersion)) {
            response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        } else {
            response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_0, HttpResponseStatus.OK);
        }
        try {
            handle(request, response);
        } catch (Exception e) {
            log.error("出错了:", e);
            response.setStatus(HttpResponseStatus.INTERNAL_SERVER_ERROR);
            response.content().clear().writeBytes("服务器错误".getBytes(StandardCharsets.UTF_8));
        }
        HttpUtil.setContentLength(response, response.content().readableBytes());
        ctx.writeAndFlush(response);
    }

    /**
     * 业务逻辑处理方法
     *
     * @param request  请求
     * @param response 响应
     */
    public abstract void handle(FullHttpRequest request, FullHttpResponse response);
}
