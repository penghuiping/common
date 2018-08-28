package com.php25.common.core.service;


import com.php25.common.core.dto.DataGridPageDto;
import com.php25.common.core.specification.SearchParamBuilder;
import org.springframework.data.domain.Sort;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

/**
 * service层方法的通用接口方法
 *
 * @author penghuiping
 * @date 2016-08-12
 */
public interface BaseService<DTO, MODEL, ID extends Serializable> {
    /**
     * 根据id查找
     *
     * @param id 实体类id主键
     * @return 返回相关DTO
     */
    Optional<DTO> findOne(ID id);

    /**
     * 根据id查找
     *
     * @param id                     实体类id主键
     * @param modelToDtoTransferable model转dto的处理函数
     * @return 返回相关DTO
     */
    Optional<DTO> findOne(ID id, ModelToDtoTransferable<MODEL, DTO> modelToDtoTransferable);

    /**
     * 保存或者更新
     *
     * @param obj 需要保存/更新的对象
     * @return 保存/更新成功的对象
     */
    Optional<DTO> save(DTO obj);

    /**
     * 保存或者更新
     *
     * @param obj                    需要保存/更新的对象
     * @param dtoToModelTransferable dto转model的处理函数
     * @param modelToDtoTransferable model转dto的处理函数
     * @return 保存/更新成功的对象
     */
    Optional<DTO> save(DTO obj, DtoToModelTransferable<MODEL, DTO> dtoToModelTransferable, ModelToDtoTransferable<MODEL, DTO> modelToDtoTransferable);

    /**
     * 保存或者更新批量
     *
     * @param objs 需要批量保存/更新的对象
     */
    void save(Iterable<DTO> objs);

    /**
     * 保存或者更新批量
     *
     * @param objs                   需要批量保存/更新的对象
     * @param dtoToModelTransferable dto转model的处理函数
     */
    void save(Iterable<DTO> objs, DtoToModelTransferable<MODEL, DTO> dtoToModelTransferable);

    /**
     * 物理删除
     *
     * @param obj 需要删除的dto对象
     */
    void delete(DTO obj);

    /**
     * 批量物理删除
     *
     * @param objs 需要批量删除的dto对象
     */
    void delete(List<DTO> objs);

    /**
     * 根据ids查找
     *
     * @param ids 需要查询的ids
     * @return 返回符合条件的结果
     */
    Optional<List<DTO>> findAll(Iterable<ID> ids);

    /**
     * 根据id查找
     *
     * @param modelToDtoTransferable
     * @param ids
     * @return
     */
    Optional<List<DTO>> findAll(Iterable<ID> ids, ModelToDtoTransferable<MODEL, DTO> modelToDtoTransferable);

    /**
     * 查找所有的
     *
     * @return
     */
    Optional<List<DTO>> findAll();


    /**
     * 查找所有的
     *
     * @param modelToDtoTransferable
     * @return
     */
    Optional<List<DTO>> findAll(ModelToDtoTransferable<MODEL, DTO> modelToDtoTransferable);

    /**
     * 分页条件筛选查找
     *
     * @param pageNum
     * @param pageSize
     * @param searchParams
     * @return
     */
    Optional<DataGridPageDto<DTO>> query(Integer pageNum, Integer pageSize, String searchParams);

    /**
     * 分页条件筛选查找
     *
     * @param pageNum
     * @param pageSize
     * @param searchParams
     * @param direction
     * @param property
     * @return
     */
    Optional<DataGridPageDto<DTO>> query(Integer pageNum, Integer pageSize, String searchParams, Sort.Direction direction, String property);

    /**
     * 分页条件筛选查找
     *
     * @param pageNum
     * @param pageSize
     * @param searchParams
     * @param modelToDtoTransferable
     * @param direction
     * @param property
     * @return
     */
    Optional<DataGridPageDto<DTO>> query(Integer pageNum, Integer pageSize, String searchParams, ModelToDtoTransferable<MODEL, DTO> modelToDtoTransferable, Sort.Direction direction, String property);

    /**
     * 分页条件筛选查找
     *
     * @param pageNum
     * @param pageSize
     * @param searchParams
     * @param customerModelToDtoTransferable
     * @param sort
     * @return
     */
    Optional<DataGridPageDto<DTO>> query(Integer pageNum, Integer pageSize, String searchParams, ModelToDtoTransferable<MODEL, DTO> customerModelToDtoTransferable, Sort sort);

    /**
     * 分页条件筛选查找
     *
     * @param pageNum
     * @param pageSize
     * @param searchParamBuilder
     * @param customerModelToDtoTransferable
     * @param sort
     * @return
     */
    Optional<DataGridPageDto<DTO>> query(Integer pageNum, Integer pageSize, SearchParamBuilder searchParamBuilder, ModelToDtoTransferable<MODEL, DTO> customerModelToDtoTransferable, Sort sort);

    /**
     * 筛选计算数量
     *
     * @param searchParams
     * @return
     */
    Long count(String searchParams);


}
