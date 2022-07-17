package com.php25.common.core.net.http;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

/**
 * @author penghuiping
 * @date 2022/7/16 20:27
 */
public abstract class HttpProtocolHelper {
    private static final Logger log = LoggerFactory.getLogger(HttpProtocolHelper.class);

    public static void configContentTypePlainTextUtf8(FullHttpResponse fullHttpResponse) {
        fullHttpResponse.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=utf-8");
    }

    public static void configContentTypeApplicationJsonUtf8(FullHttpResponse fullHttpResponse) {
        fullHttpResponse.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json; charset=utf-8");
    }


    public static void writeAndFlush(ChannelHandlerContext ctx, FullHttpRequest request, FullHttpResponse response) {
        boolean isKeepAlive = HttpUtil.isKeepAlive(request);
        if (isKeepAlive) {
            response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
            HttpUtil.setContentLength(response, response.content().readableBytes());
        }
        ChannelFuture channelFuture = ctx.writeAndFlush(response);
        if (!isKeepAlive) {
            channelFuture.addListener(ChannelFutureListener.CLOSE);
        }
    }

    public static void defaultHandleError(FullHttpResponse response, Exception e) {
        log.error("HttpServer处理请求发生错误:", e);
        response.setStatus(HttpResponseStatus.INTERNAL_SERVER_ERROR);
        response.content().clear().writeBytes("HTTP STATUS 500,服务器错误,请联系管理员".getBytes(StandardCharsets.UTF_8));
    }
}
