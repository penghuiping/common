package com.php25.timetasks.cron;

import org.assertj.core.api.Assertions;
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

        String cron8 = " 0 15 10 ? * MON-FRI";
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
        String cron5 = "30 10 1 ? 10 SUN 2011";
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
        Assertions.assertThat(time0).isEqualTo(LocalDateTime.of(2020,10,30,10,15,0));

        String cron2 = "0 * * 11,12,20 * ?";
        LocalDateTime time22= Cron.nextExecuteTime(cron2, now);
        log.info("{}", time22.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        Assertions.assertThat(time22).isEqualTo(LocalDateTime.of(2020,7,11,0,0,0));

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
    public void cronErrorTest() {
        LocalDateTime now = LocalDateTime.now();
        //{秒数} {分钟} {小时} {日期} {月份} {星期} {年份(可为空)}
        String cron0 = "0 15-30/2 10 30 13 ?";
        LocalDateTime time0 = Cron.nextExecuteTime(cron0, now);
        log.info("{}", time0.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }




}
