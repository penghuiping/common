package com.php25.timetasks.cron;

import com.php25.timetasks.exception.CronException;

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

    public boolean isWeekOrDay() {
        return weekOrDay;
    }

    private boolean workday = false;

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
     * @return
     */
    public LocalDateTime execCronExpr(AST ast) {
        IntStream[] timeStreams = getAllPossibleTimeStream(ast);
        TimeNode rootNode = getTimeNode(timeStreams);
        LocalDateTime now = LocalDateTime.now();
        boolean getFirst = false;
        TimeNode node = rootNode;
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
                    LocalDateTime time = LocalDateTime.of(year, month, 1, 0, 0);
                    boolean leapYear = time.getChronology().isLeapYear(time.getYear());
                    int maxDay = LocalDateTime.of(year, month, 1, 0, 0).getMonth().length(leapYear);
                    if (weekOfMonth >= 0) {
                        IntStream stream = IntStream.empty();
                        int z = 0;
                        for (int i = 1; i <= maxDay; i++) {
                            LocalDateTime temp = LocalDateTime.of(year, month, i, 0, 0);
                            for (int j = 0; j < node.getTimes().length; j++) {
                                int weekday = node.getTimes()[j];
                                if (weekday == mapToCronWeekday(temp.getDayOfWeek().getValue())) {
                                    z++;
                                    if (weekOfMonth > 0 && z == weekOfMonth) {
                                        stream = IntStream.concat(stream, IntStream.of(temp.getDayOfMonth()));
                                    } else if (weekOfMonth == 0) {
                                        stream = IntStream.concat(stream, IntStream.of(temp.getDayOfMonth()));
                                    }
                                    break;
                                }
                            }
                        }
                        node.setTimes(stream.toArray());
                    } else {
                        IntStream stream = IntStream.empty();
                        for (int i = maxDay; i >= 1; i--) {
                            LocalDateTime temp = LocalDateTime.of(year, month, i, 0, 0);
                            for (int j = 0; j < node.getTimes().length; j++) {
                                int weekday = node.getTimes()[j];
                                if (weekday == mapToCronWeekday(temp.getDayOfWeek().getValue())) {
                                    stream = IntStream.concat(stream, IntStream.of(temp.getDayOfMonth()));
                                    i = 0;
                                    break;
                                }
                            }
                        }
                        node.setTimes(stream.toArray());
                    }
                    nowTime = now.getDayOfMonth();
                    break;
                }
                case DAYS: {
                    int year = rootNode.getTimes()[rootNode.getIndex()];
                    int month = rootNode.getNext().getTimes()[rootNode.getNext().getIndex()];
                    LocalDateTime time = LocalDateTime.of(year, month, 1, 0, 0);
                    boolean leapYear = time.getChronology().isLeapYear(time.getYear());
                    int maxDay = LocalDateTime.of(year, month, 1, 0, 0).getMonth().length(leapYear);

                    if (node.getTimes().length == 1 && 0 == node.getTimes()[0]) {
                        //day = 0 的情况
                        node.setTimes(IntStream.rangeClosed(1, maxDay).toArray());
                    } else if (node.getTimes().length == 1 && 0 > node.getTimes()[0]) {
                        //day < 0 的情况
                        if (!workday) {
                            node.setTimes(IntStream.of(node.getTimes()[0] + 1 + maxDay).toArray());
                        } else {
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
                node = node.getPrevious();
                if (node.getIndex() + 1 < node.getTimes().length) {
                    node.setIndex(node.getIndex() + 1);
                }
            } else {
                node = node.getNext();
            }

            if (node == null) {
                break;
            }
        }

        return fromTimeNode(rootNode);
    }

    public IntStream execCronNode(AST ast, ChronoUnit unit) {
        if (ast instanceof ASTThreeExpr) {
            ASTThreeExpr node = (ASTThreeExpr) ast;
            return execThreeExpr(node, unit);
        } else {
            ASTLastExpr node = (ASTLastExpr) ast;
            return execLastExpr(node, unit);
        }
    }

    public IntStream execLastExpr(ASTLastExpr node, ChronoUnit unit) {
        if (Tokens.isL(node.getLeft()) && null == node.getRight()) {
            //1. L
            if (unit.equals(ChronoUnit.WEEKS)) {
                this.weekOfMonth = -1;
                return getScopeTime(unit);
            } else {
                return IntStream.of(-1);
            }
        } else if (Tokens.isDigit(node.getLeft()) && Tokens.isL(node.getRight())) {
            //2. 5L
            if (unit.equals(ChronoUnit.WEEKS)) {
                this.weekOfMonth = -1;
                return IntStream.of(Integer.parseInt(node.getLeft().getValue()));
            } else {
                return IntStream.of(-Integer.parseInt(node.getLeft().getValue()));
            }
        } else if (Tokens.isL(node.getLeft()) && Tokens.isW(node.getRight())) {
            //3. LW
            this.workday = true;
            return IntStream.of(-1);
        } else {
            throw new CronException("cron表达式问题");
        }
    }

    public IntStream execThreeExpr(ASTThreeExpr node, ChronoUnit unit) {
        AST left = node.getLeft();
        AST linkSymbol = node.getLinkSymbol();
        AST right = node.getRight();
        IntStream leftResult = null;
        Token leftResult1 = null;

        if (left instanceof ASTThreeExpr) {
            leftResult = execThreeExpr((ASTThreeExpr) left, unit);
        } else {
            ASTSimplestSymbol ss = (ASTSimplestSymbol) left;
            leftResult1 = execSimplestSymbol(ss, unit);
        }

        if (null != linkSymbol && null != right) {
            ASTLinkSymbol linkSymbol1 = (ASTLinkSymbol) linkSymbol;
            if (linkSymbol1.getValue().getType().equals(TokenType.horizontal_bar)) {
                //1-5 sun-sat
                ASTSimplestSymbol ss1 = (ASTSimplestSymbol) right;
                Token rightResult = execSimplestSymbol(ss1, unit);
                if (TokenType.digit.equals(leftResult1.getType()) && TokenType.digit.equals(rightResult.getType())) {
                    return IntStream.rangeClosed(Integer.parseInt(leftResult1.getValue()), Integer.parseInt(rightResult.getValue()));
                } else if (TokenType.letter.equals(leftResult1.getType()) && TokenType.letter.equals(rightResult.getType())) {
                    return IntStream.rangeClosed(getFromWeekName(leftResult1.getValue()), getFromWeekName(rightResult.getValue()));
                } else {
                    throw new CronException("cron表达式问题,连接符'-'连接的只能是数字或者星期名");
                }

            } else if (linkSymbol1.getValue().getType().equals(TokenType.slanting_bar)) {
                //如果是'/'存在2种情况 1. 1-10/5  2. 10/5
                if (null != leftResult) {
                    //情况1
                    ASTSimplestSymbol ss1 = (ASTSimplestSymbol) right;
                    int rightResult = Integer.parseInt(ss1.getToken().getValue());
                    return leftResult.filter(value -> value % rightResult == 0);
                } else {
                    //情况2
                    ASTSimplestSymbol ss1 = (ASTSimplestSymbol) right;
                    int leftResult_ = Integer.parseInt(leftResult1.getValue());
                    int rightResult = Integer.parseInt(ss1.getToken().getValue());
                    return getScopeTime(unit).filter(value -> value >= leftResult_).filter(value -> value % rightResult == 0);
                }

            } else if (linkSymbol1.getValue().getType().equals(TokenType.comma)) {
                //1,2   sun,mon
                ASTSimplestSymbol ss1 = (ASTSimplestSymbol) right;
                Token rightResult = execSimplestSymbol(ss1, unit);
                if (null != leftResult) {
                    if (Tokens.isDigit(rightResult)) {
                        return IntStream.concat(leftResult, IntStream.of(Integer.parseInt(rightResult.getValue())));
                    } else {
                        return IntStream.concat(leftResult, IntStream.of(getFromWeekName(rightResult.getValue())));
                    }
                } else {
                    if (Tokens.isDigit(rightResult)) {
                        return IntStream.of(Integer.parseInt(leftResult1.getValue()), Integer.parseInt(rightResult.getValue()));
                    } else {
                        return IntStream.of(Integer.parseInt(leftResult1.getValue()), getFromWeekName(rightResult.getValue()));
                    }
                }

            } else {
                //只能出现在{星期} 4#2 表示一星期的第4天，一个月的第2周，一星期从星期日开始
                ASTSimplestSymbol ss1 = (ASTSimplestSymbol) right;
                int left_ = Integer.parseInt(leftResult1.getValue());
                int right_ = Integer.parseInt(ss1.getToken().getValue());
                this.weekOfMonth = right_;
                return IntStream.of(left_);
            }
        } else {
            if (Tokens.isDigit(leftResult1)) {
                return IntStream.of(Integer.parseInt(leftResult1.getValue()));
            } else if (Tokens.isLetter(leftResult1)) {
                return IntStream.of(getFromWeekName(leftResult1.getValue()));
            } else if (Tokens.isQuestionMark(leftResult1)) {
                if (ChronoUnit.WEEKS.equals(unit)) {
                    this.weekOrDay = true;
                } else {
                    this.weekOrDay = false;
                }
                return IntStream.empty();
            } else {
                return getScopeTime(unit);
            }
        }
    }

    public Token execSimplestSymbol(ASTSimplestSymbol ss, ChronoUnit unit) {
        return ss.getToken();
    }

    private static final String[] weeks = new String[]{"SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT"};

    private static int getFromWeekName(String name) {
        int i = 1;
        for (String week : weeks) {
            if (week.equals(name)) {
                return i;
            }
            i++;
        }
        throw new CronException("name不合法，请使用正确的星期名");
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

    private static IntStream getScopeTime(ChronoUnit unit) {
        if (ChronoUnit.SECONDS == unit) {
            return IntStream.rangeClosed(0, 59);
        } else if (ChronoUnit.MINUTES == unit) {
            return IntStream.rangeClosed(0, 59);
        } else if (ChronoUnit.HOURS == unit) {
            return IntStream.rangeClosed(0, 23);
        } else if (ChronoUnit.DAYS == unit) {
            return IntStream.of(0);
        } else if (ChronoUnit.WEEKS == unit) {
            return IntStream.rangeClosed(1, 7);
        } else if (ChronoUnit.MONTHS == unit) {
            return IntStream.rangeClosed(1, 12);
        } else if (ChronoUnit.YEARS == unit) {
            return IntStream.rangeClosed(1970, 2099).filter(value -> value >= LocalDateTime.now().getYear());
        } else {
            throw new CronException("不支持此时间单位" + unit.toString());
        }
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
        IntStream years;
        IntStream weeks;
        IntStream months;
        IntStream days;
        IntStream hours;
        IntStream minutes;
        IntStream seconds;
        if (cronExpr.getNodes().size() == 6) {
            years = getScopeTime(ChronoUnit.YEARS);
            weeks = execCronNode(cronExpr.getNodes().get(5), ChronoUnit.WEEKS);
            months = execCronNode(cronExpr.getNodes().get(4), ChronoUnit.MONTHS);
            days = execCronNode(cronExpr.getNodes().get(3), ChronoUnit.DAYS);
            hours = execCronNode(cronExpr.getNodes().get(2), ChronoUnit.HOURS);
            minutes = execCronNode(cronExpr.getNodes().get(1), ChronoUnit.MINUTES);
            seconds = execCronNode(cronExpr.getNodes().get(0), ChronoUnit.SECONDS);
        } else if (cronExpr.getNodes().size() == 7) {
            years = execCronNode(cronExpr.getNodes().get(6), ChronoUnit.YEARS);
            weeks = execCronNode(cronExpr.getNodes().get(5), ChronoUnit.WEEKS);
            months = execCronNode(cronExpr.getNodes().get(4), ChronoUnit.MONTHS);
            days = execCronNode(cronExpr.getNodes().get(3), ChronoUnit.DAYS);
            hours = execCronNode(cronExpr.getNodes().get(2), ChronoUnit.HOURS);
            minutes = execCronNode(cronExpr.getNodes().get(1), ChronoUnit.MINUTES);
            seconds = execCronNode(cronExpr.getNodes().get(0), ChronoUnit.SECONDS);
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
