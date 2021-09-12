package com.php25.common.ws.retry;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @author penghuiping
 * @date 2021/8/22 22:10
 */
@Setter
@Getter
public class RetryObject<T> {

    private String id;

    private T value;

    private Date createTime;

    private Date lastModifiedTime;

    private Integer retryNumber;

    private RejectAction<T> rejectAction;
}
