package com.php25.common.id.service;


import com.php25.common.id.service.entity.SegmentId;

/**
 * @author penghuiping
 * @date 2022-01-05
 */
public interface SegmentIdService {

    /**
     * 根据业务类型获取下一个号段对象
     *
     * @param bizType 业务类型
     * @return 号段对象
     */
    SegmentId getNextSegmentId(String bizType);

}
