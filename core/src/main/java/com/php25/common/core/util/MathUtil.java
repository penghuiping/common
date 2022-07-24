package com.php25.common.core.util;

/**
 * @author penghuiping
 * @date 2022/7/22 20:14
 */
public abstract class MathUtil {

    /**
     * 是否是2的幂
     *
     * @param number 数字
     * @return true: 这个数字是2的幂
     */
    public static boolean isPowerOfTwo(long number) {
        return (number & (number - 1)) == 0;
    }

    /**
     * 相当于num0%num1
     *
     * @param num0 除数
     * @param num1 被除数(必须是2的幂)
     * @return 余
     */
    public static long mod(long num0, long num1) {
        return num0 & (num1 - 1);
    }

    /**
     * 是否是偶数
     *
     * @param num 数字
     * @return true: 偶数
     */
    public static boolean isEven(long num) {
        return (num & 1) != 1;
    }
}
