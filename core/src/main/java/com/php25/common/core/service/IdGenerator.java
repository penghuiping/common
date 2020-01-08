package com.php25.common.core.service;

/**
 * @author penghuiping
 * @date 2017/9/18
 * <p>
 * id生成器
 */
public interface IdGenerator {
    /**
     * 实体类主键生产器 生成字符串主键
     *
     * @return string
     */
    public String getUUID();

    public String getJUID();

}
