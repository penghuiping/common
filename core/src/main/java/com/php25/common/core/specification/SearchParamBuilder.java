package com.php25.common.core.specification;

import java.util.ArrayList;
import java.util.List;

public class SearchParamBuilder {
    private List<SearchParam> searchParamList;

    public SearchParamBuilder() {
        this.searchParamList = new ArrayList<>();
    }

    public SearchParamBuilder append(SearchParam searchParam) {
        searchParamList.add(searchParam);
        return this;
    }

    public List<SearchParam> build() {
        return this.searchParamList;
    }
}
