package com.php25.common.core.specification;

/**
 * @author penghuiping
 * @date 2016-04-12
 *
 * 查询条件
 */
public class SearchParam {
    private String fieldName;
    private Object value;
    private Operator operator;

    private SearchParam() {
    }

    public String getFieldName() {
        return fieldName;
    }


    public Object getValue() {
        return value;
    }


    public Operator getOperator() {
        return operator;
    }


    public static class Builder {
        private SearchParam target;

        public Builder() {
            this.target = new SearchParam();
        }

        public Builder fieldName(String fieldName) {
            target.fieldName = fieldName;
            return this;
        }

        public Builder value(Object value) {
            target.value = value;
            return this;
        }

        public Builder operator(Operator operator) {
            target.operator = operator;
            return this;
        }

        public SearchParam build() {
            return this.target;
        }
    }
}