package com.php25.common.jdbcsample.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.php25.common.jdbcsample.model.Company;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * @author: penghuiping
 * @date: 2018/11/14 14:06
 * @description:
 */
public interface CompanyMapper extends BaseMapper<Company> {

    @Select("select * from t_company a where a.name=#{name}")
    public Company queryByName(String name);

    @Select("select * from t_customer a join t_company b on a.company_id=b.id where b.name=#{name}")
    public List<Map> join(String name);
}
