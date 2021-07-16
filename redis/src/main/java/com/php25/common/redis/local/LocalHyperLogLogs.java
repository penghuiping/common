package com.php25.common.redis.local;

import com.php25.common.redis.RHyperLogLogs;
import org.apache.commons.lang3.NotImplementedException;

/**
 * @author penghuiping
 * @date 2021/2/24 16:59
 */
public class LocalHyperLogLogs implements RHyperLogLogs {

    @Override
    public void add(Object... values) {
        throw new NotImplementedException("local redis暂不支持HyperLogLogs数据结构");
    }

    @Override
    public Long size() {
        throw new NotImplementedException("local redis暂不支持HyperLogLogs数据结构");
    }
}
