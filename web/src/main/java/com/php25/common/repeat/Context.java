package com.php25.common.repeat;

import org.aspectj.lang.ProceedingJoinPoint;

/**
 * @author penghuiping
 * @date 2023/2/11 22:25
 */
public class Context {

    private final ProceedingJoinPoint pjp;

    public Context(ProceedingJoinPoint pjp) {
        this.pjp = pjp;
    }

    public ProceedingJoinPoint getPjp() {
        return pjp;
    }
}
