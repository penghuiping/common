package com.php25.timetasks.cron;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * @author penghuiping
 * @date 2020/5/18 14:03
 */
public class CronTest {
    private Logger log = LoggerFactory.getLogger(CronTest.class);

    @Test
    public void cronParser() {
        String cron1 = "30 * * * * ?";
        List<Token> tokens1 = Cron.lexer(cron1);
        log.info("{}", tokens1);

        String cron2 = "0 15 10 ? * 5#3";
        List<Token> tokens2 = Cron.lexer(cron2);
        log.info("{}", tokens2);
    }

    @Test
    public void cronAST() {
        String cron1 = "30 10 1 ? 10 SUN 2011";
        List<Token> tokens1 = Cron.lexer(cron1);
        AST ast1 = Cron.ast(tokens1);
        ASTS.printAST(ast1);
        System.out.println();

        String cron2 = "0 15 10 ? * 5#3";
        List<Token> tokens2 = Cron.lexer(cron2);
        AST ast2 = Cron.ast(tokens2);
        ASTS.printAST(ast2);
        System.out.println();

        String cron3 = "15,30,45 * * * * ?";
        List<Token> tokens3 = Cron.lexer(cron3);
        AST ast3 = Cron.ast(tokens3);
        ASTS.printAST(ast3);
        System.out.println();


        String cron4 = "15-30/5 * * * * ?";
        List<Token> tokens4 = Cron.lexer(cron4);
        AST ast4 = Cron.ast(tokens4);
        ASTS.printAST(ast4);
        System.out.println();

        String cron5 = "0 15 10 LW * ?";
        List<Token> tokens5 = Cron.lexer(cron5);
        AST ast5 = Cron.ast(tokens5);
        ASTS.printAST(ast5);
        System.out.println();

        String cron6 = "0 15 10 ? * 5L";
        List<Token> tokens6 = Cron.lexer(cron6);
        AST ast6 = Cron.ast(tokens6);
        ASTS.printAST(ast6);
        System.out.println();

        String cron7 = "15/5 * * * * ?";
        List<Token> tokens7 = Cron.lexer(cron7);
        AST ast7 = Cron.ast(tokens7);
        ASTS.printAST(ast7);
        System.out.println();

        String cron8 = "0 15 10 ? * MON-FRI";
        List<Token> tokens8 = Cron.lexer(cron8);
        AST ast8 = Cron.ast(tokens8);
        ASTS.printAST(ast8);

    }

    @Test
    public void cronToTimeStream() {
        String cron0 = "0 15 10 L * ?";
        ASTS.printAllPossibleTimeStream(Cron.ast(Cron.lexer(cron0)));
        String cron1 = "0 15 10 ? * L";
        ASTS.printAllPossibleTimeStream(Cron.ast(Cron.lexer(cron1)));
        String cron2 = "0 15 10 ? * 5L";
        ASTS.printAllPossibleTimeStream(Cron.ast(Cron.lexer(cron2)));
        String cron3 = "15,30,45 * * * * ?";
        ASTS.printAllPossibleTimeStream(Cron.ast(Cron.lexer(cron3)));
        String cron4 = "15-30/5 * * * * ?";
        ASTS.printAllPossibleTimeStream(Cron.ast(Cron.lexer(cron4)));
        String cron5 = "30 10 1 ? 10 SUN 2030";
        ASTS.printAllPossibleTimeStream(Cron.ast(Cron.lexer(cron5)));
        String cron6 = "0 15 10 ? * 5#3";
        ASTS.printAllPossibleTimeStream(Cron.ast(Cron.lexer(cron6)));
        String cron7 = "0 15 10 ? * MON-FRI";
        ASTS.printAllPossibleTimeStream(Cron.ast(Cron.lexer(cron7)));
        String cron8 = "15/5 * * * * ?";
        ASTS.printAllPossibleTimeStream(Cron.ast(Cron.lexer(cron8)));
        String cron9 = "0 15 10 LW * ?";
        ASTS.printAllPossibleTimeStream(Cron.ast(Cron.lexer(cron9)));
    }

