package com.php25.common.specification;

import org.elasticsearch.index.query.QueryBuilder;
import org.nutz.dao.sql.Criteria;
import org.springframework.data.jpa.domain.Specification;

public class BaseSpecsFactory {

    private static BaseNutzSpecs baseNutzSpecs = new BaseNutzSpecs();
    private static BaseJpaSpecs baseJpaSpecs = new BaseJpaSpecs();
    private static BaseEsSpecs baseEsSpecs = new BaseEsSpecs();


    public static BaseSpecs<Criteria> getNutzInstance() {
        return baseNutzSpecs;
    }

    public static BaseSpecs<Specification> getJpaInstance() {
        return baseJpaSpecs;
    }

    public static BaseSpecs<QueryBuilder> getEsInstance() {
        return baseEsSpecs;
    }
}
