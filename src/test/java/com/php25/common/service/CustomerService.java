package com.php25.common.service;

import com.php25.common.dto.CustomerDto;
import com.php25.common.model.Customer;

/**
 * Created by penghuiping on 2018/5/1.
 */
public interface CustomerService extends BaseService<CustomerDto, Customer, Long>, SoftDeletable<CustomerDto> {

}
