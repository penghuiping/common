package com.php25.timetasks.cron;

import com.php25.timetasks.exception.CronException;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.IntStream;

/**
 * 验证AST语法
 * <p>
 * {秒数} {分钟} {小时} {日期} {月份} {星期} {年份(可为空)}
 * <p>
 * SUN-MON    只能出现在{星期},每周日至每周一触发
 * SUN,MON    只能出现在{星期},每周日，每周一触发
 * SUN        只能出现在{星期},每周日触发
 * 10,20,30
 * 10
 * 15-45      15开始每隔1触发一次直到45
 * 15/5       15开始每隔5触发一次
 * 15-30/5    15开始每隔5触发一次直到30
 * 4#2        只能出现在{星期},表示一星期的第4天，一个月的第2周，一星期从星期日开始
 * LW         W表示工作日，只能出现在{日期},LW表示一个月的最后一天工作日，5W表示这个月第5个工作日，超出范围则取本月的最后一个工作日
 * L          表示最后，只能出现在{日期}与{星期},在{日期}表示每月的最后一天,在{星期}表示最后一周
 * 5L         在{日期}表示每月的倒数第5天,在{星期}表示最后一个星期四
 * ?          只有{日期}与{星期}才能用，表示不启用,所以{日期}与{星期}位，不能同时存在
 * *
 * <p>
 * Grammar
 * <p>
 * CronExpr := (LastExpr|ThreeExpr)+
 * ThreeExpr :=   SimplestSymbol (LinkSymbol SimplestSymbol)*
 * LastExpr := (数字)?(英文字符L)(W)?
 * SimplestSymbol :=  数字 | 英文字符 | 问好 | 星号
 * LinkSymbol := '-' | '/' | '#'
 * <p>
 *
 * @author penghuiping
 * @date 2020/6/2 14:08
 */
public class AstValid {

    public void validateCronExpr(AST ast) {
        if (ast instanceof ASTCronExpr) {
            ASTCronExpr cronExpr = (ASTCronExpr) ast;
            SymbolCronExpr symbolCronExpr = new SymbolCronExpr();
            symbolCronExpr.previous = null;
            cronExpr.symbol = symbolCronExpr;
            List<AST> astList = cronExpr.getNodes();
            for (int i = 0; i < astList.size(); i++) {
                AST cronNode = astList.get(i);
                if (cronNode instanceof ASTThreeExpr) {
                    //ASTThreeExpr
                    ASTThreeExpr threeExpr = (ASTThreeExpr) cronNode;
                    SymbolThreeExpr symbolThreeExpr = new SymbolThreeExpr();
                    symbolThreeExpr.previous = symbolCronExpr;
                    threeExpr.symbol = symbolThreeExpr;
                    threeExpr.symbol.unit = fromIndex(i);
                } else {
                    //LastExpr
                    ASTLastExpr lastExpr = (ASTLastExpr) cronNode;
                    SymbolLastExpr symbolLastExpr = new SymbolLastExpr();
                    symbolLastExpr.previous = symbolCronExpr;
                    lastExpr.symbol = symbolLastExpr;
                    lastExpr.symbol.unit = fromIndex(i);
                }
                validateCronNode(cronNode);
            }
        }
    }

    private void validateCronNode(AST ast) {
        if (ast instanceof ASTThreeExpr) {
            ASTThreeExpr node = (ASTThreeExpr) ast;
            validateThreeExpr(node);
        } else {
            ASTLastExpr node = (ASTLastExpr) ast;
            validateLastExpr(node);
        }
    }

