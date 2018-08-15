package com.php25.common.core.specification;

import com.fasterxml.jackson.core.type.TypeReference;
import com.php25.common.core.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.util.List;

/**
 * Created by penghuiping on 16/4/12.
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

    public abstract T getSpecs(final SearchParamBuilder searchParamBuilder);
}
