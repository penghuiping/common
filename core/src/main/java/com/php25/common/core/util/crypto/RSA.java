package com.php25.common.core.util.crypto;

import com.php25.common.core.exception.Exceptions;
import com.php25.common.core.util.AssertUtil;
import com.php25.common.core.util.DigestUtil;
import com.php25.common.core.util.crypto.constant.GlobalBouncyCastleProvider;
import com.php25.common.core.util.crypto.constant.KeyType;
import com.php25.common.core.util.crypto.constant.RsaAlgorithm;
import org.springframework.util.FastByteArrayOutputStream;

import javax.crypto.Cipher;
import java.security.Key;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author penghuiping
 * @date 2019/9/4 16:28
 */
public class RSA {

    /**
     * 算法
     */
    protected RsaAlgorithm algorithm;
    /**
     * 公钥
     */
    protected PublicKey publicKey;
    /**
     * 私钥
     */
    protected PrivateKey privateKey;
    /**
     * 锁
     */
    protected Lock lock = new ReentrantLock();

    /**
     * Cipher负责完成加密或解密工作
     */
    protected Cipher cipher;

    /**
     * 加密的块大小
     */
    protected int encryptBlockSize = -1;
    /**
     * 解密的块大小
     */
    protected int decryptBlockSize = -1;


    /**
     * 构造 私钥和公钥同时为空时生成一对新的私钥和公钥<br>
     * 私钥和公钥可以单独传入一个，如此则只能使用此钥匙来做加密或者解密
     *
     * @param algorithm  RSA算法类型
     * @param privateKey 私钥
     * @param publicKey  公钥
     */
    public RSA(RsaAlgorithm algorithm, PrivateKey privateKey, PublicKey publicKey) {
        AssertUtil.notNull(algorithm, "algorithm不能为null");
        AssertUtil.notNull(privateKey, "privateKey不能为null");
        AssertUtil.notNull(publicKey, "publicKey不能为null");

        this.algorithm = algorithm;
        this.publicKey = publicKey;
        this.privateKey = privateKey;
        this.cipher = createCipher(algorithm.getValue());
    }


    /**
     * 创建{@link Cipher}
     *
     * @param algorithm 算法
     */
    private Cipher createCipher(String algorithm) {
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
     * @param data    被加密的bytes
     * @param keyType 私钥或公钥 {@link KeyType}
     * @return 加密后的bytes
     */
    public byte[] encrypt(byte[] data, KeyType keyType) {
        final Key key = getKeyByType(keyType);
        final int maxBlockSize = this.encryptBlockSize < 0 ? data.length : this.encryptBlockSize;

        lock.lock();
        try {
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return doFinal(data, maxBlockSize);
        } catch (Exception e) {
            throw Exceptions.throwIllegalStateException("RSA加密出错", e);
        } finally {
            lock.unlock();
        }
    }

    /**
     * 加密
     *
     * @param data 数据
     * @return 加密后的Base64
     */
    public String encryptBase64(byte[] data, KeyType keyType) {
        return DigestUtil.encodeBase64(encrypt(data, keyType));
    }


    /**
     * 解密
     *
     * @param data    被解密的bytes
     * @param keyType 私钥或公钥 {@link KeyType}
     * @return 解密后的bytes
     */
    public byte[] decrypt(byte[] data, KeyType keyType) {
        final Key key = getKeyByType(keyType);
        final int maxBlockSize = this.decryptBlockSize < 0 ? data.length : this.decryptBlockSize;

        lock.lock();
        try {
            cipher.init(Cipher.DECRYPT_MODE, key);
            return doFinal(data, maxBlockSize);
        } catch (Exception e) {
            throw Exceptions.throwIllegalStateException("RSA解密出错", e);
        } finally {
            lock.unlock();
        }
    }

    /**
     * 解密
     *
     * @param data 数据
     * @return 加密后的Base64
     */
    public String decryptBase64Str(String data, KeyType keyType) {
        byte[] tmp = DigestUtil.decodeBase64(data);
        return new String(decrypt(tmp, keyType));
    }


    /**
     * 加密或解密
     *
     * @param data         被加密或解密的内容数据
     * @param maxBlockSize 最大块（分段）大小
     * @return 加密或解密后的数据
     */
    private byte[] doFinal(byte[] data, int maxBlockSize) {
        try {
            // 模长
            final int dataLength = data.length;
            // 不足分段
            if (dataLength <= maxBlockSize) {
                return this.cipher.doFinal(data, 0, dataLength);
            }
            // 分段解密
            return doFinalWithBlock(data, maxBlockSize);
        } catch (Exception e) {
            throw Exceptions.throwIllegalStateException("RSA加密或解密操作出错", e);
        }
    }

    /**
     * 分段加密或解密
     *
     * @param data         数据
     * @param maxBlockSize 最大分段的段大小，不能为小于1
     * @return 加密或解密后的数据
     */
    private byte[] doFinalWithBlock(byte[] data, int maxBlockSize) {
        try {
            final int dataLength = data.length;
            final FastByteArrayOutputStream out = new FastByteArrayOutputStream();
            int offSet = 0;
            // 剩余长度
            int remainLength = dataLength;
            int blockSize;
            // 对数据分段处理
            while (remainLength > 0) {
                blockSize = Math.min(remainLength, maxBlockSize);
                out.write(cipher.doFinal(data, offSet, blockSize));

                offSet += blockSize;
                remainLength = dataLength - offSet;
            }
            return out.toByteArray();
        } catch (Exception e) {
            throw Exceptions.throwIllegalStateException("RSA加密或解密操作出错", e);
        }
    }

    /**
     * 根据密钥类型获得相应密钥
     *
     * @param type 类型 {@link KeyType}
     * @return {@link Key}
     */
    protected Key getKeyByType(KeyType type) {
        switch (type) {
            case PrivateKey:
                if (null == this.privateKey) {
                    throw Exceptions.throwIllegalStateException("Private key must not null when use it !");
                }
                return this.privateKey;
            case PublicKey:
                if (null == this.publicKey) {
                    throw Exceptions.throwIllegalStateException("Public key must not null when use it !");
                }
                return this.publicKey;
            default:
                break;
        }
        throw Exceptions.throwIllegalStateException("Uknown key type: " + type);
    }
}
