package com.php25.common.coresample;

import com.google.common.hash.Hashing;
import com.php25.common.core.util.DigestUtil;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.security.KeyPair;

/**
 * @Auther: penghuiping
 * @Date: 2018/6/20 10:35
 * @Description:
 */
public class DigestUtilTest {

    private static final Logger logger = LoggerFactory.getLogger(DigestUtilTest.class);

    @Test
    public void hash() {
        logger.info(DigestUtil.MD5Str("123123123"));

        logger.info(Hashing.md5().hashString("123123123", Charset.forName("utf-8")).toString());

        logger.info(DigestUtil.SHA256Str("123123123"));

        logger.info(Hashing.sha256().hashString("123123123", Charset.forName("utf-8")).toString());
    }



}
