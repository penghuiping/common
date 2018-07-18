package com.php25.common.dto;

/**
 * @author zengzh
 * @date create at 2018/5/16 15:06
 */
public class RedisLockInfo {

    /**
     * 锁ID UUID
     */
    private String lockId;

    /**
     * REDIS KEY
     */
    private String redisKey;

    /**
     * 过期时间
     */
    private Long expire;

    /**
     * 尝试获取锁超时时间
     */
    private Long tryTimeout;

    /**
     * 尝试获取锁次数
     */
    private int tryCount;

    public RedisLockInfo(String lockId, String redisKey, Long expire, Long tryTimeout, int tryCount) {
        this.lockId = lockId;
        this.redisKey = redisKey;
        this.expire = expire;
        this.tryTimeout = tryTimeout;
        this.tryCount = tryCount;
    }

    public String getLockId() {
        return lockId;
    }

    public void setLockId(String lockId) {
        this.lockId = lockId;
    }

    public String getRedisKey() {
        return redisKey;
    }

    public void setRedisKey(String redisKey) {
        this.redisKey = redisKey;
    }

    public Long getExpire() {
        return expire;
    }

    public void setExpire(Long expire) {
        this.expire = expire;
    }

    public Long getTryTimeout() {
        return tryTimeout;
    }

    public void setTryTimeout(Long tryTimeout) {
        this.tryTimeout = tryTimeout;
    }

    public int getTryCount() {
        return tryCount;
    }

    public void setTryCount(int tryCount) {
        this.tryCount = tryCount;
    }

    @Override
    public String toString() {
        return "RedisLockInfo{" +
                "lockId='" + lockId + '\'' +
                ", redisKey='" + redisKey + '\'' +
                ", expire=" + expire +
                ", tryTimeout=" + tryTimeout +
                ", tryCount=" + tryCount +
                '}';
    }
}
