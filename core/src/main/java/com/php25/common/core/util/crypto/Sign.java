package com.php25.common.core.util.crypto;

import com.php25.common.core.exception.Exceptions;
import com.php25.common.core.util.AssertUtil;
import com.php25.common.core.util.DigestUtil;
import com.php25.common.core.util.crypto.constant.SignAlgorithm;

import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 数字签名
 *
 * @author penghuiping
 * @date 2019/9/5 10:18
 */
public class Sign {

    /**
     * 算法
     */
    protected SignAlgorithm algorithm;
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
     * 签名，用于签名和验证
     */
    protected Signature signature;

    /**
     * 构造
     * <p>
     * 私钥和公钥同时为空时生成一对新的私钥和公钥<br>
     * 私钥和公钥可以单独传入一个，如此则只能使用此钥匙来做签名或验证
     *
     * @param algorithm  算法
     * @param privateKey 私钥
     * @param publicKey  公钥
     */
    public Sign(SignAlgorithm algorithm, PrivateKey privateKey, PublicKey publicKey) {
        AssertUtil.notNull(algorithm, "algorithm不能为null");
        AssertUtil.notNull(privateKey, "privateKey不能为null");
        AssertUtil.notNull(publicKey, "publicKey不能为null");
        try {
            signature = Signature.getInstance(algorithm.getValue());
        } catch (NoSuchAlgorithmException e) {
            throw Exceptions.throwIllegalStateException("没有此签名算法", e);
        }

        this.algorithm = algorithm;
        this.privateKey = privateKey;
        this.publicKey = publicKey;
    }

    /**
     * 用私钥对信息生成数字签名
     *
     * @param data 加密数据
     * @return 签名
     */
    public byte[] sign(byte[] data) {
        lock.lock();
        try {
            signature.initSign(this.privateKey);
            signature.update(data);
            return signature.sign();
        } catch (Exception e) {
            throw Exceptions.throwIllegalStateException("签名出错", e);
        } finally {
            lock.unlock();
        }
    }


    /**
     * 用私钥对信息生成数字签名
     *
     * @param data 数据
     * @return 签名后的Base64
     */
    public String signBase64(byte[] data) {
        return DigestUtil.encodeBase64(sign(data));
    }

    /**
     * 用公钥检验数字签名的合法性
     *
     * @param data 数据
     * @param sign 签名
     * @return 是否验证通过
     */
    public boolean verify(byte[] data, byte[] sign) {
        lock.lock();
        try {
            signature.initVerify(this.publicKey);
            signature.update(data);
            return signature.verify(sign);
        } catch (Exception e) {
            throw Exceptions.throwIllegalStateException("验证签名出错", e);
        } finally {
            lock.unlock();
        }
    }

    /**
     * 用公钥检验数字签名的合法性
     *
     * @param data 数据
     * @param sign 签名
     * @return 是否验证通过
     */
    public boolean verifyBase64(byte[] data, String sign) {
        byte[] bytes = DigestUtil.decodeBase64(sign);
        return verify(data, bytes);
    }
}