    /**
     * validateLastExpr 只能支持 L、LW、5L
     *
     * @param node
     */
    private void validateLastExpr(ASTLastExpr node) {
        SymbolLastExpr symbol = (SymbolLastExpr) node.symbol;
        if (Tokens.isL(node.getLeft()) && null == node.getRight()) {
            //1. L
            if (ChronoUnit.WEEKS.equals(symbol.unit)) {
                symbol.weekOfMonth = -1;
                symbol.possibleTimeValues = getScopeTime(symbol.unit);
            } else {
                symbol.possibleTimeValues = IntStream.of(-1);
            }
        } else if (Tokens.isDigit(node.getLeft()) && Tokens.isL(node.getRight())) {
            //2. 5L
            if (ChronoUnit.WEEKS.equals(symbol.unit)) {
                symbol.weekOfMonth = -1;
                symbol.possibleTimeValues = IntStream.of(Integer.parseInt(node.getLeft().getValue()));
            } else {
                symbol.possibleTimeValues = IntStream.of(-Integer.parseInt(node.getLeft().getValue()));
            }
        } else if (Tokens.isL(node.getLeft()) && Tokens.isW(node.getRight())) {
            //3. LW
            SymbolCronExpr top = getTopSymbol(symbol);
            top.workday = true;
            symbol.possibleTimeValues = IntStream.of(-1);
        } else {
            throw new CronException("cron表达式问题,请查看是否符合L、5L、LW这类格式");
        }
    }

