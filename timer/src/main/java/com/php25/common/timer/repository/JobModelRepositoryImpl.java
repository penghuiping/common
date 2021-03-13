package com.php25.common.timer.repository;

import com.php25.common.db.DbType;
import com.php25.common.db.Queries;
import com.php25.common.db.QueriesExecute;
import com.php25.common.db.core.sql.SqlParams;
import com.php25.common.timer.model.JobModel;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

/**
 * @author penghuiping
 * @date 2020/8/24 13:56
 */
@RequiredArgsConstructor
public class JobModelRepositoryImpl implements JobModelRepository {

    private final DbType dbType;

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void create(JobModel job) {
        SqlParams sqlParams = Queries.of(dbType).from(JobModel.class).insert(job);
        QueriesExecute.of(dbType).singleJdbc().with(jdbcTemplate).insert(sqlParams);
    }

    @Override
    public void update(JobModel job) {
        SqlParams sqlParams = Queries.of(dbType).from(JobModel.class).update(job);
        QueriesExecute.of(dbType).singleJdbc().with(jdbcTemplate).update(sqlParams);
    }

    @Override
    public void deleteAll(List<String> jobIds) {
        SqlParams sqlParams = Queries.of(dbType).from(JobModel.class).whereIn("id", jobIds).delete();
        QueriesExecute.of(dbType).singleJdbc().with(jdbcTemplate).delete(sqlParams);
    }

    @Override
    public List<JobModel> findAllEnabled() {
        SqlParams sqlParams = Queries.of(dbType).from(JobModel.class).whereEq("enable", 1).select();
        return QueriesExecute.of(dbType).singleJdbc().with(jdbcTemplate).select(sqlParams);
    }

    @Override
    public JobModel findById(String id) {
        SqlParams sqlParams = Queries.of(dbType).from(JobModel.class).whereEq("id", id).single();
        return QueriesExecute.of(dbType).singleJdbc().with(jdbcTemplate).single(sqlParams);
    }
}
