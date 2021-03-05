package com.php25.common.redis.local;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @author penghuiping
 * @date 2021/3/2 21:28
 */
class CmdResponse {
    private static final Logger log = LoggerFactory.getLogger(CmdResponse.class);

    private final LinkedBlockingQueue<Optional<Object>> pipe;

    public CmdResponse() {
        this.pipe = new LinkedBlockingQueue<>();
    }

    public void setResult(Object result) {
        this.pipe.offer(Optional.ofNullable(result));
    }

    public Optional<Object> getResult(long timeout, TimeUnit unit) {
        try {
            Optional<Object> res = this.pipe.poll(timeout, unit);
            if (res == null) {
                return Optional.empty();
            }
            return res;
        } catch (InterruptedException e) {
            log.error("获取redis执行指令结果出错!", e);
            return Optional.empty();
        }
    }
}