    private void validateThreeExpr(ASTThreeExpr node) {
        AST left = node.getLeft();
        AST linkSymbol = node.getLinkSymbol();
        AST right = node.getRight();
        if (left instanceof ASTThreeExpr) {
            SymbolThreeExpr symbol = new SymbolThreeExpr();
            symbol.unit = node.symbol.unit;
            symbol.previous = node.symbol;
            left.symbol = symbol;
            validateThreeExpr((ASTThreeExpr) left);
        } else {
            SymbolSimplestSymbol symbol = new SymbolSimplestSymbol();
            symbol.unit = node.symbol.unit;
            symbol.previous = node.symbol;
            left.symbol = symbol;
            ASTSimplestSymbol ss = (ASTSimplestSymbol) left;
            validateSimplestSymbol(ss);
        }

        if (null != linkSymbol && null != right) {
            ASTLinkSymbol linkSymbol1 = (ASTLinkSymbol) linkSymbol;

            if (TokenType.horizontal_bar.equals(linkSymbol1.getValue().getType())) {
                //1-5 sun-sat
                if (!(left instanceof ASTSimplestSymbol) || !(right instanceof ASTSimplestSymbol)) {
                    throw new CronException("cron表达式问题,'-'前面与后面只能是数字或者SUN-SAT英文字母的星期");
                }
                ASTSimplestSymbol left1 = (ASTSimplestSymbol) left;
                ASTSimplestSymbol right1 = (ASTSimplestSymbol) right;

                SymbolSimplestSymbol symbol = new SymbolSimplestSymbol();
                symbol.unit = node.symbol.unit;
                symbol.previous = node.symbol;
                right1.symbol = symbol;
                validateSimplestSymbol(right1);

                int left2 = left.symbol.possibleTimeValues.findFirst().orElse(0);
                int right2 = right.symbol.possibleTimeValues.findFirst().orElse(0);
                if (TokenType.digit.equals(left1.getToken().getType()) && TokenType.digit.equals(right1.getToken().getType())) {
                    node.symbol.possibleTimeValues = IntStream.rangeClosed(left2, right2);
                } else if (TokenType.letter.equals(left1.getToken().getType()) && TokenType.letter.equals(right1.getToken().getType())) {
                    node.symbol.possibleTimeValues = IntStream.rangeClosed(left2, right2);
                } else {
                    throw new CronException("cron表达式问题,'-'前面与后面只能是数字或者SUN-SAT英文字母的星期");
                }
            } else if (TokenType.slanting_bar.equals(linkSymbol1.getValue().getType())) {
                //如果是'/'存在2种情况 1. 1-10/5  2. 10/5
                if (left instanceof ASTThreeExpr) {
                    //情况1
                    if (!(right instanceof ASTSimplestSymbol)) {
                        throw new CronException("cron表达式问题,'/'后面只能是数字");
                    }
                    ASTSimplestSymbol right1 = (ASTSimplestSymbol) right;
                    if (!TokenType.digit.equals(right1.getToken().getType())) {
                        throw new CronException("cron表达式问题,'/'后面只能是数字");
                    }

                    SymbolSimplestSymbol symbol = new SymbolSimplestSymbol();
                    symbol.unit = node.symbol.unit;
                    symbol.previous = node.symbol;
                    right1.symbol = symbol;
                    validateSimplestSymbol(right1);

                    int rightResult = Integer.parseInt(right1.getToken().getValue());
                    node.symbol.possibleTimeValues = left.symbol.possibleTimeValues
                            .filter(value -> value % rightResult == 0);
                } else {
                    //情况2
                    if (!(right instanceof ASTSimplestSymbol)) {
                        throw new CronException("cron表达式问题,'/'后面只能是数字");
                    }
                    ASTSimplestSymbol right1 = (ASTSimplestSymbol) right;
                    if (!TokenType.digit.equals(right1.getToken().getType())) {
                        throw new CronException("cron表达式问题,'/'后面只能是数字");
                    }

                    ASTSimplestSymbol left1 = (ASTSimplestSymbol) left;
                    if (!TokenType.digit.equals(left1.getToken().getType())) {
                        throw new CronException("cron表达式问题,'/'只支持1.1-10/5  2.10/5，这两类格式");
                    }

                    SymbolSimplestSymbol symbol = new SymbolSimplestSymbol();
                    symbol.unit = node.symbol.unit;
                    symbol.previous = node.symbol;
                    right1.symbol = symbol;
                    validateSimplestSymbol(right1);

                    int leftResult = Integer.parseInt(left1.getToken().getValue());
                    int rightResult = Integer.parseInt(right1.getToken().getValue());
                    node.symbol.possibleTimeValues = getScopeTime(node.symbol.unit)
                            .filter(value -> value >= leftResult)
                            .filter(value -> value % rightResult == 0);
                }
            } else if (TokenType.comma.equals(linkSymbol1.getValue().getType())) {
                //2种情况 1. 1,2   2. sun,mon
                if (!(right instanceof ASTSimplestSymbol)) {
                    throw new CronException("cron表达式问题,','前面与后面只能是数字或者SUN-SAT英文字母的星期");
                }
                ASTSimplestSymbol right1 = (ASTSimplestSymbol) right;

                SymbolSimplestSymbol symbol = new SymbolSimplestSymbol();
                symbol.unit = node.symbol.unit;
                symbol.previous = node.symbol;
                right1.symbol = symbol;
                validateSimplestSymbol(right1);

                if (left instanceof ASTThreeExpr) {
                    node.symbol.possibleTimeValues = IntStream.concat(left.symbol.possibleTimeValues, right1.symbol.possibleTimeValues);
                } else {
                    ASTSimplestSymbol left1 = (ASTSimplestSymbol) left;
                    if (TokenType.digit.equals(left1.getToken().getType()) && TokenType.digit.equals(right1.getToken().getType())) {
                        node.symbol.possibleTimeValues = IntStream.concat(left1.symbol.possibleTimeValues, right1.symbol.possibleTimeValues);
                    } else if (TokenType.letter.equals(left1.getToken().getType()) && TokenType.letter.equals(left1.getToken().getType())) {
                        node.symbol.possibleTimeValues = IntStream.concat(left1.symbol.possibleTimeValues, right1.symbol.possibleTimeValues);
                    } else {
                        throw new CronException("cron表达式问题,','前面与后面只能是数字或者SUN-SAT英文字母的星期");
                    }
                }
            } else if (TokenType.well_number.equals(linkSymbol1.getValue().getType())) {
                //只能出现在{星期} 4#2 表示一星期的第4天，一个月的第2周，一星期从星期日开始
                if (!ChronoUnit.WEEKS.equals(node.symbol.unit)) {
                    throw new CronException("cron表达式问题,'#'只能出现在week位");
                }

                if (!(left instanceof ASTSimplestSymbol) || !(right instanceof ASTSimplestSymbol)) {
                    throw new CronException("cron表达式问题,'#'前面与后面只能是数字");
                }
                ASTSimplestSymbol left1 = (ASTSimplestSymbol) left;
                ASTSimplestSymbol right1 = (ASTSimplestSymbol) right;

                SymbolSimplestSymbol symbol = new SymbolSimplestSymbol();
                symbol.unit = node.symbol.unit;
                symbol.previous = node.symbol;
                right1.symbol = symbol;
                validateSimplestSymbol(right1);

                if (TokenType.digit.equals(left1.getToken().getType()) && TokenType.digit.equals(right1.getToken().getType())) {
                    int left2 = Integer.parseInt(left1.getToken().getValue());
                    int right2 = Integer.parseInt(right1.getToken().getValue());
                    node.symbol.weekOfMonth = right2;
                    node.symbol.possibleTimeValues = IntStream.of(left2);
                } else {
                    throw new CronException("cron表达式问题,'#'前面与后面只能是数字");
                }
            } else {
                throw new CronException("cron表达式问题,不支持的连接符:" + linkSymbol1.getValue().getValue());
            }
        } else {
            node.symbol.possibleTimeValues = left.symbol.possibleTimeValues;
        }
    }

