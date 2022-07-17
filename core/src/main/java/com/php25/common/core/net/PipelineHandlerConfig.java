package com.php25.common.core.net;

import io.netty.channel.ChannelPipeline;

/**
 * @author penghuiping
 * @date 2022/7/16 16:35
 */
public abstract class PipelineHandlerConfig {

    /**
     * 配置pipeline
     * @param channelPipeline pipeline
     */
    public abstract void config(ChannelPipeline channelPipeline);
}
