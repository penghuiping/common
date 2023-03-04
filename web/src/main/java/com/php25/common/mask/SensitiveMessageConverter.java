package com.php25.common.mask;

import ch.qos.logback.classic.pattern.MessageConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import com.php25.common.core.util.StringUtil;

/**
 * logback日志脱敏
 *
 * @author penghuiping
 * @date 2022/03/27 14:31
 */
public class SensitiveMessageConverter extends MessageConverter {
    private final MaskManager maskManager = new MaskManager();

    @Override
    public String convert(ILoggingEvent event) {
        // 获取原始日志
        String requestLogMsg = event.getFormattedMessage();
        // 获取返回脱敏后的日志
        return filterSensitive(requestLogMsg);
    }

    /**
     * 对敏感信息脱敏
     */
    private String filterSensitive(String content) {
        if (StringUtil.isBlank(content)) {
            return content;
        }
        return maskManager.maskMessage(content);
    }

}

