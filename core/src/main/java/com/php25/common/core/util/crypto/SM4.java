package com.php25.common.core.util.crypto;


import com.php25.common.core.util.crypto.constant.Mode;
import com.php25.common.core.util.crypto.constant.Padding;
import com.php25.common.core.util.crypto.constant.SymmetricAlgorithm;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

/**
 * 国密对称堆成加密算法SM4实现
 *
 * <p>
 * 国密算法包括：
 * <ol>
 *     <li>非对称加密和签名：SM2</li>
 *     <li>摘要签名算法：SM3</li>
 *     <li>对称加密：SM4</li>
 * </ol>
 */
public class SM4 extends AbstractSymmetric {

	public SM4(Mode mode, Padding padding, SecretKey key, IvParameterSpec iv) {
		super(SymmetricAlgorithm.SM4, mode, padding, key, iv);
	}

	public SM4(SecretKey key, IvParameterSpec iv) {
		super(SymmetricAlgorithm.SM4, key, iv);
	}

	public SM4(SecretKey key) {
		super(SymmetricAlgorithm.SM4, key);
	}
}
