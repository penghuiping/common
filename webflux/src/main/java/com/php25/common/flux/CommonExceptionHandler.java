package com.php25.common.flux;

import com.php25.common.core.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ServerWebInputException;

import javax.validation.ConstraintViolationException;

/**
 * @author: penghuiping
 * @date: 2019/7/18 09:41
 * @description:
 */
@RestControllerAdvice
public class CommonExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(CommonExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public ResponseEntity<JSONResponse> handleCustomException(Exception e) {
        log.error("出错啦!!", e);
        if (e instanceof ServerWebInputException || e instanceof ConstraintViolationException) {
            JSONResponse jsonResponse = new JSONResponse();
            jsonResponse.setErrorCode(ApiErrorCode.input_params_error.value);
            jsonResponse.setMessage(e.getMessage());
            return ResponseEntity.ok(jsonResponse);
        } else if (e instanceof BusinessException) {
            BusinessException businessException = (BusinessException) e;
            JSONResponse jsonResponse = new JSONResponse();
            jsonResponse.setErrorCode(ApiErrorCode.business_error.value);
            jsonResponse.setMessage(businessException.getBusinessErrorStatus().toString2());
            return ResponseEntity.ok(jsonResponse);
        } else {
            JSONResponse jsonResponse = new JSONResponse();
            jsonResponse.setErrorCode(ApiErrorCode.server_error.value);
            jsonResponse.setMessage("server error");
            return ResponseEntity.ok(jsonResponse);
        }
    }

}
