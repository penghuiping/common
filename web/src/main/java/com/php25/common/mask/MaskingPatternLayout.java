package com.php25.common.mask;

import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;

/**
 * logback日志脱敏
 *
 * @author penghuiping
 * @date 2022/03/27 14:31
 */
public class MaskingPatternLayout extends PatternLayout {
    private final MaskManager maskManager = new MaskManager();

    public void addMaskPattern(String maskPattern) {
        maskManager.addMaskPattern(maskPattern);
    }

    @Override
    public String doLayout(ILoggingEvent event) {
        return maskManager.maskMessage(super.doLayout(event));
    }


}

