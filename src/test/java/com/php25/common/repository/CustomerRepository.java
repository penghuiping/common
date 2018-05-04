package com.php25.common.repository;

import com.php25.common.model.Customer;
import org.springframework.stereotype.Repository;

/**
 * Created by penghuiping on 2018/5/1.
 */
@Repository
public interface CustomerRepository extends BaseRepository<Customer,Long> {

}
