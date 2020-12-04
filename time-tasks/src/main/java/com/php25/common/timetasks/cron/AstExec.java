package com.php25.common.timetasks.cron;

import com.php25.common.core.util.TimeUtil;
import com.php25.common.timetasks.exception.CronException;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.stream.IntStream;

/**
 * 执行AST
 *
 * @author penghuiping
 * @date 2020/6/2 11:05
 */
public class AstExec {

    /**
     * 一个月的第几周，-1:最后一周,0:全部,1:第一周
     */
    private int weekOfMonth = 0;

    /**
     * false:week true:day
     */
    private boolean weekOrDay = false;

    private boolean workday = false;

    public boolean isWeekOrDay() {
        return weekOrDay;
    }

    public int getWeekOfMonth() {
        return weekOfMonth;
    }

    public boolean isWorkday() {
        return workday;
    }

    /**
     * 返回从当前时间算的下次执行时间
     *
     * @param ast
     * @param baseTime 基时间,下次执行的时间需要在基时间之后
     * @return
     */
    public LocalDateTime execCronExpr(AST ast, LocalDateTime baseTime) {
        IntStream[] timeStreams = getAllPossibleTimeStream(ast);
        TimeNode rootNode = getTimeNode(timeStreams);
        LocalDateTime now = baseTime;
        boolean getFirst = false;
        TimeNode node = rootNode;
        //第一次访问 week node
        boolean flag = true;
        int[] weekTimes = null;
        while (true) {
            int nowTime = -1;
            switch (node.getUnit()) {
                case YEARS: {
                    nowTime = now.getYear();
                    break;
                }
                case MONTHS: {
                    nowTime = now.getMonth().getValue();
                    break;
                }
                case WEEKS: {
                    int year = rootNode.getTimes()[rootNode.getIndex()];
                    int month = rootNode.getNext().getTimes()[rootNode.getNext().getIndex()];
                    int maxDay = TimeUtil.getLastDayOfMonth(year, month);
                    if (flag) {
                        weekTimes = node.getTimes();
                        flag = false;
                    }
                    IntStream weekStream = IntStream.empty();
                    int t = -1;
                    for (int i = 1; i <= maxDay; i++) {
                        LocalDateTime temp = LocalDateTime.of(year, month, i, 0, 0);
                        for (int j = 0; j < weekTimes.length; j++) {
                            //weekday 1-7
                            int weekday = weekTimes[j];
                            if (weekday == mapToCronWeekday(temp.getDayOfWeek().getValue())) {
                                if (weekOfMonth > 0) {
                                    //>0表示: 一个月中的某一周
                                    //先判断第一周从哪里开始算
                                    if(t<0) {
                                        LocalDateTime start0 = TimeUtil.getWeekDayOfMonth(year, 1, 1, month).minusDays(1);
                                        LocalDateTime end0 = TimeUtil.getWeekDayOfMonth(year, 7, 1, month).plusDays(1);
                                        if (temp.isAfter(start0) && temp.isBefore(end0)) {
                                            t = 0;
                                        } else {
                                            t = 1;
                                        }
                                    }

                                    LocalDateTime start = TimeUtil.getWeekDayOfMonth(year, 1, weekOfMonth+t, month).minusDays(1);
                                    LocalDateTime end = TimeUtil.getWeekDayOfMonth(year, 7, weekOfMonth+t, month).plusDays(1);
                                    if (temp.isAfter(start) && temp.isBefore(end)) {
                                        weekStream = IntStream.concat(weekStream, IntStream.of(temp.getDayOfMonth()));
                                    }
                                } else if (weekOfMonth == 0) {
                                    //0表示: 一个月中的全部周
                                    weekStream = IntStream.concat(weekStream, IntStream.of(temp.getDayOfMonth()));
                                } else {
                                    //<0表示: 一个月的倒数第几周
                                    // 这种情况在cron表达式中，只有L字母出现会发生，表示最后的意思，比如本月最后一个星期五。
                                    // 这里给出最后两周的时间，因为很可能本月最后一周没有星期五
                                    LocalDateTime start = TimeUtil.getWeekDayOfMonthReverse(year, 1, 2, month).minusDays(1);
                                    LocalDateTime end = TimeUtil.getWeekDayOfMonthReverse(year, 7, 1, month).plusDays(1);
                                    if (temp.isAfter(start) && temp.isBefore(end)) {
                                        weekStream = IntStream.concat(weekStream, IntStream.of(temp.getDayOfMonth()));
                                    }
                                }
                                break;
                            }
                        }
                    }

                    if (weekOfMonth < 0) {
                        node.setTimes(new int[]{weekStream.max().getAsInt()});
                    } else {
                        node.setTimes(weekStream.toArray());
                    }
                    nowTime = now.getDayOfMonth();
                    break;
                }
                case DAYS: {
                    int year = rootNode.getTimes()[rootNode.getIndex()];
                    int month = rootNode.getNext().getTimes()[rootNode.getNext().getIndex()];
                    int maxDay = TimeUtil.getLastDayOfMonth(year, month);
                    if (1 == node.getTimes().length && 0 == node.getTimes()[0]) {
                        //day = 0 的情况 ，表示每月的全部日子
                        node.setTimes(IntStream.rangeClosed(1, maxDay).toArray());
                    } else if (1 == node.getTimes().length && 0 > node.getTimes()[0]) {
                        //day < 0 的情况，表示从每月最后一天开始倒数第几天
                        if (!workday) {
                            //没有工作日范围限定
                            node.setTimes(IntStream.of(maxDay + node.getTimes()[0] + 1).toArray());
                        } else {
                            //只能是工作日
                            int[] value = IntStream.rangeClosed(1, maxDay).mapToObj(v -> LocalDateTime.of(year, month, v, 0, 0))
                                    .filter(localDateTime -> localDateTime.getDayOfWeek().getValue() >= 1 && localDateTime.getDayOfWeek().getValue() <= 5)
                                    .mapToInt(LocalDateTime::getDayOfMonth).toArray();
                            node.setTimes(IntStream.of(value[value.length - 1]).toArray());
                        }
                    }
                    nowTime = now.getDayOfMonth();
                    break;
                }
                case HOURS: {
                    nowTime = now.getHour();
                    break;
                }
                case MINUTES: {
                    nowTime = now.getMinute();
                    break;
                }
                case SECONDS: {
                    nowTime = now.getSecond();
                    break;
                }
                default: {
                    throw new CronException("不可能发生");
                }
            }

            boolean needGoBack = true;
            for (int i = node.getIndex(); i < node.getTimes().length; i++) {
                int time = node.getTimes()[i];
                if (i == 0 && getFirst) {
                    node.setIndex(i);
                    needGoBack = false;
                    break;
                } else if (time == nowTime) {
                    node.setIndex(i);
                    needGoBack = false;
                    break;
                } else if (time > nowTime) {
                    node.setIndex(i);
                    getFirst = true;
                    needGoBack = false;
                    break;
                }
            }

            if (needGoBack) {
                //开始回溯
                while (true) {
                    node = node.getPrevious();
                    if (null == node) {
                        return null;
                    }

                    if (node.getIndex() + 1 < node.getTimes().length) {
                        node.setIndex(node.getIndex() + 1);
                        break;
                    }

                    if (node.getIndex() + 1 == node.getTimes().length) {
                        node.setIndex(0);
                    }
                }
            } else {
                //获取下一个节点
                node = node.getNext();
            }

            if (node == null) {
                break;
            }
        }
        return fromTimeNode(rootNode);
    }

