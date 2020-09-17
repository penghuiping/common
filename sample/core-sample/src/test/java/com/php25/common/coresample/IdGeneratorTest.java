package com.php25.common.coresample;

import com.php25.common.core.mess.IdGenerator;
import com.php25.common.core.mess.IdGeneratorImpl;
import com.php25.common.core.mess.SnowflakeIdWorker;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author penghuiping
 * @date 2020/9/17 16:48
 */
public class IdGeneratorTest {

    private static final Logger log = LoggerFactory.getLogger(IdGeneratorTest.class);

    IdGenerator idGeneratorService = new IdGeneratorImpl();

    SnowflakeIdWorker snowflakeIdWorker = new SnowflakeIdWorker();

    @Test
    public void idGenerator() throws Exception {
        Assertions.assertThat(idGeneratorService.getUUID()).isNotBlank();
        Assertions.assertThat(idGeneratorService.getUUID().length()).isEqualTo(32);
        Assertions.assertThat(idGeneratorService.getJUID()).isNotBlank();
        Assertions.assertThat(idGeneratorService.getJUID().length()).isEqualTo(32);
        log.info("uuid:" + idGeneratorService.getUUID());
        log.info("juid:" + idGeneratorService.getJUID());
    }

    @Test
    public void snowflakeId() {
        Assertions.assertThat(snowflakeIdWorker.nextId()).isGreaterThan(0);
        log.info("snowflakeId:{}",snowflakeIdWorker.nextId());
    }
}
