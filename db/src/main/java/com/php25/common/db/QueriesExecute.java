package com.php25.common.db;

import com.php25.common.db.core.execute.BaseSqlExecute;
import com.php25.common.db.core.execute.BaseSqlShardExecute;
import com.php25.common.db.core.execute.MysqlSqlExecute;
import com.php25.common.db.core.execute.MysqlSqlShardExecute;
import com.php25.common.db.core.execute.OracleSqlExecute;
import com.php25.common.db.core.execute.PostgresSqlExecute;
import com.php25.common.db.core.execute.PostgresSqlShardExecute;

/**
 * @author penghuiping
 * @date 2021/1/4 14:09
 */
public class QueriesExecute {

    private final DbType dbType;


    public QueriesExecute(DbType dbType) {
        this.dbType = dbType;
    }

    public static QueriesExecute of(DbType dbType) {
        return new QueriesExecute(dbType);
    }

    public static QueriesExecute mysql() {
        return new QueriesExecute(DbType.MYSQL);
    }

    public static QueriesExecute oracle() {
        return new QueriesExecute(DbType.ORACLE);
    }

    public static QueriesExecute postgres() {
        return new QueriesExecute(DbType.POSTGRES);
    }


    public BaseSqlExecute singleJdbc() {
        BaseSqlExecute baseSqlExecute = null;
        switch (this.dbType) {
            case MYSQL:
                baseSqlExecute = new MysqlSqlExecute();
                break;
            case ORACLE:
                baseSqlExecute = new OracleSqlExecute();
                break;
            case POSTGRES:
                baseSqlExecute = new PostgresSqlExecute();
                break;
            default:
                baseSqlExecute = new MysqlSqlExecute();
                break;
        }
        return baseSqlExecute;
    }

    public BaseSqlShardExecute shardJdbc() {
        BaseSqlShardExecute baseSqlExecute = null;
        switch (this.dbType) {
            case MYSQL:
                baseSqlExecute = new MysqlSqlShardExecute();
                break;
            case ORACLE:
                break;
            case POSTGRES:
                baseSqlExecute = new PostgresSqlShardExecute();
                break;
            default:
                baseSqlExecute = new MysqlSqlShardExecute();
                break;
        }
        return baseSqlExecute;
    }


}