    private void validateSimplestSymbol(ASTSimplestSymbol ss) {
        Token token = ss.getToken();
        if (Tokens.isDigit(token)) {
            //token: [0-9]
            ss.symbol.possibleTimeValues = IntStream.of(Integer.parseInt(token.getValue()));
        } else if (Tokens.isLetter(token)) {
            //token: SUN-SAT
            ss.symbol.possibleTimeValues = IntStream.of(getFromWeekName(token.getValue()));
        } else if (Tokens.isQuestionMark(token)) {
            //token: ?
            if (ChronoUnit.WEEKS.equals(ss.symbol.unit)) {
                SymbolCronExpr top = getTopSymbol(ss.symbol);
                top.weekOrDay = true;
            } else {
                SymbolCronExpr top = getTopSymbol(ss.symbol);
                top.weekOrDay = false;
            }
            ss.symbol.possibleTimeValues = IntStream.empty();
        } else if (Tokens.isAsterisk(token)) {
            //token: *
            ss.symbol.possibleTimeValues = getScopeTime(ss.symbol.unit);
        } else {
            throw new CronException("cron表达式问题，不支持的token:" + token.getValue());
        }
    }

    private static final String[] weeks = new String[]{"SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT"};

    private int getFromWeekName(String name) {
        int i = 1;
        for (String week : weeks) {
            if (week.equals(name)) {
                return i;
            }
            i++;
        }
        throw new CronException("name不合法，请使用正确的星期名");
    }

    private SymbolCronExpr getTopSymbol(Symbol symbol) {
        Symbol result = null;
        while (true) {
            if (null == symbol.previous) {
                result = symbol;
                break;
            }
            symbol = symbol.previous;
        }
        return (SymbolCronExpr) result;
    }


    private IntStream getScopeTime(ChronoUnit unit) {
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
     * @param index
     * @return
     */
    private ChronoUnit fromIndex(int index) {
        switch (index) {
            case 0:
                return ChronoUnit.SECONDS;
            case 1:
                return ChronoUnit.MINUTES;
            case 2:
                return ChronoUnit.HOURS;
            case 3:
                return ChronoUnit.DAYS;
            case 4:
                return ChronoUnit.MONTHS;
            case 5:
                return ChronoUnit.WEEKS;
            case 6:
                return ChronoUnit.YEARS;
            default:
                throw new CronException("不可能发生!");
        }
    }
}
