package com.php25.common.redis;

import com.google.common.base.Charsets;
import com.google.common.hash.Funnels;
import com.google.common.hash.Hashing;
import com.google.common.primitives.Longs;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * redis版本 布隆过滤器
 *
 * @author penghuiping
 * @date 2020/1/8 13:46
 */
public class RBloomFilterImpl implements RBloomFilter{
    /**
     * 对应于redis中的key
     */
    private String name;

    private StringRedisTemplate redisTemplate;

    /**
     * bit数组长度
     */
    private long numBits;
    /**
     * hash函数数量
     */
    private int numHashFunctions;


    /**
     * @param name               对应redis中的key值
     * @param redisTemplate
     * @param expectedInsertions 预计插入量
     * @param fpp                可接受的错误率
     */
    public RBloomFilterImpl(StringRedisTemplate redisTemplate, String name, long expectedInsertions, double fpp) {
        this.redisTemplate = redisTemplate;
        this.name = name;
        this.numBits = optimalNumOfBits(expectedInsertions, fpp);
        this.numHashFunctions = optimalNumOfHashFunctions(expectedInsertions, numBits);
    }


    /**
     * 判断keys是否存在于集合
     */
    @Override
    public boolean mightContain(String key) {
        long[] indexes = getIndexes(key);
        List<Object> result = redisTemplate.executePipelined(new RedisCallback<Object>() {

            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                byte[] key = RBloomFilterImpl.this.name.getBytes(Charsets.UTF_8);
                List<Object> list = new ArrayList<>();
                for (long index : indexes) {
                    Boolean result = connection.getBit(key, index);
                    list.add(result);
                }
                return null;
            }
        });
        return !result.contains(false);
    }

    /**
     * 将key存入redis bitmap
     */
    @Override
    public void put(String key) {
        long[] indexes = getIndexes(key);
        redisTemplate.executePipelined(new RedisCallback<Object>() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                byte[] key = RBloomFilterImpl.this.name.getBytes(Charsets.UTF_8);
                List<Object> list = new ArrayList<>();
                for (long index : indexes) {
                    Boolean result = connection.setBit(key, index, true);
                    list.add(result);
                }
                return null;
            }
        });
    }

    /**
     * 根据key获取bitmap下标
     */
    private long[] getIndexes(String key) {
        long[] result = new long[numHashFunctions];
        byte[] bytes = hash(key);

        long hash1 = lowerEight(bytes);
        long hash2 = upperEight(bytes);

        long combinedHash = hash1;
        for (int i = 0; i < numHashFunctions; i++) {
            result[i] = (combinedHash & Long.MAX_VALUE) % numBits;
            combinedHash += hash2;
        }
        return result;
    }

    /**
     * 计算hash函数个数
     */
    private int optimalNumOfHashFunctions(long n, long m) {
        return Math.max(1, (int) Math.round((double) m / n * Math.log(2)));
    }

    /**
     * 计算bit数组长度
     */
    private long optimalNumOfBits(long n, double p) {
        if (p == 0) {
            p = Double.MIN_VALUE;
        }
        return (long) (-n * Math.log(p) / (Math.log(2) * Math.log(2)));
    }


    private byte[] hash(String key) {
        return Hashing.murmur3_128().hashObject(key, Funnels.stringFunnel(Charsets.UTF_8)).asBytes();
    }

    private long lowerEight(byte[] bytes) {
        return Longs.fromBytes(
                bytes[7], bytes[6], bytes[5], bytes[4], bytes[3], bytes[2], bytes[1], bytes[0]);
    }

    private long upperEight(byte[] bytes) {
        return Longs.fromBytes(
                bytes[15], bytes[14], bytes[13], bytes[12], bytes[11], bytes[10], bytes[9], bytes[8]);
    }
}
