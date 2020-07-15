package com.php25.common.auditlog.dto;

/**
 * @author penghuiping
 * @date 2020/7/13 15:10
 */
public class AuditLogContent {

    private String userId;

    private String className;

    private String methodName;

    private String params;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }
}
