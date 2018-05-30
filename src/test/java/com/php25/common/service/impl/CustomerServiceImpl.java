package com.php25.common.service.impl;

import com.php25.common.dto.CustomerDto;
import com.php25.common.model.Customer;
import com.php25.common.repository.CustomerRepository;
import com.php25.common.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

/**
 * Created by penghuiping on 2018/5/1.
 */
@Primary
@Service
public class CustomerServiceImpl extends BaseServiceImpl<CustomerDto, Customer,Long> implements CustomerService {

    private CustomerRepository customerRepository;

    @Autowired
    public void setAdminUserRepository(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
        this.baseRepository = customerRepository;
    }
}