    @Test
    public void cronNextExecTime() {
        LocalDateTime now = LocalDateTime.now();
        String cron0 = "0 15 10 LW 10 ?";
        LocalDateTime time0 = Cron.nextExecuteTime(cron0, now);
        log.info("{}", time0.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        String cron2 = "0 * * 20 * ?";
        LocalDateTime time22 = Cron.nextExecuteTime(cron2, now);
        log.info("{}", time22.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        String cron3 = "15,30,45 * * * * ?";
        LocalDateTime time = Cron.nextExecuteTime(cron3, now);
        log.info("{}", time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        String cron4 = "15-30/5 * * * * ?";
        LocalDateTime time1 = Cron.nextExecuteTime(cron4, now);
        log.info("{}", time1.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        String cron5 = "30 10 1 ? 10 FRI-SAT 2021";
        LocalDateTime time2 = Cron.nextExecuteTime(cron5, now);
        log.info("{}", time2.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        String cron6 = "0 15 10 ? * 5#3";
        LocalDateTime time3 = Cron.nextExecuteTime(cron6, now);
        log.info("{}", time3.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        String cron7 = "0 15 10 ? * MON-FRI";
        LocalDateTime time4 = Cron.nextExecuteTime(cron7, now);
        log.info("{}", time4.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        String cron8 = "0 15 10 ? * 5L";
        LocalDateTime time5 = Cron.nextExecuteTime(cron8, now);
        log.info("{}", time5.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        String cron9 = "0 0 1 * * ?";
        LocalDateTime time6 = Cron.nextExecuteTime(cron9, now);
        log.info("{}", time6.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }


    @Test
    public void cronNextExecTime2() {
        LocalDateTime now = null;
        String cron0 = null;
        LocalDateTime time0 = null;

        //每天上午10点，下午2点，4点
        now = LocalDateTime.now();
        cron0 = "0 0 10,14,16 * * ?";
        time0 = Cron.nextExecuteTime(cron0, now);
        log.info("{}", time0.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        //朝九晚五工作时间内每半小时
        now = LocalDateTime.now();
        cron0 = "0 0/30 9-17 * * ?";
        time0 = Cron.nextExecuteTime(cron0, now);
        log.info("{}", time0.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        //表示每个星期三中午12点
        now = LocalDateTime.now();
        cron0 = "0 0 12 ? * WED";
        time0 = Cron.nextExecuteTime(cron0, now);
        log.info("{}", time0.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        //每天中午12点触发
        now = LocalDateTime.now();
        cron0 = "0 0 12 * * ?";
        time0 = Cron.nextExecuteTime(cron0, now);
        log.info("{}", time0.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        //每天上午10:15触发
        now = LocalDateTime.now();
        cron0 = "0 15 10 ? * *";
        time0 = Cron.nextExecuteTime(cron0, now);
        log.info("{}", time0.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        //每天上午10:15触发
        now = LocalDateTime.now();
        cron0 = "0 15 10 * * ?";
        time0 = Cron.nextExecuteTime(cron0, now);
        log.info("{}", time0.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        //每天上午10:15触发
        now = LocalDateTime.now();
        cron0 = "0 15 10 * * ? *";
        time0 = Cron.nextExecuteTime(cron0, now);
        log.info("{}", time0.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        //2025年的每天上午10:15触发
        now = LocalDateTime.now();
        cron0 = "0 15 10 * * ? 2025";
        time0 = Cron.nextExecuteTime(cron0, now);
        log.info("{}", time0.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        //在每天下午2点到下午2:59期间的每1分钟触发
        now = LocalDateTime.now();
        cron0 = "0 * 14 * * ?";
        time0 = Cron.nextExecuteTime(cron0, now);
        log.info("{}", time0.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        //在每天下午2点到下午2:55期间的每5分钟触发
        now = LocalDateTime.now();
        cron0 = "0 0/5 14 * * ?";
        time0 = Cron.nextExecuteTime(cron0, now);
        log.info("{}", time0.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        //在每天下午2点到2:55期间和下午6点到6:55期间的每5分钟触发
        now = LocalDateTime.now();
        cron0 = "0 0/5 14,18 * * ?";
        time0 = Cron.nextExecuteTime(cron0, now);
        log.info("{}", time0.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        //在每天下午2点到下午2:05期间的每1分钟触发
        now = LocalDateTime.now();
        cron0 = "0 0-5 14 * * ?";
        time0 = Cron.nextExecuteTime(cron0, now);
        log.info("{}", time0.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        //每年三月的星期三的下午2:10和2:44触发
        now = LocalDateTime.now();
        cron0 = "0 10,44 14 ? 3 WED";
        time0 = Cron.nextExecuteTime(cron0, now);
        log.info("{}", time0.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        //周一至周五的上午10:15触发
        now = LocalDateTime.now();
        cron0 = "0 15 10 ? * MON-FRI";
        time0 = Cron.nextExecuteTime(cron0, now);
        log.info("{}", time0.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        //每周二、四、六下午五点
        now = LocalDateTime.now();
        cron0 = "0 0 17 ? * TUE,THU,SAT";
        time0 = Cron.nextExecuteTime(cron0, now);
        log.info("{}", time0.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        //每月15日上午10:15触发
        now = LocalDateTime.now();
        cron0 = "0 15 10 15 * ?";
        time0 = Cron.nextExecuteTime(cron0, now);
        log.info("{}", time0.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        //每月最后一日的上午10:15触发
        now = LocalDateTime.now();
        cron0 = "0 15 10 L * ?";
        time0 = Cron.nextExecuteTime(cron0, now);
        log.info("{}", time0.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        //每月的最后一个星期五上午10:15触发
        now = LocalDateTime.now();
        cron0 = "0 15 10 ? * 6L";
        time0 = Cron.nextExecuteTime(cron0, now);
        log.info("{}", time0.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        //2022年至2025年的每月的最后一个星期五上午10:15触发
        now = LocalDateTime.now();
        cron0 = "0 15 10 ? * 6L 2021-2025";
        time0 = Cron.nextExecuteTime(cron0, now);
        log.info("{}", time0.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        //每月的第三个星期五上午10:15触发
        now = LocalDateTime.now();
        cron0 = "0 15 10 ? * 6#3";
        time0 = Cron.nextExecuteTime(cron0, now);
        log.info("{}", time0.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }

    @Test
    public void cronNextExecTime3() {
        LocalDateTime now = null;
        String cron0 = null;
        LocalDateTime time0 = null;

        //每秒执行一次
        now = LocalDateTime.now();
        cron0 = "0/1 * * * * ? 2020";
        time0 = Cron.nextExecuteTime(cron0, now.plusSeconds(5));
        log.info("{}", time0.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));


    }
}
