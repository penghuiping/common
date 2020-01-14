package com.php25.common.db.repository;

import org.springframework.data.repository.CrudRepository;

/**
 * @author penghuiping
 * @date 2020/1/14 13:38
 */
public interface BaseDbRepository<T, ID> extends CrudRepository<T, ID>, JdbcDbRepository<T, ID> {
}
