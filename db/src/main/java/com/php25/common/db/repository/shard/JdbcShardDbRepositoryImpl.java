package com.php25.common.db.repository.shard;

import com.php25.common.db.Db;
import com.php25.common.db.cnd.CndJdbc;
import com.php25.common.db.manager.JdbcModelManager;
import com.php25.common.db.repository.JdbcDbRepository;
import com.php25.common.db.specification.SearchParamBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author penghuiping
 * @date 2020/9/9 16:46
 */
public class JdbcShardDbRepositoryImpl<T, ID> implements JdbcDbRepository<T, ID> {

    protected List<Db> dbList;

    protected Class<?> model;

    protected String pkName;

    protected ShardRule shardRule;

    public JdbcShardDbRepositoryImpl(List<Db> dbList, ShardRule shardRule) {
        this.dbList = dbList;
        Type genType = getClass().getGenericSuperclass();
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
        this.model = (Class<?>) params[0];
        this.pkName = JdbcModelManager.getPrimaryKeyFieldName(model);
        this.shardRule = shardRule;
    }

    @Override
    public List<T> findAllEnabled() {
        List<T> result = new ArrayList<>();
        for (Db db : dbList) {
            List<T> tmp = db.cndJdbc(model).whereEq("enable", 1).select();
            result.addAll(tmp);
        }
        return result;
    }

    @Override
    public Optional<T> findByIdEnable(ID id) {
        Db db = shardRule.shardPrimaryKey(this.dbList, id);
        return Optional.of(db.cndJdbc(model).ignoreCollection(false).whereEq(pkName, id).andEq("enable", 1).single());
    }

    @Override
    public Optional<T> findOne(SearchParamBuilder searchParamBuilder) {
        T result = null;
        for (Db db : dbList) {
            CndJdbc cnd = db.cndJdbc(model).ignoreCollection(false).andSearchParamBuilder(searchParamBuilder);
            T tmp = cnd.single();
            if (tmp != null) {
                result = tmp;
                return Optional.of(result);
            }
        }
        return Optional.empty();
    }

    @Override
    public List<T> findAll(SearchParamBuilder searchParamBuilder) {
        List<T> result = new ArrayList<>();
        for (Db db : dbList) {
            CndJdbc cnd = db.cndJdbc(model).andSearchParamBuilder(searchParamBuilder);
            List<T> tmp = cnd.select();
            if (tmp != null && !tmp.isEmpty()) {
                result.addAll(tmp);
            }
        }
        return result;
    }

    @Override
    public Page<T> findAll(SearchParamBuilder searchParamBuilder, Pageable pageable) {
        return null;
    }

    @Override
    public List<T> findAll(SearchParamBuilder searchParamBuilder, Sort sort) {
        return null;
    }

    @Override
    public long count(SearchParamBuilder searchParamBuilder) {
        long count = 0L;
        for (Db db : dbList) {
            CndJdbc cnd = db.cndJdbc(model).andSearchParamBuilder(searchParamBuilder);
            count = count + cnd.count();
        }
        return count;
    }
}
