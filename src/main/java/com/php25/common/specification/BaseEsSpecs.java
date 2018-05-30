package com.php25.common.specification;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import java.io.IOException;
import java.util.List;

/**
 * Created by penghuiping on 16/4/12.
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
                        list = objectMapper.readValue((String) s.getValue(), List.class);
                    } catch (IOException e) {
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
