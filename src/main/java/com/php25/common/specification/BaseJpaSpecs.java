package com.php25.common.specification;

import com.fasterxml.jackson.core.type.TypeReference;
import com.php25.common.util.TimeUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.Assert;

import javax.persistence.criteria.*;
import java.util.Date;
import java.util.List;

/**
 * Created by penghuiping on 16/4/12.
 */
public class BaseJpaSpecs extends BaseSpecs<Specification> {
    private final static BeanWrapperImpl convert = new BeanWrapperImpl();

    private static Predicate toPredicate(Root<?> root, CriteriaQuery<?> query,
                                         CriteriaBuilder builder, SearchParam searchParam) {

        String fieldName = searchParam.getFieldName();
        String operator = searchParam.getOperator().name();
        Object value = searchParam.getValue();

        Path expression = null;
        if (fieldName.contains(".")) {
            String[] names = StringUtils.split(fieldName, ".");
            expression = root.get(names[0]);
            for (int i = 1; i < names.length; i++) {
                expression = expression.get(names[i]);
            }
        } else {
            expression = root.get(fieldName);
        }

        Operator op = Operator.EQ;

        if (null != operator) {
            if ("eq".equals(operator.toLowerCase())) {
                op = Operator.EQ;
            } else if ("ne".equals(operator.toLowerCase())) {
                op = Operator.NE;
            } else if ("like".equals(operator.toLowerCase())) {
                op = Operator.LIKE;
            } else if ("gt".equals(operator.toLowerCase())) {
                op = Operator.GT;
            } else if ("lt".equals(operator.toLowerCase())) {
                op = Operator.LT;
            } else if ("gte".equals(operator.toLowerCase())) {
                op = Operator.GTE;
            } else if ("lte".equals(operator.toLowerCase())) {
                op = Operator.LTE;
            } else if ("in".equals(operator.toLowerCase())) {
                op = Operator.IN;
            } else if ("nin".equals(operator.toLowerCase())) {
                op = Operator.NIN;
            } else {
                op = Operator.EQ;
            }
        }


        Object objValue = null;
        if (op == Operator.IN || op == Operator.NIN) {
            Assert.isInstanceOf(String.class, value, "使用in操作值必须是List的json字符串");
            int errorCount = 0;
            //尝试是否可以转化为Long
            try {
                List<Long> temp1 = objectMapper.readValue((String) value, new TypeReference<List<Long>>() {
                });
                objValue = temp1;
            } catch (Exception e) {
                errorCount++;
            }

            if (errorCount > 0) {
                //尝试是否可以转化成String
                try {
                    List<String> temp = objectMapper.readValue((String) value, new TypeReference<List<String>>() {
                    });
                    objValue = temp;
                } catch (Exception e) {
                    throw new RuntimeException("使用in操作值必须是List的json字符串,并且List的泛型只能是String或者Long", e);
                }
            }
        } else {
            if (expression.getJavaType().isInstance(new Date())) {
                if (value instanceof String)
                    objValue = TimeUtil.parseDate((String) value);
            } else {
                objValue = convert.convertIfNecessary(value, expression.getJavaType());
            }
        }


        switch (op) {
            case EQ:
                return builder.equal(expression, objValue);
            case NE:
                return builder.notEqual(expression, objValue);
            case LIKE:
                return builder.like((Expression<String>) expression, "%" + objValue + "%");
            case LT:
                return builder.lessThan(expression, (Comparable) objValue);
            case GT:
                return builder.greaterThan(expression, (Comparable) objValue);
            case LTE:
                return builder.lessThanOrEqualTo(expression, (Comparable) objValue);
            case GTE:
                return builder.greaterThanOrEqualTo(expression, (Comparable) objValue);
            case IN:
                return builder.in(expression).value(objValue);
            case NIN:
                return builder.not(builder.in(expression).value(objValue));
            default:
                return null;
        }
    }

    @Override
    public Specification getSpecs(SearchParamBuilder searchParamBuilder) {
        Assert.notNull(searchParamBuilder, "searchParamBuilder不能为null");
        final List<SearchParam> searchParams = searchParamBuilder.build();
        return (root, criteriaQuery, criteriaBuilder) -> {
            Predicate p = null;
            for (SearchParam s : searchParams) {
                try {
                    if (null == p)
                        p = BaseJpaSpecs.toPredicate(root, criteriaQuery, criteriaBuilder, s);
                    else
                        p = criteriaBuilder.and(p, BaseJpaSpecs.toPredicate(root, criteriaQuery, criteriaBuilder, s));
                } catch (Exception e) {
                    logger.error("查询出错", e);
                }
            }
            return p;
        };
    }
}
