package com.php25.common.db;


import com.php25.common.db.core.sql.BaseQuery;
import com.php25.common.db.core.sql.BaseQuery0;
import com.php25.common.db.core.sql.GroupQuery;
import com.php25.common.db.core.sql.MysqlQuery;
import com.php25.common.db.core.sql.OracleQuery;
import com.php25.common.db.core.sql.PostgresQuery;

/**
 * @author penghuiping
 * @date 2021/1/4 13:50
 */
public class Queries {
    private final DbType dbType;

    public Queries(DbType dbType) {
        this.dbType = dbType;
    }

    public static Queries of(DbType dbType) {
        return new Queries(dbType);
    }

    public static Queries mysql() {
        return new Queries(DbType.MYSQL);
    }

    public static Queries oracle() {
        return new Queries(DbType.ORACLE);
    }

    public static Queries postgres() {
        return new Queries(DbType.POSTGRES);
    }


    public static BaseQuery0 group() {
        return new GroupQuery();
    }

    public BaseQuery from(Class<?> cls) {
        BaseQuery query = null;
        switch (dbType) {
            case MYSQL:
                query = new MysqlQuery(cls);
                break;
            case ORACLE:
                query = new OracleQuery(cls);
                break;
            case POSTGRES:
                query = new PostgresQuery(cls);
                break;
            default:
                query = new MysqlQuery(cls);
                break;
        }
        return query;
    }

    public BaseQuery from(Class<?> cls, String alias) {
        BaseQuery query = null;
        switch (dbType) {
            case MYSQL:
                query = new MysqlQuery(cls, alias);
                break;
            case ORACLE:
                query = new OracleQuery(cls, alias);
                break;
            case POSTGRES:
                query = new PostgresQuery(cls, alias);
                break;
            default:
                query = new MysqlQuery(cls, alias);
                break;
        }
        return query;
    }

}
