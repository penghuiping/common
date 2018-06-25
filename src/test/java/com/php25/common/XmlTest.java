package com.php25.common;

import com.php25.common.dto.CustomerDto;
import com.thoughtworks.xstream.XStream;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * @Auther: penghuiping
 * @Date: 2018/6/25 10:30
 * @Description:
 */
public class XmlTest {

    private Logger logger = LoggerFactory.getLogger(XmlTest.class);


    @Test
    public void objToXml() {
        CustomerDto customerDto = new CustomerDto();
        customerDto.setUsername("test");
        customerDto.setCreateTime(new Date());
        customerDto.setEnable(1);
        customerDto.setId(1l);
        customerDto.setPassword("1231312312333");
        customerDto.setUpdateTime(new Date());
        XStream xStream = new XStream();
        xStream.processAnnotations(CustomerDto.class);
        String xml = xStream.toXML(customerDto);
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
        XStream xStream = new XStream();
        xStream.processAnnotations(CustomerDto.class);
        String xml = xStream.toXML(customerDto);


        xStream.processAnnotations(CustomerDto.class);
        CustomerDto tmp = (CustomerDto) xStream.fromXML(xml);
        return;
    }
}
