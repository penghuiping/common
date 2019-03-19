package com.php25.common.jdbc.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Lists;
import com.php25.common.core.dto.DataGridPageDto;
import com.php25.common.core.service.BaseAsyncServiceImpl;
import com.php25.common.core.service.BaseService;
import com.php25.common.core.service.DtoToModelTransferable;
import com.php25.common.core.service.ModelToDtoTransferable;
import com.php25.common.core.service.SoftDeletable;
import com.php25.common.core.specification.SearchParam;
import com.php25.common.core.specification.SearchParamBuilder;
import com.php25.common.core.util.AssertUtil;
import com.php25.common.core.util.JsonUtil;
import com.php25.common.core.util.ReflectUtil;
import com.php25.common.jdbc.repository.BaseRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * service层 实现类基实现类，所有的service层类都应该继承这个类
 *
 * @author: penghuiping
 * @date: 2018/8/16 22:46
 */
public abstract class BaseServiceImpl<DTO, MODEL, ID extends Serializable> extends BaseAsyncServiceImpl<DTO,MODEL,ID> {
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
        AssertUtil.notNull(id, "id不能为null");
        AssertUtil.notNull(modelToDtoTransferable, "modelToDtoTransferable不能为null");
        try {
            DTO dto = dtoClass.newInstance();
            Optional<MODEL> model = baseRepository.findById(id);
            if (model.isPresent()) {
                modelToDtoTransferable.modelToDto(model.get(), dto);
                return Optional.ofNullable(dto);
            } else {
                return Optional.empty();
            }
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException("出错啦!", e);
        }
    }

    @Override
    public Optional<DTO> save(DTO obj) {
        return save(obj, BeanUtils::copyProperties, BeanUtils::copyProperties);
    }

    @Override
    public Optional<DTO> save(DTO obj, DtoToModelTransferable<MODEL, DTO> dtoToModelTransferable, ModelToDtoTransferable<MODEL, DTO> modelToDtoTransferable) {
        AssertUtil.notNull(obj, "dto不能为null");
        AssertUtil.notNull(dtoToModelTransferable, "dtoToModelTransferable不能为null");
        AssertUtil.notNull(modelToDtoTransferable, "modelToDtoTransferable不能为null");
        try {
            MODEL a = modelClass.newInstance();
            dtoToModelTransferable.dtoToModel(obj, a);
            DTO dto = dtoClass.newInstance();
            a = baseRepository.save(a);
            modelToDtoTransferable.modelToDto(a, dto);
            return Optional.ofNullable(dto);
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException("出错啦!", e);
        }
    }

    @Override
    public void save(Iterable<DTO> objs) {
        save(objs, BeanUtils::copyProperties);
    }

    @Override
    public void save(Iterable<DTO> objs, DtoToModelTransferable<MODEL, DTO> dtoToModelTransferable) {
        AssertUtil.notEmpty((List<DTO>) objs, "dtos至少需要包含一个元素");
        AssertUtil.notNull(dtoToModelTransferable, "dtoToModelTransferable不能为null");
        List<MODEL> models = (Lists.newArrayList(objs)).stream().map(dto -> {
            try {
                MODEL model = modelClass.newInstance();
                dtoToModelTransferable.dtoToModel(dto, model);
                return model;
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException("出错啦!", e);
            }
        }).filter(Objects::nonNull).collect(Collectors.toList());
        baseRepository.saveAll(models);
    }

    @Override
    public void delete(DTO obj) {
        AssertUtil.notNull(obj, "dto不能为null");
        try {
            MODEL a = modelClass.newInstance();
            BeanUtils.copyProperties(obj, a);
            baseRepository.delete(a);
        } catch (Exception e) {
            throw new RuntimeException("出错啦!", e);
        }
    }

    @Override
    public void delete(List<DTO> objs) {
        AssertUtil.notEmpty(objs, "dtos至少需要包含一个元素");
        List<MODEL> models = objs.stream().map(dto -> {
            try {
                MODEL a = modelClass.newInstance();
                BeanUtils.copyProperties(dto, a);
                return a;
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException("出错啦!", e);
            }
        }).collect(Collectors.toList());
        baseRepository.deleteAll(models);
    }

    @Override
    public Optional<DataGridPageDto<DTO>> query(Integer pageNum, Integer pageSize, String searchParams) {
        return query(pageNum, pageSize, searchParams, BeanUtils::copyProperties, null);
    }

    @Override
    public Optional<DataGridPageDto<DTO>> query(Integer pageNum, Integer pageSize, String searchParams, Sort.Direction direction, String property) {
        return query(pageNum, pageSize, searchParams, BeanUtils::copyProperties
                , direction, property);
    }

    @Override
    public Optional<DataGridPageDto<DTO>> query(Integer pageNum, Integer pageSize, String searchParams, ModelToDtoTransferable<MODEL, DTO> modelToDtoTransferable, Sort.Direction direction, String property) {
        AssertUtil.notNull(direction, "direction不能为null");
        AssertUtil.notNull(property, "property不能为null");
        Sort.Order order = new Sort.Order(direction, property);
        Sort sort = Sort.by(order);
        return query(pageNum, pageSize, searchParams, modelToDtoTransferable, sort);
    }

    @Override
    public Optional<DataGridPageDto<DTO>> query(Integer pageNum, Integer pageSize, String searchParams, ModelToDtoTransferable<MODEL, DTO> modelToDtoTransferable, Sort sort) {
        AssertUtil.notNull(pageNum, "pageNum不能为null");
        AssertUtil.notNull(pageSize, "pageSize不能为null");
        AssertUtil.hasText(searchParams, "searchParams不能为空,如没有搜索条件请使用[]");
        AssertUtil.notNull(modelToDtoTransferable, "modelToDtoTransferable不能为null");
        PageRequest pageRequest = null;
        Page<MODEL> modelPage = null;
        List<MODEL> adminUserModelList = null;

        List<SearchParam> searchParams1 = JsonUtil.fromJson(searchParams, new TypeReference<List<SearchParam>>() {
        });
        SearchParamBuilder searchParamBuilder = new SearchParamBuilder().append(searchParams1);

        if (-1 == pageNum) {
            adminUserModelList = baseRepository.findAll(searchParamBuilder);
        } else {
            if (null != sort) {
                pageRequest = PageRequest.of(pageNum - 1, pageSize, sort);
            } else {
                pageRequest = PageRequest.of(pageNum - 1, pageSize);
            }

            modelPage = baseRepository.findAll(searchParamBuilder, pageRequest);
            adminUserModelList = modelPage.getContent();
        }

        if (null == adminUserModelList) {
            adminUserModelList = Lists.newArrayList();
        }
        List<DTO> adminUserDtoList = adminUserModelList.stream().map(model -> {
            try {
                DTO dto = dtoClass.newInstance();
                modelToDtoTransferable.modelToDto(model, dto);
                return dto;
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException("出错啦!", e);
            }
        }).collect(Collectors.toList());

        PageImpl<DTO> dtoPage = null;
        if (-1 == pageNum) {
            dtoPage = new PageImpl<DTO>(adminUserDtoList, Pageable.unpaged(), adminUserModelList.size());
        } else {
            dtoPage = new PageImpl<DTO>(adminUserDtoList, pageRequest, modelPage.getTotalElements());
        }
        return Optional.ofNullable(toDataGridPageDto(dtoPage));
    }

    @Override
    public Optional<DataGridPageDto<DTO>> query(Integer pageNum, Integer pageSize, SearchParamBuilder searchParamBuilder, ModelToDtoTransferable<MODEL, DTO> modelToDtoTransferable, Sort sort) {
        AssertUtil.notNull(pageNum, "pageNum不能为null");
        AssertUtil.notNull(pageSize, "pageSize不能为null");
        AssertUtil.notNull(searchParamBuilder, "searchParamBuilder不能为null");
        AssertUtil.notNull(modelToDtoTransferable, "modelToDtoTransferable不能为null");

        PageRequest pageRequest = null;
        Page<MODEL> modelPage = null;
        List<MODEL> adminUserModelList = null;

        if (-1 == pageNum) {
            adminUserModelList = baseRepository.findAll(searchParamBuilder);
        } else {
            if (null != sort) {
                pageRequest = PageRequest.of(pageNum - 1, pageSize, sort);
            } else {
                pageRequest = PageRequest.of(pageNum - 1, pageSize);
            }
            modelPage = baseRepository.findAll(searchParamBuilder, pageRequest);
            adminUserModelList = modelPage.getContent();
        }

        if (null == adminUserModelList) {
            adminUserModelList = Lists.newArrayList();
        }
        List<DTO> adminUserDtoList = adminUserModelList.stream().map(model -> {
            try {
                DTO dto = dtoClass.newInstance();
                modelToDtoTransferable.modelToDto(model, dto);
                return dto;
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException("出错啦!", e);
            }
        }).collect(Collectors.toList());

        PageImpl<DTO> dtoPage = null;
        if (-1 == pageNum) {
            dtoPage = new PageImpl<DTO>(adminUserDtoList, Pageable.unpaged(), adminUserModelList.size());
        } else {
            dtoPage = new PageImpl<DTO>(adminUserDtoList, pageRequest, modelPage.getTotalElements());
        }
        return Optional.ofNullable(toDataGridPageDto(dtoPage));
    }

    @Override
    public Optional<List<DTO>> findAll(Iterable<ID> ids) {
        return findAll(ids, BeanUtils::copyProperties);
    }

    @Override
    public Optional<List<DTO>> findAll(Iterable<ID> ids, ModelToDtoTransferable<MODEL, DTO> modelToDtoTransferable) {
        AssertUtil.notEmpty((List<ID>) ids, "ids集合至少需要包含一个元素");
        AssertUtil.notNull(modelToDtoTransferable, "modelToDtoTransferable不能为null");
        List<MODEL> result = Lists.newArrayList(baseRepository.findAllById(ids));
        return Optional.ofNullable(result.stream()
                .map(model -> {
                    try {
                        DTO dto = dtoClass.newInstance();
                        modelToDtoTransferable.modelToDto(model, dto);
                        return dto;
                    } catch (InstantiationException | IllegalAccessException e) {
                        throw new RuntimeException("出错啦!", e);
                    }
                }).collect(Collectors.toList()));
    }

    @Override
    public Optional<List<DTO>> findAll() {
        return findAll((ModelToDtoTransferable<MODEL, DTO>) BeanUtils::copyProperties);
    }

    @Override
    public Optional<List<DTO>> findAll(ModelToDtoTransferable<MODEL, DTO> modelToDtoTransferable) {
        AssertUtil.notNull(modelToDtoTransferable, "modelToDtoTransferable不能为null");
        List<MODEL> result = Lists.newArrayList(baseRepository.findAll());
        return Optional.ofNullable(result.stream()
                .map(model -> {
                    try {
                        DTO dto = dtoClass.newInstance();
                        modelToDtoTransferable.modelToDto(model, dto);
                        return dto;
                    } catch (InstantiationException | IllegalAccessException e) {
                        throw new RuntimeException("出错啦!", e);
                    }
                }).collect(Collectors.toList()));
    }

    @Override
    public void softDelete(DTO obj) {
        AssertUtil.notNull(obj, "dto不能为null");
        try {
            //判断是否可以进行软删除
            Field field = obj.getClass().getDeclaredField("enable");
            if (null != field) {
                //可以进行软删除
                ReflectUtil.getMethod(obj.getClass(), "setEnable", new Class[]{Integer.class}).invoke(obj, 2);
                this.save(obj);
            }
        } catch (NoSuchFieldException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("此对象不支持软删除!", e);
        }
    }

    @Override
    public void softDelete(List<DTO> objs) {
        AssertUtil.notEmpty(objs, "dtos集合至少需要包含一个元素");
        try {
            //判断是否可以进行软删除
            Field field = objs.get(0).getClass().getDeclaredField("enable");
            if (null != field) {
                for (DTO obj : objs) {
                    //可以进行软删除
                    ReflectUtil.getMethod(obj.getClass(), "setEnable", new Class[]{Integer.class}).invoke(obj, 2);
                }
                this.save(objs);
            }
        } catch (NoSuchFieldException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("此对象不支持软删除!", e);
        }
    }

    @Override
    public Long count(String searchParams) {
        AssertUtil.hasText(searchParams, "searchParams不能为空,如没有搜索条件请使用[]");
        List<SearchParam> searchParams1 = JsonUtil.fromJson(searchParams, new TypeReference<List<SearchParam>>() {
        });
        SearchParamBuilder searchParamBuilder = new SearchParamBuilder().append(searchParams1);
        return baseRepository.count(searchParamBuilder);
    }
}
