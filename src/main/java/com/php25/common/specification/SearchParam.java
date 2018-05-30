package com.php25.common.specification;

/**
 * Created by penghuiping on 16/4/12.
 */
public class SearchParam {
    private String fieldName;
    private Object value;
    private String operator;

    private SearchParam() {
    }

    private SearchParam(String fieldName, Object value, String operator) {
        this.fieldName = fieldName;
        this.value = value;
        this.operator = operator;
    }

    public String getFieldName() {
        return fieldName;
    }


    public Object getValue() {
        return value;
    }


    public String getOperator() {
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

        public Builder operator(String operator) {
            target.operator = operator;
            return this;
        }

        public SearchParam build() {
            return this.target;
        }
    }
}
