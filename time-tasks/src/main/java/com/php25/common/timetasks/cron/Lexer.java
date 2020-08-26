package com.php25.common.timetasks.cron;

import com.php25.common.timetasks.exception.CronException;

import java.util.ArrayList;
import java.util.List;

/**
 * @author penghuiping
 * @date 2020/5/18 22:40
 */
class Lexer {

    public static List<Token> parse(String cron) {
        List<Token> tokens = new ArrayList<>();
        char[] arr = cron.toCharArray();
        for (int i = 0; i < arr.length; ) {
            char c = arr[i];
            if (Character.isDigit(c)) {
                StringBuilder sb = new StringBuilder();
                getDigit(sb, cron, i);
                Token token = new Token();
                token.setValue(sb.toString());
                token.setType(TokenType.digit);
                token.setSize(token.getValue().toCharArray().length);
                tokens.add(token);
                i = i + token.getSize();
                continue;
            }

            if (Character.isLetter(c)) {
                if (c == 'L') {
                    Token token = new Token();
                    token.setValue(c + "");
                    token.setType(TokenType.L);
                    token.setSize(1);
                    tokens.add(token);
                    i = i + 1;
                    continue;
                }

                if (c == 'W') {
                    //是否是单词WED
                    if(arr[i+1]!='E') {
                        Token token = new Token();
                        token.setValue(c + "");
                        token.setType(TokenType.W);
                        token.setSize(1);
                        tokens.add(token);
                        i = i + 1;
                        continue;
                    }
                }

                StringBuilder sb = new StringBuilder();
                getLetter(sb, cron, i);
                Token token = new Token();
                token.setValue(sb.toString());
                token.setType(TokenType.letter);
                token.setSize(token.getValue().toCharArray().length);
                tokens.add(token);
                i = i + token.getSize();
                continue;
            }

            if (c == ',') {
                Token token = new Token();
                token.setValue(c + "");
                token.setType(TokenType.comma);
                token.setSize(1);
                tokens.add(token);
                i = i + 1;
                continue;
            }


            if (c == '-') {
                Token token = new Token();
                token.setValue(c + "");
                token.setType(TokenType.horizontal_bar);
                token.setSize(1);
                tokens.add(token);
                i = i + 1;
                continue;
            }

            if (c == '*') {
                Token token = new Token();
                token.setValue(c + "");
                token.setType(TokenType.asterisk);
                token.setSize(1);
                tokens.add(token);
                i = i + 1;
                continue;
            }

            if (c == '?') {
                Token token = new Token();
                token.setValue(c + "");
                token.setType(TokenType.question_mark);
                token.setSize(1);
                tokens.add(token);
                i = i + 1;
                continue;
            }

            if (c == '/') {
                Token token = new Token();
                token.setValue(c + "");
                token.setType(TokenType.slanting_bar);
                token.setSize(1);
                tokens.add(token);
                i = i + 1;
                continue;
            }

            if (c == '#') {
                Token token = new Token();
                token.setValue(c + "");
                token.setType(TokenType.well_number);
                token.setSize(1);
                tokens.add(token);
                i = i + 1;
                continue;
            }

            if (c == ' ') {
                StringBuilder sb = new StringBuilder();
                getSpace(sb, cron, i);
                Token token = new Token();
                token.setValue(sb.toString());
                token.setType(TokenType.space);
                token.setSize(token.getValue().toCharArray().length);
                tokens.add(token);
                i = i + token.getSize();
                continue;
            }
            throw new CronException("cron表达式不正确,错误位置在第" + i + "字符");
        }

        return tokens;
    }

    private static void getDigit(StringBuilder token, String text, int column) {
        if (column < text.length()) {
            char v = text.charAt(column);
            if (Character.isDigit(v)) {
                token.append(v);
                getDigit(token, text, ++column);
            }
        }
    }

    private static void getLetter(StringBuilder token, String text, int column) {
        if (column < text.length()) {
            char v = text.charAt(column);
            if (Character.isLetter(v)) {
                token.append(v);
                getLetter(token, text, ++column);
            }
        }
    }

    private static void getSpace(StringBuilder token, String text, int column) {
        if (column < text.length()) {
            char v = text.charAt(column);
            if (v == ' ') {
                token.append(v);
                getSpace(token, text, ++column);
            }
        }
    }
}
