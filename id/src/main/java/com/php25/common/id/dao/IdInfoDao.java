package com.php25.common.id.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.php25.common.id.dao.po.IdInfoPo;
import org.apache.ibatis.annotations.Param;

/**
 * @author penghuiping
 * @date 2022-01-05
 */
public interface IdInfoDao extends BaseMapper<IdInfoPo> {
    /**
     * 根据bizType获取db中的tinyId对象
     *
     * @param bizType 业务类型
     * @return IdInfoPo持久化对象
     */
    IdInfoPo queryByBizType(@Param("bizType") String bizType);


    /**
     * 根据id、oldMaxId、version、bizType更新最新的maxId
     *
     * @param id       主键
     * @param newMaxId 新的最大值
     * @param oldMaxId 老的最大值
     * @param version  版本号
     * @param bizType  业务类型
     * @return 大于0 更新成功
     */
    int updateMaxId(@Param("id") Long id, @Param("newMaxId") Long newMaxId,
                    @Param("oldMaxId") Long oldMaxId, @Param("version") Long version,
                    @Param("bizType") String bizType);
}
