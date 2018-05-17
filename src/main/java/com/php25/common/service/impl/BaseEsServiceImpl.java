package com.php25.common.service.impl;

import com.google.common.collect.Lists;
import com.php25.common.dto.DataGridPageDto;
import com.php25.common.repository.BaseEsRepository;
import com.php25.common.service.BaseService;
import com.php25.common.service.DtoToModelTransferable;
import com.php25.common.service.ModelToDtoTransferable;
import com.php25.common.specification.BaseSpecsFactory;
import com.php25.common.specification.SearchParamBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by penghuiping on 16/8/12.
 */
public abstract class BaseEsServiceImpl<DTO, MODEL, ID extends Serializable> implements BaseService<DTO, MODEL, ID> {

    private static Logger logger = LoggerFactory.getLogger(BaseEsServiceImpl.class);

    protected BaseEsRepository<MODEL, ID> baseRepository;

    private Class<DTO> dtoClass;

    private Class<MODEL> modelClass;

    public BaseEsServiceImpl() {
        Type genType = getClass().getGenericSuperclass();
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
        dtoClass = (Class) params[0];
        modelClass = (Class) params[1];
    }

    protected <T> DataGridPageDto<T> toDataGridPageDto(Page<T> page) {
        DataGridPageDto<T> dataGridPageDto = new DataGridPageDto<T>();
        dataGridPageDto.setData(page.getContent());
        dataGridPageDto.setRecordsTotal(page.getTotalElements());
        dataGridPageDto.setRecordsFiltered(page.getTotalElements());
        return dataGridPageDto;
    }

    @Override
    public Optional<DTO> findOne(ID id) {
        Assert.notNull(id, "id不能为null");
        return findOne(id, (model, dto) -> BeanUtils.copyProperties(model, dto));
    }

    @Override
    public Optional<DTO> findOne(ID id, ModelToDtoTransferable<MODEL, DTO> modelToDtoTransferable) {
        Assert.notNull(id, "id不能为null");
        Assert.notNull(modelToDtoTransferable, "modelToDtoTransferable不能为null");
        try {
            DTO dto = dtoClass.newInstance();
            MODEL model = baseRepository.findOne(id);
            modelToDtoTransferable.modelToDto(model, dto);
            return Optional.ofNullable(dto);
        } catch (Exception e) {
            logger.error("出错啦！", e);
            return null;
        }
    }

    @Override
    public Optional<DTO> save(DTO obj) {
        Assert.notNull(obj, "dto不能为null");
        return save(obj, (dto, model) -> BeanUtils.copyProperties(dto, model), (model, dto) -> BeanUtils.copyProperties(model, dto));
    }

    @Override
    public Optional<DTO> save(DTO obj, DtoToModelTransferable<MODEL, DTO> dtoToModelTransferable, ModelToDtoTransferable<MODEL, DTO> modelToDtoTransferable) {
        Assert.notNull(obj, "dto不能为null");
        Assert.notNull(dtoToModelTransferable, "dtoToModelTransferable不能为null");
        Assert.notNull(modelToDtoTransferable, "modelToDtoTransferable不能为null");
        try {
            MODEL a = modelClass.newInstance();
            dtoToModelTransferable.dtoToModel(obj, a);
            DTO dto = dtoClass.newInstance();
            a = baseRepository.save(a);
            modelToDtoTransferable.modelToDto(a, dto);
            return Optional.ofNullable(dto);
        } catch (Exception e) {
            logger.error("出错啦！", e);
            return null;
        }
    }

    @Override
    public void save(Iterable<DTO> objs) {
        Assert.notEmpty((List<DTO>) objs, "dtos至少需要包含一个元素");
        save(objs, (dto, model) -> BeanUtils.copyProperties(dto, model));
    }

    @Override
    public void save(Iterable<DTO> objs, DtoToModelTransferable<MODEL, DTO> dtoToModelTransferable) {
        Assert.notEmpty((List<DTO>) objs, "dtos至少需要包含一个元素");
        Assert.notNull(dtoToModelTransferable, "dtoToModelTransferable不能为null");
        List<MODEL> models = (Lists.newArrayList(objs)).stream().map(dto -> {
            try {
                MODEL model = modelClass.newInstance();
                dtoToModelTransferable.dtoToModel(dto, model);
                return model;
            } catch (Exception e) {
                return null;
            }
        }).collect(Collectors.toList());
        baseRepository.save(models);
    }

    @Override
    public void delete(DTO obj) {
        Assert.notNull(obj, "dto不能为null");
        try {
            MODEL a = modelClass.newInstance();
            BeanUtils.copyProperties(obj, a);
            baseRepository.delete(a);
        } catch (Exception e) {
            logger.error("出错啦!", e);
        }
    }

    @Override
    public void delete(List<DTO> objs) {
        Assert.notEmpty(objs, "dtos至少需要包含一个元素");
        List<MODEL> models = objs.stream().map(dto -> {
            try {
                MODEL a = modelClass.newInstance();
                BeanUtils.copyProperties(dto, a);
                return a;
            } catch (Exception e) {
                return null;
            }
        }).collect(Collectors.toList());
        baseRepository.delete(models);
    }

