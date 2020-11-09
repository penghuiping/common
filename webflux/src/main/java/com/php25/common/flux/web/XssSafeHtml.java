package com.php25.common.flux.web;

import org.hibernate.validator.constraints.SafeHtml;

import javax.validation.Payload;
import java.lang.annotation.Annotation;

/**
 * @author penghuiping
 * @date 2020/4/16 13:41
 */
public class XssSafeHtml implements SafeHtml {
    private final WhiteListType whiteListType;

    private final String[] additionalTags;

    public XssSafeHtml() {
        this.whiteListType = WhiteListType.BASIC;
        this.additionalTags = new String[0];
    }

    public XssSafeHtml(WhiteListType whiteListType, String[] additionalTags) {
        this.whiteListType = whiteListType;
        this.additionalTags = additionalTags;
    }

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
        return this.whiteListType;
    }

    @Override
    public String[] additionalTags() {
        return this.additionalTags;
    }

    @Override
    public SafeHtml.Tag[] additionalTagsWithAttributes() {
        return new SafeHtml.Tag[0];
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
