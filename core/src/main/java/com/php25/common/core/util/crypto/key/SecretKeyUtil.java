package com.php25.common.core.util.crypto.key;

import com.php25.common.core.exception.Exceptions;
import com.php25.common.core.util.AssertUtil;
import com.php25.common.core.util.StringUtil;
import com.php25.common.core.util.crypto.constant.GlobalBouncyCastleProvider;
import com.php25.common.core.util.crypto.constant.RsaAlgorithm;
import com.php25.common.core.util.crypto.constant.SignAlgorithm;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * @author penghuiping
 * @date 2019/9/4 15:34
 */
public abstract class SecretKeyUtil {
    /**
     * 生成加密秘钥 16位秘钥 AES
     *
     * @return
     */
    public static SecretKeySpec getAesKey(final String password) {
        //返回生成指定算法密钥生成器的 KeyGenerator 对象
        KeyGenerator kg = null;
        try {
            kg = getKeyGenerator("AES");
            //AES 要求密钥长度为 128、192、256
            SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
            secureRandom.setSeed(password.getBytes());
            kg.init(128, secureRandom);
            //生成一个密钥
            SecretKey secretKey = kg.generateKey();
            // 转换为AES专用密钥
            return new SecretKeySpec(secretKey.getEncoded(), "AES");
        } catch (NoSuchAlgorithmException e) {
            throw Exceptions.throwIllegalStateException("生成AES秘钥失败!", e);
        }
    }

    /**
     * 生成加密秘钥 8位秘钥 DES
     *
     * @return
     */
    public static SecretKeySpec getDesKey(final String password) {
        //返回生成指定算法密钥生成器的 KeyGenerator 对象
        KeyGenerator kg = null;
        try {
            kg = getKeyGenerator("DES");
            //DES 要求密钥长度为 64
            SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
            secureRandom.setSeed(password.getBytes());
            kg.init(64, secureRandom);
            //生成一个密钥
            SecretKey secretKey = kg.generateKey();
            // 转换为DES专用密钥
            return new SecretKeySpec(secretKey.getEncoded(), "DES");
        } catch (NoSuchAlgorithmException e) {
            throw Exceptions.throwIllegalStateException("生成DES秘钥失败!", e);
        }
    }


    /**
     * 生成RSA加密秘钥 128位秘钥
     *
     * @return
     */
    public static KeyPair getRsaKey(RsaAlgorithm rsaAlgorithm) {
        return getRsaKey(rsaAlgorithm, null);
    }

    /**
     * 生成RSA加密秘钥 128字节位秘钥
     *
     * @param seed 随机种子
     * @return
     */
    public static KeyPair getRsaKey(RsaAlgorithm rsaAlgorithm, final String seed) {
        return getPairKey(rsaAlgorithm.getValue(), seed,1024);
    }

    /**
     * 生成RSA加密秘钥
     *
     * @param seed 随机种子
     * @return
     */
    public static KeyPair getRsaKey(RsaAlgorithm rsaAlgorithm, final String seed,int keySize) {
        return getPairKey(rsaAlgorithm.getValue(), seed,keySize);
    }


    /**
     * 生成sign加密秘钥 128位秘钥
     *
     * @return
     */
    public static KeyPair getSignKey(SignAlgorithm signAlgorithm) {
        return getSignKey(signAlgorithm, null);
    }


    /**
     * 生成sign加密秘钥 128位秘钥
     *
     * @return
     */
    public static KeyPair getSignKey(SignAlgorithm signAlgorithm, final String seed) {
        return getPairKey(signAlgorithm.getValue(), seed,1024);
    }

    /**
     * 生成sign加密秘钥
     *
     * @return
     */
    public static KeyPair getSignKey(SignAlgorithm signAlgorithm, final String seed,int keySize) {
        return getPairKey(signAlgorithm.getValue(), seed,keySize);
    }


    /**
     * 生成私钥，仅用于非对称加密<br>
     * 采用PKCS#8规范，此规范定义了私钥信息语法和加密私钥语法<br>
     * 算法见：https://docs.oracle.com/javase/7/docs/technotes/guides/security/StandardNames.html#KeyFactory
     *
     * @param algorithm 算法
     * @param key       密钥，必须为DER编码存储
     * @return 私钥 {@link PrivateKey}
     */
    public static PrivateKey generatePrivateKey(String algorithm, byte[] key) {
        if (null == key) {
            return null;
        }
        return generatePrivateKey(algorithm, new PKCS8EncodedKeySpec(key));
    }


    /**
     * 生成私钥，仅用于非对称加密<br>
     * 算法见：https://docs.oracle.com/javase/7/docs/technotes/guides/security/StandardNames.html#KeyFactory
     *
     * @param algorithm 算法
     * @param keySpec   {@link KeySpec}
     * @return 私钥 {@link PrivateKey}
     * @since 3.1.1
     */
    private static PrivateKey generatePrivateKey(String algorithm, KeySpec keySpec) {
        if (null == keySpec) {
            return null;
        }
        algorithm = getAlgorithmAfterWith(algorithm);
        try {
            return getKeyFactory(algorithm).generatePrivate(keySpec);
        } catch (Exception e) {
            throw Exceptions.throwIllegalStateException("生成非对称加密私钥失败", e);
        }
    }


