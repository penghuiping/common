package com.php25.common.core.mess;


import com.fasterxml.uuid.EthernetAddress;
import com.fasterxml.uuid.Generators;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * id生成器
 *
 * @author penghuiping
 * @date 2017/9/18
 */
@Service("idGeneratorImpl")
public class IdGeneratorImpl implements IdGenerator {

    @Lazy
    @Autowired
    private SnowflakeIdWorker snowflakeIdWorker;


    @Override
    public String getUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    @Override
    public String getJUID() {
        return Generators.timeBasedGenerator(EthernetAddress.fromInterface()).generate().toString().replaceAll("-", "");
    }

    @Override
    public Long getSnowflakeId() {
        return snowflakeIdWorker.nextId();
    }
}
