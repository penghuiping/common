package com.php25.common.core.service;

import com.php25.common.core.dto.DataGridPageDto;
import com.php25.common.core.specification.SearchParamBuilder;
import org.springframework.data.domain.Sort;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

/**
 * @author: penghuiping
 * @date: 2019/3/19 14:37
 * @description:
 */
public abstract class AbstractBaseAsyncServiceImpl<DTO, MODEL, ID extends Serializable> implements BaseService<DTO, MODEL, ID>, BaseAsyncService<DTO, MODEL, ID>, SoftDeletable<DTO> {

    @Override
    public Mono<Optional<DTO>> findOneAsync(ID id) {
        return Mono.fromCallable(() -> this.findOne(id)).subscribeOn(Schedulers.parallel());
    }

    @Override
    public Mono<Optional<DTO>> findOneAsync(ID id, ModelToDtoTransferable<MODEL, DTO> modelToDtoTransferable) {
        return Mono.fromCallable(() -> this.findOne(id, modelToDtoTransferable)).subscribeOn(Schedulers.parallel());
    }

    @Override
    public Mono<Optional<DTO>> saveAsync(DTO obj) {
        return Mono.fromCallable(() -> save(obj)).subscribeOn(Schedulers.parallel());
    }

    @Override
    public Mono<Optional<DTO>> saveAsync(DTO obj, DtoToModelTransferable<MODEL, DTO> dtoToModelTransferable, ModelToDtoTransferable<MODEL, DTO> modelToDtoTransferable) {
        return Mono.fromCallable(() -> save(obj, dtoToModelTransferable, modelToDtoTransferable)).subscribeOn(Schedulers.parallel());
    }

    @Override
    public Mono saveAsync(Iterable<DTO> objs) {
        return Mono.fromCallable(() -> {
            save(objs);
            return true;
        }).onErrorReturn(false).subscribeOn(Schedulers.parallel());
    }

    @Override
    public Mono saveAsync(Iterable<DTO> objs, DtoToModelTransferable<MODEL, DTO> dtoToModelTransferable) {
        return Mono.fromCallable(() -> {
            save(objs, dtoToModelTransferable);
            return true;
        }).onErrorReturn(false).subscribeOn(Schedulers.parallel());
    }

    @Override
    public Mono<Boolean> deleteAsync(DTO obj) {
        return Mono.fromCallable(() -> {
            delete(obj);
            return true;
        }).onErrorReturn(false).subscribeOn(Schedulers.parallel());
    }

    @Override
    public Mono<Boolean> deleteAsync(List<DTO> objs) {
        return Mono.fromCallable(() -> {
            delete(objs);
            return true;
        }).onErrorReturn(false).subscribeOn(Schedulers.parallel());
    }

    @Override
    public Mono<Optional<DataGridPageDto<DTO>>> queryAsync(Integer pageNum, Integer pageSize, String searchParams) {
        return Mono.fromCallable(() -> query(pageNum, pageSize, searchParams)).subscribeOn(Schedulers.parallel());
    }

    @Override
    public Mono<Optional<DataGridPageDto<DTO>>> queryAsync(Integer pageNum, Integer pageSize, String searchParams, Sort.Direction direction, String property) {
        return Mono.fromCallable(() -> query(pageNum, pageSize, searchParams, direction, property)).subscribeOn(Schedulers.parallel());
    }

    @Override
    public Mono<Optional<DataGridPageDto<DTO>>> queryAsync(Integer pageNum, Integer pageSize, String searchParams, ModelToDtoTransferable<MODEL, DTO> modelToDtoTransferable, Sort.Direction direction, String property) {
        return Mono.fromCallable(() -> query(pageNum, pageSize, searchParams, modelToDtoTransferable, direction, property)).subscribeOn(Schedulers.parallel());
    }

    @Override
    public Mono<Optional<DataGridPageDto<DTO>>> queryAsync(Integer pageNum, Integer pageSize, String searchParams, ModelToDtoTransferable<MODEL, DTO> customerModelToDtoTransferable, Sort sort) {
        return Mono.fromCallable(() -> query(pageNum, pageSize, searchParams, customerModelToDtoTransferable, sort)).subscribeOn(Schedulers.parallel());
    }

    @Override
    public Mono<Optional<DataGridPageDto<DTO>>> queryAsync(Integer pageNum, Integer pageSize, SearchParamBuilder searchParamBuilder, ModelToDtoTransferable<MODEL, DTO> customerModelToDtoTransferable, Sort sort) {
        return Mono.fromCallable(() -> query(pageNum, pageSize, searchParamBuilder, customerModelToDtoTransferable, sort)).subscribeOn(Schedulers.parallel());
    }

    @Override
    public Mono<Optional<List<DTO>>> findAllAsync(Iterable<ID> ids) {
        return Mono.fromCallable(() -> findAll(ids)).subscribeOn(Schedulers.parallel());
    }

    @Override
    public Mono<Optional<List<DTO>>> findAllAsync(Iterable<ID> ids, ModelToDtoTransferable<MODEL, DTO> modelToDtoTransferable) {
        return Mono.fromCallable(() -> findAll(ids, modelToDtoTransferable)).subscribeOn(Schedulers.parallel());
    }

    @Override
    public Mono<Optional<List<DTO>>> findAllAsync() {
        return Mono.fromCallable(() -> {
            Optional<List<DTO>> result = findAll();
            return result;
        }).subscribeOn(Schedulers.parallel());
    }

    @Override
    public Mono<Optional<List<DTO>>> findAllAsync(ModelToDtoTransferable<MODEL, DTO> modelToDtoTransferable) {
        return Mono.fromCallable(() -> findAll(modelToDtoTransferable)).subscribeOn(Schedulers.parallel());
    }

    @Override
    public Mono<Boolean> softDeleteAsync(DTO obj) {
        return Mono.fromCallable(() -> {
            softDelete(obj);
            return true;
        }).onErrorReturn(false).subscribeOn(Schedulers.parallel());
    }

    @Override
    public Mono<Boolean> softDeleteAsync(List<DTO> objs) {
        return Mono.fromCallable(() -> {
            softDelete(objs);
            return true;
        }).onErrorReturn(false).subscribeOn(Schedulers.parallel());
    }

    @Override
    public Mono<Long> countAsync(String searchParams) {
        return Mono.fromCallable(() -> count(searchParams)).subscribeOn(Schedulers.parallel());
    }
}
