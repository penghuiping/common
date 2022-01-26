package com.php25.common.web;

import com.php25.common.core.exception.BusinessErrorStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;

/**
 * @author penghuiping
 * @date 2018/6/25 11:04
 * <p>
 * controller层类一般都需要继承这个接口
 */
@Validated
@CrossOrigin
public class JsonController {
    protected JsonResponse<Object> failed(BusinessErrorStatus returnStatus) {
        JsonResponse<Object> ret = new JsonResponse<>();
        ret.setCode(returnStatus.getCode());
        ret.setMessage(returnStatus.getDesc());
        return ret;
    }

    protected <T> JsonResponse<T> failed0(BusinessErrorStatus returnStatus) {
        JsonResponse<T> ret = new JsonResponse<>();
        ret.setCode(returnStatus.getCode());
        ret.setMessage(returnStatus.getDesc());
        return ret;
    }

    protected <T> JsonResponse<T> succeed(T obj) {
        JsonResponse<T> ret = new JsonResponse<>();
        ret.setCode(ApiErrorCode.ok.getCode());
        ret.setData(obj);
        ret.setMessage(ApiErrorCode.ok.getDesc());
        return ret;
    }

}
