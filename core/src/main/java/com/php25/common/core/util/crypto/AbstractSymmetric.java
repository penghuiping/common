package com.php25.common.core.util.crypto;

import com.php25.common.core.exception.Exceptions;
import com.php25.common.core.util.AssertUtil;
import com.php25.common.core.util.DigestUtil;
import com.php25.common.core.util.crypto.constant.GlobalBouncyCastleProvider;
import com.php25.common.core.util.crypto.constant.Mode;
import com.php25.common.core.util.crypto.constant.Padding;
import com.php25.common.core.util.crypto.constant.SymmetricAlgorithm;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.security.Provider;
import java.security.spec.AlgorithmParameterSpec;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author penghuiping
 * @date 2019/9/4 15:47
 */
abstract class AbstractSymmetric {

    /**
     * SecretKey 负责保存对称密钥
     */
    protected SecretKey secretKey;
    /**
     * Cipher负责完成加密或解密工作
     */
    protected Cipher cipher;
    /**
     * 加密解密参数
     */
    protected AlgorithmParameterSpec params;

    protected Lock lock = new ReentrantLock();

    protected SymmetricAlgorithm symmetricAlgorithm;

    /**
     * 构造
     *
     * @param mode    模式{@link Mode}
     * @param padding {@link Padding}补码方式
     * @param key     密钥，支持三种密钥长度：128、192、256位
     * @param iv      偏移向量，加盐
     */
    public AbstractSymmetric(SymmetricAlgorithm symmetricAlgorithm, Mode mode, Padding padding, SecretKey key, IvParameterSpec iv) {
        AssertUtil.notNull(symmetricAlgorithm, "symmetricAlgorithm不能为null");
        AssertUtil.notNull(mode, "mode不能为null");
        AssertUtil.notNull(padding, "padding不能为null");
        AssertUtil.notNull(key, "key不能为null");
        AssertUtil.notNull(iv, "iv不能为null");
        String algorithm = String.format("%s/%s/%s", symmetricAlgorithm.getValue(), mode.name(), padding.name());
        this.symmetricAlgorithm = symmetricAlgorithm;
        this.secretKey = key;
        this.cipher = createCipher(algorithm);
        this.params = iv;
    }

    /**
     * 构造
     *
     * @param key 密钥，支持三种密钥长度：128、192、256位
     * @param iv  偏移向量，加盐
     */
    public AbstractSymmetric(SymmetricAlgorithm symmetricAlgorithm, SecretKey key, IvParameterSpec iv) {
        AssertUtil.notNull(symmetricAlgorithm, "symmetricAlgorithm不能为null");
        AssertUtil.notNull(key, "key不能为null");
        AssertUtil.notNull(iv, "iv不能为null");
        this.secretKey = key;
        this.cipher = createCipher(symmetricAlgorithm.getValue());
        this.symmetricAlgorithm = symmetricAlgorithm;
        this.params = iv;
    }

    /**
     * 构造
     *
     * @param key 密钥，支持三种密钥长度：128、192、256位
     */
    public AbstractSymmetric(SymmetricAlgorithm symmetricAlgorithm, SecretKey key) {
        AssertUtil.notNull(symmetricAlgorithm, "symmetricAlgorithm不能为null");
        AssertUtil.notNull(key, "key不能为null");
        this.secretKey = key;
        this.symmetricAlgorithm = symmetricAlgorithm;
        this.cipher = createCipher(symmetricAlgorithm.getValue());
    }


    /**
     * 创建{@link Cipher}
     *
     * @param algorithm 算法
     */
    public Cipher createCipher(String algorithm) {
        final Provider provider = GlobalBouncyCastleProvider.INSTANCE.getProvider();
        Cipher cipher;
        try {
            cipher = (null == provider) ? Cipher.getInstance(algorithm) : Cipher.getInstance(algorithm, provider);
        } catch (Exception e) {
            throw Exceptions.throwIllegalStateException("初始化Cipher出错", e);
        }
        return cipher;
    }

    /**
     * 加密
     *
     * @param data 被加密的bytes
     * @return 加密后的bytes
     */
    public byte[] encrypt(byte[] data) {
        lock.lock();
        try {
            if (null == this.params) {
                cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            } else {
                cipher.init(Cipher.ENCRYPT_MODE, secretKey, params);
            }
            return cipher.doFinal(data);
        } catch (Exception e) {
            throw Exceptions.throwIllegalStateException(this.symmetricAlgorithm.getValue() + "加密出错", e);
        } finally {
            lock.unlock();
        }
    }

    /**
     * 加密
     *
     * @param data 数据
     * @return 加密后的Hex
     */
    public String encryptHex(byte[] data) {
        return new String(DigestUtil.bytes2hex(encrypt(data)));
    }

    /**
     * 加密
     *
     * @param data 数据
     * @return 加密后的Base64
     */
    public String encryptBase64(byte[] data) {
        return DigestUtil.encodeBase64(encrypt(data));
    }


    /**
     * 解密
     *
     * @param bytes 被解密的bytes
     * @return 解密后的bytes
     */
    public byte[] decrypt(byte[] bytes) {
        lock.lock();
        try {
            if (null == this.params) {
                cipher.init(Cipher.DECRYPT_MODE, secretKey);
            } else {
                cipher.init(Cipher.DECRYPT_MODE, secretKey, params);
            }
            return cipher.doFinal(bytes);
        } catch (Exception e) {
            throw Exceptions.throwIllegalStateException(this.symmetricAlgorithm.getValue() + "解密出错", e);
        } finally {
            lock.unlock();
        }
    }

    /**
     * 解密
     *
     * @param data 加密后的Hex
     * @return 解密内容
     */
    public String decryptHexStr(String data) {
        byte[] tmp = DigestUtil.hex2bytes(data);
        return new String(decrypt(tmp));
    }

    /**
     * 解密
     *
     * @param data 数据
     * @return 加密后的Base64
     */
    public String decryptBase64Str(String data) {
        byte[] tmp = DigestUtil.decodeBase64(data);
        return new String(decrypt(tmp));
    }
}
