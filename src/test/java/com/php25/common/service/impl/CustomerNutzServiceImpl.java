package com.php25.common.service.impl;

import com.php25.common.dto.CustomerDto;
import com.php25.common.model.Customer;
import com.php25.common.service.CustomerService;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

/**
 * Created by penghuiping on 2018/5/1.
 */
@Primary
@Service
public class CustomerNutzServiceImpl extends BaseNutzServiceImpl<CustomerDto, Customer, Long> implements CustomerService {

}
