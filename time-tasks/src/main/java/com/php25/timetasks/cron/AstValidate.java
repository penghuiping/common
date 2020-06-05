package com.php25.timetasks.cron;

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
public class AstValidate {

    public static void validateCronExpr(AST ast) {

    }

    public static void validateCronNode(AST ast) {

    }

    public static void validateLastExpr(ASTLastExpr node) {

    }

    public static void validateThreeExpr(ASTThreeExpr node) {

    }

    public static void validateSimplestSymbol(ASTSimplestSymbol ss) {

    }


}
