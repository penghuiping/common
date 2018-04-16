package com.php25.common.service;

import java.io.IOException;

/**
 * classpath资源文件加载
 *
 * @author penghuiping
 * @Time 2016-12-18
 */
public interface ResourceAwareService {

    /**
     * 加载beetl配置文件内容，并以json字符串的形式返回
     *
     * @return string
     * @Exception IOException
     * @author penghuiping
     * @Time 2016-12-18
     */
    public String loadBeetlProperties() throws IOException;
}