    private static int mapToCronWeekday(int weekday) {
        int result = -1;
        switch (weekday) {
            case 7:
                result = 1;
                break;
            default:
                result = weekday + 1;
                break;
        }
        return result;
    }


    /**
     * week与day范围 比较特殊 0:表示全部 -1表示最后  1表示第一
     *
     * @param ast
     * @return
     */
    IntStream[] getAllPossibleTimeStream(AST ast) {
        if (!(ast instanceof ASTCronExpr)) {
            throw new CronException("无法解析此AST");
        }
        ASTCronExpr cronExpr = (ASTCronExpr) ast;
        AstValid astValid = new AstValid();
        astValid.validateCronExpr(cronExpr);

        this.weekOrDay = ((SymbolCronExpr) cronExpr.symbol).weekOrDay;
        this.workday = ((SymbolCronExpr) cronExpr.symbol).workday;

        IntStream years;
        IntStream weeks;
        IntStream months;
        IntStream days;
        IntStream hours;
        IntStream minutes;
        IntStream seconds;
        if (cronExpr.getNodes().size() == 6) {
            years = IntStream.rangeClosed(1970, 2099).filter(value -> value >= LocalDateTime.now().getYear());
            weeks = cronExpr.getNodes().get(5).symbol.possibleTimeValues;
            this.weekOfMonth = cronExpr.getNodes().get(5).symbol.weekOfMonth;
            months = cronExpr.getNodes().get(4).symbol.possibleTimeValues;
            days = cronExpr.getNodes().get(3).symbol.possibleTimeValues;
            hours = cronExpr.getNodes().get(2).symbol.possibleTimeValues;
            minutes = cronExpr.getNodes().get(1).symbol.possibleTimeValues;
            seconds = cronExpr.getNodes().get(0).symbol.possibleTimeValues;
        } else if (cronExpr.getNodes().size() == 7) {
            years = cronExpr.getNodes().get(6).symbol.possibleTimeValues;
            weeks = cronExpr.getNodes().get(5).symbol.possibleTimeValues;
            this.weekOfMonth = cronExpr.getNodes().get(5).symbol.weekOfMonth;
            months = cronExpr.getNodes().get(4).symbol.possibleTimeValues;
            days = cronExpr.getNodes().get(3).symbol.possibleTimeValues;
            hours = cronExpr.getNodes().get(2).symbol.possibleTimeValues;
            minutes = cronExpr.getNodes().get(1).symbol.possibleTimeValues;
            seconds = cronExpr.getNodes().get(0).symbol.possibleTimeValues;
        } else {
            throw new CronException("Cron表达式问题,cron表达式节点只能有6或7个");
        }
        return new IntStream[]{seconds, minutes, hours, days, months, weeks, years};
    }

