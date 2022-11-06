package com.php25.common.coresample;

import com.google.common.hash.Hashing;
import com.php25.common.core.util.DigestUtil;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

/**
 * @Auther: penghuiping
 * @Date: 2018/6/20 10:35
 * @Description:
 */
public class DigestUtilTest {

    private static final Logger logger = LoggerFactory.getLogger(DigestUtilTest.class);

    @Test
    public void hash() {
        Assertions.assertThat(DigestUtil.md5Str("123123123").toUpperCase()).isEqualTo("F5BB0C8DE146C67B44BABBF4E6584CC0");
        String tmp = Hashing.md5().hashString("123123123", StandardCharsets.UTF_8).toString().toUpperCase();
        Assertions.assertThat(tmp).isEqualTo("F5BB0C8DE146C67B44BABBF4E6584CC0");
        Assertions.assertThat(DigestUtil.sha256Str("123123123").toLowerCase()).isEqualTo("932f3c1b56257ce8539ac269d7aab42550dacf8818d075f0bdf1990562aae3ef");
        String tmp1 = Hashing.sha256().hashString("123123123", StandardCharsets.UTF_8).toString();
        Assertions.assertThat(tmp1.toLowerCase()).isEqualTo("932f3c1b56257ce8539ac269d7aab42550dacf8818d075f0bdf1990562aae3ef");
    }

    @Test
    public void sm3() {
        System.out.println(DigestUtil.sm3Str("aaaaa"));
    }


}
