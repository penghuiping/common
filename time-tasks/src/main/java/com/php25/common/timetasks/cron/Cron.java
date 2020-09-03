package com.php25.common.timetasks.cron;

import com.php25.common.core.mess.LruCache;
import com.php25.common.core.mess.LruCacheImpl;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 1. cron表达式格式：
 * {秒数} {分钟} {小时} {日期} {月份} {星期} {年份(可为空)}
 * <p>
 * 2. cron表达式各占位符解释
 * {秒数} ==> 允许值范围: 0~59 ,不允许为空值
 * {分钟} ==> 允许值范围: 0~59 ,不允许为空值
 * {小时} ==> 允许值范围: 0~23 ,不允许为空值
 * {日期} ==> 允许值范围: 1~31 ,不允许为空值
 * {月份} ==> 允许值范围: 1~12 ,不允许为空值
 * {星期} ==> 允许值范围: 1~7或SUN-SAT 1代表星期天(一星期的第一天)，以此类推，7代表星期六,不允许为空值
 * {年份} ==> 允许值范围: 1970~2099 ,允许为空
 * <p>
 * "*" 代表每隔1(秒/分/小时/日/月/星期/年)触发；
 * "," 代表在指定的秒数触发，比如"0,15,45"代表0秒、15秒和45秒时触发任务
 * "-" 代表在指定的范围内触发，比如"25-45"代表从25秒开始触发到45秒结束触发，每隔1秒触发1次
 * "?" 只有{日期}与{星期}才能用，表示互斥，即意味着若明确指定{星期}触发，则表示{日期}无意义，以免引起 冲突和混乱
 * /：表示起始时间开始触发，然后每隔固定时间触发一次。例如在Minutes域使用5/20,则意味着5分钟触发一次，而25，45等分别触发一次.
 * L：表示最后，只能出现在DayofWeek和DayofMonth域。如果在DayofWeek域使用5L,意味着在最后的一个星期四触发。
 * W:表示有效工作日(周一到周五),只能出现在DayofMonth域，系统将在离指定日期的最近的有效工作日触发事件。例如：在 DayofMonth使用5W，如果5日是星期六，则将在最近的工作日：星期五，即4日触发。如果5日是星期天，则在6日(周一)触发；如果5日在星期一到星期五中的一天，则就在5日触发。另外一点，W的最近寻找不会跨过月份 。
 * LW:这两个字符可以连用，表示在某个月最后一个工作日，即最后一个星期五。
 * #:用于确定每个月第几个星期几，只能出现在DayofMonth域。例如在4#2，表示某月的第二个星期三。
 * <p>
 * 经典案例：
 * <p>
 * "30 * * * * ?" 每半分钟触发任务
 * "1 0 0 *  *  ?"每天0时0分0秒执行任务
 * "30 10 * * * ?" 每小时的10分30秒触发任务
 * "30 10 1 * * ?" 每天1点10分30秒触发任务
 * "30 10 1 20 * ?" 每月20号1点10分30秒触发任务
 * "30 10 1 20 10 ? *" 每年10月20号1点10分30秒触发任务
 * "30 10 1 20 10 ? 2011" 2011年10月20号1点10分30秒触发任务
 * "30 10 1 ? 10 * 2011" 2011年10月每天1点10分30秒触发任务
 * "30 10 1 ? 10 SUN 2011" 2011年10月每周日1点10分30秒触发任务
 * "15,30,45 * * * * ?" 每15秒，30秒，45秒时触发任务
 * "15-45 * * * * ?" 15到45秒内，每秒都触发任务
 * "15/5 * * * * ?" 每分钟的每15秒开始触发，每隔5秒触发一次
 * "15-30/5 * * * * ?" 每分钟的15秒到30秒之间开始触发，每隔5秒触发一次
 * "0 0/3 * * * ?" 每小时的第0分0秒开始，每三分钟触发一次
 * "0 15 10 ? * MON-FRI" 星期一到星期五的10点15分0秒触发任务
 * "0 15 10 L * ?" 每个月最后一天的10点15分0秒触发任务
 * "0 15 10 LW * ?" 每个月最后一个工作日的10点15分0秒触发任务
 * "0 15 10 ? * 5L" 每个月最后一个星期四的10点15分0秒触发任务
 * "0 15 10 ? * 5#3" 每个月第三周的星期四的10点15分0秒触发任务
 *
 * @author penghuiping
 * @date 2020/5/15 23:00
 */
public class Cron {

    final static LruCache<String, AST> lruCache = new LruCacheImpl<>(256);

    /**
     * 解析cron表达式为token流
     *
     * @param cron cron表达式
     * @return token流
     */
    static List<Token> lexer(String cron) {
        return Lexer.parse(cron);
    }

    /**
     * 构建AST树
     *
     * @param tokens token流
     * @return AST树
     */
    static AST ast(List<Token> tokens) {
        Tokens tokens1 = new Tokens(tokens);
        return AstBuild.getCronExpr(tokens1);
    }

    /**
     * 执行AST树
     *
     * @param ast AST树
     */
    static LocalDateTime execute(AST ast, LocalDateTime baseTime) {
        AstExec astExec = new AstExec();
        return astExec.execCronExpr(ast, baseTime);
    }

    /**
     * 把cron表达式翻译成下次需要执行的时间点
     *
     * @param cron cron表达式
     * @param cron baseTime 基时间，下次执行的时间点需要在基时间之后
     * @return
     */
    public static LocalDateTime nextExecuteTime(String cron, LocalDateTime baseTime) {
        AST ast = lruCache.getValue(cron);
        if (null == ast) {
            ast = ast(lexer(cron));
            lruCache.putValueIfAbsent(cron,ast);
        }
        return execute(ast, baseTime);
    }


}