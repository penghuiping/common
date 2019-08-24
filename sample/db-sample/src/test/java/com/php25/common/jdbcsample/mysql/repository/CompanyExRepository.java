package com.php25.common.jdbcsample.mysql.repository;

import com.php25.common.db.repository.JdbcDbRepository;
import com.php25.common.jdbcsample.mysql.model.Company;

/**
 * @author: penghuiping
 * @date: 2019/8/24 15:38
 * @description:
 */
public interface CompanyExRepository extends JdbcDbRepository<Company,Long> {
}
