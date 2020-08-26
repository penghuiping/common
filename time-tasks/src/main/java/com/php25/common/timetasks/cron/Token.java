package com.php25.common.timetasks.cron;

/**
 * @author penghuiping
 * @date 2020/5/15 23:19
 */
class Token {
    private String value;

    private TokenType type;

    private int size;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public TokenType getType() {
        return type;
    }

    public void setType(TokenType type) {
        this.type = type;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    @Override
    public String toString() {
        return value;
    }
}
