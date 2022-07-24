package com.php25.common.validation.test;

import com.php25.common.validation.annotation.Email;
import com.php25.common.validation.annotation.IdCard;
import com.php25.common.validation.annotation.Ipv4;
import com.php25.common.validation.annotation.Ipv6;
import com.php25.common.validation.annotation.Mobile;
import com.php25.common.validation.annotation.MoneyString;
import com.php25.common.validation.annotation.ZipCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.NotBlank;


/**
 * @author penghuiping
 * @date 2019/9/9 17:20
 */
public class MyObject {
    private static final Logger log = LoggerFactory.getLogger(MyObject.class);

    public void printEmail(@Email String email) {
        log.info("邮箱:{}", email);
    }

    public void printIdCard(@NotBlank @IdCard String idcard) {
        log.info("身份证:{}", idcard);
    }

    public void printIpv4(@Ipv4 String ipv4) {
        log.info("ipv4:{}", ipv4);
    }

    public void printIpv6(@Ipv6 String ipv6) {
        log.info("ipv6:{}", ipv6);
    }

    public void printMobile(@Mobile String mobile) {
        log.info("手机号:{}", mobile);
    }

    public void printMoney(@MoneyString String money) {
        log.info("金钱:{}", money);
    }

    public void printZipCode(@ZipCode String zipCode) {
        log.info("邮政编码:{}", zipCode);
    }

}
