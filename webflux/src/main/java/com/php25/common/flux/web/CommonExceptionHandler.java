package com.php25.common.flux.web;

import com.php25.common.core.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
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
        if(e instanceof org.springframework.web.bind.MethodArgumentNotValidException) {
            MethodArgumentNotValidException methodArgumentNotValidException = (MethodArgumentNotValidException)e;
            FieldError fieldError= methodArgumentNotValidException.getBindingResult().getFieldError();
            JSONResponse jsonResponse = new JSONResponse();
            jsonResponse.setErrorCode(ApiErrorCode.input_params_error.value);
            if (fieldError == null) {
                jsonResponse.setMessage("input_params_error");
            } else {
                jsonResponse.setMessage(fieldError.getField() + fieldError.getDefaultMessage());
            }
            return ResponseEntity.ok(jsonResponse);
        }else if (e instanceof WebExchangeBindException) {
            log.warn("请求访问参数错误!!", e);
            WebExchangeBindException e1 = (WebExchangeBindException) e;
            JSONResponse jsonResponse = new JSONResponse();
            jsonResponse.setErrorCode(ApiErrorCode.input_params_error.value);
            FieldError fieldError = e1.getFieldError();
            if (fieldError == null) {
                jsonResponse.setMessage("input_params_error");
            } else {
                jsonResponse.setMessage(fieldError.getField() + fieldError.getDefaultMessage());
            }
            return ResponseEntity.ok(jsonResponse);
        } else if (e instanceof ConstraintViolationException || e instanceof javax.validation.ConstraintDeclarationException) {
            log.warn("请求访问参数错误!!", e);
            JSONResponse jsonResponse = new JSONResponse();
            jsonResponse.setErrorCode(ApiErrorCode.input_params_error.value);
            jsonResponse.setMessage(e.getMessage());
            return ResponseEntity.ok(jsonResponse);
        } else if (e instanceof ServerWebInputException) {
            log.warn("请求访问参数错误!!", e);
            JSONResponse jsonResponse = new JSONResponse();
            jsonResponse.setErrorCode(ApiErrorCode.input_params_error.value);
            jsonResponse.setMessage("input_params_error");
            return ResponseEntity.ok(jsonResponse);
//        } else if (e instanceof HttpRequestMethodNotSupportedException) {
//            log.warn("请求访问方法错误!!", e);
//            JSONResponse jsonResponse = new JSONResponse();
//            jsonResponse.setErrorCode(ApiErrorCode.http_request_method_not_supported.value);
//            jsonResponse.setMessage("http_request_method_not_supported");
//            return ResponseEntity.ok(jsonResponse);
        } else if (e instanceof BusinessException) {
            log.warn("出现业务错误!!", e);
            BusinessException businessException = (BusinessException) e;
            JSONResponse jsonResponse = new JSONResponse();
            jsonResponse.setErrorCode(businessException.getCode());
            jsonResponse.setMessage(businessException.getMessage());
            return ResponseEntity.ok(jsonResponse);
        } else {
            log.error("出错啦!!", e);
            JSONResponse jsonResponse = new JSONResponse();
            jsonResponse.setErrorCode(ApiErrorCode.unknown_error.value);
            jsonResponse.setMessage("unknown_error");
            return ResponseEntity.ok(jsonResponse);
        }
    }
}
