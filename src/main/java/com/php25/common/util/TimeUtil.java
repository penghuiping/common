package com.php25.common.util;

import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 时间处理帮助类
 *
 * 建议使用org.apache.commons.lang.time.DateUtils
 *
 * @author penghuiping
 * @Time 2014/8/13.
 */
public class TimeUtil {
    public static final SimpleDateFormat DEFAULT_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static final SimpleDateFormat DATE_FORMAT_DATE = new SimpleDateFormat("yyyy-MM-dd");
    public static final SimpleDateFormat NEW_DATE_FORMAT = new SimpleDateFormat("yyyyMMddHHmmss");

    public static String getNewTime() {
        return getCurrentTimeInString(NEW_DATE_FORMAT);
    }

    /**
     * 把日期类型的字符串，转换成日期类型
     *
     * @param dateStr
     * @return
     * @author penghuiping
     * @Time 2014/8/13.
     */
    public static Date parseDate(String dateStr) {
        try {
            return DateUtils.parseDate(dateStr, new String[]{"yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd", "yyyyMMddHHmmss"});
        } catch (ParseException e) {
            Logger.getLogger(TimeUtil.class).error(e);
            return null;
        }
    }


    /**
     * 把时间戳转成字符串
     *
     * @param timeInMillis
     * @param dateFormat
     * @return
     */
    public static String getTime(long timeInMillis, SimpleDateFormat dateFormat) {
        return dateFormat.format(new Date(timeInMillis));
    }

    /**
     * 把日期转成字符串
     *
     * @param date
     * @param dateFormat
     * @return
     */
    public static String getTime(Date date, SimpleDateFormat dateFormat) {
        return dateFormat.format(date);
    }

    /**
     * 把时间戳转成字符串，用默认的格式
     *
     * @param timeInMillis
     * @return
     */
    public static String getTime(long timeInMillis) {
        return getTime(timeInMillis, DEFAULT_DATE_FORMAT);
    }

    /**
     * 把日期转成字符串，用默认的格式
     *
     * @param date
     * @return
     */
    public static String getTime(Date date) {
        return getTime(date, DEFAULT_DATE_FORMAT);
    }

    /**
     * 获取当前时间戳
     *
     * @return
     */
    public static long getCurrentTimeInLong() {
        return System.currentTimeMillis();
    }

    /**
     * 获取当前时间，字符串格式，默认的格式
     *
     * @return
     */
    public static String getCurrentTimeInString() {
        return getTime(getCurrentTimeInLong());
    }

    /**
     * 获取当前时间，字符串形式
     *
     * @param dateFormat
     * @return
     */
    public static String getCurrentTimeInString(SimpleDateFormat dateFormat) {
        return getTime(getCurrentTimeInLong(), dateFormat);
    }

    /**
     * 获取某天的开始时间
     *
     * @param date 日期
     * @return 某天的开始时间
     */
    public static Date getBeginTimeOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    /**
     * 获取某天的结束时间
     *
     * @param date 日期
     * @return 某天的结束时间
     */
    public static Date getEndTimeOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }

    /**
     * 偏移天
     *
     * @param date    日期
     * @param offsite 偏移天数，正数向未来偏移，负数向历史偏移
     * @return 偏移后的日期
     */
    public static Date offsiteDay(Date date, int offsite) {
        return offsiteDate(date, Calendar.DAY_OF_YEAR, offsite);
    }

    /**
     * 偏移周
     *
     * @param date    日期
     * @param offsite 偏移周数，正数向未来偏移，负数向历史偏移
     * @return 偏移后的日期
     */
    public static Date offsiteWeek(Date date, int offsite) {
        return offsiteDate(date, Calendar.WEEK_OF_YEAR, offsite);
    }

    /**
     * 偏移月
     *
     * @param date    日期
     * @param offsite 偏移月数，正数向未来偏移，负数向历史偏移
     * @return 偏移后的日期
     */
    public static Date offsiteMonth(Date date, int offsite) {
        return offsiteDate(date, Calendar.MONTH, offsite);
    }

    /**
     * 获取指定日期偏移指定时间后的时间
     *
     * @param date          基准日期
     * @param calendarField 偏移的粒度大小（小时、天、月等）使用Calendar中的常数
     * @param offsite       偏移量，正数为向后偏移，负数为向前偏移
     * @return 偏移后的日期
     */
    public static Date offsiteDate(Date date, int calendarField, int offsite) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(calendarField, offsite);
        return cal.getTime();
    }
}
