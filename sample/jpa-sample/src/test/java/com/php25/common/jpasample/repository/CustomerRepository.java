package com.php25.common.jpasample.repository;

import com.php25.common.jpa.repository.BaseRepository;
import com.php25.common.jpasample.model.Customer;
import org.springframework.stereotype.Repository;

/**
 * Created by penghuiping on 2018/5/1.
 */
@Repository
public interface CustomerRepository extends BaseRepository<Customer, Long> {

}
