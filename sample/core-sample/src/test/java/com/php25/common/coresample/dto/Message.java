package com.php25.common.coresample.dto;

/**
 * @Auther: penghuiping
 * @Date: 2018/8/16 15:13
 * @Description:
 */
public class Message {

    int type;

    String content;

    String result;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
