package com.php25.common.core.util;

import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * 加密的一些帮助工具
 *
 * @author penghuiping
 * @date 2017-02-04
 */
public class DigestUtil {

    /**
     * Used to build output as Hex
     */
    private static final char[] DIGITS_LOWER = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd',
            'e', 'f'};
    private static org.slf4j.Logger logger = LoggerFactory.getLogger(DigestUtil.class);

    /**
     * MD5加密
     *
     * @param str 需要加密的字符串
     * @return byte[]  md5加密后的结果
     */
    private static byte[] MD5(String str) {
        Assert.hasText(str, "str不能为空");
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            return messageDigest.digest(str.getBytes("utf8"));
        } catch (Exception e) {
            throw new RuntimeException("出错啦!", e);
        }
    }

    /**
     * MD5 加密
     *
     * @param str 需要加密的字符串
     * @return string 直接返回32位的md5加密字符串
     */
    public static String MD5Str(String str) {
        return new String(DigestUtil.bytes2hex(DigestUtil.MD5(str)));
    }


    /**
     * SHA1 加密
     *
     * @param str 需要加密的字符串
     * @return 直接返回32位的SHA1 加密字符串
     */
    public static String SHAStr(String str) {
        return new String(DigestUtil.bytes2hex(DigestUtil.SHA(str, "sha-1")));
    }

    /**
     * SHA256 加密
     *
     * @param str 需要加密的字符串
     * @return 直接返回SHA256 加密字符串
     */
    public static String SHA256Str(String str) {
        return new String(DigestUtil.bytes2hex(DigestUtil.SHA(str, "sha-256")));
    }

    /**
     * sha1加密
     *
     * @param str 需要加密的字符串
     * @return 返回加密结果
     */
    private static byte[] SHA(String str, String shaAlgorithm) {
        Assert.hasText(str, "str不能为空");
        Assert.hasText(shaAlgorithm, "shaAlgorithm不能为空");
        try {
            MessageDigest md = MessageDigest.getInstance(shaAlgorithm);
            return md.digest(str.getBytes("utf8"));
        } catch (Exception e) {
            throw new RuntimeException("出错啦!", e);
        }

    }


    /**
     * DES算法加密
     *
     * @param source 需要加密的字符串
     * @param key    需要用来des加密的秘钥
     * @return 返回加密结果
     */
    public static String encryptDES(String source, String key) {
        Assert.hasText(source, "source不能为空");
        Assert.isTrue(!StringUtil.isBlank(key) && key.trim().length() >= 8, "key的长度至少是8");
        try {
            DESKeySpec desKeySpec = new DESKeySpec(key.getBytes());
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
            Cipher cipher = Cipher.getInstance("DES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return new String(DigestUtil.bytes2hex(cipher.doFinal(source.getBytes())));
        } catch (Exception e) {
            throw new RuntimeException("出错啦!", e);
        }
    }


    /**
     * DES算法解密
     *
     * @param source 需要解密的字符串
     * @param key    需要用来des解密的秘钥
     * @return 返回解密结果
     */
    public static String decryptDES(String source, String key) {
        Assert.hasText(source, "source不能为空");
        Assert.isTrue(!StringUtil.isBlank(key) && key.trim().length() >= 8, "key的长度至少是8");
        try {
            DESKeySpec desKeySpec = new DESKeySpec(key.getBytes());
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
            Cipher cipher = Cipher.getInstance("DES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] bytes = cipher.doFinal(DigestUtil.hex2bytes(source));
            return new String(bytes);
        } catch (Exception e) {
            throw new RuntimeException("出错啦!", e);
        }
    }


    /**
     * AES加密
     *
     * @param source 需要加密的字符串
     * @param key    需要加密的秘钥
     * @return 返回加密结果
     */
    public static String encryptAES(String source, String key) {
        Assert.hasText(source, "source不能为空");
        Assert.isTrue(!StringUtil.isBlank(key) && key.length() == 16 || key.length() == 24 || key.length() == 32, "key的长度需要16、24或者32");
        try {
            SecretKey secretKey = new SecretKeySpec(key.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return new String(DigestUtil.bytes2hex(cipher.doFinal(source.getBytes())));
        } catch (Exception e) {
            throw new RuntimeException("出错啦!", e);
        }

    }


    /**
     * AES解密
     *
     * @param source 需要解密的字符串
     * @param key    需要解密的秘钥
     * @return 返回解密结果
     */
    public static String decryptAES(String source, String key) {
        Assert.hasText(source, "source不能为空");
        Assert.isTrue(!StringUtil.isBlank(key) && key.length() == 16 || key.length() == 24 || key.length() == 32, "key的长度需要16、24或者32");
        try {
            SecretKey secretKey = new SecretKeySpec(key.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return new String(cipher.doFinal(DigestUtil.hex2bytes(source)));
        } catch (Exception e) {
            throw new RuntimeException("出错啦!", e);
        }
    }

    /**
     * 生成RSA算法密钥队
     *
     * @return RSA秘钥对
     */
    public static KeyPair getKeyPair() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(512);
            return keyPairGenerator.generateKeyPair();
        } catch (Exception e) {
            throw new RuntimeException("出错啦!", e);
        }

    }

    /**
     * 获得RSA算法公钥
     *
     * @param keyPair RSA秘钥对
     * @return 返回RSA算法公钥字符串
     */
    public static String getPublicKey(KeyPair keyPair) {
        Assert.notNull(keyPair, "keyPair不能为null");
        try {
            PublicKey publicKey = keyPair.getPublic();
            byte[] bytes = publicKey.getEncoded();
            return Base64.getEncoder().encodeToString(bytes);
        } catch (Exception e) {
            throw new RuntimeException("出错啦!", e);
        }
    }

    /**
     * 获得RSA算法密钥
     *
     * @param keyPair RSA密钥对
     * @return 返回RSA算法密钥字符串
     */
    public static String getPrivateKey(KeyPair keyPair) {
        Assert.notNull(keyPair, "keyPair不能为null");
        try {
            PrivateKey privateKey = keyPair.getPrivate();
            byte[] bytes = privateKey.getEncoded();
            return Base64.getEncoder().encodeToString(bytes);
        } catch (Exception e) {
            throw new RuntimeException("出错啦!", e);
        }
    }

    /**
     * 加载公钥
     *
     * @param pubStr 公钥字符串
     * @return 公钥
     */
    private static PublicKey loadPublicKey(String pubStr) {
        Assert.hasText(pubStr, "pubStr不能为空");
        try {
            byte[] keyBytes = Base64.getDecoder().decode(pubStr);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePublic(keySpec);
        } catch (Exception e) {
            throw new RuntimeException("出错啦!", e);
        }
    }

    /**
     * 加载秘钥
     *
     * @param priStr 密钥字符串
     * @return 密钥
     */
    private static PrivateKey loadPrivateKey(String priStr) {
        Assert.hasText(priStr, "priStr不能为空");
        try {
            byte[] keyBytes = Base64.getDecoder().decode(priStr);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePrivate(keySpec);
        } catch (Exception e) {
            throw new RuntimeException("出错啦!", e);
        }
    }


    /**
     * RSA算法加密
     *
     * @param content   需要加密的内容
     * @param publicKey 公钥
     * @return 加密后的内容
     */
    public static String publicEncrypt(String content, String publicKey) {
        Assert.notNull(content, "content不能为null");
        Assert.notNull(publicKey, "publicKey不能为null");
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, DigestUtil.loadPublicKey(publicKey));
            return new String(DigestUtil.bytes2hex(cipher.doFinal(content.getBytes())));
        } catch (Exception e) {
            throw new RuntimeException("出错啦!", e);
        }
    }


    /**
     * RSA算法解密
     *
     * @param content    需要解密的内容
     * @param privateKey 私钥
     * @return 解密后的内容
     */
    public static String privateDecrypt(String content, String privateKey) {
        Assert.notNull(content, "content不能为null");
        Assert.notNull(privateKey, "privateKey不能为null");
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, DigestUtil.loadPrivateKey(privateKey));
            return new String(cipher.doFinal(DigestUtil.hex2bytes(content)));
        } catch (Exception e) {
            throw new RuntimeException("出错啦!", e);
        }
    }


    /**
     * 进行数字签名
     *
     * @param content    需要签名的内容
     * @param privateKey 私要
     * @return 返回签名结果
     */
    public static String sign(String content, String privateKey) {
        Assert.notNull(content, "content不能为null");
        Assert.notNull(privateKey, "privateKey不能为null");
        try {
            Signature signature = Signature.getInstance("SHA1withRSA");
            signature.initSign(DigestUtil.loadPrivateKey(privateKey));
            signature.update(content.getBytes());
            return new String(DigestUtil.bytes2hex(signature.sign()));
        } catch (Exception e) {
            throw new RuntimeException("出错啦!", e);
        }
    }

    /**
     * 校验签名
     *
     * @param content   需要签名的内容
     * @param sign      签名
     * @param publicKey 公钥
     * @return true:签名合法;false:签名不合法
     */
    public static boolean verify(String content, String sign, String publicKey) {
        Assert.notNull(content, "content不能为null");
        Assert.notNull(sign, "sign不能为null");
        Assert.notNull(publicKey, "publicKey不能为null");
        try {
            Signature signature = Signature.getInstance("SHA1withRSA");
            signature.initVerify(DigestUtil.loadPublicKey(publicKey));
            signature.update(content.getBytes());
            return signature.verify(DigestUtil.hex2bytes(sign));
        } catch (Exception e) {
            throw new RuntimeException("出错啦!", e);
        }
    }

    /**
     * 2进制转16进制
     *
     * @param data 把字节流转换成16进制的字符
     * @return 16进制字符
     */
    public static char[] bytes2hex(final byte[] data) {
        Assert.notNull(data, "data不能为null");
        final int l = data.length;
        final char[] out = new char[l << 1];
        // two characters form the hex value.
        for (int i = 0, j = 0; i < l; i++) {
            out[j++] = DIGITS_LOWER[(0xF0 & data[i]) >>> 4];
            out[j++] = DIGITS_LOWER[0x0F & data[i]];
        }
        return out;
    }

    /**
     * 16进制转2进制
     *
     * @param hexStr 16进制字符串
     * @return 2进制字节数组
     */
    public static byte[] hex2bytes(String hexStr) {
        Assert.hasText(hexStr, "hexStr不能为空");
        byte[] result = new byte[hexStr.length() / 2];
        for (int i = 0; i < hexStr.length() / 2; i++) {
            int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
            int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), 16);
            result[i] = (byte) (high * 16 + low);
        }
        return result;
    }

    /**
     * base64 加密
     *
     * @param data 需要加密的字节数据
     * @return 加密后的字符串
     */
    public static String encodeBase64(byte[] data) {
        Assert.notNull(data, "data不能为null");
        return Base64.getEncoder().encodeToString(data);
    }

    /**
     * base64 解密
     *
     * @param text 需要解密的内容
     * @return 解密后的字节数据
     */
    public static byte[] decodeBase64(String text) {
        Assert.hasText(text, "text不能为空");
        return Base64.getDecoder().decode(text);
    }
}