package com.php25.timetasks.cron;

import java.util.List;


/**
 * @author penghuiping
 * @date 2020/5/18 16:54
 */
class Tokens {

    private List<Token> tokens;

    private int index;

    public Tokens(List<Token> tokens) {
        this.tokens = tokens;
    }

    public Token getCurrent() {
        if (index < tokens.size()) {
            return tokens.get(index);
        } else {
            return null;
        }
    }

    public Token getNext() {
        if (index + 1 < tokens.size()) {
            return tokens.get(index + 1);
        } else {
            return null;
        }
    }

    public boolean eat() {
        if (index < tokens.size()) {
            index++;
            return true;
        } else {
            return false;
        }
    }

    public static boolean isSimplestSymbol(Token token) {
        if (null == token) {
            return false;
        }

        if (token.getType().equals(TokenType.digit)
                || token.getType().equals(TokenType.letter)
                || token.getType().equals(TokenType.question_mark)
                || token.getType().equals(TokenType.asterisk)) {
            return true;
        }
        return false;
    }

    public static boolean isLinkSymbol(Token token) {
        if (null == token) {
            return false;
        }

        if (token.getType().equals(TokenType.horizontal_bar)
                || token.getType().equals(TokenType.slanting_bar)
                || token.getType().equals(TokenType.comma)
                || token.getType().equals(TokenType.well_number)) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isDigit(Token token) {
        if (null == token) {
            return false;
        }

        if (token.getType().equals(TokenType.digit)) {
            return true;
        }
        return false;
    }

    public static boolean isQuestionMark(Token token) {
        if (null == token) {
            return false;
        }

        if (token.getType().equals(TokenType.question_mark)) {
            return true;
        }
        return false;
    }

    public static boolean isAsterisk(Token token) {
        if (null == token) {
            return false;
        }

        if (token.getType().equals(TokenType.asterisk)) {
            return true;
        }
        return false;
    }

    public static boolean isLetter(Token token) {
        if (null == token) {
            return false;
        }

        if (token.getType().equals(TokenType.letter)) {
            return true;
        }
        return false;
    }

    public static boolean isSpace(Token token) {
        if (null == token) {
            return false;
        }

        if (token.getType().equals(TokenType.space)) {
            return true;
        }
        return false;
    }

    public static boolean isL(Token token) {
        if (null == token) {
            return false;
        }

        if (token.getType().equals(TokenType.L)) {
            return true;
        }
        return false;
    }

    public static boolean isW(Token token) {
        if (null == token) {
            return false;
        }

        if (token.getType().equals(TokenType.W)) {
            return true;
        }
        return false;
    }
}
