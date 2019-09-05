package com.php25.common.core.util.crypto;

import com.php25.common.core.util.crypto.constant.Mode;
import com.php25.common.core.util.crypto.constant.Padding;
import com.php25.common.core.util.crypto.constant.SymmetricAlgorithm;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

/**
 * AES加密算法实现<br>
 * 高级加密标准（英语：Advanced Encryption Standard，缩写：AES），在密码学中又称Rijndael加密法<br>
 * 对于Java中AES的默认模式是：AES/ECB/PKCS5Padding，如果使用CryptoJS，请调整为：padding: CryptoJS.pad.Pkcs7
 *
 * @author penghuiping
 * @date 2019/9/4 10:56
 */
public class AES extends AbstractSymmetric {


    public AES(Mode mode, Padding padding, SecretKey key, IvParameterSpec iv) {
        super(SymmetricAlgorithm.AES, mode, padding, key, iv);
    }

    public AES(SecretKey key, IvParameterSpec iv) {
        super(SymmetricAlgorithm.AES, key, iv);
    }

    public AES(SecretKey key) {
        super(SymmetricAlgorithm.AES, key);
    }
}
