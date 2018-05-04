package com.php25.common.service;


import com.php25.common.dto.DataGridPageDto;
import org.springframework.data.domain.Sort;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

/**
 * @author penghuiping
 * @Timer 16/8/12.
 */
public interface BaseService<DTO, MODEL,ID extends Serializable> {
    /**
     * 根据id查找
     *
     * @param id
     * @return
     * @author penghuiping
     * @Timer 16/8/12.
     */
    Optional<DTO> findOne(ID id);

    /**
     * 根据id查找
     *
     * @param id
     * @param modelToDtoTransferable
     * @return
     * @author penghuiping
     * @Timer 16/8/12.
     */
    Optional<DTO> findOne(ID id, ModelToDtoTransferable<MODEL, DTO> modelToDtoTransferable);

    /**
     * 保存或者更新
     *
     * @param obj
     * @return
     * @author penghuiping
     * @Timer 16/8/12.
     */
    Optional<DTO> save(DTO obj);

    /**
     * 保存或者更新
     *
     * @param obj
     * @param dtoToModelTransferable
     * @param modelToDtoTransferable
     * @return
     * @author penghuiping
     * @Timer 16/8/12.
     */
    Optional<DTO> save(DTO obj, DtoToModelTransferable<MODEL, DTO> dtoToModelTransferable, ModelToDtoTransferable<MODEL, DTO> modelToDtoTransferable);

    /**
     * 保存或者更新批量
     *
     * @param objs
     * @author penghuiping
     * @Timer 16/8/12.
     */
    void save(Iterable<DTO> objs);

    /**
     * 保存或者更新批量
     *
     * @param objs
     * @param dtoToModelTransferable
     * @author penghuiping
     * @Timer 16/8/12.
     */
    void save(Iterable<DTO> objs, DtoToModelTransferable<MODEL, DTO> dtoToModelTransferable);

    /**
     * 物理删除
     *
     * @param obj
     * @author penghuiping
     * @Timer 16/8/12.
     */
    void delete(DTO obj);

    /**
     * 批量物理删除
     *
     * @param objs
     * @author penghuiping
     * @Timer 16/8/12.
     */
    void delete(List<DTO> objs);

    /**
     * 根据id查找
     *
     * @param ids
     * @return
     * @author penghuiping
     * @Timer 16/8/12.
     */
    Optional<List<DTO>> findAll(Iterable<ID> ids);

    /**
     * 根据id查找
     *
     * @param modelToDtoTransferable
     * @param ids
     * @return
     * @author penghuiping
     * @Timer 16/8/12.
     */
    Optional<List<DTO>> findAll(Iterable<ID> ids, ModelToDtoTransferable<MODEL, DTO> modelToDtoTransferable);

    /**
     * 查找所有的
     *
     * @return
     * @author penghuiping
     * @Timer 16/8/12.
     */
    Optional<List<DTO>> findAll();


    /**
     * 查找所有的
     *
     * @param modelToDtoTransferable
     * @return
     * @author penghuiping
     * @Timer 16/8/12.
     */
    Optional<List<DTO>> findAll(ModelToDtoTransferable<MODEL, DTO> modelToDtoTransferable);

    /**
     * 分页条件筛选查找
     *
     * @param pageNum
     * @param pageSize
     * @param searchParams
     * @return
     * @author penghuiping
     * @Timer 16/8/12.
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
     * @author penghuiping
     * @Timer 16/8/12.
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
     * 筛选计算数量
     *
     * @param searchParams
     * @return
     * @author penghuiping
     * @Timer 16/8/12.
     */
    Long count(String searchParams);


}
