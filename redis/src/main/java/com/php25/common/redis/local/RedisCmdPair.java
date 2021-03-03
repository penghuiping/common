package com.php25.common.redis.local;

/**
 * @author penghuiping
 * @date 2021/3/2 22:07
 */
class RedisCmdPair {

    private CmdRequest request;

    private CmdResponse response;

    public RedisCmdPair(CmdRequest request, CmdResponse response) {
        this.request = request;
        this.response = response;
    }

    public CmdRequest getRequest() {
        return request;
    }

    public void setRequest(CmdRequest request) {
        this.request = request;
    }

    public CmdResponse getResponse() {
        return response;
    }

    public void setResponse(CmdResponse response) {
        this.response = response;
    }
}
