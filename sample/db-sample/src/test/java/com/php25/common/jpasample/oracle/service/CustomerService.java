package com.php25.common.jpasample.oracle.service;

import com.php25.common.core.service.BaseService;
import com.php25.common.core.service.SoftDeletable;
import com.php25.common.jpasample.oracle.dto.CustomerDto;
import com.php25.common.jpasample.oracle.model.Customer;

/**
 * Created by penghuiping on 2018/5/1.
 */
public interface CustomerService extends BaseService<CustomerDto, Customer, Long>, SoftDeletable<CustomerDto> {

}
