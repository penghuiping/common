package com.php25.common.specification;

/**
 * Created by penghuiping on 16/4/12.
 */
public class SearchParam {
    private String fieldName;
    private Object value;
    private String operator;

    public SearchParam() {
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
