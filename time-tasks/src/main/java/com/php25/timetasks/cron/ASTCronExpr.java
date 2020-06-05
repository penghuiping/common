package com.php25.timetasks.cron;

import java.util.List;

/**
 * CronExpr := (LastExpr|ThreeExpr)+
 *
 * @author penghuiping
 * @date 2020/5/18 16:21
 */
class ASTCronExpr extends AST{
    private List<AST> nodes;

    public ASTCronExpr(List<AST> nodes) {
        this.nodes = nodes;
    }

    public List<AST> getNodes() {
        return nodes;
    }
}
