package com.php25.common.core.util.crypto.constant;

/**
 * 对称算法类型<br>
 */
public enum SymmetricAlgorithm {
    /**
     * 默认的AES加密方式：AES/CBC/PKCS5Padding
     */
    AES("AES"),
    /**
     * 默认的DES加密方式：DES/ECB/PKCS5Padding
     */
    DES("DES"),
    /**
     * 3DES算法，默认实现为：DESede/CBC/PKCS5Padding
     */
    DESede("DESede");

    private String value;

    /**
     * 构造
     *
     * @param value 算法的字符串表示，区分大小写
     */
    private SymmetricAlgorithm(String value) {
        this.value = value;
    }

    /**
     * 获得算法的字符串表示形式
     *
     * @return 算法字符串
     */
    public String getValue() {
        return this.value;
    }
}