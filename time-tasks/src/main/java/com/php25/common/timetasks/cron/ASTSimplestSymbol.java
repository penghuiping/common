package com.php25.common.timetasks.cron;

import com.php25.common.timetasks.exception.CronException;

/**
 * 1. 数字
 * 2. 英文字符
 * 3. 问好
 * 4. 星号
 *
 * @author penghuiping
 * @date 2020/5/18 14:58
 */
class ASTSimplestSymbol extends AST {
    private Token token;

    public ASTSimplestSymbol(Token token) {
        if (token.getType().equals(TokenType.digit)
                || token.getType().equals(TokenType.letter)
                || token.getType().equals(TokenType.question_mark)
                || token.getType().equals(TokenType.asterisk)) {
            this.token = token;
            return;
        }
        throw new CronException("token类型不合法,请使用数字、英文字符、问好、星号");
    }

    public Token getToken() {
        return token;
    }
}
