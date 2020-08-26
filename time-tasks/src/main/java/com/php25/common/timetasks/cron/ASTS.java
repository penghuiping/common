package com.php25.common.timetasks.cron;

import com.php25.common.timetasks.exception.CronException;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

/**
 * 构建AST的工具类
 *
 * @author penghuiping
 * @date 2020/5/18 15:09
 */
class ASTS {
    public static void printAST(AST ast) {
        printAST0(ast, 0);
    }

    private static void printAST0(AST ast, int deep) {
        for (int i = 0; i < deep; i++) {
            System.out.print("\t");
        }
        if (ast == null) {
            System.out.println();
            return;
        }

        if (ast instanceof ASTCronExpr) {
            ASTCronExpr expr = (ASTCronExpr) ast;
            System.out.println("ASTCronExpr");
            List<AST> nodes = expr.getNodes();
            for (int i = 0; i < nodes.size(); i++) {
                AST node = nodes.get(i);
                printAST0(node, deep + 1);
            }
        } else if (ast instanceof ASTLastExpr) {
            ASTLastExpr expr = (ASTLastExpr) ast;
            System.out.print(expr.getLeft());
            System.out.print(expr.getRight());
            System.out.println();
        } else if (ast instanceof ASTLinkSymbol) {
            ASTLinkSymbol expr = (ASTLinkSymbol) ast;
            System.out.print(expr.getValue());
            System.out.println();
        } else if (ast instanceof ASTSimplestSymbol) {
            ASTSimplestSymbol expr = (ASTSimplestSymbol) ast;
            System.out.print(expr.getToken().getValue());
            System.out.println();
        } else if (ast instanceof ASTThreeExpr) {
            ASTThreeExpr expr = (ASTThreeExpr) ast;
            System.out.println("ASTThreeExpr");
            printAST0(expr.getLeft(), deep + 1);
            printAST0(expr.getLinkSymbol(), deep + 1);
            printAST0(expr.getRight(), deep + 1);
        } else {
            throw new CronException("不可能发生");
        }
    }

    public static void printAllPossibleTimeStream(AST ast) {
        AstExec astExec = new AstExec();
        IntStream[] timeStream = astExec.getAllPossibleTimeStream(ast);
        System.out.println("year:" + Arrays.toString(timeStream[6].toArray()));
        System.out.println("weeks:" + Arrays.toString(timeStream[5].toArray()));
        System.out.println("months:" + Arrays.toString(timeStream[4].toArray()));
        System.out.println("days:" + Arrays.toString(timeStream[3].toArray()));
        System.out.println("hours:" + Arrays.toString(timeStream[2].toArray()));
        System.out.println("minutes:" + Arrays.toString(timeStream[1].toArray()));
        System.out.println("seconds:" + Arrays.toString(timeStream[0].toArray()));
        System.out.println("weekOfMonth:" + astExec.getWeekOfMonth());
        System.out.println("workdayScope:" + astExec.isWorkday());
        System.out.println("use days:" + astExec.isWeekOrDay());
        System.out.println();
    }
}
