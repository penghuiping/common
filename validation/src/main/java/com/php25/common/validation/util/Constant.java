package com.php25.common.validation.util;

/**
 * @author penghuiping
 * @date 2019/9/9 16:43
 */
public class Constant {

    /** 正则表达式匹配中文汉字 */
    public final static String RE_CHINESE = "[\u4E00-\u9FFF]";
    /** 正则表达式匹配中文字符串 */
    public final static String RE_CHINESES = RE_CHINESE + "+";
}
