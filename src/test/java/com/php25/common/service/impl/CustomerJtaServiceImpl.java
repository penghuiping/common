package com.php25.common.service.impl;

import com.php25.common.dto.CustomerDto;
import com.php25.common.model.Customer;
import com.php25.common.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManagerFactory;


//@Service
public class CustomerJtaServiceImpl extends BaseJtaServiceImpl<CustomerDto, Customer, Long> implements CustomerService {

    @Autowired
    public CustomerJtaServiceImpl(EntityManagerFactory entityManagerFactory) {
        super(entityManagerFactory);
    }
}
