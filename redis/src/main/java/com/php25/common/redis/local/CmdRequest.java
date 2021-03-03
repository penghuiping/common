package com.php25.common.redis.local;

import java.util.List;

/**
 * @author penghuiping
 * @date 2021/3/2 21:11
 */
class CmdRequest {
    private String cmd;

    private List<Object> params;


    public CmdRequest(String cmd, List<Object> params) {
        this.cmd = cmd;
        this.params = params;
    }

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public List<Object> getParams() {
        return params;
    }

    public void setParams(List<Object> params) {
        this.params = params;
    }
}
