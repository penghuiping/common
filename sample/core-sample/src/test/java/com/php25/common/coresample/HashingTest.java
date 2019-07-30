package com.php25.common.coresample;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.php25.common.CommonAutoConfigure;
import com.php25.common.core.service.ConsistentHashingService;
import com.php25.common.core.service.ConsistentHashingServiceImpl;
import com.php25.common.core.service.IdGeneratorService;
import org.assertj.core.util.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.nio.charset.Charset;
import java.security.SecureRandom;
import java.util.List;

/**
 * @Auther: penghuiping
 * @Date: 2018/8/16 16:05
 * @Description:
 */
@SpringBootTest(classes = {CommonAutoConfigure.class})
@RunWith(SpringRunner.class)
public class HashingTest {

    ConsistentHashingService consistentHashingService;

    @Autowired
    IdGeneratorService idGeneratorService;


    private Logger logger = LoggerFactory.getLogger(HashingTest.class);

    @Test
    public void consistentHashing() throws Exception {
        String[] servers = new String[]{"192.168.1.1", "192.168.1.2"};

        consistentHashingService = new ConsistentHashingServiceImpl(servers, 100);
        String serverIp = consistentHashingService.getServer("HELLOWORLD");
        logger.info("serverIp:" + serverIp);

        List<String> v = Lists.newArrayList();
        for (String server : servers) {
            for (int i = 0; i < 100; i++) {
                v.add(server + "&&VN" + i);
            }
        }
        HashCode hashCode = Hashing.crc32c().hashString("HELLOWORLD", Charset.defaultCharset());
        logger.info("HashCode:" + hashCode.asInt());
        logger.info("serverIp:" + v.get(Hashing.consistentHash(hashCode, v.size())));
    }

    @Test
    public void idGeneratorService() throws Exception {
        logger.info("snowflake:" + idGeneratorService.getSnowflakeId());
        logger.info("uuid:" + idGeneratorService.getUUID());
        logger.info("juid:"+idGeneratorService.getJUID());
    }

}
