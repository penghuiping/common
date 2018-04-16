package com.php25.common.service.impl;

import com.google.common.collect.Lists;
import com.php25.common.dto.DataGridPageDto;
import com.php25.common.service.BaseService;
import com.php25.common.service.DtoToModelTransferable;
import com.php25.common.service.ModelToDtoTransferable;
import com.php25.common.service.SoftDeletable;
import com.php25.common.specification.BaseNutzSpecs;
import org.apache.log4j.Logger;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.pager.Pager;
import org.nutz.dao.sql.Criteria;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort;

import javax.transaction.Transactional;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by penghuiping on 16/8/12.
 */
@Transactional
public abstract class BaseNutzServiceImpl<DTO, MODEL> implements BaseService<DTO, MODEL>, SoftDeletable<DTO> {

    @Autowired
    protected Dao dao;

    private Class<DTO> dtoClass;

    private Class<MODEL> modelClass;

    public BaseNutzServiceImpl() {
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
    public DTO findOne(String id) {
        return findOne(id, (model, dto) -> BeanUtils.copyProperties(model, dto));
    }

    @Override
    public DTO findOne(String id, ModelToDtoTransferable<MODEL, DTO> modelToDtoTransferable) {
        try {
            DTO dto = dtoClass.newInstance();
            MODEL model = dao.fetch(modelClass, id);
            modelToDtoTransferable.modelToDto(model, dto);
            return dto;
        } catch (Exception e) {
            Logger.getLogger(BaseServiceImpl.class).error("", e);
            return null;
        }
    }

    @Override
    public DTO save(DTO obj) {
        return save(obj, (dto, model) -> BeanUtils.copyProperties(dto, model), (model, dto) -> BeanUtils.copyProperties(model, dto));
    }

    @Override
    public DTO save(DTO obj, DtoToModelTransferable<MODEL, DTO> dtoToModelTransferable, ModelToDtoTransferable<MODEL, DTO> modelToDtoTransferable) {
        try {
            MODEL a = modelClass.newInstance();
            dtoToModelTransferable.dtoToModel(obj, a);
            DTO dto = dtoClass.newInstance();
            a = dao.insertOrUpdate(a);
            modelToDtoTransferable.modelToDto(a, dto);
            return dto;
        } catch (Exception e) {
            Logger.getLogger(BaseServiceImpl.class).error(e);
            return null;
        }
    }

    @Override
    public void save(Iterable<DTO> objs) {
        save(objs, (dto, model) -> BeanUtils.copyProperties(dto, model));
    }

    @Override
    public void save(Iterable<DTO> objs, DtoToModelTransferable<MODEL, DTO> dtoToModelTransferable) {
        List<MODEL> models = (Lists.newArrayList(objs)).stream().map(dto -> {
            try {
                MODEL model = modelClass.newInstance();
                dtoToModelTransferable.dtoToModel(dto, model);
                return model;
            } catch (Exception e) {
                return null;
            }
        }).collect(Collectors.toList());

        models.forEach(a -> {
            dao.insertOrUpdate(a);
        });
    }

    @Override
    public void delete(DTO obj) {
        try {
            MODEL a = modelClass.newInstance();
            BeanUtils.copyProperties(obj, a);
            dao.delete(a);
        } catch (Exception e) {
            Logger.getLogger(BaseServiceImpl.class).error(e);
        }
    }

    @Override
    public void delete(List<DTO> objs) {
        List<MODEL> models = objs.stream().map(dto -> {
            try {
                MODEL a = modelClass.newInstance();
                BeanUtils.copyProperties(dto, a);
                return a;
            } catch (Exception e) {
                return null;
            }
        }).collect(Collectors.toList());
        dao.delete(models);
    }

    @Override
    public DataGridPageDto<DTO> query(Integer iDisplayStart, Integer iDisplayLength, String searchParams) {
        return query(iDisplayStart, iDisplayLength, searchParams, Sort.Direction.DESC, "createTime");
    }

    @Override
    public DataGridPageDto<DTO> query(Integer iDisplayStart, Integer iDisplayLength, String searchParams, Sort.Direction direction, String... properties) {
        return query(iDisplayStart, iDisplayLength, searchParams, (model, dto) -> BeanUtils.copyProperties(model, dto)
                , direction, properties);
    }


    @Override
    public DataGridPageDto<DTO> query(Integer iDisplayStart, Integer iDisplayLength, String searchParams, ModelToDtoTransferable<MODEL, DTO> customerModelToDtoTransferable, Sort.Direction direction, String... properties) {
        Sort.Order order = new Sort.Order(direction, properties[0]);
        Sort sort = new Sort(order);
        return query(iDisplayStart, iDisplayLength, searchParams, customerModelToDtoTransferable, sort);
    }

    @Override
    public DataGridPageDto<DTO> query(Integer iDisplayStart, Integer iDisplayLength, String searchParams, ModelToDtoTransferable<MODEL, DTO> customerModelToDtoTransferable, Sort sort) {
        Pager pageRequest = null;
        List<MODEL> adminUserModelList = null;

        if (-1 == iDisplayStart) {
            adminUserModelList = dao.query(modelClass, BaseNutzSpecs.<MODEL>getNutzSpecs(searchParams));
        } else {
            pageRequest = new Pager(iDisplayStart - 1, iDisplayLength);
            final Criteria cri = BaseNutzSpecs.<MODEL>getNutzSpecs(searchParams);
            if (null != sort) {
                sort.forEach(a -> {
                    if (a.getDirection().isAscending()) {
                        cri.getOrderBy().asc(a.getProperty());
                    } else
                        cri.getOrderBy().desc(a.getProperty());
                });

            }
            adminUserModelList = dao.query(modelClass, cri, pageRequest);
        }

        if (null == adminUserModelList) adminUserModelList = Lists.newArrayList();
        List<DTO> adminUserDtoList = adminUserModelList.stream().map(model -> {
            try {
                DTO dto = dtoClass.newInstance();
                customerModelToDtoTransferable.modelToDto(model, dto);
                return dto;
            } catch (Exception e) {
                Logger.getLogger(BaseServiceImpl.class).error(e);
                return null;
            }
        }).collect(Collectors.toList());

        PageImpl<DTO> dtoPage = new PageImpl<DTO>(adminUserDtoList, null, adminUserModelList.size());
        return toDataGridPageDto(dtoPage);
    }

    @Override
    public List<DTO> findAll(Iterable<String> ids) {
        return findAll(ids, (model, dto) -> BeanUtils.copyProperties(model, dto));
    }

    @Override
    public List<DTO> findAll(Iterable<String> ids, ModelToDtoTransferable<MODEL, DTO> modelToDtoTransferable) {
        Criteria cri = Cnd.cri();
        List<String> tmp = Lists.newArrayList(ids);
        String[] tmpStr = new String[tmp.size()];
        cri.where().andIn("id", tmp.toArray(tmpStr));
        List<MODEL> result = dao.query(modelClass, cri);
        return result.stream()
                .map(model -> {
                    try {
                        DTO dto = dtoClass.newInstance();
                        modelToDtoTransferable.modelToDto(model, dto);
                        return dto;
                    } catch (Exception e) {
                        Logger.getLogger(BaseServiceImpl.class).error(e);
                        return null;
                    }
                }).collect(Collectors.toList());
    }

    @Override
    public List<DTO> findAll() {
        return findAll((model, dto) -> BeanUtils.copyProperties(model, dto));
    }

    @Override
    public List<DTO> findAll(ModelToDtoTransferable<MODEL, DTO> modelToDtoTransferable) {
        Criteria cri = Cnd.cri();
        List<MODEL> result = dao.query(modelClass, cri);
        return result.stream()
                .map(model -> {
                    try {
                        DTO dto = dtoClass.newInstance();
                        modelToDtoTransferable.modelToDto(model, dto);
                        return dto;
                    } catch (Exception e) {
                        Logger.getLogger(BaseServiceImpl.class).error(e);
                        return null;
                    }
                }).collect(Collectors.toList());
    }

    @Override
    public void softDelete(DTO obj) {
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
            Logger.getLogger(BaseServiceImpl.class).info("此对象不支持软删除");
            Logger.getLogger(BaseServiceImpl.class).error("此对象不支持软删除", e);
        }
    }

    @Override
    public void softDelete(List<DTO> objs) {
        if (null != objs && objs.size() > 0) {
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
                Logger.getLogger(BaseServiceImpl.class).info("此对象不支持软删除");
            }
        }
    }

    @Override
    public Long count(String searchParams) {
        Long result = new Long(dao.count(modelClass, BaseNutzSpecs.getNutzSpecs(searchParams)));
        return result;
    }
}