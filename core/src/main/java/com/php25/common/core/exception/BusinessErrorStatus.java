package com.php25.common.core.exception;

/**
 * 错误码为字符串类型，共5位，分成两个部分:错误产生来源+四位数字编号
 * <p>
 * 错误产生来源分为 A/B/C:
 * A. 表示错误来源于用户，比如参数错误，用户安装版本过低，用户支付 超时等问题;
 * B. 表示错误来源于当前系统，往往是业务逻辑出错，或程序健壮性差等问题;
 * C. 表示错误来源于第三方服务，比如 CDN 服务出错，消息投递超时等问题;四位数字编号从 0001 到 9999，大类之间的步长间距预留 100
 *
 * @author penghuiping
 * @date 2018/12/26 17:09
 */
public interface BusinessErrorStatus {

    String getCode();

    String getDesc();

    default String toString2() {
        return String.format("%s=%s", this.getCode(), this.getDesc());
    }
}
