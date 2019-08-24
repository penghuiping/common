package com.php25.common.jpasample.mysql.service;

import com.php25.common.core.service.BaseAsyncService;
import com.php25.common.core.service.BaseService;
import com.php25.common.core.service.SoftDeletable;
import com.php25.common.jpasample.mysql.dto.CustomerDto;
import com.php25.common.jpasample.mysql.model.Customer;

/**
 * Created by penghuiping on 2018/5/1.
 */
public interface CustomerService extends BaseService<CustomerDto, Customer, Long>, BaseAsyncService<CustomerDto, Customer, Long>, SoftDeletable<CustomerDto> {

}
