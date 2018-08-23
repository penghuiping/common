package com.php25.common.es.specification;

import com.php25.common.core.specification.BaseSpecs;
import com.php25.common.core.specification.SearchParam;
import com.php25.common.core.specification.SearchParamBuilder;
import com.php25.common.core.util.JsonUtil;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import java.util.List;

/**
 *
 * BaseSpecs的es实现，可以用来构建符合es语法的多条件查询语句
 *
 * @author penghuiping
 * @date 2016-04-12
 *
 */
public class BaseEsSpecs extends BaseSpecs<QueryBuilder> {

    @Override
    public QueryBuilder getSpecs(SearchParamBuilder searchParamBuilder) {
        final List<SearchParam> searchParams = searchParamBuilder.build();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        for (SearchParam s : searchParams) {
            switch (s.getOperator().name()) {
                case "eq":
                    boolQueryBuilder.must(QueryBuilders.termQuery(s.getFieldName(), s.getValue()));
                    break;
                case "ne":
                    boolQueryBuilder.mustNot(QueryBuilders.termQuery(s.getFieldName(), s.getValue()));
                    break;
                case "like":
                    boolQueryBuilder.must(QueryBuilders.matchQuery(s.getFieldName(), s.getValue()));
                    break;
                case "gt":
                    boolQueryBuilder.must(QueryBuilders.rangeQuery(s.getFieldName()).gt(s.getValue()));
                    break;
                case "lt":
                    boolQueryBuilder.must(QueryBuilders.rangeQuery(s.getFieldName()).lt(s.getValue()));
                    break;
                case "gte":
                    boolQueryBuilder.must(QueryBuilders.rangeQuery(s.getFieldName()).gte(s.getValue()));
                    break;
                case "lte":
                    boolQueryBuilder.must(QueryBuilders.rangeQuery(s.getFieldName()).lte(s.getValue()));
                    break;
                case "in":
                    List list = null;
                    try {
                        list = JsonUtil.fromJson((String) s.getValue(), List.class);
                    } catch (Exception e) {
                        throw new RuntimeException("in操作的值必须是List json", e);
                    }
                    boolQueryBuilder.must(QueryBuilders.termsQuery(s.getFieldName(), list));
                    break;
                default:
                    boolQueryBuilder.must(QueryBuilders.termQuery(s.getFieldName(), s.getValue()));
                    break;
            }
        }
        return boolQueryBuilder;
    }
}
