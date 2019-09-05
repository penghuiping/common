package com.php25.common.core.util.crypto;

import com.php25.common.core.util.crypto.constant.Mode;
import com.php25.common.core.util.crypto.constant.Padding;
import com.php25.common.core.util.crypto.constant.SymmetricAlgorithm;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

/**
 * @author penghuiping
 * @date 2019/9/4 15:44
 */
public class DES extends AbstractSymmetric {


    public DES(Mode mode, Padding padding, SecretKey key, IvParameterSpec iv) {
        super(SymmetricAlgorithm.DES, mode, padding, key, iv);
    }

    public DES(SecretKey key, IvParameterSpec iv) {
        super(SymmetricAlgorithm.DES, key, iv);
    }

    public DES(SecretKey key) {
        super(SymmetricAlgorithm.DES, key);
    }
}
