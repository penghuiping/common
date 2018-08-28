package com.php25.common.mvc;

import javax.servlet.http.HttpServletRequest;

/**
 * @author penghuiping
 * @date 2016/12/17.
 */
public interface HtmlService {

    /**
     * 获取项目基路径
     *
     * @param request
     * @return
     * @author penghuiping
     * @date 2016/12/17.
     */
    public String getBasePath(HttpServletRequest request);
}
