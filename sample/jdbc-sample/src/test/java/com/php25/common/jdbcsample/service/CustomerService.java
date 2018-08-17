package com.php25.common.jdbcsample.service;

import com.php25.common.core.service.BaseService;
import com.php25.common.core.service.SoftDeletable;
import com.php25.common.jdbcsample.dto.CustomerDto;
import com.php25.common.jdbcsample.model.Customer;

/**
 * Created by penghuiping on 2018/5/1.
 */
public interface CustomerService extends BaseService<CustomerDto, Customer, Long>, SoftDeletable<CustomerDto> {

}
