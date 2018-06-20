package com.php25.common.specification;

import org.elasticsearch.index.query.QueryBuilder;
import org.springframework.data.jpa.domain.Specification;

public class BaseSpecsFactory {

    private static BaseJpaSpecs baseJpaSpecs = new BaseJpaSpecs();
    private static BaseEsSpecs baseEsSpecs = new BaseEsSpecs();


    public static BaseSpecs<Specification> getJpaInstance() {
        return baseJpaSpecs;
    }

    public static BaseSpecs<QueryBuilder> getEsInstance() {
        return baseEsSpecs;
    }
}
