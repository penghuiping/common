package com.php25.common.coresample;

import com.php25.common.core.net.NioSimpleChannelInboundHandler;
import com.php25.common.core.net.NioTcpServer;
import com.php25.common.core.net.PipelineHandlerConfig;
import com.php25.common.core.net.http.HttpChannelInBoundHandler;
import com.php25.common.core.net.http.HttpPipelineHandlerConfig;
import com.php25.common.core.net.http.HttpProtocolHelper;
import com.php25.common.core.net.http.HttpServer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

/**
 * @author penghuiping
 * @date 2022/7/15 20:41
 */
public class NioTcpServerTest {
    private static final Logger log = LoggerFactory.getLogger(NioTcpServerTest.class);

    @Test
    public void test() throws InterruptedException {
        NioTcpServer nioTcpServer = new NioTcpServer(new PipelineHandlerConfig() {
            @Override
            public void config(ChannelPipeline channelPipeline) {
                channelPipeline.addLast(new NioSimpleChannelInboundHandler() {
                    @Override
                    protected String handleRequestMessage(String requestMsg) throws Exception {
                        log.info("请求数据：{}", requestMsg);
                        return "ok\n";
                    }
                });
            }
        });
        nioTcpServer.start();
    }

    @Test
    public void test1() throws Exception {
        HttpServer httpServer = new HttpServer(new HttpPipelineHandlerConfig(new HttpChannelInBoundHandler() {
            @Override
            public void handle(FullHttpRequest request, FullHttpResponse fullHttpResponse) {
                HttpProtocolHelper.configContentTypePlainTextUtf8(fullHttpResponse);
                fullHttpResponse.content().writeBytes("你好世界".getBytes(StandardCharsets.UTF_8));
            }
        }));
        httpServer.start();
    }
}
