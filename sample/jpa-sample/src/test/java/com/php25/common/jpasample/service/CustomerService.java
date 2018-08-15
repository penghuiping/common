package com.php25.common.jpasample.service;

import com.php25.common.core.service.BaseService;
import com.php25.common.core.service.SoftDeletable;
import com.php25.common.jpasample.dto.CustomerDto;
import com.php25.common.jpasample.model.Customer;

/**
 * Created by penghuiping on 2018/5/1.
 */
public interface CustomerService extends BaseService<CustomerDto, Customer, Long>, SoftDeletable<CustomerDto> {

}
