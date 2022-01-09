package com.php25.common.id.service;

import java.util.List;

/**
 * @author penghuiping
 * @date 2022-01-05
 */
public interface IdGenerator {
    /**
     * 获取id
     *
     * @return id
     */
    Long nextId();

    /**
     * 批量获取id
     *
     * @param batchSize 批量大小
     * @return 一批id
     */
    List<Long> nextId(Integer batchSize);
}
