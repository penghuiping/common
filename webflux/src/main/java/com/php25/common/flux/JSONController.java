package com.php25.common.flux;

import com.php25.common.core.exception.BusinessErrorStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import reactor.core.publisher.Mono;

/**
 * @author penghuiping
 * @date 2018/6/25 11:04
 * <p>
 * controller层类一般都需要继承这个接口
 */
@Validated
@CrossOrigin
public class JSONController {
    private static final Logger log = LoggerFactory.getLogger(JSONController.class);


    protected Mono<JSONResponse> serverError(Throwable throwable) {
        log.error("出错啦!", throwable);
        JSONResponse jsonResponse = new JSONResponse();
        jsonResponse.setErrorCode(ApiErrorCode.server_error.value);
        jsonResponse.setMessage("server error");
        return Mono.just(jsonResponse);
    }

    protected JSONResponse failed(BusinessErrorStatus returnStatus) {
        JSONResponse ret = new JSONResponse();
        ret.setErrorCode(ApiErrorCode.business_error.value);
        ret.setMessage(returnStatus.toString2());
        return ret;
    }

    protected JSONResponse succeed(Object obj) {
        JSONResponse ret = new JSONResponse();
        ret.setErrorCode(ApiErrorCode.ok.value);
        ret.setReturnObject(obj);
        return ret;
    }


    protected JSONResponse failed(String message) {
        JSONResponse ret = new JSONResponse();
        ret.setErrorCode(ApiErrorCode.business_error.value);
        ret.setMessage(message);
        return ret;
    }

    protected JSONResponse failed(String message, ApiErrorCode apiErrorCode) {
        JSONResponse ret = new JSONResponse();
        ret.setErrorCode(apiErrorCode.value);
        ret.setMessage(message);
        return ret;
    }
}
