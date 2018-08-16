package com.php25.common.jdbc;

import org.springframework.jdbc.core.JdbcOperations;

/**
 * @Auther: penghuiping
 * @Date: 2018/8/16 10:32
 * @Description:
 */
public class CndOracle extends Cnd {

    protected CndOracle(Class cls, JdbcOperations jdbcOperations) {
        this.jdbcOperations = jdbcOperations;
        this.clazz = cls;
        this.dbType = DbType.MYSQL;
    }

    @Override
    protected void addAdditionalPartSql() {
        StringBuilder sb = this.getSql();
        if (this.orderBy != null) {
            sb.append(orderBy.getOrderBy()).append(" ");
        }

        if (this.groupBy != null) {
            sb.append(groupBy.getGroupBy()).append(" ");
        }
        // 增加翻页
        if (this.startRow != -1) {
            String result = String.format("SELECT * FROM ( SELECT A.*, ROWNUM RN FROM (%s) A WHERE ROWNUM <= %s) WHERE RN >= %s", sb.toString(), pageSize, startRow);
            this.setSql(new StringBuilder(result));
        }
    }
}
