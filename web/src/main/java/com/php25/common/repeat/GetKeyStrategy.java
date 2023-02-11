package com.php25.common.repeat;

/**
 * @author penghuiping
 * @date 2023/2/11 22:23
 */
public interface GetKeyStrategy {

    /**
     * 用于获取唯一标识的加锁key
     *
     * @param context 上下文信息
     * @return 加锁key
     */
    String getKey(Context context);
}
