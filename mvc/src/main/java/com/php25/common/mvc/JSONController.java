package com.php25.common.mvc;

import com.php25.common.core.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.validation.ConstraintViolationException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author penghuiping
 * @date 2018/6/25 11:04
 * <p>
 * controller层类一般都需要继承这个接口
 */
@Validated
@CrossOrigin
public class JSONController {

    private static Logger logger = LoggerFactory.getLogger(JSONController.class);


    /**
     * 用于处理异常的
     *
     * @return
     */
    @ExceptionHandler({JsonException.class})
    public ResponseEntity<JSONResponse> exception(JsonException e) {

        if (e.getCause() instanceof ConstraintViolationException) {
            ConstraintViolationException constraintViolationException = (ConstraintViolationException) e.getCause();
            JSONResponse ret = new JSONResponse();
            ret.setErrorCode(ApiErrorCode.business_error.value);
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);

            List<String> messages = constraintViolationException.getConstraintViolations().stream().map(a -> {
                return a.getPropertyPath() + a.getMessage();
            }).collect(Collectors.toList());

            String result = null;
            result = JsonUtil.toJson(messages);
            pw.write(result);
            ret.setMessage(sw.toString());
            return ResponseEntity.ok(ret);
        } else {
            logger.error(e.getMessage(), e);
            JSONResponse ret = new JSONResponse();
            ret.setErrorCode(ApiErrorCode.server_error.value);
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            pw.write("server_error");
            ret.setMessage(sw.toString());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ret);
        }

    }

    protected JSONResponse failed(ReturnStatus returnStatus) {
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
