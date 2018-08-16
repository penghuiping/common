package com.php25.common.core.util;

import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * 加密的一些帮助工具
 *
 * @author penghuiping
 * @Time 2017-02-04
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
     * @param str
     * @return String
     * @author penghuiping
     * @Time 2017-02-04
     */
    private static byte[] MD5(String str) {
        try {
            if (str == null) {
                return null;
            }
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            return messageDigest.digest(str.getBytes("utf8"));
        } catch (Exception e) {
            logger.error("出错啦!", e);
            return null;
        }
    }

    /**
     * MD5 加密
     *
     * @param str
     * @return 直接返回32位的md5加密字符串
     */
    public static String MD5Str(String str) {
        return new String(DigestUtil.bytes2hex(DigestUtil.MD5(str)));
    }


    /**
     * SHA1 加密
     *
     * @param str
     * @return 直接返回32位的SHA1 加密字符串
     */
    public static String SHAStr(String str) {
        return new String(DigestUtil.bytes2hex(DigestUtil.SHA(str, "sha-1")));
    }

    /**
     * SHA256 加密
     *
     * @param str
     * @return 直接返回32位的SHA256 加密字符串
     */
    public static String SHA256Str(String str) {
        return new String(DigestUtil.bytes2hex(DigestUtil.SHA(str, "sha-256")));
    }

    /**
     * sha1加密
     *
     * @param str
     * @return
     * @author penghuiping
     * @Time 2017-02-04
     */
    private static byte[] SHA(String str, String shaAlgorithm) {
        try {
            if (str == null) {
                return null;
            }
            MessageDigest md = MessageDigest.getInstance(shaAlgorithm);
            return md.digest(str.getBytes("utf8"));
        } catch (Exception e) {
            logger.error("出错啦!", e);
            return null;
        }

    }


    /**
     * DES算法加密
     *
     * @param source
     * @param key
     * @return
     * @throws Exception
     * @author penghuiping
     * @Time 2017-02-04
     */
    public static String encryptDES(String source, String key) {
        try {
            Assert.isTrue(!StringUtil.isBlank(key) && key.trim().length() >= 8, "key的长度至少是8");
            DESKeySpec desKeySpec = new DESKeySpec(key.getBytes());
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey secretKey = keyFactory.generateSecret(desKeySpec);

            Cipher cipher = Cipher.getInstance("DES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return new String(DigestUtil.bytes2hex(cipher.doFinal(source.getBytes())));
        } catch (Exception e) {
            logger.error("出错啦!", e);
            return null;
        }
    }


    /**
     * DES算法解密
     *
     * @param source
     * @param key
     * @return
     * @throws Exception
     * @author penghuiping
     * @Time 2017-02-04
     */
    public static String decryptDES(String source, String key) {
        try {
            Assert.isTrue(!StringUtil.isBlank(key) && key.trim().length() >= 8, "key的长度至少是8");
            DESKeySpec desKeySpec = new DESKeySpec(key.getBytes());
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey secretKey = keyFactory.generateSecret(desKeySpec);

            Cipher cipher = Cipher.getInstance("DES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] bytes = cipher.doFinal(DigestUtil.hex2bytes(source));
            return new String(bytes);
        } catch (Exception e) {
            logger.error("出错啦!", e);
            return null;
        }
    }


    /**
     * AES加密
     *
     * @param source
     * @param key
     * @return
     * @throws Exception
     * @author penghuiping
     * @Time 2017-02-04
     */
    public static String encryptAES(String source, String key) {
        try {
            Assert.isTrue(!StringUtil.isBlank(key) && key.length() == 16 || key.length() == 24 || key.length() == 32, "key的长度需要16、24或者32");
            SecretKey secretKey = new SecretKeySpec(key.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return new String(DigestUtil.bytes2hex(cipher.doFinal(source.getBytes())));
        } catch (Exception e) {
            logger.error("出错啦!", e);
            return null;
        }

    }


    /**
     * AES解密
     *
     * @param source
     * @param key
     * @return
     * @throws Exception
     * @author penghuiping
     * @Time 2017-02-04
     */
    public static String decryptAES(String source, String key) {
        try {
            Assert.isTrue(!StringUtil.isBlank(key) && key.length() == 16 || key.length() == 24 || key.length() == 32, "key的长度需要16、24或者32");
            SecretKey secretKey = new SecretKeySpec(key.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return new String(cipher.doFinal(DigestUtil.hex2bytes(source)));
        } catch (Exception e) {
            logger.error("出错啦!", e);
            return null;
        }
    }

    /**
     * 生成RSA算法密钥队
     *
     * @return
     * @throws Exception
     * @author penghuiping
     * @Time 2017-02-04
     */
    public static KeyPair getKeyPair() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(512);
            return keyPairGenerator.generateKeyPair();
        } catch (Exception e) {
            logger.error("出错啦!", e);
            return null;
        }

    }

    /**
     * 获得RSA算法公钥
     *
     * @param keyPair
     * @return
     * @author penghuiping
     * @Time 2017-02-04
     */
    public static String getPublicKey(KeyPair keyPair) {
        try {
            PublicKey publicKey = keyPair.getPublic();
            byte[] bytes = publicKey.getEncoded();
            return Base64.getEncoder().encodeToString(bytes);
        } catch (Exception e) {
            logger.error("出错啦!", e);
            return null;
        }
    }

    /**
     * 获得RSA算法秘钥
     *
     * @param keyPair
     * @return
     * @author penghuiping
     * @Time 2017-02-04
     */
    public static String getPrivateKey(KeyPair keyPair) {
        try {
            PrivateKey privateKey = keyPair.getPrivate();
            byte[] bytes = privateKey.getEncoded();
            return Base64.getEncoder().encodeToString(bytes);
        } catch (Exception e) {
            logger.error("出错啦!", e);
            return null;
        }
    }

    /**
     * 加载公钥
     *
     * @param pubStr
     * @return
     * @throws Exception
     * @author penghuiping
     * @Time 2017-02-04
     */
    private static PublicKey loadPublicKey(String pubStr) {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(pubStr);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey publicKey = keyFactory.generatePublic(keySpec);
            return publicKey;
        } catch (Exception e) {
            logger.error("出错啦!", e);
            return null;
        }
    }

    /**
     * 加载秘钥
     *
     * @param priStr
     * @return
     * @throws Exception
     * @author penghuiping
     * @Time 2017-02-04
     */
    private static PrivateKey loadPrivateKey(String priStr) {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(priStr);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
            return privateKey;
        } catch (Exception e) {
            logger.error("出错啦!", e);
            return null;
        }
    }

    /**
     * RSA算法加密
     *
     * @param content
     * @param publicKey
     * @return
     * @throws Exception
     * @author penghuiping
     * @Time 2017-02-04
     */
    private static byte[] publicEncrypt(byte[] content, PublicKey publicKey) {
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            return cipher.doFinal(content);
        } catch (Exception e) {
            logger.error("出错啦!", e);
            return null;
        }
    }

    /**
     * RSA算法加密
     *
     * @param content
     * @param publicKey
     * @return
     * @throws Exception
     * @author penghuiping
     * @Time 2017-02-04
     */
    public static String publicEncrypt(String content, String publicKey) {
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, DigestUtil.loadPublicKey(publicKey));
            return new String(DigestUtil.bytes2hex(cipher.doFinal(content.getBytes())));
        } catch (Exception e) {
            logger.error("出错啦!", e);
            return null;
        }
    }

    /**
     * RSA算法解密
     *
     * @param content
     * @param privateKey
     * @return
     * @throws Exception
     * @author penghuiping
     * @Time 2017-02-04
     */
    private static byte[] privateDecrypt(byte[] content, PrivateKey privateKey) {
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            return cipher.doFinal(content);
        } catch (Exception e) {
            logger.error("出错啦!", e);
            return null;
        }
    }

    /**
     * RSA算法解密
     *
     * @param content
     * @param privateKey
     * @return
     * @throws Exception
     * @author penghuiping
     * @Time 2017-02-04
     */
    public static String privateDecrypt(String content, String privateKey) {
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, DigestUtil.loadPrivateKey(privateKey));
            return new String(cipher.doFinal(DigestUtil.hex2bytes(content)));
        } catch (Exception e) {
            logger.error("出错啦!", e);
            return null;
        }
    }

    /**
     * 进行数字签名
     *
     * @param content
     * @param privateKey
     * @return
     * @throws Exception
     * @author penghuiping
     * @Time 2017-02-04
     */
    private static byte[] sign(byte[] content, PrivateKey privateKey) {
        try {
            Signature signature = Signature.getInstance("SHA1withRSA");
            signature.initSign(privateKey);
            signature.update(content);
            return signature.sign();
        } catch (Exception e) {
            logger.error("出错啦!", e);
            return null;
        }
    }

    /**
     * 进行数字签名
     *
     * @param content
     * @param privateKey
     * @return
     * @throws Exception
     * @author penghuiping
     * @Time 2017-02-04
     */
    public static String sign(String content, String privateKey) {
        try {
            Signature signature = Signature.getInstance("SHA1withRSA");
            signature.initSign(DigestUtil.loadPrivateKey(privateKey));
            signature.update(content.getBytes());
            return new String(DigestUtil.bytes2hex(signature.sign()));
        } catch (Exception e) {
            logger.error("出错啦!", e);
            return null;
        }
    }

    /**
     * 校验签名
     *
     * @param content
     * @param sign
     * @param publicKey
     * @return
     * @throws Exception
     * @author penghuiping
     * @Time 2017-02-04
     */
    private static boolean verify(byte[] content, byte[] sign, PublicKey publicKey) {
        try {
            Signature signature = Signature.getInstance("SHA1withRSA");
            signature.initVerify(publicKey);
            signature.update(content);
            return signature.verify(sign);
        } catch (Exception e) {
            logger.error("出错啦!", e);
            return false;
        }
    }

    /**
     * 校验签名
     *
     * @param content
     * @param sign
     * @param publicKey
     * @return
     * @throws Exception
     * @author penghuiping
     * @Time 2017-02-04
     */
    public static boolean verify(String content, String sign, String publicKey) {
        try {
            Signature signature = Signature.getInstance("SHA1withRSA");
            signature.initVerify(DigestUtil.loadPublicKey(publicKey));
            signature.update(content.getBytes());
            return signature.verify(DigestUtil.hex2bytes(sign));
        } catch (Exception e) {
            logger.error("出错啦!", e);
            return false;
        }
    }

    /**
     * 2进制转16进制
     *
     * @param data a byte[] to convert to Hex characters
     * @return A char[] containing hexadecimal characters
     * @author penghuiping
     * @Time 2017-02-04
     */
    public static char[] bytes2hex(final byte[] data) {
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
     * @param hexStr
     * @return
     * @author penghuiping
     * @Time 2017-02-04
     */
    public static byte[] hex2bytes(String hexStr) {
        if (hexStr.length() < 1)
            return null;
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
     * @param data
     * @return
     * @author penghuiping
     * @Time 2017-02-04
     */
    public static String encodeBase64(byte[] data) {
        return Base64.getEncoder().encodeToString(data);
    }

    /**
     * base64 解密
     *
     * @param text
     * @return
     * @author penghuiping
     * @Time 2017-02-04
     */
    public static byte[] decodeBase64(String text) {
        return Base64.getDecoder().decode(text);
    }
}
