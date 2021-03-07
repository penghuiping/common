package com.php25.common.flux.web;

import com.php25.common.core.exception.BusinessErrorStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class JSONController {
    private static final Logger log = LoggerFactory.getLogger(JSONController.class);


    protected JSONResponse failed(BusinessErrorStatus returnStatus) {
        JSONResponse ret = new JSONResponse();
        ret.setCode(returnStatus.getCode());
        ret.setMessage(returnStatus.getDesc());
        return ret;
    }

    protected JSONResponse succeed(Object obj) {
        JSONResponse ret = new JSONResponse();
        ret.setCode(ApiErrorCode.ok.value);
        ret.setData(obj);
        ret.setMessage("success");
        return ret;
    }

}
