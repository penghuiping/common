package com.php25.timetasks.cron;

/**
 * LastExpr := (数字)?(英文字符L)W?
 *
 * @author penghuiping
 * @date 2020/5/18 23:07
 */
class ASTLastExpr extends AST {

    private Token left;
    private Token right;

    public ASTLastExpr(Token left, Token right) {
        this.left = left;
        this.right = right;
    }

    public Token getLeft() {
        return left;
    }

    public Token getRight() {
        return right;
    }
}