    /**
     * 生成公钥，仅用于非对称加密<br>
     * 采用X509证书规范<br>
     * 算法见：https://docs.oracle.com/javase/7/docs/technotes/guides/security/StandardNames.html#KeyFactory
     *
     * @param algorithm 算法
     * @param key       密钥，必须为DER编码存储
     * @return 公钥 {@link PublicKey}
     */
    public static PublicKey generatePublicKey(String algorithm, byte[] key) {
        if (null == key) {
            return null;
        }
        return generatePublicKey(algorithm, new X509EncodedKeySpec(key));
    }


    /**
     * 生成公钥，仅用于非对称加密<br>
     * 算法见：https://docs.oracle.com/javase/7/docs/technotes/guides/security/StandardNames.html#KeyFactory
     *
     * @param algorithm 算法
     * @param keySpec   {@link KeySpec}
     * @return 公钥 {@link PublicKey}
     */
    private static PublicKey generatePublicKey(String algorithm, KeySpec keySpec) {
        if (null == keySpec) {
            return null;
        }
        algorithm = getAlgorithmAfterWith(algorithm);
        try {
            return getKeyFactory(algorithm).generatePublic(keySpec);
        } catch (Exception e) {
            throw Exceptions.throwIllegalStateException("生成非对称加密公钥失败", e);
        }
    }


    /**
     * 生成sign加密秘钥
     *
     * @return
     */
    private static KeyPair getPairKey(String algorithmName, final String seed,int keySize) {
        AssertUtil.notNull(algorithmName, "algorithmName must be not null !");
        try {
            String algorithm = getAlgorithmAfterWith(algorithmName);
            final KeyPairGenerator keyPairGen = getKeyPairGenerator(algorithm);
            SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
            if (!StringUtil.isBlank(seed)) {
                secureRandom.setSeed(seed.getBytes());
            }
            if ("EC".equalsIgnoreCase(algorithm)) {
                // 对于EC算法，密钥长度有限制，在此使用默认256
                keyPairGen.initialize(256, secureRandom);
            } else {
                keyPairGen.initialize(keySize, secureRandom);
            }
            return keyPairGen.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            throw Exceptions.throwIllegalStateException("生成sign秘钥对失败!", e);
        }
    }


    /**
     * 获取用于密钥生成的算法<br>
     * 获取XXXwithXXX算法的后半部分算法，如果为ECDSA或SM2，返回算法为EC
     *
     * @param algorithm XXXwithXXX算法
     * @return 算法
     */
    private static String getAlgorithmAfterWith(String algorithm) {
        AssertUtil.notNull(algorithm, "algorithm must be not null !");
        int indexOfWith = algorithm.lastIndexOf("with");
        if (indexOfWith > 0) {
            algorithm = algorithm.substring(indexOfWith + "with".length());
        }
        if ("ECDSA".equalsIgnoreCase(algorithm) || "SM2".equalsIgnoreCase(algorithm)) {
            algorithm = "EC";
        }
        return algorithm;
    }


    /**
     * 获取{@link KeyPairGenerator}
     *
     * @param algorithm 非对称加密算法
     * @return {@link KeyPairGenerator}
     */
    private static KeyPairGenerator getKeyPairGenerator(String algorithm) {
        final Provider provider = GlobalBouncyCastleProvider.INSTANCE.getProvider();

        KeyPairGenerator keyPairGen;
        try {
            keyPairGen = (null == provider)
                    ? KeyPairGenerator.getInstance(getMainAlgorithm(algorithm))
                    : KeyPairGenerator.getInstance(getMainAlgorithm(algorithm), provider);
        } catch (NoSuchAlgorithmException e) {
            throw Exceptions.throwIllegalStateException("无法找到此加密算法:" + algorithm, e);
        }
        return keyPairGen;
    }

    /**
     * 获取{@link KeyGenerator}
     *
     * @param algorithm 对称加密算法
     * @return {@link KeyGenerator}
     */
    private static KeyGenerator getKeyGenerator(String algorithm) {
        final Provider provider = GlobalBouncyCastleProvider.INSTANCE.getProvider();
        KeyGenerator generator;
        try {
            generator = (null == provider)
                    ? KeyGenerator.getInstance(getMainAlgorithm(algorithm))
                    : KeyGenerator.getInstance(getMainAlgorithm(algorithm), provider);
        } catch (NoSuchAlgorithmException e) {
            throw Exceptions.throwIllegalStateException("无法找到此加密算法:" + algorithm, e);
        }
        return generator;
    }

    /**
     * 获取{@link KeyFactory}
     *
     * @param algorithm 非对称加密算法
     * @return {@link KeyFactory}
     */
    private static KeyFactory getKeyFactory(String algorithm) {
        final Provider provider = GlobalBouncyCastleProvider.INSTANCE.getProvider();

        KeyFactory keyFactory;
        try {
            keyFactory = (null == provider)
                    ? KeyFactory.getInstance(getMainAlgorithm(algorithm))
                    : KeyFactory.getInstance(getMainAlgorithm(algorithm), provider);
        } catch (NoSuchAlgorithmException e) {
            throw Exceptions.throwIllegalStateException("无法找到此加密算法:" + algorithm, e);
        }
        return keyFactory;
    }


    /**
     * 获取主体算法名，例如RSA/ECB/PKCS1Padding的主体算法是RSA
     *
     * @return 主体算法名
     */
    private static String getMainAlgorithm(String algorithm) {
        final int slashIndex = algorithm.indexOf('/');
        if (slashIndex > 0) {
            return algorithm.substring(0, slashIndex);
        }
        return algorithm;
    }


}
