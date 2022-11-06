package com.php25.common.core.util.crypto;

import com.php25.common.core.exception.Exceptions;
import com.php25.common.core.util.AssertUtil;
import com.php25.common.core.util.DigestUtil;
import com.php25.common.core.util.crypto.key.SecretKeyUtil;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.digests.SM3Digest;
import org.bouncycastle.crypto.engines.SM2Engine;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.params.ParametersWithRandom;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author penghuiping
 * @date 2022/11/5 22:36
 */
public class SM2 {

    /**
     * 算法EC
     */
    private final Digest digest = new SM3Digest();
    private final SM2Engine.Mode mode = SM2Engine.Mode.C1C3C2;
    /**
     * 锁
     */
    protected Lock lock = new ReentrantLock();
    protected SM2Engine engine;
    private ECPrivateKeyParameters privateKeyParams;
    private ECPublicKeyParameters publicKeyParams;

    public SM2(PrivateKey privateKey, PublicKey publicKey) {
        if (null != privateKey) {
            this.privateKeyParams = SecretKeyUtil.toPrivateParams(privateKey);
        }
        if (null != publicKey) {
            this.publicKeyParams = SecretKeyUtil.toPublicParams(publicKey);
        }
    }


    /**
     * 加密，SM2非对称加密的结果由C1,C2,C3三部分组成，其中：
     *
     * <pre>
     * C1 生成随机数的计算出的椭圆曲线点
     * C2 密文数据
     * C3 SM3的摘要值
     * </pre>
     *
     * @param data 被加密的bytes
     * @return 加密后的bytes
     * @since 5.1.6
     */
    public byte[] encrypt(byte[] data) {
        lock.lock();
        try {
            final SM2Engine engine = getEngine();
            engine.init(true, new ParametersWithRandom(this.publicKeyParams));
            return engine.processBlock(data, 0, data.length);
        } catch (InvalidCipherTextException e) {
            throw Exceptions.throwIllegalStateException("SM2加密失败", e);
        } finally {
            lock.unlock();
        }
    }


    /**
     * 解密
     *
     * @param data SM2密文，实际包含三部分：ECC公钥、真正的密文、公钥和原文的SM3-HASH值
     * @return 加密后的bytes
     * @since 5.1.6
     */
    public byte[] decrypt(byte[] data) {
        lock.lock();
        try {
            final SM2Engine engine = getEngine();
            engine.init(false, this.privateKeyParams);
            return engine.processBlock(data, 0, data.length);
        } catch (InvalidCipherTextException e) {
            throw Exceptions.throwIllegalStateException("SM2解密失败", e);
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
    public String encryptBase64(byte[] data) {
        return DigestUtil.encodeBase64(encrypt(data));
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

    /**
     * 获取{@link SM2Engine}，此对象为懒加载模式
     *
     * @return {@link SM2Engine}
     */
    private SM2Engine getEngine() {
        if (null == this.engine) {
            AssertUtil.notNull(this.digest, "digest must be not null !");
            this.engine = new SM2Engine(this.digest, this.mode);
        }
        this.digest.reset();
        return this.engine;
    }


}
