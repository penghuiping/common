package com.php25.common.service;

import javax.servlet.http.HttpServletRequest;

/**
 * @author penghuiping
 * @Timer 2016/12/17.
 */
public interface HtmlService {

    /**
     * 获取项目基路径
     *
     * @param request
     * @return
     * @author penghuiping
     * @Timer 2016/12/17.
     */
    public String getBasePath(HttpServletRequest request);
}
