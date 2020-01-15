package com.php25.common.db.repository;

import com.google.common.collect.Lists;
import com.php25.common.core.exception.Exceptions;
import com.php25.common.core.util.ObjectUtil;
import com.php25.common.core.util.StringUtil;
import com.php25.common.db.cnd.Column;
import com.php25.common.db.manager.JdbcModelManager;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.data.domain.Persistable;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author penghuiping
 * @date 2020/1/8 16:37
 */
public class BaseDbRepositoryImpl<T extends Persistable<ID>, ID> extends JdbcDbRepositoryImpl<T, ID> implements BaseDbRepository<T, ID> {
    public BaseDbRepositoryImpl() {
        super();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public <S extends T> S save(S s) {
        if (s.isNew()) {
            //新增
            db.cndJdbc(model).insert(s);

            //处理集合关联关系
            List<ImmutablePair<String, Object>> immutablePairs = JdbcModelManager.getTableColumnNameAndCollectionValue(s);
            for (int i = 0; i < immutablePairs.size(); i++) {
                ImmutablePair<String, Object> tmp = immutablePairs.get(i);
                Collection<Object> collection = (Collection<Object>) tmp.getRight();
                List<Object> list = new ArrayList<>(collection);
                if (list.size() > 0) {
                    db.cndJdbc(list.get(0).getClass()).insertRelation(tmp.getLeft(),s.getId(),list);
                }
            }
        } else {
            //更新
            db.cndJdbc(model).update(s);
            
            //处理集合关联关系
            List<ImmutablePair<String, Object>> immutablePairs = JdbcModelManager.getTableColumnNameAndCollectionValue(s);
            for (int i = 0; i < immutablePairs.size(); i++) {
                ImmutablePair<String, Object> tmp = immutablePairs.get(i);
                Collection<Object> collection = (Collection<Object>) tmp.getRight();
                List<Object> list = new ArrayList<>(collection);
                if (list.size() > 0) {
                    //清空所有关系
                    db.cndJdbc(list.get(0).getClass()).whereEq(tmp.getLeft(),s.getId()).delete();
                    //插入关系
                    db.cndJdbc(list.get(0).getClass()).insertRelation(tmp.getLeft(),s.getId(),list);
                }
            }
        }
        return s;
    }

    @Override
    public <S extends T> Iterable<S> saveAll(Iterable<S> objs) {
        S s = objs.iterator().next();
        if (s.isNew()) {
            db.cndJdbc(model).insertBatch(Lists.newArrayList(objs));
        } else {
            db.cndJdbc(model).updateBatch(Lists.newArrayList(objs));
        }
        return objs;
    }

    @Override
    public Optional<T> findById(ID id) {
        T t = db.cndJdbc(model).whereEq(pkName, id).single();
        if (null == t) {
            return Optional.empty();
        } else {
            return Optional.of(t);
        }
    }

    @Override
    public boolean existsById(ID id) {
        return db.cndJdbc(model).whereEq(pkName, id).count() > 0;
    }

    @Override
    public Iterable<T> findAll() {
        return db.cndJdbc(model).select();
    }

    @Override
    public Iterable<T> findAllById(Iterable<ID> ids) {
        return db.cndJdbc(model).whereIn(pkName, Lists.newArrayList(ids)).select();
    }

    @Override
    public long count() {
        return db.cndJdbc(model).count();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteById(ID id) {
        db.cndJdbc(model).whereEq(pkName, id).delete();

        //获取类中的集合属性
        Field[] fields = model.getDeclaredFields();
        List<Field> collectionFields = Lists.newArrayList(fields).stream()
                .filter(field -> Collection.class.isAssignableFrom(field.getType()))
                .collect(Collectors.toList());

        for(Field field:collectionFields) {
            Column column = field.getAnnotation(Column.class);
            if(column == null || StringUtil.isBlank(column.value())) {
                throw Exceptions.throwIllegalStateException("集合属性，必须要加上@Column注解,并且指定value值,值中间表对应的列名");
            }
            //清空所有关系
            Class type = (Class) ObjectUtil.getTypeArgument(field.getGenericType(),0);
            db.cndJdbc(type).whereEq(column.value(),id).delete();
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(T t) {
        List<ImmutablePair<String, Object>> list = JdbcModelManager.getTableColumnNameAndValue(t, true);
        Object value = null;
        for (ImmutablePair<String, Object> pair : list) {
            if (pkName.equals(pair.getLeft())) {
                value = pair.getRight();
                break;
            }
        }
        db.cndJdbc(model).whereEq(pkName, value).delete();


        //获取类中的集合属性
        Field[] fields = model.getDeclaredFields();
        List<Field> collectionFields = Lists.newArrayList(fields).stream()
                .filter(field -> Collection.class.isAssignableFrom(field.getType()))
                .collect(Collectors.toList());
        for(Field field:collectionFields) {
            Column column = field.getAnnotation(Column.class);
            if(column == null || StringUtil.isBlank(column.value())) {
                throw Exceptions.throwIllegalStateException("集合属性，必须要加上@Column注解,并且指定value值,值中间表对应的列名");
            }
            //清空所有关系
            Class type = (Class) ObjectUtil.getTypeArgument(field.getGenericType(),0);
            db.cndJdbc(type).whereEq(column.value(),t.getId()).delete();
        }
    }

    @Override
    public void deleteAll(Iterable<? extends T> objs) {
        List<Object> values = new ArrayList<>();
        objs.forEach(obj -> {
            List<ImmutablePair<String, Object>> list = JdbcModelManager.getTableColumnNameAndValue(obj, true);
            Object value = null;
            for (ImmutablePair<String, Object> pair : list) {
                if (pkName.equals(pair.getLeft())) {
                    value = pair.getRight();
                    break;
                }
            }
            if (value != null) {
                values.add(value);
            }
        });
        db.cndJdbc(model).whereIn(pkName, values).delete();

        //处理中间表关系

    }

    @Override
    public void deleteAll() {
        db.cndJdbc(model).delete();
    }
}
