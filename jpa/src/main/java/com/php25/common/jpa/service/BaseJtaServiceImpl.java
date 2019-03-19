package com.php25.common.jpa.service;

import com.google.common.collect.Lists;
import com.php25.common.core.dto.DataGridPageDto;
import com.php25.common.core.service.BaseAsyncServiceImpl;
import com.php25.common.core.service.DtoToModelTransferable;
import com.php25.common.core.service.ModelToDtoTransferable;
import com.php25.common.core.specification.BaseSpecsFactory;
import com.php25.common.core.specification.Operator;
import com.php25.common.core.specification.SearchParam;
import com.php25.common.core.specification.SearchParamBuilder;
import com.php25.common.core.util.AssertUtil;
import com.php25.common.core.util.JsonUtil;
import com.php25.common.core.util.ReflectUtil;
import com.php25.common.jpa.specification.BaseJpaSpecs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.SynchronizationType;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * service层 jta方式的基础实现，如果是用jta方式操作数据库，所有
 *
 * @author penghuiping
 * @date 2016-08-12
 */
public abstract class BaseJtaServiceImpl<DTO, MODEL, ID extends Serializable> extends BaseAsyncServiceImpl<DTO, MODEL, ID> {
    private static Logger logger = LoggerFactory.getLogger(BaseJtaServiceImpl.class);

    protected EntityManagerFactory entityManagerFactory;

    private Class<DTO> dtoClass;

