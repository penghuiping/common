package com.php25.common.core.mess.ratelimiter;

/**
 * @author penghuiping
 * @date 2022/7/19 22:26
 */
class Slot {
    private long startTime;
    private long count;
    private long page;

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public long getPage() {
        return page;
    }

    public void setPage(long page) {
        this.page = page;
    }
}
