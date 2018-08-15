package com.php25.common.jpasample.service.impl;

import com.php25.common.jpa.service.BaseJtaServiceImpl;
import com.php25.common.jpasample.dto.CustomerDto;
import com.php25.common.jpasample.model.Customer;
import com.php25.common.jpasample.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManagerFactory;


//@Service
public class CustomerJtaServiceImpl extends BaseJtaServiceImpl<CustomerDto, Customer, Long> implements CustomerService {

    @Autowired
    public CustomerJtaServiceImpl(EntityManagerFactory entityManagerFactory) {
        super(entityManagerFactory);
    }
}
