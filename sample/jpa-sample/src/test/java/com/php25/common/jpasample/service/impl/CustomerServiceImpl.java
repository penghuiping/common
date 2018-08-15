package com.php25.common.jpasample.service.impl;

import com.php25.common.core.service.SoftDeletable;
import com.php25.common.jpa.service.BaseServiceImpl;
import com.php25.common.jpasample.dto.CustomerDto;
import com.php25.common.jpasample.model.Customer;
import com.php25.common.jpasample.repository.CustomerRepository;
import com.php25.common.jpasample.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

/**
 * Created by penghuiping on 2018/5/1.
 */
@Primary
@Service
public class CustomerServiceImpl extends BaseServiceImpl<CustomerDto, Customer, Long> implements CustomerService, SoftDeletable<CustomerDto> {

    private CustomerRepository customerRepository;

    @Autowired
    public void setAdminUserRepository(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
        this.baseRepository = customerRepository;
    }
}
