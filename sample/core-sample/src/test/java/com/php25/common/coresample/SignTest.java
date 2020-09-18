package com.php25.common.coresample;

import com.php25.common.core.util.crypto.Sign;
import com.php25.common.core.util.crypto.constant.SignAlgorithm;
import com.php25.common.core.util.crypto.key.SecretKeyPair;
import com.php25.common.core.util.crypto.key.SecretKeyUtil;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.KeyPair;

/**
 * @author penghuiping
 * @date 2019/9/5 10:49
 */
public class SignTest {

    private static final Logger log = LoggerFactory.getLogger(SignTest.class);

    @Test
    public void signAndVerifyTest() {
        signAndVerify(SignAlgorithm.NONEwithRSA);
        signAndVerify(SignAlgorithm.MD2withRSA);
        signAndVerify(SignAlgorithm.MD5withRSA);
        signAndVerify(SignAlgorithm.SHA1withRSA);
        signAndVerify(SignAlgorithm.SHA256withRSA);
        signAndVerify(SignAlgorithm.SHA384withRSA);
        signAndVerify(SignAlgorithm.SHA512withRSA);
        signAndVerify(SignAlgorithm.NONEwithDSA);
        signAndVerify(SignAlgorithm.SHA1withDSA);
        signAndVerify(SignAlgorithm.NONEwithECDSA);
        signAndVerify(SignAlgorithm.SHA1withECDSA);
        signAndVerify(SignAlgorithm.SHA1withECDSA);
        signAndVerify(SignAlgorithm.SHA256withECDSA);
        signAndVerify(SignAlgorithm.SHA384withECDSA);
        signAndVerify(SignAlgorithm.SHA512withECDSA);
    }


    private void signAndVerify(SignAlgorithm signAlgorithm) {
        KeyPair keyPair = SecretKeyUtil.getSignKey(signAlgorithm);
        SecretKeyPair secretKeyPair = new SecretKeyPair(keyPair, signAlgorithm);
        log.info("===>publicKey:{}", secretKeyPair.getPublicKey());
        log.info("===>privateKey:{}", secretKeyPair.getPrivateKey());
        Sign sign = new Sign(signAlgorithm, keyPair.getPrivate(), keyPair.getPublic());
        String signString = sign.signBase64("hello world,你好!!".getBytes());
        boolean value = sign.verifyBase64("hello world,你好!!".getBytes(), signString);
        Assertions.assertThat(value).isTrue();
    }


}