    @Override
    public Optional<DataGridPageDto<DTO>> query(Integer pageNum, Integer pageSize, String searchParams) {
        Assert.notNull(pageNum, "pageNum不能为null");
        Assert.notNull(pageSize, "pageSize不能为null");
        Assert.hasText(searchParams, "searchParams不能为空,如没有搜索条件请使用[]");
        return query(pageNum, pageSize, searchParams, Sort.Direction.DESC, "createTime");
    }

    @Override
    public Optional<DataGridPageDto<DTO>> query(Integer pageNum, Integer pageSize, String searchParams, Sort.Direction direction, String property) {
        Assert.notNull(pageNum, "pageNum不能为null");
        Assert.notNull(pageSize, "pageSize不能为null");
        Assert.hasText(searchParams, "searchParams不能为空,如没有搜索条件请使用[]");
        Assert.notNull(direction, "direction不能为null");
        Assert.notNull(property, "property不能为null");
        return query(pageNum, pageSize, searchParams, (model, dto) -> BeanUtils.copyProperties(model, dto)
                , direction, property);
    }


    @Override
    public Optional<DataGridPageDto<DTO>> query(Integer pageNum, Integer pageSize, String searchParams, ModelToDtoTransferable<MODEL, DTO> modelToDtoTransferable, Sort.Direction direction, String property) {
        Assert.notNull(pageNum, "pageNum不能为null");
        Assert.notNull(pageSize, "pageSize不能为null");
        Assert.hasText(searchParams, "searchParams不能为空");
        Assert.notNull(modelToDtoTransferable, "modelToDtoTransferable不能为null");
        Assert.notNull(direction, "direction不能为null");
        Assert.notNull(property, "property不能为null");
        Sort.Order order = new Sort.Order(direction, property);
        Sort sort = new Sort(order);
        return query(pageNum, pageSize, searchParams, modelToDtoTransferable, sort);
    }

    @Override
    public Optional<DataGridPageDto<DTO>> query(Integer pageNum, Integer pageSize, String searchParams, ModelToDtoTransferable<MODEL, DTO> modelToDtoTransferable, Sort sort) {
        Assert.notNull(pageNum, "pageNum不能为null");
        Assert.notNull(pageSize, "pageSize不能为null");
        Assert.hasText(searchParams, "searchParams不能为空,如没有搜索条件请使用[]");
        Assert.notNull(modelToDtoTransferable, "modelToDtoTransferable不能为null");
        Assert.notNull(sort, "sort不能为null");

        PageRequest pageRequest = null;
        Page<MODEL> modelPage = null;
        List<MODEL> adminUserModelList = null;

        if (-1 == pageNum) {
            adminUserModelList = (List<MODEL>) baseRepository.search(BaseSpecsFactory.getEsInstance().getSpecs(searchParams));
        } else {
            pageRequest = new PageRequest(pageNum - 1, pageSize, sort);
            NativeSearchQueryBuilder searchQueryBuilder = new NativeSearchQueryBuilder();
            searchQueryBuilder.withQuery(BaseSpecsFactory.getEsInstance().getSpecs(searchParams))
                    .withPageable(pageRequest)
                    .withSort(SortBuilders.scoreSort()
                            .order(SortOrder.DESC));
            modelPage = baseRepository.search(searchQueryBuilder.build());
            adminUserModelList = modelPage.getContent();
        }

        if (null == adminUserModelList) adminUserModelList = Lists.newArrayList();
        List<DTO> adminUserDtoList = adminUserModelList.stream().map(model -> {
            try {
                DTO dto = dtoClass.newInstance();
                modelToDtoTransferable.modelToDto(model, dto);
                return dto;
            } catch (Exception e) {
                logger.error("出错啦！", e);
                return null;
            }
        }).collect(Collectors.toList());

        PageImpl<DTO> dtoPage = null;
        if (-1 == pageNum) {
            dtoPage = new PageImpl<DTO>(adminUserDtoList, null, adminUserModelList.size());
        } else {
            dtoPage = new PageImpl<DTO>(adminUserDtoList, null, modelPage.getTotalElements());
        }

        return Optional.ofNullable(toDataGridPageDto(dtoPage));
    }

