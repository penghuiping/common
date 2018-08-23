package com.php25.common.jpa.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.io.Serializable;
import java.util.List;

/**
 * database repository层的基础接口
 * @author penghuiping
 * @date 2016-04-03
 *
 */
@NoRepositoryBean
public interface BaseRepository<T, ID extends Serializable> extends PagingAndSortingRepository<T, ID>, JpaSpecificationExecutor<T> {

    /**
     * 获取所有有效的数据项,在软删除的环境中,指的是没有软删除的数据
     *
     * @return
     */
    public List<T> findAllEnabled();

}
