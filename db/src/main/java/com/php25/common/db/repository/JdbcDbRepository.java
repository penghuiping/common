package com.php25.common.db.repository;

import com.php25.common.db.specification.SearchParamBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.lang.Nullable;

import java.util.List;
import java.util.Optional;

/**
 * db扩展接口功能
 *
 * @author penghuiping
 * @date 2019/7/25
 */
public interface JdbcDbRepository<T, ID > {

    /**
     * 获取所有有效的数据项,在软删除的环境中,指的是没有软删除的数据
     *
     * @return
     */
    List<T> findAllEnabled();

    /**
     * 根据id获取有效数据
     *
     * @param id
     * @return
     */
    Optional<T> findByIdEnable(ID id);

    /**
     * 根据查询条件，查询符合条件的一条数据
     *
     * @param searchParamBuilder 查询条件
     * @return
     */
    Optional<T> findOne(@Nullable SearchParamBuilder searchParamBuilder);

    /**
     * 根据查询条件，查询符合条件的一组数据
     *
     * @param searchParamBuilder 查询条件
     * @return
     */
    List<T> findAll(@Nullable SearchParamBuilder searchParamBuilder);

    /**
     * 根据查询条件，用分页的方式，查询符合条件的一组数据
     *
     * @param searchParamBuilder 查询条件
     * @param pageable           分页参数
     * @return
     */
    Page<T> findAll(@Nullable SearchParamBuilder searchParamBuilder, Pageable pageable);

    /**
     * 根据查询条件，查询符合条件的一组数据，并且根据排序条件进行排序
     *
     * @param searchParamBuilder 查询条件
     * @param sort               排序条件
     * @return
     */
    List<T> findAll(@Nullable SearchParamBuilder searchParamBuilder, Sort sort);

    /**
     * 根据查询条件，统计符合条件的记录数
     *
     * @param searchParamBuilder
     * @return
     */
    long count(@Nullable SearchParamBuilder searchParamBuilder);

}