    @Override
    public Optional<DataGridPageDto<DTO>> query(Integer pageNum, Integer pageSize, SearchParamBuilder searchParamBuilder, ModelToDtoTransferable<MODEL, DTO> modelToDtoTransferable, Sort sort) {
        Assert.notNull(pageNum, "pageNum不能为null");
        Assert.notNull(pageSize, "pageSize不能为null");
        Assert.notNull(searchParamBuilder, "SearchParamBuilder不能为null");
        Assert.notNull(modelToDtoTransferable, "modelToDtoTransferable不能为null");
        Assert.notNull(sort, "sort不能为null");

        PageRequest pageRequest = null;
        Page<MODEL> modelPage = null;
        List<MODEL> adminUserModelList = null;

        if (-1 == pageNum) {
            adminUserModelList = (List<MODEL>) baseRepository.search(BaseSpecsFactory.getEsInstance().getSpecs(searchParamBuilder));
        } else {
            pageRequest = new PageRequest(pageNum - 1, pageSize, sort);
            NativeSearchQueryBuilder searchQueryBuilder = new NativeSearchQueryBuilder();
            searchQueryBuilder.withQuery(BaseSpecsFactory.getEsInstance().getSpecs(searchParamBuilder))
                    .withPageable(pageRequest)
                    .withSort(SortBuilders.scoreSort()
                            .order(SortOrder.DESC));
            modelPage = baseRepository.search(searchQueryBuilder.build());
            adminUserModelList = modelPage.getContent();
        }

        if (null == adminUserModelList) adminUserModelList = Lists.newArrayList();
        List<DTO> adminUserDtoList = adminUserModelList.stream().map(model -> {
            try {
                DTO dto = dtoClass.newInstance();
                modelToDtoTransferable.modelToDto(model, dto);
                return dto;
            } catch (Exception e) {
                logger.error("出错啦！", e);
                return null;
            }
        }).collect(Collectors.toList());

        PageImpl<DTO> dtoPage = null;
        if (-1 == pageNum) {
            dtoPage = new PageImpl<DTO>(adminUserDtoList, null, adminUserModelList.size());
        } else {
            dtoPage = new PageImpl<DTO>(adminUserDtoList, null, modelPage.getTotalElements());
        }

        return Optional.ofNullable(toDataGridPageDto(dtoPage));
    }

    public Optional<DataGridPageDto<DTO>> query(SearchQuery searchQuery, ModelToDtoTransferable<MODEL, DTO> modelToDtoTransferable) {
        Assert.notNull(searchQuery, "searchQuery不能为null");
        Assert.notNull(modelToDtoTransferable, "modelToDtoTransferable不能为null");
        Page<MODEL> modelPage = baseRepository.search(searchQuery);
        List<MODEL> adminUserModelList = modelPage.getContent();
        if (null == adminUserModelList) adminUserModelList = Lists.newArrayList();
        List<DTO> adminUserDtoList = adminUserModelList.stream().map(model -> {
            try {
                DTO dto = dtoClass.newInstance();
                modelToDtoTransferable.modelToDto(model, dto);
                return dto;
            } catch (Exception e) {
                logger.error("出错啦!", e);
                return null;
            }
        }).collect(Collectors.toList());
        PageImpl<DTO> dtoPage = new PageImpl<DTO>(adminUserDtoList, null, adminUserModelList.size());
        return Optional.ofNullable(toDataGridPageDto(dtoPage));
    }

    @Override
    public Optional<List<DTO>> findAll(Iterable<ID> ids) {
        Assert.notEmpty((List<ID>) ids, "ids集合至少需要包含一个元素");
        return findAll(ids, (model, dto) -> BeanUtils.copyProperties(model, dto));
    }

    @Override
    public Optional<List<DTO>> findAll(Iterable<ID> ids, ModelToDtoTransferable<MODEL, DTO> modelToDtoTransferable) {
        Assert.notEmpty((List<ID>) ids, "ids集合至少需要包含一个元素");
        Assert.notNull(modelToDtoTransferable, "modelToDtoTransferable不能为null");
        List<MODEL> result = Lists.newArrayList(baseRepository.findAll(ids));
        return Optional.ofNullable(result.stream()
                .map(model -> {
                    try {
                        DTO dto = dtoClass.newInstance();
                        modelToDtoTransferable.modelToDto(model, dto);
                        return dto;
                    } catch (Exception e) {
                        logger.error("出错啦!", e);
                        return null;
                    }
                }).collect(Collectors.toList()));
    }

    @Override
    public Optional<List<DTO>> findAll() {
        return findAll((model, dto) -> BeanUtils.copyProperties(model, dto));
    }

    @Override
    public Optional<List<DTO>> findAll(ModelToDtoTransferable<MODEL, DTO> modelToDtoTransferable) {
        Assert.notNull(modelToDtoTransferable, "modelToDtoTransferable不能为null");
        List<MODEL> result = Lists.newArrayList(baseRepository.findAll());
        return Optional.ofNullable(result.stream()
                .map(model -> {
                    try {
                        DTO dto = dtoClass.newInstance();
                        modelToDtoTransferable.modelToDto(model, dto);
                        return dto;
                    } catch (Exception e) {
                        logger.error("出错啦！", e);
                        return null;
                    }
                }).collect(Collectors.toList()));
    }


    @Override
    public Long count(String searchParams) {
        Assert.hasText(searchParams, "searchParams不能为空,如没有搜索条件请使用[]");
        return new Long(Lists.newArrayList(baseRepository.search(BaseSpecsFactory.getEsInstance().getSpecs(searchParams))).size());
    }


}