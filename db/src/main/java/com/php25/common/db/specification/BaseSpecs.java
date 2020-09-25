package com.php25.common.db.specification;

import com.fasterxml.jackson.core.type.TypeReference;
import com.php25.common.core.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.util.List;

/**
 * BaseSpecs的基实现，所有的具体的BaseSpecs都应该继承这个类。
 *
 * @author penghuiping
 * @date 2016-04-12
 */
public abstract class BaseSpecs<T> {
    protected static Logger logger = LoggerFactory.getLogger(BaseSpecs.class);

    public T getSpecs(final String json) {
        Assert.hasText(json, "搜索条件不能为空，如果没有搜索条件请使用[]");
        final List<SearchParam> searchParams = JsonUtil.fromJson(json, new TypeReference<List<SearchParam>>() {
        });
        SearchParamBuilder searchParamBuilder = new SearchParamBuilder();
        //构建searchParamBuilder
        for (SearchParam searchParam : searchParams) {
            searchParamBuilder.append(searchParam);
        }
        return getSpecs(searchParamBuilder);

    }

    /**
     * 通过SearchParamBuilder构建查询条件
     *
     * @param searchParamBuilder 查询条件
     * @return
     */
    public abstract T getSpecs(final SearchParamBuilder searchParamBuilder);
}
