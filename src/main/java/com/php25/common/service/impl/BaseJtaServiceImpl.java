package com.php25.common.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.php25.common.dto.DataGridPageDto;
import com.php25.common.service.BaseService;
import com.php25.common.service.DtoToModelTransferable;
import com.php25.common.service.ModelToDtoTransferable;
import com.php25.common.service.SoftDeletable;
import com.php25.common.specification.BaseSpecs;
import com.php25.common.specification.Operator;
import com.php25.common.specification.SearchParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.SynchronizationType;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by penghuiping on 16/8/12.
 */
@Transactional
public abstract class BaseJtaServiceImpl<DTO, MODEL, ID extends Serializable> implements BaseService<DTO, MODEL, ID>, SoftDeletable<DTO> {
    private static Logger logger = LoggerFactory.getLogger(BaseJtaServiceImpl.class);

    protected EntityManagerFactory entityManagerFactory;

    private Class<DTO> dtoClass;

    private Class<MODEL> modelClass;

    @Autowired
    private ObjectMapper objectMapper;

    public BaseJtaServiceImpl(EntityManagerFactory entityManagerFactory) {
        Type genType = getClass().getGenericSuperclass();
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
        dtoClass = (Class) params[0];
        modelClass = (Class) params[1];
        this.entityManagerFactory = entityManagerFactory;
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
            EntityManager entityManager = entityManagerFactory.createEntityManager(SynchronizationType.SYNCHRONIZED);
            DTO dto = dtoClass.newInstance();
            MODEL model = entityManager.find(modelClass, id);
            modelToDtoTransferable.modelToDto(model, dto);
            return Optional.ofNullable(dto);
        } catch (Exception e) {
            logger.error("出错啦!", e);
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
            EntityManager entityManager = entityManagerFactory.createEntityManager(SynchronizationType.SYNCHRONIZED);
            MODEL a = modelClass.newInstance();
            dtoToModelTransferable.dtoToModel(obj, a);
            DTO dto = dtoClass.newInstance();
            entityManager.persist(a);
            modelToDtoTransferable.modelToDto(a, dto);
            return Optional.ofNullable(dto);
        } catch (Exception e) {
            logger.error("出错啦!", e);
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
        EntityManager entityManager = entityManagerFactory.createEntityManager(SynchronizationType.SYNCHRONIZED);
        List<MODEL> models = (Lists.newArrayList(objs)).stream().map(dto -> {
            try {
                MODEL model = modelClass.newInstance();
                dtoToModelTransferable.dtoToModel(dto, model);
                return model;
            } catch (Exception e) {
                return null;
            }
        }).collect(Collectors.toList());
        models.forEach(entityManager::persist);
    }

    @Override
    public void delete(DTO obj) {
        Assert.notNull(obj, "dto不能为null");
        EntityManager entityManager = entityManagerFactory.createEntityManager(SynchronizationType.SYNCHRONIZED);
        try {
            MODEL a = modelClass.newInstance();
            BeanUtils.copyProperties(obj, a);
            entityManager.remove(a);
        } catch (Exception e) {
            logger.error("出错啦!", e);
        }
    }

    @Override
    public void delete(List<DTO> objs) {
        Assert.notEmpty(objs, "dtos至少需要包含一个元素");
        EntityManager entityManager = entityManagerFactory.createEntityManager(SynchronizationType.SYNCHRONIZED);
        List<MODEL> models = objs.stream().map(dto -> {
            try {
                MODEL a = modelClass.newInstance();
                BeanUtils.copyProperties(dto, a);
                return a;
            } catch (Exception e) {
                return null;
            }
        }).collect(Collectors.toList());
        models.forEach(a -> {
            entityManager.remove(a);
        });

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
        EntityManager entityManager = entityManagerFactory.createEntityManager(SynchronizationType.SYNCHRONIZED);
        PageRequest pageRequest = null;
        Page<MODEL> modelPage = null;
        List<MODEL> adminUserModelList = null;


        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<MODEL> cq = cb.createQuery(modelClass);
        Root<MODEL> root = cq.from(modelClass);

        sort.iterator().forEachRemaining(order -> {
            Order order1 = null;
            switch (order.getDirection()) {
                case ASC:
                    cb.asc(root.get(order.getProperty()));
                    break;
                case DESC:
                    cb.desc(root.get(order.getProperty()));
                    break;
            }
            cq.orderBy(order1);
        });

        BaseSpecs.<MODEL>getSpecs(searchParams).toPredicate(root, cq, cb);
        if (-1 == pageNum) {
            adminUserModelList = entityManager.createQuery(cq).getResultList();
        } else {
            pageRequest = new PageRequest(pageNum - 1, pageSize, sort);
            adminUserModelList = entityManager.createQuery(cq).setFirstResult(pageRequest.getOffset()).setMaxResults(pageRequest.getPageSize()).getResultList();
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
        Assert.notEmpty((List<ID>) ids, "ids集合至少需要包含一个元素");
        return findAll(ids, (model, dto) -> BeanUtils.copyProperties(model, dto));
    }

    @Override
    public Optional<List<DTO>> findAll(Iterable<ID> ids, ModelToDtoTransferable<MODEL, DTO> modelToDtoTransferable) {
        Assert.notEmpty((List<ID>) ids, "ids集合至少需要包含一个元素");
        Assert.notNull(modelToDtoTransferable, "modelToDtoTransferable不能为null");
        EntityManager entityManager = entityManagerFactory.createEntityManager(SynchronizationType.SYNCHRONIZED);

        List<ID> ids1 = Lists.newArrayList(ids);
        SearchParam searchParam = new SearchParam();
        searchParam.setFieldName("id");
        searchParam.setOperator(Operator.IN.name());

        try {
            searchParam.setValue(objectMapper.writeValueAsString(ids));
            String searchParams = objectMapper.writeValueAsString(Lists.newArrayList(searchParam));
            Optional<DataGridPageDto<DTO>> optionalDtoDataGridPageDto = this.query(-1, 1, searchParams, modelToDtoTransferable, Sort.Direction.DESC, "id");
            return Optional.ofNullable(optionalDtoDataGridPageDto.isPresent() ? optionalDtoDataGridPageDto.get().getData() : null);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }


    }

    @Override
    public Optional<List<DTO>> findAll() {
        return findAll((model, dto) -> BeanUtils.copyProperties(model, dto));
    }

    @Override
    public Optional<List<DTO>> findAll(ModelToDtoTransferable<MODEL, DTO> modelToDtoTransferable) {
        Assert.notNull(modelToDtoTransferable, "modelToDtoTransferable不能为null");
        Optional<DataGridPageDto<DTO>> optionalDtoDataGridPageDto = this.query(-1, 1, "[]", modelToDtoTransferable, Sort.Direction.DESC, "id");
        return Optional.ofNullable(optionalDtoDataGridPageDto.isPresent() ? optionalDtoDataGridPageDto.get().getData() : null);
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
        EntityManager entityManager = entityManagerFactory.createEntityManager(SynchronizationType.SYNCHRONIZED);
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<MODEL> cq = cb.createQuery(modelClass);
        Root<MODEL> root = cq.from(modelClass);
        BaseSpecs.<MODEL>getSpecs(searchParams).toPredicate(root, cq, cb);
        return new Long(entityManager.createQuery(cq).getResultList().size());
    }

}