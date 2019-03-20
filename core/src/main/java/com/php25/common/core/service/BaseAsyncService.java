package com.php25.common.core.service;

import com.php25.common.core.dto.DataGridPageDto;
import com.php25.common.core.specification.SearchParamBuilder;
import org.springframework.data.domain.Sort;
import reactor.core.publisher.Mono;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

/**
 * @author: penghuiping
 * @date: 2019/3/19 22:49
 * @description:
 */
public interface BaseAsyncService<DTO, MODEL, ID extends Serializable> {

    /**
     * 根据id查找-异步方式
     *
     * @param id 实体类id主键
     * @return 返回相关DTO
     */
    Mono<Optional<DTO>> findOneAsync(ID id);

    /**
     * 根据id查找-异步方式
     *
     * @param id                     实体类id主键
     * @param modelToDtoTransferable model转dto的处理函数
     * @return 返回相关DTO
     */
    Mono<Optional<DTO>> findOneAsync(ID id, ModelToDtoTransferable<MODEL, DTO> modelToDtoTransferable);


    /**
     * 保存或者更新-异步方式
     *
     * @param obj 需要保存/更新的对象
     * @return 保存/更新成功的对象
     */
    Mono<Optional<DTO>> saveAsync(DTO obj);

    /**
     * 保存或者更新-异步方式
     *
     * @param obj                    需要保存/更新的对象
     * @param dtoToModelTransferable dto转model的处理函数
     * @param modelToDtoTransferable model转dto的处理函数
     * @return 保存/更新成功的对象
     */
    Mono<Optional<DTO>> saveAsync(DTO obj, DtoToModelTransferable<MODEL, DTO> dtoToModelTransferable, ModelToDtoTransferable<MODEL, DTO> modelToDtoTransferable);


    /**
     * 保存或者更新批量-异步方式
     *
     * @param objs 需要批量保存/更新的对象
     * @return Mono
     */
    Mono<Boolean> saveAsync(Iterable<DTO> objs);

    /**
     * 保存或者更新批量-异步方式
     *
     * @param objs                   需要批量保存/更新的对象
     * @param dtoToModelTransferable dto转model的处理函数
     * @return Mono
     */
    Mono<Boolean> saveAsync(Iterable<DTO> objs, DtoToModelTransferable<MODEL, DTO> dtoToModelTransferable);


    /**
     * 物理删除-异步方式
     *
     * @param obj 需要删除的dto对象
     * @return Mono
     */
    Mono<Boolean> deleteAsync(DTO obj);


    /**
     * 批量物理删除-异步方式
     *
     * @param objs 需要批量删除的dto对象
     * @return Mono
     */
    Mono<Boolean> deleteAsync(List<DTO> objs);


    /**
     * 根据ids查找-异步方式
     *
     * @param ids 需要查询的ids
     * @return 返回符合条件的结果
     */
    Mono<Optional<List<DTO>>> findAllAsync(Iterable<ID> ids);


    /**
     * 根据id查找-异步方式
     *
     * @param modelToDtoTransferable
     * @param ids
     * @return mono
     */
    Mono<Optional<List<DTO>>> findAllAsync(Iterable<ID> ids, ModelToDtoTransferable<MODEL, DTO> modelToDtoTransferable);


    /**
     * 查找所有的-异步方式
     *
     * @return
     */
    Mono<Optional<List<DTO>>> findAllAsync();


    /**
     * 查找所有的-异步方式
     *
     * @param modelToDtoTransferable
     * @return
     */
    Mono<Optional<List<DTO>>> findAllAsync(ModelToDtoTransferable<MODEL, DTO> modelToDtoTransferable);


    /**
     * 分页条件筛选查找-异步方式
     *
     * @param pageNum
     * @param pageSize
     * @param searchParams
     * @return
     */
    Mono<Optional<DataGridPageDto<DTO>>> queryAsync(Integer pageNum, Integer pageSize, String searchParams);


    /**
     * 分页条件筛选查找-异步方式
     *
     * @param pageNum
     * @param pageSize
     * @param searchParams
     * @param direction
     * @param property
     * @return
     */
    Mono<Optional<DataGridPageDto<DTO>>> queryAsync(Integer pageNum, Integer pageSize, String searchParams, Sort.Direction direction, String property);


    /**
     * 分页条件筛选查找-异步方式
     *
     * @param pageNum
     * @param pageSize
     * @param searchParams
     * @param modelToDtoTransferable
     * @param direction
     * @param property
     * @return
     */
    Mono<Optional<DataGridPageDto<DTO>>> queryAsync(Integer pageNum, Integer pageSize, String searchParams, ModelToDtoTransferable<MODEL, DTO> modelToDtoTransferable, Sort.Direction direction, String property);


    /**
     * 分页条件筛选查找-异步方式
     *
     * @param pageNum
     * @param pageSize
     * @param searchParams
     * @param customerModelToDtoTransferable
     * @param sort
     * @return
     */
    Mono<Optional<DataGridPageDto<DTO>>> queryAsync(Integer pageNum, Integer pageSize, String searchParams, ModelToDtoTransferable<MODEL, DTO> customerModelToDtoTransferable, Sort sort);


    /**
     * 分页条件筛选查找-异步方式
     *
     * @param pageNum
     * @param pageSize
     * @param searchParamBuilder
     * @param customerModelToDtoTransferable
     * @param sort
     * @return
     */
    Mono<Optional<DataGridPageDto<DTO>>> queryAsync(Integer pageNum, Integer pageSize, SearchParamBuilder searchParamBuilder, ModelToDtoTransferable<MODEL, DTO> customerModelToDtoTransferable, Sort sort);


    /**
     * 筛选计算数量-异步方式
     *
     * @param searchParams
     * @return mono
     */
    Mono<Long> countAsync(String searchParams);
}
