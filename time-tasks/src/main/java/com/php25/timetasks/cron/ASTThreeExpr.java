package com.php25.timetasks.cron;

/**
 * 三位表达式: a x b
 *
 * @author penghuiping
 * @date 2020/5/18 14:50
 */
class ASTThreeExpr extends AST {
    private AST left;

    private AST linkSymbol;

    private AST right;

    public ASTThreeExpr(AST left, AST linkSymbol, AST right) {
        this.left = left;
        this.linkSymbol = linkSymbol;
        this.right = right;
    }

    public AST getLeft() {
        return left;
    }

    public AST getLinkSymbol() {
        return linkSymbol;
    }

    public AST getRight() {
        return right;
    }
}
