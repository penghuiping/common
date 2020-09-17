package com.php25.common.coresample;

import com.fasterxml.jackson.core.type.TypeReference;
import com.php25.common.core.util.XmlUtil;
import com.php25.common.coresample.dto.CustomerDto;
import org.assertj.core.util.Lists;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;

/**
 * @Auther: penghuiping
 * @Date: 2018/6/25 10:30
 * @Description:
 */
public class XmlTest {

    private Logger logger = LoggerFactory.getLogger(XmlTest.class);

    @Test
    public void objToXml() throws Exception {
        CustomerDto customerDto = new CustomerDto();
        customerDto.setUsername("test");
        customerDto.setCreateTime(new Date());
        customerDto.setEnable(1);
        customerDto.setId(1l);
        customerDto.setPassword("1231312312333");
        customerDto.setUpdateTime(new Date());
        String xml = XmlUtil.toXml(customerDto);
        logger.info("===========>xml:" + xml);
    }

    @Test
    public void xmlToObj() {
        CustomerDto customerDto = new CustomerDto();
        customerDto.setUsername("test");
        customerDto.setCreateTime(new Date());
        customerDto.setEnable(1);
        customerDto.setId(1l);
        customerDto.setPassword("1231312312333");
        customerDto.setUpdateTime(new Date());

        CustomerDto customerDto1 = new CustomerDto();
        customerDto1.setUsername("test1");
        customerDto1.setCreateTime(new Date());
        customerDto1.setEnable(1);
        customerDto1.setId(2l);
        customerDto1.setPassword("1231312312333");
        customerDto1.setUpdateTime(new Date());

        String xml = XmlUtil.toXml(Lists.newArrayList(customerDto, customerDto1));
        logger.info(xml);

        List<CustomerDto> tmp = XmlUtil.fromXml(xml, new TypeReference<List<CustomerDto>>() {
        });

        xml = XmlUtil.toPrettyXml(tmp);
        logger.info(xml);
    }
}
