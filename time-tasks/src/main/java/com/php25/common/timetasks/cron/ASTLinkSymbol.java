package com.php25.common.timetasks.cron;

import com.php25.common.timetasks.exception.CronException;

/**
 * 连接符号 - / # ,
 *
 * @author penghuiping
 * @date 2020/5/18 14:51
 */
class ASTLinkSymbol extends AST {
    private Token value;

    public ASTLinkSymbol(Token value) {
        if (value.getType().equals(TokenType.horizontal_bar)
                || value.getType().equals(TokenType.slanting_bar)
                || value.getType().equals(TokenType.comma)
                || value.getType().equals(TokenType.well_number)) {
            this.value = value;
            return;
        }

        throw new CronException("token类型不合法,请使用-或/或#");
    }

    public Token getValue() {
        return value;
    }
}