    private Class<MODEL> modelClass;

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
        return findOne(id, BeanUtils::copyProperties);
    }

    @Override
    public Optional<DTO> findOne(ID id, ModelToDtoTransferable<MODEL, DTO> modelToDtoTransferable) {
        AssertUtil.notNull(id, "id不能为null");
        AssertUtil.notNull(modelToDtoTransferable, "modelToDtoTransferable不能为null");
        EntityManager entityManager = null;
        try {
            entityManager = entityManagerFactory.createEntityManager(SynchronizationType.SYNCHRONIZED);
            DTO dto = dtoClass.newInstance();
            MODEL model = entityManager.find(modelClass, id);
            modelToDtoTransferable.modelToDto(model, dto);
            return Optional.ofNullable(dto);
        } catch (Exception e) {
            logger.error("出错啦!", e);
            throw new RuntimeException(e);
        } finally {
            if (null != entityManager && entityManager.isOpen()) {
                entityManager.flush();
                entityManager.close();
            }
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
        EntityManager entityManager = null;
        try {
            entityManager = entityManagerFactory.createEntityManager(SynchronizationType.SYNCHRONIZED);
            MODEL a = modelClass.newInstance();
            dtoToModelTransferable.dtoToModel(obj, a);
            DTO dto = dtoClass.newInstance();
            entityManager.merge(a);
            modelToDtoTransferable.modelToDto(a, dto);
            return Optional.ofNullable(dto);
        } catch (Exception e) {
            logger.error("出错啦!", e);
            throw new RuntimeException(e);
        } finally {
            if (null != entityManager && entityManager.isOpen()) {
                entityManager.flush();
                entityManager.close();
            }
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
        EntityManager entityManager = null;
        try {
            entityManager = entityManagerFactory.createEntityManager(SynchronizationType.SYNCHRONIZED);
            List<MODEL> models = (Lists.newArrayList(objs)).stream().map(dto -> {
                try {
                    MODEL model = modelClass.newInstance();
                    dtoToModelTransferable.dtoToModel(dto, model);
                    return model;
                } catch (Exception e) {
                    return null;
                }
            }).collect(Collectors.toList());
            EntityManager finalEntityManager = entityManager;
            models.forEach(a -> {
                finalEntityManager.merge(a);
            });
        } catch (Exception e) {
            logger.error("出错啦!", e);
            throw new RuntimeException(e);
        } finally {
            if (null != entityManager && entityManager.isOpen()) {
                entityManager.flush();
                entityManager.close();
            }
        }

    }

    @Override
    public void delete(DTO obj) {
        AssertUtil.notNull(obj, "dto不能为null");
        EntityManager entityManager = null;
        try {
            entityManager = entityManagerFactory.createEntityManager(SynchronizationType.SYNCHRONIZED);
            MODEL a = modelClass.newInstance();
            BeanUtils.copyProperties(obj, a);
            if (!entityManager.contains(a)) {
                a = entityManager.merge(a);
            }
            entityManager.remove(a);
        } catch (Exception e) {
            logger.error("出错啦!", e);
            throw new RuntimeException(e);
        } finally {
            if (null != entityManager && entityManager.isOpen()) {
                entityManager.flush();
                entityManager.close();
            }
        }
    }

    @Override
    public void delete(List<DTO> objs) {
        AssertUtil.notEmpty(objs, "dtos至少需要包含一个元素");
        EntityManager entityManager = null;
        try {
            entityManager = entityManagerFactory.createEntityManager(SynchronizationType.SYNCHRONIZED);
            List<MODEL> models = objs.stream().map(dto -> {
                try {
                    MODEL a = modelClass.newInstance();
                    BeanUtils.copyProperties(dto, a);
                    return a;
                } catch (Exception e) {
                    return null;
                }
            }).collect(Collectors.toList());
            EntityManager finalEntityManager = entityManager;
            models.forEach(a -> {
                if (!finalEntityManager.contains(a)) {
                    a = finalEntityManager.merge(a);
                }
                finalEntityManager.remove(a);
            });
        } catch (Exception e) {
            logger.error("出错啦!", e);
            throw new RuntimeException(e);
        } finally {
            if (null != entityManager && entityManager.isOpen()) {
                entityManager.flush();
                entityManager.close();
            }
        }
    }

    @Override
    public Optional<DataGridPageDto<DTO>> query(Integer pageNum, Integer pageSize, String searchParams) {
        return query(pageNum, pageSize, searchParams, Sort.Direction.DESC, "createTime");
    }

    @Override
    public Optional<DataGridPageDto<DTO>> query(Integer pageNum, Integer pageSize, String searchParams, Sort.Direction direction, String property) {
        return query(pageNum, pageSize, searchParams, BeanUtils::copyProperties, direction, property);
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
        AssertUtil.notNull(sort, "sort不能为null");
        EntityManager entityManager = null;
        PageRequest pageRequest = null;
        List<MODEL> adminUserModelList = null;

        try {
            entityManager = entityManagerFactory.createEntityManager(SynchronizationType.SYNCHRONIZED);
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<MODEL> cq = cb.createQuery(modelClass);
            Root<MODEL> root = cq.from(modelClass);

            sort.iterator().forEachRemaining(order -> {
                Order order1 = null;
                switch (order.getDirection()) {
                    case ASC:
                        order1 = cb.asc(root.get(order.getProperty()));
                        break;
                    case DESC:
                        order1 = cb.desc(root.get(order.getProperty()));
                        break;
                    default:
                        throw new RuntimeException("不可能到这里");
                }
                cq.orderBy(order1);
            });
            cq.select(root);

            Specification specification = BaseSpecsFactory.<Specification>getInstance(BaseJpaSpecs.class).getSpecs(searchParams);
            Predicate predicate = specification.toPredicate(root, cq, cb);
            if (-1 == pageNum) {
                if (null != predicate) {
                    cq.where(predicate);
                    adminUserModelList = entityManager.createQuery(cq).getResultList();
                } else {
                    adminUserModelList = entityManager.createQuery("select a from " + modelClass.getName() + " a").getResultList();
                }
            } else {
                pageRequest = PageRequest.of(pageNum - 1, pageSize, sort);
                adminUserModelList = entityManager.createQuery(cq).setFirstResult(new Long(pageRequest.getOffset()).intValue()).setMaxResults(pageRequest.getPageSize()).getResultList();
            }

            if (null == adminUserModelList) {
                adminUserModelList = Lists.newArrayList();
            }
            List<DTO> adminUserDtoList = adminUserModelList.stream().map(model -> {
                try {
                    DTO dto = dtoClass.newInstance();
                    modelToDtoTransferable.modelToDto(model, dto);
                    return dto;
                } catch (Exception e) {
                    logger.error("出错啦!", e);
                    throw new RuntimeException(e);
                }
            }).collect(Collectors.toList());

            PageImpl<DTO> dtoPage = null;
            if (-1 == pageNum) {
                dtoPage = new PageImpl<DTO>(adminUserDtoList, Pageable.unpaged(), adminUserModelList.size());
            } else {
                dtoPage = new PageImpl<DTO>(adminUserDtoList, pageRequest, adminUserModelList.size());
            }

            return Optional.ofNullable(toDataGridPageDto(dtoPage));
        } catch (Exception e) {
            logger.error("出错啦!", e);
            throw new RuntimeException(e);
        } finally {
            if (null != entityManager && entityManager.isOpen()) {
                entityManager.flush();
                entityManager.close();
            }
        }
    }

    @Override
    public Optional<DataGridPageDto<DTO>> query(Integer pageNum, Integer pageSize, SearchParamBuilder searchParamBuilder, ModelToDtoTransferable<MODEL, DTO> modelToDtoTransferable, Sort sort) {
        AssertUtil.notNull(pageNum, "pageNum不能为null");
        AssertUtil.notNull(pageSize, "pageSize不能为null");
        AssertUtil.notNull(searchParamBuilder, "searchParamBuilder不能为null");
        AssertUtil.notNull(modelToDtoTransferable, "modelToDtoTransferable不能为null");
        AssertUtil.notNull(sort, "sort不能为null");
        EntityManager entityManager = null;
        PageRequest pageRequest = null;
        List<MODEL> adminUserModelList = null;

        try {
            entityManager = entityManagerFactory.createEntityManager(SynchronizationType.SYNCHRONIZED);
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<MODEL> cq = cb.createQuery(modelClass);
            Root<MODEL> root = cq.from(modelClass);

            sort.iterator().forEachRemaining(order -> {
                Order order1 = null;
                switch (order.getDirection()) {
                    case ASC:
                        order1 = cb.asc(root.get(order.getProperty()));
                        break;
                    case DESC:
                        order1 = cb.desc(root.get(order.getProperty()));
                        break;
                    default:
                        throw new RuntimeException("不可能到这里");
                }
                cq.orderBy(order1);
            });
            cq.select(root);

            Specification specification = BaseSpecsFactory.<Specification>getInstance(BaseJpaSpecs.class).getSpecs(searchParamBuilder);
            Predicate predicate = specification.toPredicate(root, cq, cb);
            if (-1 == pageNum) {
                if (null != predicate) {
                    cq.where(predicate);
                    adminUserModelList = entityManager.createQuery(cq).getResultList();
                } else {
                    adminUserModelList = entityManager.createQuery("select a from " + modelClass.getName() + " a").getResultList();
                }
            } else {
                pageRequest = PageRequest.of(pageNum - 1, pageSize, sort);
                adminUserModelList = entityManager.createQuery(cq).setFirstResult(new Long(pageRequest.getOffset()).intValue()).setMaxResults(pageRequest.getPageSize()).getResultList();
            }

            if (null == adminUserModelList) {
                adminUserModelList = Lists.newArrayList();
            }
            List<DTO> adminUserDtoList = adminUserModelList.stream().map(model -> {
                try {
                    DTO dto = dtoClass.newInstance();
                    modelToDtoTransferable.modelToDto(model, dto);
                    return dto;
                } catch (Exception e) {
                    logger.error("出错啦!", e);
                    throw new RuntimeException(e);
                }
            }).collect(Collectors.toList());

            PageImpl<DTO> dtoPage = null;
            if (-1 == pageNum) {
                dtoPage = new PageImpl<DTO>(adminUserDtoList, Pageable.unpaged(), adminUserModelList.size());
            } else {
                dtoPage = new PageImpl<DTO>(adminUserDtoList, pageRequest, adminUserModelList.size());
            }

            return Optional.ofNullable(toDataGridPageDto(dtoPage));
        } catch (Exception e) {
            logger.error("出错啦!", e);
            throw new RuntimeException(e);
        } finally {
            if (null != entityManager && entityManager.isOpen()) {
                entityManager.flush();
                entityManager.close();
            }
        }
    }

    @Override
    public Optional<List<DTO>> findAll(Iterable<ID> ids) {
        return findAll(ids, BeanUtils::copyProperties);
    }

    @Override
    public Optional<List<DTO>> findAll(Iterable<ID> ids, ModelToDtoTransferable<MODEL, DTO> modelToDtoTransferable) {
        AssertUtil.notEmpty((List<ID>) ids, "ids集合至少需要包含一个元素");
        AssertUtil.notNull(modelToDtoTransferable, "modelToDtoTransferable不能为null");
        List<ID> ids1 = Lists.newArrayList(ids);
        SearchParam searchParam = null;
        new SearchParam.Builder().fieldName("id").operator(Operator.IN).value(JsonUtil.toJson(ids)).build();
        String searchParams = JsonUtil.toJson(Lists.newArrayList(searchParam));
        Optional<DataGridPageDto<DTO>> optionalDtoDataGridPageDto = this.query(-1, 1, searchParams, modelToDtoTransferable, Sort.Direction.DESC, "id");
        return Optional.ofNullable(optionalDtoDataGridPageDto.isPresent() ? optionalDtoDataGridPageDto.get().getData() : null);

    }

    @Override
    public Optional<List<DTO>> findAll() {
        return findAll((ModelToDtoTransferable<MODEL, DTO>) BeanUtils::copyProperties);
    }

    @Override
    public Optional<List<DTO>> findAll(ModelToDtoTransferable<MODEL, DTO> modelToDtoTransferable) {
        Optional<DataGridPageDto<DTO>> optionalDtoDataGridPageDto = this.query(-1, 1, "[]", modelToDtoTransferable, Sort.Direction.DESC, "id");
        return Optional.ofNullable(optionalDtoDataGridPageDto.isPresent() ? optionalDtoDataGridPageDto.get().getData() : null);
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
        } catch (Exception e) {
            logger.error("此对象不支持软删除!", e);
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
        } catch (Exception e) {
            logger.error("此对象不支持软删除!", e);
        }
    }

    @Override
    public Long count(String searchParams) {
        AssertUtil.hasText(searchParams, "searchParams不能为空,如没有搜索条件请使用[]");
        EntityManager entityManager = null;
        try {
            entityManager = entityManagerFactory.createEntityManager(SynchronizationType.SYNCHRONIZED);
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<MODEL> cq = cb.createQuery(modelClass);
            Root<MODEL> root = cq.from(modelClass);
            BaseSpecsFactory.<Specification>getInstance(BaseJpaSpecs.class).getSpecs(searchParams).toPredicate(root, cq, cb);
            return new Long(entityManager.createQuery(cq).getResultList().size());
        } catch (Exception e) {
            logger.error("出错啦!", e);
            throw new RuntimeException(e);
        } finally {
            if (null != entityManager && entityManager.isOpen()) {
                entityManager.flush();
                entityManager.close();
            }
        }
    }


}