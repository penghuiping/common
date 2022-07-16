package com.php25.common.core.net.http;

import com.php25.common.core.net.NioTcpServer;

/**
 * @author penghuiping
 * @date 2022/7/16 16:11
 */
public class HttpServer {
    private final NioTcpServer nioTcpServer;

    public HttpServer(HttpPipelineHandlerConfig pipelineHandlerConfig) {
        this.nioTcpServer = new NioTcpServer(pipelineHandlerConfig);
    }

    public void start() throws InterruptedException {
        nioTcpServer.start();
    }
}
