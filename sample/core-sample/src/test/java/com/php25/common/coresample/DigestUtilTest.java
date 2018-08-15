package com.php25.common.coresample;

import com.php25.common.core.util.DigestUtil;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

        logger.info(DigestUtil.SHAStr("123123123"));
    }

    @Test
    public void AES() {
        String key = "1111111111111111";
        logger.info("key:" + key);
        String value = DigestUtil.encryptAES("hello world", key);
        logger.info("value:" + value);
        String result = DigestUtil.decryptAES(value, key);
        logger.info(result);
        Assert.assertEquals(result, "hello world");
    }

    @Test
    public void DES() {
        String key = "123456789";
        logger.info("key:" + key);
        String value = DigestUtil.encryptDES("hello world", key);
        logger.info("value:" + value);
        String result = DigestUtil.decryptDES(value, key);
        logger.info(result);
        Assert.assertEquals(result, "hello world");
    }

    @Test
    public void RSA() {
        KeyPair keyPair = DigestUtil.getKeyPair();
        String privateKey = DigestUtil.getPrivateKey(keyPair);
        String publicKey = DigestUtil.getPublicKey(keyPair);

        logger.info("privateKey:" + privateKey);
        logger.info("publicKey:" + publicKey);

        String encryptContent = DigestUtil.publicEncrypt("hello world", publicKey);
        String content = DigestUtil.privateDecrypt(encryptContent, privateKey);
        logger.info("content:" + content);
        Assert.assertEquals(content, "hello world");
    }


    @Test
    public void sign() {
        KeyPair keyPair = DigestUtil.getKeyPair();
        String privateKey = DigestUtil.getPrivateKey(keyPair);
        String publicKey = DigestUtil.getPublicKey(keyPair);

        logger.info("privateKey:" + privateKey);
        logger.info("publicKey:" + publicKey);

        String sign = DigestUtil.sign("hello world", privateKey);
        logger.info("sign:" + sign);
        Boolean result = DigestUtil.verify("hello world", sign, publicKey);
        Assert.assertEquals(result, true);
    }
}
