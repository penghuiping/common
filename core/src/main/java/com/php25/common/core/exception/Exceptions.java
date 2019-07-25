package com.php25.common.core.exception;

/**
 * @author: penghuiping
 * @date: 2019/7/11 10:00
 * @description:
 */
public class Exceptions {

    public static ImpossibleException throwImpossibleException() {
        return new ImpossibleException("这种情况不可能发生!!!");
    }

    public static BusinessException throwBusinessException(String code, String message) {
        return new BusinessException(code, message);
    }

    public static BusinessException throwBusinessException(BusinessErrorStatus businessErrorStatus) {
        return new BusinessException(businessErrorStatus);
    }


    public static IllegalStateException throwIllegalStateException(String message, Throwable e) {
        return new IllegalStateException(message, e);
    }

    public static IllegalStateException throwIllegalStateException(String message) {
        return new IllegalStateException(message);
    }
}