    /**
     * 构造TimeNode链表  years->months->weeks/days->hours->minutes->seconds
     *
     * @param streams
     * @return
     */
    TimeNode getTimeNode(IntStream[] streams) {
        int[] seconds = streams[0].toArray();
        int[] minutes = streams[1].toArray();
        int[] hours = streams[2].toArray();
        int[] days = streams[3].toArray();
        int[] months = streams[4].toArray();
        int[] weeks = streams[5].toArray();
        int[] years = streams[6].toArray();

        TimeNode rootNode = null;

        if (isWeekOrDay()) {
            //day
            TimeNode secondsNode = new TimeNode(seconds, null, 0, ChronoUnit.SECONDS);
            TimeNode minutesNode = new TimeNode(minutes, secondsNode, 0, ChronoUnit.MINUTES);
            TimeNode hoursNode = new TimeNode(hours, minutesNode, 0, ChronoUnit.HOURS);
            TimeNode daysNode = new TimeNode(days, hoursNode, 0, ChronoUnit.DAYS);
            TimeNode monthsNode = new TimeNode(months, daysNode, 0, ChronoUnit.MONTHS);
            TimeNode yearsNode = new TimeNode(years, monthsNode, 0, ChronoUnit.YEARS);
            yearsNode.setPrevious(null);
            monthsNode.setPrevious(yearsNode);
            daysNode.setPrevious(monthsNode);
            hoursNode.setPrevious(daysNode);
            minutesNode.setPrevious(hoursNode);
            secondsNode.setPrevious(minutesNode);
            rootNode = yearsNode;
        } else {
            //week
            TimeNode secondsNode = new TimeNode(seconds, null, 0, ChronoUnit.SECONDS);
            TimeNode minutesNode = new TimeNode(minutes, secondsNode, 0, ChronoUnit.MINUTES);
            TimeNode hoursNode = new TimeNode(hours, minutesNode, 0, ChronoUnit.HOURS);
            TimeNode weeksNode = new TimeNode(weeks, hoursNode, 0, ChronoUnit.WEEKS);
            TimeNode monthsNode = new TimeNode(months, weeksNode, 0, ChronoUnit.MONTHS);
            TimeNode yearsNode = new TimeNode(years, monthsNode, 0, ChronoUnit.YEARS);
            yearsNode.setPrevious(null);
            monthsNode.setPrevious(yearsNode);
            weeksNode.setPrevious(monthsNode);
            hoursNode.setPrevious(weeksNode);
            minutesNode.setPrevious(hoursNode);
            secondsNode.setPrevious(minutesNode);
            rootNode = yearsNode;
        }
        return rootNode;
    }

    /**
     * 把TimeNode链表转化成LocalDateTime，得出下次执行时间
     *
     * @param node 链表
     * @return 下次执行时间
     */
    LocalDateTime fromTimeNode(TimeNode node) {
        TimeNode yearsNode = node;
        TimeNode monthsNode = yearsNode.getNext();
        TimeNode daysNode = monthsNode.getNext();
        TimeNode hoursNode = daysNode.getNext();
        TimeNode minutesNode = hoursNode.getNext();
        TimeNode secondsNode = minutesNode.getNext();

        int year = yearsNode.getTimes()[yearsNode.getIndex()];
        int month = monthsNode.getTimes()[monthsNode.getIndex()];
        int day = daysNode.getTimes()[daysNode.getIndex()];
        int hour = hoursNode.getTimes()[hoursNode.getIndex()];
        int minute = minutesNode.getTimes()[minutesNode.getIndex()];
        int second = secondsNode.getTimes()[secondsNode.getIndex()];
        return LocalDateTime.of(year, month, day, hour, minute, second);
    }
}
