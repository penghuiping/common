package com.php25.common.core.net.http;

import com.php25.common.core.net.PipelineHandlerConfig;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

/**
 * @author penghuiping
 * @date 2022/7/16 16:44
 */
public class HttpPipelineHandlerConfig extends PipelineHandlerConfig {
    private final HttpChannelInBoundHandler httpChannelInBoundHandler;

    public HttpPipelineHandlerConfig(HttpChannelInBoundHandler httpChannelInBoundHandler) {
        this.httpChannelInBoundHandler = httpChannelInBoundHandler;
    }

    @Override
    public void config(ChannelPipeline channelPipeline) {
        channelPipeline.addLast(new HttpRequestDecoder());
        channelPipeline.addLast(new HttpObjectAggregator(65535));
        channelPipeline.addLast(new HttpResponseEncoder());
        channelPipeline.addLast(httpChannelInBoundHandler);
    }

}
