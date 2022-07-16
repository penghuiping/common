package com.php25.common.coresample;

import com.php25.common.core.net.NioSimpleChannelInboundHandler;
import com.php25.common.core.net.NioTcpServer;
import io.netty.channel.ChannelHandler;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author penghuiping
 * @date 2022/7/15 20:41
 */
public class NioTcpServerTest {
    private static final Logger log = LoggerFactory.getLogger(NioTcpServerTest.class);

    @Test
    public void test() throws InterruptedException {
        List<ChannelHandler> handlers = new ArrayList<>();
        handlers.add(new NioSimpleChannelInboundHandler() {
            @Override
            protected String handleRequestMessage(String requestMsg) throws Exception {
                log.info("请求数据：{}", requestMsg);
                return "ok\n";
            }
        });
        NioTcpServer nioTcpServer = new NioTcpServer(handlers);
        nioTcpServer.start();
    }
}
