package com.php25.common.core.service;

import java.io.IOException;

/**
 * classpath资源文件加载
 *
 * @author penghuiping
 * @date 2016-12-18
 */
public interface ResourceAwareService {

    /**
     * 加载classpath中的properties配置文件内容，并以json字符串的形式返回
     *
     * @param fileName classpath中的properties配置文件名
     * @return string
     * @throws IOException
     * @author penghuiping
     * @date 2016-12-18
     */
    public String loadProperties(String fileName) throws IOException;
}
