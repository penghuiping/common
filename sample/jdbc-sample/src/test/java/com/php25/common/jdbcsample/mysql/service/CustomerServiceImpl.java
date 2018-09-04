package com.php25.common.jdbcsample.mysql.service;

import com.php25.common.core.service.SoftDeletable;
import com.php25.common.jdbc.service.BaseServiceImpl;
import com.php25.common.jdbcsample.mysql.dto.CustomerDto;
import com.php25.common.jdbcsample.mysql.model.Customer;
import com.php25.common.jdbcsample.mysql.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

/**
 * Created by penghuiping on 2018/5/1.
 */
@Primary
@Service
public class CustomerServiceImpl extends BaseServiceImpl<CustomerDto, Customer, Long> implements CustomerService, SoftDeletable<CustomerDto> {

    @Autowired
    private CustomerRepository customerRepository;

}
