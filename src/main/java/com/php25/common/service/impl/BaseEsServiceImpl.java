package com.php25.common.service.impl;

import com.google.common.collect.Lists;
import com.php25.common.dto.DataGridPageDto;
import com.php25.common.repository.BaseEsRepository;
import com.php25.common.service.BaseService;
import com.php25.common.service.DtoToModelTransferable;
import com.php25.common.service.ModelToDtoTransferable;
import com.php25.common.specification.BaseEsSpecs;
import org.apache.log4j.Logger;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;

import javax.transaction.Transactional;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by penghuiping on 16/8/12.
 */
@Transactional
public abstract class BaseEsServiceImpl<DTO, MODEL> implements BaseService<DTO, MODEL> {

    protected BaseEsRepository<MODEL, String> baseRepository;

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
    public DTO findOne(String id) {
        return findOne(id, (model, dto) -> BeanUtils.copyProperties(model, dto));
    }

    @Override
    public DTO findOne(String id, ModelToDtoTransferable<MODEL, DTO> modelToDtoTransferable) {
        try {
            DTO dto = dtoClass.newInstance();
            MODEL model = baseRepository.findOne(id);
            modelToDtoTransferable.modelToDto(model, dto);
            return dto;
        } catch (Exception e) {
            Logger.getLogger(BaseEsServiceImpl.class).error("", e);
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
            a = baseRepository.save(a);
            modelToDtoTransferable.modelToDto(a, dto);
            return dto;
        } catch (Exception e) {
            Logger.getLogger(BaseEsServiceImpl.class).error(e);
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
        baseRepository.save(models);
    }

    @Override
    public void delete(DTO obj) {
        try {
            MODEL a = modelClass.newInstance();
            BeanUtils.copyProperties(obj, a);
            baseRepository.delete(a);
        } catch (Exception e) {
            Logger.getLogger(BaseEsServiceImpl.class).error(e);
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
        baseRepository.delete(models);
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
        PageRequest pageRequest = null;
        Page<MODEL> modelPage = null;
        List<MODEL> adminUserModelList = null;

        if (-1 == iDisplayStart) {
            adminUserModelList = (List<MODEL>) baseRepository.search(BaseEsSpecs.getEsSpecs(searchParams));
        } else {
            pageRequest = new PageRequest(iDisplayStart - 1, iDisplayLength, sort);
            NativeSearchQueryBuilder searchQueryBuilder = new NativeSearchQueryBuilder();
            searchQueryBuilder.withQuery(BaseEsSpecs.getEsSpecs(searchParams))
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
                customerModelToDtoTransferable.modelToDto(model, dto);
                return dto;
            } catch (Exception e) {
                Logger.getLogger(BaseEsServiceImpl.class).error(e);
                return null;
            }
        }).collect(Collectors.toList());

        PageImpl<DTO> dtoPage = null;
        if (-1 == iDisplayStart) {
            dtoPage = new PageImpl<DTO>(adminUserDtoList, null, adminUserModelList.size());
        } else {
            dtoPage = new PageImpl<DTO>(adminUserDtoList, null, modelPage.getTotalElements());
        }

        return toDataGridPageDto(dtoPage);
    }

    public DataGridPageDto<DTO> query(SearchQuery searchQuery, ModelToDtoTransferable<MODEL, DTO> customerModelToDtoTransferable) {
        Page<MODEL> modelPage = baseRepository.search(searchQuery);
        List<MODEL> adminUserModelList = modelPage.getContent();
        if (null == adminUserModelList) adminUserModelList = Lists.newArrayList();
        List<DTO> adminUserDtoList = adminUserModelList.stream().map(model -> {
            try {
                DTO dto = dtoClass.newInstance();
                customerModelToDtoTransferable.modelToDto(model, dto);
                return dto;
            } catch (Exception e) {
                Logger.getLogger(BaseEsServiceImpl.class).error(e);
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
        List<MODEL> result = Lists.newArrayList(baseRepository.findAll(ids));
        return result.stream()
                .map(model -> {
                    try {
                        DTO dto = dtoClass.newInstance();
                        modelToDtoTransferable.modelToDto(model, dto);
                        return dto;
                    } catch (Exception e) {
                        Logger.getLogger(BaseEsServiceImpl.class).error(e);
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
        List<MODEL> result = Lists.newArrayList(baseRepository.findAll());
        return result.stream()
                .map(model -> {
                    try {
                        DTO dto = dtoClass.newInstance();
                        modelToDtoTransferable.modelToDto(model, dto);
                        return dto;
                    } catch (Exception e) {
                        Logger.getLogger(BaseEsServiceImpl.class).error(e);
                        return null;
                    }
                }).collect(Collectors.toList());
    }


    @Override
    public Long count(String searchParams) {
        return -1l;
    }

}