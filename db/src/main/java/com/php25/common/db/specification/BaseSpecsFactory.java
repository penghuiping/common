package com.php25.common.db.specification;


import com.php25.common.db.exception.DbException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: penghuiping
 * @date: 2018/6/4 11:04
 * <p>
 * BaseSpecs的工厂类，用于获取不同的BaseSpecs
 * <p>
 * 例子:
 * <p>
 * SearchParamBuilder builder = new SearchParamBuilder();
 * builder.append(new SearchParam.Builder().fieldName("username").operator(Operator.EQ).fieldName("小明").build());
 * BaseSpecsFactory.getInstance(BaseJpaSpecs.class).getSpecs(builder);
 */
public class BaseSpecsFactory {

    private static final Logger log = LoggerFactory.getLogger(BaseSpecsFactory.class);

    private static final ConcurrentHashMap<String, BaseSpecs<?>> concurrentHashMap = new ConcurrentHashMap<>();

    public static <T> BaseSpecs<T> getInstance(Class<? extends BaseSpecs<?>> cls) {
        try {
            BaseSpecs<?> result = concurrentHashMap.get(cls.getName());
            if (null == result) {
                result = cls.newInstance();
                concurrentHashMap.put(cls.getName(), result);
            }
            return (BaseSpecs<T>) result;
        } catch (InstantiationException | IllegalAccessException e) {
            throw new DbException("获取BaseSpecs实例失败", e);
        }


    }

}
