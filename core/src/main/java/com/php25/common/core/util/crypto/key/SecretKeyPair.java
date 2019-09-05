package com.php25.common.core.util.crypto.key;

import com.php25.common.core.util.AssertUtil;
import com.php25.common.core.util.DigestUtil;
import com.php25.common.core.util.crypto.constant.UnSymmetricAlgorithm;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * @author penghuiping
 * @date 2019/9/4 18:06
 */
public class SecretKeyPair {

    private String privateKey;

    private String publicKey;

    private UnSymmetricAlgorithm unSymmetricAlgorithm;

    public SecretKeyPair(KeyPair keyPair, UnSymmetricAlgorithm unSymmetricAlgorithm) {
        AssertUtil.notNull(keyPair, "keyPair不能为null");
        AssertUtil.notNull(unSymmetricAlgorithm, "unSymmetricAlgorithm不能为null");
        this.privateKey = DigestUtil.encodeBase64(keyPair.getPrivate().getEncoded());
        this.publicKey = DigestUtil.encodeBase64(keyPair.getPublic().getEncoded());
        this.unSymmetricAlgorithm = unSymmetricAlgorithm;
    }


    public SecretKeyPair(String privateKey, String publicKey, UnSymmetricAlgorithm unSymmetricAlgorithm) {
        AssertUtil.hasText(privateKey, "privateKey不能为空");
        AssertUtil.hasText(publicKey, "publicKey不能为为空");
        AssertUtil.notNull(unSymmetricAlgorithm, "unSymmetricAlgorithm不能为null");
        this.privateKey = privateKey;
        this.publicKey = publicKey;
        this.unSymmetricAlgorithm = unSymmetricAlgorithm;
    }

    public KeyPair toKeyPair() {
        PrivateKey privateKey1 = loadPrivateKey(unSymmetricAlgorithm, privateKey);
        PublicKey publicKey1 = loadPublicKey(unSymmetricAlgorithm, publicKey);
        return new KeyPair(publicKey1, privateKey1);
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public String getPublicKey() {
        return publicKey;
    }

    /**
     * 加载公钥
     * 采用X509证书规范
     *
     * @param publicKey 公钥字符串
     * @return 公钥
     */
    private PublicKey loadPublicKey(UnSymmetricAlgorithm algorithm, String publicKey) {
        byte[] keyBytes = DigestUtil.decodeBase64(publicKey);
        return SecretKeyUtil.generatePublicKey(algorithm.getValue(), keyBytes);
    }

    /**
     * 加载秘钥
     * 采用PKCS#8规范，此规范定义了私钥信息语法和加密私钥语法
     *
     * @param privateKey 密钥字符串
     * @return 密钥
     */
    private PrivateKey loadPrivateKey(UnSymmetricAlgorithm algorithm, String privateKey) {
        byte[] keyBytes = DigestUtil.decodeBase64(privateKey);
        return SecretKeyUtil.generatePrivateKey(algorithm.getValue(), keyBytes);
    }
}
