package com.php25.common.jpasample.mysql.service;

import com.php25.common.core.service.SoftDeletable;
import com.php25.common.db.service.BaseServiceImpl;
import com.php25.common.jpasample.mysql.dto.CustomerDto;
import com.php25.common.jpasample.mysql.model.Customer;
import com.php25.common.jpasample.mysql.repository.CustomerRepository;
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
    public void setCustomerRepository(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
        this.baseRepository = customerRepository;
    }
}
