package com.php25.timetasks.cron;

import java.util.ArrayList;
import java.util.List;

/**
 * 构建AST
 * Grammar
 * <p>
 * CronExpr := (LastExpr|ThreeExpr)+
 * ThreeExpr :=   SimplestSymbol (LinkSymbol SimplestSymbol)*
 * LastExpr := (数字)(英文字符L)
 * SimplestSymbol :=  数字 | 英文字符 | 问好 | 星号
 * LinkSymbol := '-' | '/' | '#'
 * <p>
 *
 * @author penghuiping
 * @date 2020/6/2 11:07
 */
public class AstBuild {

    public static AST getLinkSymbol(Tokens tokens) {
        Token token = tokens.getCurrent();
        if (Tokens.isLinkSymbol(token)) {
            tokens.eat();
            return new ASTLinkSymbol(token);
        }
        return null;
    }


    public static AST getSimplestSymbol(Tokens tokens) {
        Token token = tokens.getCurrent();
        if (Tokens.isSimplestSymbol(token)) {
            tokens.eat();
            return new ASTSimplestSymbol(token);
        }
        return null;
    }

    public static AST getThreeExpr(Tokens tokens) {
        Token token = tokens.getCurrent();
        AST result = null;
        if (Tokens.isSimplestSymbol(token)) {
            result = getSimplestSymbol(tokens);
            while (true) {
                Token token1 = tokens.getCurrent();
                Token token2 = tokens.getNext();
                if (Tokens.isLinkSymbol(token1) && Tokens.isSimplestSymbol(token2)) {
                    AST linkSymbol = getLinkSymbol(tokens);
                    AST simplestSymbol = getSimplestSymbol(tokens);
                    result = new ASTThreeExpr(result, linkSymbol, simplestSymbol);
                } else {
                    break;
                }
            }
            if (result instanceof ASTSimplestSymbol) {
                result = new ASTThreeExpr(result, null, null);
            }
        }
        return result;
    }

    public static AST getLastExpr(Tokens tokens) {
        Token token = tokens.getCurrent();
        Token token1 = tokens.getNext();
        if (token != null && token1 != null && Tokens.isDigit(token) && "L".equals(token1.getValue())) {
            tokens.eat();
            tokens.eat();
            return new ASTLastExpr(token, token1);
        } else if (token != null && token1 != null && "L".equals(token.getValue()) && "W".equals(token1.getValue())) {
            tokens.eat();
            tokens.eat();
            return new ASTLastExpr(token, token1);
        } else if (token != null && token1 != null && "L".equals(token1.getValue()) && "W".equals(token.getValue())) {
            tokens.eat();
            tokens.eat();
            return new ASTLastExpr(token, token1);
        } else if (token != null && "L".equals(token.getValue())) {
            tokens.eat();
            return new ASTLastExpr(token, null);
        }
        return null;
    }

    public static AST getCronExpr(Tokens tokens) {
        List<AST> nodes = new ArrayList<>();
        while (true) {
            AST node = getLastExpr(tokens);
            if (node == null) {
                node = getThreeExpr(tokens);
                if (node == null) {
                    break;
                } else {
                    nodes.add(node);
                }
            } else {
                nodes.add(node);
            }
            Token token = tokens.getCurrent();
            if (Tokens.isSpace(token)) {
                tokens.eat();
            }
        }
        return new ASTCronExpr(nodes);
    }
}
