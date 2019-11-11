package com.php25.common.coresample;

import com.php25.common.core.util.DigestUtil;
import com.php25.common.core.util.RandomUtil;
import com.php25.common.core.util.crypto.AES;
import com.php25.common.core.util.crypto.DES;
import com.php25.common.core.util.crypto.constant.Mode;
import com.php25.common.core.util.crypto.constant.Padding;
import com.php25.common.core.util.crypto.key.SecretKeyUtil;
import org.junit.Test;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

/**
 * @author: penghuiping
 * @date: 2019/9/4 13:50
 * @description:
 */
public class SymmetricCryptoTest {

    @Test
    public void DesTest1() {
        SecretKey secretKey = SecretKeyUtil.getDesKey("123123");
        IvParameterSpec ivParameterSpec = new IvParameterSpec(RandomUtil.randomBytes(8));
        DES aes = new DES(Mode.CBC, Padding.PKCS5Padding, secretKey, ivParameterSpec);
        String result = aes.encryptBase64("hello world".getBytes());
        System.out.println(result);
        String data = aes.decryptBase64Str(result);
        System.out.println(data);
    }

    @Test
    public void DesTest2() {
        SecretKey secretKey = SecretKeyUtil.getDesKey("123123");
        System.out.print(DigestUtil.encodeBase64(secretKey.getEncoded()));
        DES aes = new DES(secretKey);
        String result = aes.encryptBase64("hello world".getBytes());
        System.out.println(result);
        String data = aes.decryptBase64Str(result);
        System.out.println(data);
    }


    @Test
    public void AesTest1() {
        SecretKey secretKey = SecretKeyUtil.getAesKey("123123");
        IvParameterSpec ivParameterSpec = new IvParameterSpec(RandomUtil.randomBytes(16));
        AES aes = new AES(Mode.CBC, Padding.PKCS7Padding, secretKey, ivParameterSpec);
        String result = aes.encryptBase64("hello world".getBytes());
        System.out.println(result);
        String data = aes.decryptBase64Str(result);
        System.out.println(data);
    }


    @Test
    public void AesTest2() {
        SecretKey secretKey = SecretKeyUtil.getAesKey("123123");
        System.out.print(DigestUtil.encodeBase64(secretKey.getEncoded()));
        AES aes = new AES(secretKey);
        String result = aes.encryptBase64("hello world".getBytes());
        System.out.println(result);
        String data = aes.decryptBase64Str(result);
        System.out.println(data);
    }
}
