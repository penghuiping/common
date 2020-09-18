package com.php25.common.coresample;

import com.php25.common.core.util.RandomUtil;
import com.php25.common.core.util.crypto.AES;
import com.php25.common.core.util.crypto.DES;
import com.php25.common.core.util.crypto.constant.Mode;
import com.php25.common.core.util.crypto.constant.Padding;
import com.php25.common.core.util.crypto.key.SecretKeyUtil;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

/**
 * @author: penghuiping
 * @date: 2019/9/4 13:50
 * @description:
 */
public class SymmetricCryptoTest {

    private static final Logger log = LoggerFactory.getLogger(SymmetricCryptoTest.class);

    @Test
    public void DesTest1() {
        SecretKey secretKey = SecretKeyUtil.getDesKey("123123");
        IvParameterSpec ivParameterSpec = new IvParameterSpec(RandomUtil.randomBytes(8));
        DES aes = new DES(Mode.CBC, Padding.PKCS5Padding, secretKey, ivParameterSpec);
        String result = aes.encryptBase64("hello world".getBytes());
        log.info("DES加密结果:{}", result);
        String data = aes.decryptBase64Str(result);
        Assertions.assertThat("hello world").isEqualTo(data);
    }

    @Test
    public void DesTest2() {
        SecretKey secretKey = SecretKeyUtil.getDesKey("123123");
        DES aes = new DES(secretKey);
        String result = aes.encryptBase64("hello world".getBytes());
        log.info("DES加密结果:{}", result);
        String data = aes.decryptBase64Str(result);
        Assertions.assertThat("hello world").isEqualTo(data);
    }


    @Test
    public void AesTest1() {
        SecretKey secretKey = SecretKeyUtil.getAesKey("123123");
        IvParameterSpec ivParameterSpec = new IvParameterSpec(RandomUtil.randomBytes(16));
        AES aes = new AES(Mode.CBC, Padding.PKCS7Padding, secretKey, ivParameterSpec);
        String result = aes.encryptBase64("hello world".getBytes());
        log.info("AES加密结果:{}", result);
        String data = aes.decryptBase64Str(result);
        Assertions.assertThat("hello world").isEqualTo(data);
    }


    @Test
    public void AesTest2() {
        SecretKey secretKey = SecretKeyUtil.getAesKey("123123");
        AES aes = new AES(secretKey);
        String result = aes.encryptBase64("hello world".getBytes());
        log.info("AES加密结果:{}", result);
        String data = aes.decryptBase64Str(result);
        Assertions.assertThat("hello world").isEqualTo(data);
    }
}
