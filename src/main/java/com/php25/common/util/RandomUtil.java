package com.php25.common.util;


import java.util.Random;
import java.util.UUID;

/**
 * 随机数帮助类
 * @author penghuiping
 * @Time 2014/8/13.
 */
public class RandomUtil {
    public static final String NUMBERS_AND_LETTERS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static final String NUMBERS             = "0123456789";
    public static final String LETTERS             = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static final String CAPITAL_LETTERS     = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static final String LOWER_CASE_LETTERS  = "abcdefghijklmnopqrstuvwxyz";

    /**
     * 随即获取给定长度的字符串，数字+字母形式+大小写敏感
     * @param length
     * @return
     */
    public static String getRandomNumbersAndLetters(int length) {
        return getRandom(NUMBERS_AND_LETTERS, length);
    }

    /**
     * 获取给定长度的随机数
     * @param length
     * @return
     */
    public static String getRandomNumbers(int length) {
        return getRandom(NUMBERS, length);
    }

    /**
     * 获取给定长度的随即字符串，字母形式+大小写敏感
     * @param length
     * @return
     */
    public static String getRandomLetters(int length) {
        return getRandom(LETTERS, length);
    }

    /**
     * 获取给定长度的大写字符
     * @param length
     * @return
     */
    public static String getRandomCapitalLetters(int length) {
        return getRandom(CAPITAL_LETTERS, length);
    }

    /**
     * 获取给定长度的小写字符
     * @param length
     * @return
     */
    public static String getRandomLowerCaseLetters(int length) {
        return getRandom(LOWER_CASE_LETTERS, length);
    }

    /**
     * 给定字符串数据源，从中生产随机字符串
     * @param source 字符串数据源
     * @param length 给定长度
     * @return  如果数据源为null或者""，返回null
     */
    public static String getRandom(String source, int length) {
        return StringUtil.isEmpty(source) ? null : getRandom(source.toCharArray(), length);
    }

    /**
     * 给定字符串数据源，从中生产随机字符串
     * @param sourceChar
     * @param length
     * @return 如果数据源为null或者""，返回null
     */
    public static String getRandom(char[] sourceChar, int length) {
        if (sourceChar == null || sourceChar.length == 0 || length < 0) {
            return null;
        }

        StringBuilder str = new StringBuilder(length);
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            str.append(sourceChar[random.nextInt(sourceChar.length)]);
        }
        return str.toString();
    }

    /**
     * 随机返回0到max之间的数字
     * @param max
     * @return 如果max小于0 则返回0
     */
    public static int getRandom(int max) {
        return getRandom(0, max);
    }

    /**
     * 随机返回min与max之间的数字
     * @param min
     * @param max
     * @return 如果min大于max返回0，如果min等于max返回min
     */
    public static int getRandom(int min, int max) {
        if (min > max) {
            return 0;
        }
        if (min == max) {
            return min;
        }
        return min + new Random().nextInt(max - min);
    }

    /**
     * 从obj[]数组中，随机获取大小不定的obj[]数组
     * @param objArray 源数组
     * @return 长度不定的obj数组，里面的元素是源数组的元素
     */
    public static Object[] shuffle(Object[] objArray) {
        if (objArray == null) {
            return null;
        }

        return shuffle(objArray, getRandom(objArray.length));
    }

    /**
     * 从object[]数组中，随机获取指定大小的object[]数组
     * @param objArray 源数组
     * @param shuffleCount 指定生产的数组大小
     * @return 回object数组，大小为shuffleCount，里面的元素是源数组的元素.如果objArray是null或者shuffleCount大于objArray的长度返回null
     */
    public static Object[] shuffle(Object[] objArray, int shuffleCount) {
        int length;
        if (objArray == null || shuffleCount < 0 || (length = objArray.length) < shuffleCount) {
            return null;
        }

        Object[] out = new Object[shuffleCount];
        for (int i = 1; i <= shuffleCount; i++) {
            int random = getRandom(length - i);
            out[i - 1] = objArray[random];
            Object temp = objArray[length - i];
            objArray[length - i] = objArray[random];
            objArray[random] = temp;
        }
        return out;
    }

    /**
     * 从int[]数组中，随机获取大小不定的int[]数组
     * @param intArray 源数组
     * @return 长度不定的int数组，里面的元素是源数组的元素
     */
    public static int[] shuffle(int[] intArray) {
        if (intArray == null) {
            return null;
        }

        return shuffle(intArray, getRandom(intArray.length));
     }

     /**
     * 从int[]数组中，随机获取指定大小的int[]数组
     * @param intArray 源数组
     * @param shuffleCount 指定生产的数组大小
     * @return  返回int数组，大小为shuffleCount，里面的元素是源数组的元素.如果intArray是null或者shuffleCount大于intArray的长度返回null
     */
    public static int[] shuffle(int[] intArray, int shuffleCount) {
        int length;
        if (intArray == null || shuffleCount < 0 || (length = intArray.length) < shuffleCount) {
            return null;
        }

        int[] out = new int[shuffleCount];
        for (int i = 1; i <= shuffleCount; i++) {
            int random = getRandom(length - i);
            out[i - 1] = intArray[random];
            int temp = intArray[length - i];
            intArray[length - i] = intArray[random];
            intArray[random] = temp;
        }
        return out;
    }

    /**
     * 获取随机uuid
     * @return
     */
    public static String randomUUID() {
        return UUID.randomUUID().toString();
    }
}
