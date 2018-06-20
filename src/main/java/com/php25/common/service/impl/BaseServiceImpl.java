package com.php25.common.service.impl;

import com.google.common.collect.Lists;
import com.php25.common.dto.DataGridPageDto;
import com.php25.common.repository.BaseRepository;
import com.php25.common.service.BaseService;
import com.php25.common.service.DtoToModelTransferable;
import com.php25.common.service.ModelToDtoTransferable;
import com.php25.common.service.SoftDeletable;
import com.php25.common.specification.BaseSpecsFactory;
import com.php25.common.specification.SearchParamBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by penghuiping on 16/8/12.
 */
public abstract class BaseServiceImpl<DTO, MODEL, ID extends Serializable> implements BaseService<DTO, MODEL, ID>, SoftDeletable<DTO> {
    private static Logger logger = LoggerFactory.getLogger(BaseServiceImpl.class);

    protected BaseRepository<MODEL, ID> baseRepository;

    private Class<DTO> dtoClass;

    private Class<MODEL> modelClass;

    public BaseServiceImpl() {
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
        return findOne(id, BeanUtils::copyProperties);
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
            logger.error("出错啦!", e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<DTO> save(DTO obj) {
        return save(obj, BeanUtils::copyProperties, BeanUtils::copyProperties);
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
            logger.error("出错啦!", e);
            return Optional.empty();
        }
    }

    @Override
    public void save(Iterable<DTO> objs) {
        save(objs, BeanUtils::copyProperties);
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
        }).filter(Objects::nonNull).collect(Collectors.toList());
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
        return query(pageNum, pageSize, searchParams, Sort.Direction.DESC, "createTime");
    }

    @Override
    public Optional<DataGridPageDto<DTO>> query(Integer pageNum, Integer pageSize, String searchParams, Sort.Direction direction, String property) {
        return query(pageNum, pageSize, searchParams, BeanUtils::copyProperties
                , direction, property);
    }


    @Override
    public Optional<DataGridPageDto<DTO>> query(Integer pageNum, Integer pageSize, String searchParams, ModelToDtoTransferable<MODEL, DTO> modelToDtoTransferable, Sort.Direction direction, String property) {
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
            adminUserModelList = baseRepository.findAll(BaseSpecsFactory.getJpaInstance().getSpecs(searchParams));
        } else {
            pageRequest = new PageRequest(pageNum - 1, pageSize, sort);
            modelPage = baseRepository.findAll(BaseSpecsFactory.getJpaInstance().getSpecs(searchParams), pageRequest);
            adminUserModelList = modelPage.getContent();
        }

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
        Assert.notNull(searchParamBuilder, "searchParamBuilder不能为null");
        Assert.notNull(modelToDtoTransferable, "modelToDtoTransferable不能为null");
        Assert.notNull(sort, "sort不能为null");

        PageRequest pageRequest = null;
        Page<MODEL> modelPage = null;
        List<MODEL> adminUserModelList = null;

        if (-1 == pageNum) {
            adminUserModelList = baseRepository.findAll(BaseSpecsFactory.getJpaInstance().getSpecs(searchParamBuilder));
        } else {
            pageRequest = new PageRequest(pageNum - 1, pageSize, sort);
            modelPage = baseRepository.findAll(BaseSpecsFactory.getJpaInstance().getSpecs(searchParamBuilder), pageRequest);
            adminUserModelList = modelPage.getContent();
        }

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

        PageImpl<DTO> dtoPage = null;
        if (-1 == pageNum) {
            dtoPage = new PageImpl<DTO>(adminUserDtoList, null, adminUserModelList.size());
        } else {
            dtoPage = new PageImpl<DTO>(adminUserDtoList, null, modelPage.getTotalElements());
        }
        return Optional.ofNullable(toDataGridPageDto(dtoPage));
    }

    @Override
    public Optional<List<DTO>> findAll(Iterable<ID> ids) {
        return findAll(ids, BeanUtils::copyProperties);
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
        return findAll((ModelToDtoTransferable<MODEL, DTO>) BeanUtils::copyProperties);
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
                        logger.error("出错啦!", e);
                        return null;
                    }
                }).collect(Collectors.toList()));
    }

    @Override
    public void softDelete(DTO obj) {
        Assert.notNull(obj, "dto不能为null");
        try {
            //判断是否可以进行软删除
            Field field = obj.getClass().getDeclaredField("enable");
            if (null != field) {
                //可以进行软删除
                Method m = obj.getClass().getDeclaredMethod("setEnable", Integer.class);
                m.invoke(obj, 2);
                this.save(obj);
            }
        } catch (Exception e) {
            logger.error("此对象不支持软删除!", e);
        }
    }

    @Override
    public void softDelete(List<DTO> objs) {
        Assert.notEmpty(objs, "dtos集合至少需要包含一个元素");
        try {
            //判断是否可以进行软删除
            Field field = objs.get(0).getClass().getDeclaredField("enable");
            if (null != field) {
                for (DTO obj : objs) {
                    //可以进行软删除
                    Method m = obj.getClass().getDeclaredMethod("setEnable", Integer.class);
                    m.invoke(obj, 2);
                }
                this.save(objs);
            }
        } catch (Exception e) {
            logger.error("此对象不支持软删除!", e);
        }
    }

    @Override
    public Long count(String searchParams) {
        Assert.hasText(searchParams, "searchParams不能为空,如没有搜索条件请使用[]");
        Long result = baseRepository.count(BaseSpecsFactory.getJpaInstance().getSpecs(searchParams));
        return result;
    }

}