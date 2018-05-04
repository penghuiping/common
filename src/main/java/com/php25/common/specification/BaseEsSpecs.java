package com.php25.common.specification;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.php25.common.util.StringUtil;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

/**
 * Created by penghuiping on 16/4/12.
 */
public class BaseEsSpecs {
    protected static final ObjectMapper objectMapper = new ObjectMapper();
    private static Logger logger = LoggerFactory.getLogger(BaseEsSpecs.class);

    public static QueryBuilder getEsSpecs(final String json) {
        if (StringUtil.isBlank(json)) return null;
        try {
            final List<SearchParam> searchParams = objectMapper.readValue(json, new TypeReference<List<SearchParam>>() {
            });

            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
            for (SearchParam s : searchParams) {
                switch (s.getOperator()) {
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
                        List list = objectMapper.readValue((String) s.getValue(), List.class);
                        boolQueryBuilder.must(QueryBuilders.termsQuery(s.getFieldName(), list));
                        break;
                    default:
                        boolQueryBuilder.must(QueryBuilders.termQuery(s.getFieldName(), s.getValue()));
                        break;
                }
            }
            return boolQueryBuilder;
        } catch (IOException e) {
            logger.error("查询json解析出错", e);
            return null;
        }
    }
}
