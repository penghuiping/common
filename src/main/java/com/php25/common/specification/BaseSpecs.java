package com.php25.common.specification;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.List;

/**
 * Created by penghuiping on 16/4/12.
 */
public abstract class BaseSpecs<T> {
    protected static ObjectMapper objectMapper = new ObjectMapper();
    protected static Logger logger = LoggerFactory.getLogger(BaseSpecs.class);

    public T getSpecs(final String json) {
        Assert.hasText(json, "搜索条件不能为空，如果没有搜索条件请使用[]");
        try {
            final List<SearchParam> searchParams = objectMapper.readValue(json, new TypeReference<List<SearchParam>>() {
            });
            SearchParamBuilder searchParamBuilder = new SearchParamBuilder();
            //构建searchParamBuilder
            for (SearchParam searchParam : searchParams) {
                searchParamBuilder.append(searchParam);
            }
            return getSpecs(searchParamBuilder);
        } catch (IOException e) {
            logger.error("查询json解析出错", e);
            return null;
        }
    }

    public abstract T getSpecs(final SearchParamBuilder searchParamBuilder);
}
