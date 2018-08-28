package com.php25.common.core.specification;

import java.util.ArrayList;
import java.util.List;

/**
 * @author penghuiping
 * @date 2016-04-12
 * <p>
 * 查询条件拼装类
 * <p>
 * 例子:
 * <p>
 * SearchParamBuilder builder = new SearchParamBuilder().and(new SearchParam.Builder().fieldName("username").operator(Operator.EQ).fieldName("小明").build());
 */
public class SearchParamBuilder {
    private List<SearchParam> searchParamList;

    public static SearchParamBuilder builder() {
        return new SearchParamBuilder();
    }

    public SearchParamBuilder() {
        this.searchParamList = new ArrayList<>();
    }

    public SearchParamBuilder append(SearchParam searchParam) {
        searchParamList.add(searchParam);
        return this;
    }

    public SearchParamBuilder append(List<SearchParam> searchParams) {
        for (SearchParam searchParam : searchParams) {
            searchParamList.add(searchParam);
        }
        return this;
    }

    public List<SearchParam> build() {
        return this.searchParamList;
    }
}