package com.php25.common.jdbcsample.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.php25.common.jdbcsample.mapper.CompanyMapper;
import com.php25.common.jdbcsample.model.Company;
import org.springframework.stereotype.Service;

/**
 * @author: penghuiping
 * @date: 2018/11/15 09:43
 * @description:
 */
@Service
public class CompanyServiceImpl extends ServiceImpl<CompanyMapper, Company> implements CompanyService {
}
