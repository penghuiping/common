package com.php25.common.flux.trace;

import brave.ScopedSpan;
import brave.Tracer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;


/**
 * 调用链埋点,编码方式执行埋点
 *
 * @author penghuiping
 * @date 2019/8/5 17:24
 */
@ConditionalOnBean(value = {Tracer.class})
@Component
public class TracedWrapper {

    @Autowired
    private Tracer tracer;

    public <Return> Return wrap(String spanName, Function<Return> function) {
        ScopedSpan span = tracer.startScopedSpan(spanName);
        try {
            return function.apply();
        } catch (RuntimeException | Error e) {
            span.error(e);
            throw e;
        } finally {
            span.finish();
        }
    }
}
