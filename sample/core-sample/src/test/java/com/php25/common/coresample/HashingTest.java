package com.php25.common.coresample;

import com.google.common.base.Charsets;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.php25.common.core.mess.ConsistentHashing;
import com.php25.common.core.mess.ConsistentHashingImpl;
import com.php25.common.core.mess.IdGenerator;
import com.php25.common.core.mess.IdGeneratorImpl;
import com.php25.common.core.util.HashUtil;
import org.assertj.core.util.Lists;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.util.List;

/**
 * @Auther: penghuiping
 * @Date: 2018/8/16 16:05
 * @Description:
 */

public class HashingTest {

    ConsistentHashing consistentHashingService;

    IdGenerator idGeneratorService = new IdGeneratorImpl();

    private static Logger log = LoggerFactory.getLogger(HashingTest.class);

    @Test
    public void consistentHashing() throws Exception {
        String[] servers = new String[]{"192.168.1.1", "192.168.1.2"};

        consistentHashingService = new ConsistentHashingImpl(servers, 100);
        String serverIp = consistentHashingService.getServer("HELLOWORLD");
        log.info("serverIp:" + serverIp);
        List<String> v = Lists.newArrayList();
        for (String server : servers) {
            for (int i = 0; i < 100; i++) {
                v.add(server + "&&VN" + i);
            }
        }
        HashCode hashCode = Hashing.crc32c().hashString("HELLOWORLD", Charset.defaultCharset());
        log.info("HashCode:" + hashCode.asInt());
        log.info("serverIp:" + v.get(Hashing.consistentHash(hashCode, v.size())));
    }

    @Test
    public void murmur128() {
        String hello = "hello";
        long value1 = Hashing.murmur3_128().hashBytes(hello.getBytes(Charsets.UTF_8)).asLong();
        log.info("guava:{},{}", (int) value1, (int) (value1 >>> 32));
        log.info("hashUtil:{}", HashUtil.murmur128(hello.getBytes(Charsets.UTF_8)));
    }

    @Test
    public void idGeneratorService() throws Exception {
        log.info("uuid:" + idGeneratorService.getUUID());
        log.info("juid:" + idGeneratorService.getJUID());
    }

}
