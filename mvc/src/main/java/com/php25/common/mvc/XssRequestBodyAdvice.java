package com.php25.common.mvc;

/**
 * @author penghuiping
 * @date 2019/12/26 09:07
 */

import com.google.common.base.Charsets;
import com.php25.common.core.exception.Exceptions;
import org.hibernate.validator.constraints.SafeHtml;
import org.hibernate.validator.internal.constraintvalidators.hv.SafeHtmlValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdviceAdapter;

import javax.validation.Payload;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;


/**
 * @author penghuiping
 * @date 2019/12/25 23:24
 */
public class XssRequestBodyAdvice extends RequestBodyAdviceAdapter {
    private static final Logger log = LoggerFactory.getLogger(XssRequestBodyAdvice.class);

    @Override
    public boolean supports(MethodParameter methodParameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public HttpInputMessage beforeBodyRead(HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) throws IOException {
        InputStream inputStream = inputMessage.getBody();
        ReadableByteChannel readableByteChannel = Channels.newChannel(inputStream);
        ByteBuffer buff = ByteBuffer.allocate(512);

        StringBuilder content =new StringBuilder();
        while (true) {
            buff.clear();
            int count = readableByteChannel.read(buff);
            if(count<=0) {
                break;
            }
            content.append(new String(buff.array(),0,buff.position(), Charsets.UTF_8));
        }

        log.info("request body:{}", content.toString());
        SafeHtmlValidator safeHtmlValidator = new SafeHtmlValidator();
        safeHtmlValidator.initialize(new XssSafeHtml());
        boolean result = safeHtmlValidator.isValid(content.toString(), null);
        if (result) {
            return new HttpInputMessage() {
                @Override
                public InputStream getBody() throws IOException {
                    return new ByteArrayInputStream(content.toString().getBytes(Charsets.UTF_8));
                }

                @Override
                public HttpHeaders getHeaders() {
                    return inputMessage.getHeaders();
                }
            };
        } else {
            throw Exceptions.throwBusinessException("999999", "requestBody存在不安全的html内容");
        }
    }



    private class XssSafeHtml implements SafeHtml {
        @Override
        public String message() {
            return "存在不安全的html内容";
        }

        @Override
        public Class<?>[] groups() {
            return new Class[0];
        }

        @Override
        public Class<? extends Payload>[] payload() {
            return new Class[0];
        }

        @Override
        public WhiteListType whitelistType() {
            return WhiteListType.BASIC;
        }

        @Override
        public String[] additionalTags() {
            return new String[0];
        }

        @Override
        public Tag[] additionalTagsWithAttributes() {
            return new Tag[0];
        }

        @Override
        public String baseURI() {
            return "";
        }

        @Override
        public Class<? extends Annotation> annotationType() {
            return null;
        }
    }
}

