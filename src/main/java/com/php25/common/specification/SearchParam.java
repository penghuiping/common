package com.php25.common.specification;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.php25.common.util.TimeUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanWrapperImpl;

import javax.persistence.criteria.*;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by penghuiping on 16/4/12.
 */
public class SearchParam {
    private final static BeanWrapperImpl convert = new BeanWrapperImpl();
    private final static ObjectMapper objectMapper = new ObjectMapper();
    private String fieldName;
    private Object value;
    private String operator;

    public SearchParam() {
    }

    public Predicate toPredicate(Root<?> root, CriteriaQuery<?> query,
                                 CriteriaBuilder builder) throws IOException {
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
            } else {
                op = Operator.EQ;
            }
        }


        Object objValue = null;
        if (op == Operator.IN) {
            if (value instanceof String) {
                List<String> temp = objectMapper.readValue((String) value, new TypeReference<List<String>>() {
                });
                objValue = temp;

                //尝试是否可以转化成int
                try {
                    List<Integer> temp1 = temp.stream().map(a->Integer.parseInt(a)).collect(Collectors.toList());
                    objValue = temp1;
                } catch (Exception e) {

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
            default:
                return null;
        }
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }
}
