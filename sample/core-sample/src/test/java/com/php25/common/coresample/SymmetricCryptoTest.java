package com.php25.common.coresample;

import com.php25.common.core.util.RandomUtil;
import com.php25.common.core.util.crypto.AES;
import com.php25.common.core.util.crypto.DES;
import com.php25.common.core.util.crypto.SM4;
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

    @Test
    public void SM4Test1() {
        SecretKey secretKey = SecretKeyUtil.getSM4Key("123123");
        IvParameterSpec ivParameterSpec = new IvParameterSpec(RandomUtil.randomBytes(16));
        SM4 sm4 = new SM4(Mode.CBC, Padding.PKCS5Padding, secretKey, ivParameterSpec);
        String result = sm4.encryptBase64("hello world".getBytes());
        log.info("SM4加密结果:{}", result);
        String data = sm4.decryptBase64Str(result);
        Assertions.assertThat("hello world").isEqualTo(data);
    }

    @Test
    public void SM4Test2() {
        SecretKey secretKey = SecretKeyUtil.getSM4Key("123123");
        SM4 sm4 = new SM4(secretKey);
        String result = sm4.encryptBase64("hello world".getBytes());
        log.info("SM4加密结果:{}", result);
        String data = sm4.decryptBase64Str(result);
        Assertions.assertThat("hello world").isEqualTo(data);
    }

}
