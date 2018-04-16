package com.php25.common.service;


import com.php25.common.dto.DataGridPageDto;
import org.springframework.data.domain.Sort;

import java.util.List;

/**
 * @author penghuiping
 * @Timer 16/8/12.
 */
public interface BaseService<DTO, MODEL> {
    /**
     * 根据id查找
     *
     * @param id
     * @return
     * @author penghuiping
     * @Timer 16/8/12.
     */
    DTO findOne(String id);

    /**
     * 根据id查找
     *
     * @param id
     * @param modelToDtoTransferable
     * @return
     * @author penghuiping
     * @Timer 16/8/12.
     */
    DTO findOne(String id, ModelToDtoTransferable<MODEL, DTO> modelToDtoTransferable);

    /**
     * 保存或者更新
     *
     * @param obj
     * @return
     * @author penghuiping
     * @Timer 16/8/12.
     */
    DTO save(DTO obj);

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
    public DTO save(DTO obj, DtoToModelTransferable<MODEL, DTO> dtoToModelTransferable, ModelToDtoTransferable<MODEL, DTO> modelToDtoTransferable);

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
    List<DTO> findAll(Iterable<String> ids);

    /**
     * 根据id查找
     *
     * @param modelToDtoTransferable
     * @param ids
     * @return
     * @author penghuiping
     * @Timer 16/8/12.
     */
    List<DTO> findAll(Iterable<String> ids, ModelToDtoTransferable<MODEL, DTO> modelToDtoTransferable);

    /**
     * 查找所有的
     *
     * @return
     * @author penghuiping
     * @Timer 16/8/12.
     */
    List<DTO> findAll();


    /**
     * 查找所有的
     *
     * @param modelToDtoTransferable
     * @return
     * @author penghuiping
     * @Timer 16/8/12.
     */
    List<DTO> findAll(ModelToDtoTransferable<MODEL, DTO> modelToDtoTransferable);

    /**
     * 分页条件筛选查找
     *
     * @param iDisplayStart
     * @param iDisplayLength
     * @param searchParams
     * @return
     * @author penghuiping
     * @Timer 16/8/12.
     */
    DataGridPageDto<DTO> query(Integer iDisplayStart, Integer iDisplayLength, String searchParams);

    /**
     * 分页条件筛选查找
     *
     * @param iDisplayStart
     * @param iDisplayLength
     * @param searchParams
     * @param direction
     * @param properties
     * @return
     */
    DataGridPageDto<DTO> query(Integer iDisplayStart, Integer iDisplayLength, String searchParams, Sort.Direction direction, String... properties);

    /**
     * 分页条件筛选查找
     *
     * @param iDisplayStart
     * @param iDisplayLength
     * @param searchParams
     * @param modelToDtoTransferable
     * @param direction
     * @param properties
     * @return
     * @author penghuiping
     * @Timer 16/8/12.
     */
    DataGridPageDto<DTO> query(Integer iDisplayStart, Integer iDisplayLength, String searchParams, ModelToDtoTransferable<MODEL, DTO> modelToDtoTransferable, Sort.Direction direction, String... properties);

    /**
     * 分页条件筛选查找
     * @param iDisplayStart
     * @param iDisplayLength
     * @param searchParams
     * @param customerModelToDtoTransferable
     * @param sort
     * @return
     */
    public DataGridPageDto<DTO> query(Integer iDisplayStart, Integer iDisplayLength, String searchParams, ModelToDtoTransferable<MODEL, DTO> customerModelToDtoTransferable, Sort sort);

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
