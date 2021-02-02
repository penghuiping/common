//package com.php25.common.db.repository.shard;
//
//import com.php25.common.core.util.PageUtil;
//import com.php25.common.db.DbType;
//import com.php25.common.db.EntitiesScan;
//import com.php25.common.db.core.manager.JdbcModelManager;
//import com.php25.common.db.core.shard.ShardRule;
//import com.php25.common.db.core.sql.BaseQuery;
//import com.php25.common.db.exception.DbException;
//import com.php25.common.db.repository.JdbcDbRepository;
//import com.php25.common.db.specification.SearchParamBuilder;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.domain.Persistable;
//import org.springframework.data.domain.Sort;
//
//import java.lang.reflect.ParameterizedType;
//import java.lang.reflect.Type;
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.List;
//import java.util.Optional;
//import java.util.stream.Collectors;
//
///**
// * @author penghuiping
// * @date 2020/9/9 16:46
// */
//public class JdbcShardDbRepositoryImpl<T extends Persistable<ID>, ID extends Comparable<ID>> implements JdbcDbRepository<T, ID> {
//
//    protected Class<?> model;
//
//    protected String pkName;
//
//    protected DbType dbType;
//
//
//    public JdbcShardDbRepositoryImpl(DbType dbType) {
//        Type genType = getClass().getGenericSuperclass();
//        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
//        this.model = (Class<?>) params[0];
//        this.pkName = JdbcModelManager.getPrimaryKeyFieldName(model);
//        this.dbType = dbType;
//    }
//
//    @Override
//    public List<T> findAllEnabled() {
//        List<T> result = new ArrayList<>();
//        for (EntitiesScan db : dbList) {
//            List<T> tmp = db.getBaseSqlExecute().select(db.from(model).whereEq("enable", 1).select());
//            result.addAll(tmp);
//        }
//        return result;
//    }
//
//    @Override
//    public Optional<T> findByIdEnable(ID id) {
//        EntitiesScan db = shardRule.shard(this.dbList, id);
//        return Optional.of(db.getBaseSqlExecute().single(db.from(model).whereEq(pkName, id).andEq("enable", 1).single()));
//    }
//
//    @Override
//    public Optional<T> findOne(SearchParamBuilder searchParamBuilder) {
//        T result = null;
//        for (EntitiesScan db : dbList) {
//            BaseQuery cnd = db.from(model).andSearchParamBuilder(searchParamBuilder);
//            T tmp = db.getBaseSqlExecute().single(cnd.single());
//            if (tmp != null) {
//                result = tmp;
//                return Optional.of(result);
//            }
//        }
//        return Optional.empty();
//    }
//
//    @Override
//    public List<T> findAll(SearchParamBuilder searchParamBuilder) {
//        List<T> result = new ArrayList<>();
//        for (EntitiesScan db : dbList) {
//            BaseQuery cnd = db.from(model).andSearchParamBuilder(searchParamBuilder);
//            List<T> tmp = db.getBaseSqlExecute().select(cnd.select());
//            if (tmp != null && !tmp.isEmpty()) {
//                result.addAll(tmp);
//            }
//        }
//        return result;
//    }
//
//    @Override
//    public Page<T> findAll(SearchParamBuilder searchParamBuilder, Pageable pageable) {
//        if (!pageable.getSort().isUnsorted()) {
//            throw new DbException("暂不支持排序操作");
//        }
//
//        int dbNumber = dbList.size();
//        String pk = JdbcModelManager.getPrimaryKeyFieldName(model);
//        int[] page = PageUtil.transToStartEnd(pageable.getPageNumber(), pageable.getPageSize());
//        int offset = page[0];
//
//        //这次使用二次查询法实现分布式分页
//        int perDbOffset = offset / dbNumber;
//
//        T min = null;
//        List<List<T>> lists = new ArrayList<>();
//        for (EntitiesScan db : dbList) {
//            BaseQuery cnd = db.from(model).andSearchParamBuilder(searchParamBuilder);
//            List<T> list = db.getBaseSqlExecute().select(cnd.limit(perDbOffset, pageable.getPageSize()).select());
//            lists.add(list);
//            if (null != list && !list.isEmpty()) {
//                T localMin = list.get(0);
//                if (null == min || (min.getId().compareTo(localMin.getId()) > 0)) {
//                    min = list.get(0);
//                }
//            }
//        }
//        long globalOffset = 0L;
//        List<List<T>> lists1 = new ArrayList<>();
//        for (int i = 0; i < dbList.size(); i++) {
//            List<T> tmp = lists.get(i);
//            EntitiesScan db = dbList.get(i);
//            List<T> list = db.getBaseSqlExecute().select(db.from(model).andBetween(pk, min.getId(), tmp.get(tmp.size() - 1).getId()).select());
//            lists1.add(list);
//            globalOffset = globalOffset + perDbOffset + list.size() - tmp.size();
//        }
//        List<T> result = lists1.stream().flatMap(Collection::stream).sorted((o1, o2) -> o1.getId().compareTo(o2.getId())).collect(Collectors.toList());
//        int index = (int) (offset - globalOffset);
//        int toIndex = index + pageable.getPageSize();
//        result = result.subList(index, Math.min(toIndex, result.size()));
//        long total = this.count(searchParamBuilder);
//        return new PageImpl<T>(result, pageable, total);
//    }
//
//
//    @Override
//    public List<T> findAll(SearchParamBuilder searchParamBuilder, Sort sort) {
//        throw new DbException("暂不支持排序操作");
//    }
//
//    @Override
//    public long count(SearchParamBuilder searchParamBuilder) {
//        long count = 0L;
//        for (EntitiesScan db : dbList) {
//            BaseQuery cnd = db.from(model).andSearchParamBuilder(searchParamBuilder);
//            count = count + db.getBaseSqlExecute().count(cnd.count());
//        }
//        return count;
//    }
//}
