package com.php25.common.redis;

import com.google.common.collect.Lists;
import io.lettuce.core.RedisAsyncCommandsImpl;
import io.lettuce.core.api.async.RedisAsyncCommands;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.integration.support.locks.ExpirableLockRegistry;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Implementation of {@link ExpirableLockRegistry} providing a distributed lock using Redis.
 * Locks are stored under the key {@code registryKey:lockKey}. Locks expire after
 * (default 60) seconds. Threads unlocking an
 * expired lock will get an {@link IllegalStateException}. This should be
 * considered as a critical error because it is possible the protected
 * resources were compromised.
 * <p>
 * Locks are reentrant.
 * <p>
 * <b>However, locks are scoped by the registry; a lock from a different registry with the
 * same key (even if the registry uses the same 'registryKey') are different
 * locks, and the second cannot be acquired by the same thread while the first is
 * locked.</b>
 * <p>
 * <b>Note: This is not intended for low latency applications.</b> It is intended
 * for resource locking across multiple JVMs.
 * <p>
 * {@link Condition}s are not supported.
 *
 * @author Gary Russell
 * @author Konstantin Yakimov
 * @author Artem Bilan
 * @author Vedran Pavic
 * @since 4.0
 */
public final class RRedisLockRegistry implements ExpirableLockRegistry, DisposableBean {

    private static final Log logger = LogFactory.getLog(RRedisLockRegistry.class);

    private static final long DEFAULT_EXPIRE_AFTER = 60000L;

    private static final String OBTAIN_LOCK_SCRIPT =
            "local lockClientId = redis.call('GET', KEYS[1])\n" +
                    "if lockClientId == ARGV[1] then\n" +
                    "  redis.call('PEXPIRE', KEYS[1], ARGV[2])\n" +
                    "  return true\n" +
                    "elseif not lockClientId then\n" +
                    "  redis.call('SET', KEYS[1], ARGV[1], 'PX', ARGV[2])\n" +
                    "  return true\n" +
                    "end\n" +
                    "return false";


    private final Map<String, RedisLock> locks = new ConcurrentHashMap<>();

    private final String clientId = UUID.randomUUID().toString();

    private final String registryKey;

    private final StringRedisTemplate redisTemplate;

    private final RedisScript<Boolean> obtainLockScript;

    private final long expireAfter;

    /**
     * An {@link ExecutorService} to call {@link StringRedisTemplate#delete(Object)} in
     * the separate thread when the current one is interrupted.
     */
    private Executor executor =
            Executors.newCachedThreadPool(new CustomizableThreadFactory("redis-lock-registry-"));

    /**
     * Flag to denote whether the {@link ExecutorService} was provided via the setter and
     * thus should not be shutdown when {@link #destroy()} is called
     */
    private boolean executorExplicitlySet;

    /**
     * Constructs a lock registry with the default (60 second) lock expiration.
     *
     * @param connectionFactory The connection factory.
     * @param registryKey       The key prefix for locks.
     */
    public RRedisLockRegistry(RedisConnectionFactory connectionFactory, String registryKey) {
        this(connectionFactory, registryKey, DEFAULT_EXPIRE_AFTER);
    }

    /**
     * Constructs a lock registry with the supplied lock expiration.
     *
     * @param connectionFactory The connection factory.
     * @param registryKey       The key prefix for locks.
     * @param expireAfter       The expiration in milliseconds.
     */
    public RRedisLockRegistry(RedisConnectionFactory connectionFactory, String registryKey, long expireAfter) {
        Assert.notNull(connectionFactory, "'connectionFactory' cannot be null");
        Assert.notNull(registryKey, "'registryKey' cannot be null");
        this.redisTemplate = new StringRedisTemplate(connectionFactory);
        this.obtainLockScript = new DefaultRedisScript<>(OBTAIN_LOCK_SCRIPT, Boolean.class);
        this.registryKey = registryKey;
        this.expireAfter = expireAfter;
    }

    /**
     * Set the {@link Executor}, where is not provided then a default of
     * cached thread pool Executor will be used.
     *
     * @param executor the executor service
     * @since 5.0.5
     */
    public void setExecutor(Executor executor) {
        this.executor = executor;
        this.executorExplicitlySet = true;
    }

    @Override
    public Lock obtain(Object lockKey) {
        Assert.isInstanceOf(String.class, lockKey);
        String path = (String) lockKey;
        return this.locks.computeIfAbsent(path, RRedisLockRegistry.RedisLock::new);
    }

    @Override
    public void expireUnusedOlderThan(long age) {
        Iterator<Map.Entry<String, RedisLock>> iterator = this.locks.entrySet().iterator();
        long now = System.currentTimeMillis();
        while (iterator.hasNext()) {
            Map.Entry<String, RedisLock> entry = iterator.next();
            RRedisLockRegistry.RedisLock lock = entry.getValue();
            if (now - lock.getLockedAt() > age && !lock.isAcquiredInThisProcess()) {
                iterator.remove();
            }
        }
    }

    @Override
    public void destroy() {
        if (!this.executorExplicitlySet) {
            ((ExecutorService) this.executor).shutdown();
        }
    }

    private final class RedisLock implements Lock {

        private final String lockKey;

        private final ReentrantLock localLock = new ReentrantLock();

        private volatile long lockedAt;

        private RedisLock(String path) {
            this.lockKey = constructLockKey(path);
        }

        private String constructLockKey(String path) {
            return RRedisLockRegistry.this.registryKey + ":" + path;
        }

        public long getLockedAt() {
            return this.lockedAt;
        }

        @Override
        public void lock() {
            this.localLock.lock();
            while (true) {
                try {
                    while (!obtainLock()) {
                        Thread.sleep(100); //NOSONAR
                    }
                    break;
                } catch (InterruptedException e) {
                    /*
                     * This method must be uninterruptible so catch and ignore
                     * interrupts and only break out of the while loop when
                     * we get the lock.
                     */
                } catch (Exception e) {
                    this.localLock.unlock();
                    rethrowAsLockException(e);
                }
            }
        }

        private void rethrowAsLockException(Exception e) {
            throw new CannotAcquireLockException("Failed to lock mutex at " + this.lockKey, e);
        }

        @Override
        public void lockInterruptibly() throws InterruptedException {
            this.localLock.lockInterruptibly();
            try {
                while (!obtainLock()) {
                    Thread.sleep(100); //NOSONAR
                }
            } catch (InterruptedException ie) {
                this.localLock.unlock();
                Thread.currentThread().interrupt();
                throw ie;
            } catch (Exception e) {
                this.localLock.unlock();
                rethrowAsLockException(e);
            }
        }

        @Override
        public boolean tryLock() {
            try {
                return tryLock(0, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        }

        @Override
        public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
            long now = System.currentTimeMillis();
            if (!this.localLock.tryLock(time, unit)) {
                return false;
            }
            try {
                long expire = now + TimeUnit.MILLISECONDS.convert(time, unit);
                boolean acquired;
                while (!(acquired = obtainLock()) && System.currentTimeMillis() < expire) { //NOSONAR
                    Thread.sleep(100); //NOSONAR
                }
                if (!acquired) {
                    this.localLock.unlock();
                }
                return acquired;
            } catch (Exception e) {
                this.localLock.unlock();
                rethrowAsLockException(e);
            }
            return false;
        }

        private boolean obtainLock() {
            String LUA = RRedisLockRegistry.this.obtainLockScript.getScriptAsString();
            List<String> keys = Collections.singletonList(RedisLock.this.lockKey);
            List<String> args = Lists.newArrayList(RRedisLockRegistry.this.clientId, String.valueOf(RRedisLockRegistry.this.expireAfter));

            Boolean success =
                    RRedisLockRegistry.this.redisTemplate.execute(new RedisCallback<Boolean>() {
                        @Override
                        public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
                            Object nativeConnection = connection.getNativeConnection();
                            // 集群模式和单点模式虽然执行脚本的方法一样，但是没有共同的接口，所以只能分开执行
                            // 集群
                            if (nativeConnection instanceof JedisCluster) {
                                Long result = (Long) ((JedisCluster) nativeConnection).eval(LUA, keys, args);
                                return result > 0;
                            }

                            // 单点
                            else if (nativeConnection instanceof Jedis) {
                                Long result = (Long) ((Jedis) nativeConnection).eval(LUA, keys, args);
                                return result > 0;
                            }

                            return null;
                        }
                    });

            boolean result = Boolean.TRUE.equals(success);

            if (result) {
                this.lockedAt = System.currentTimeMillis();
            }
            return result;
        }

        @Override
        public void unlock() {
            if (!this.localLock.isHeldByCurrentThread()) {
                throw new IllegalStateException("You do not own lock at " + this.lockKey);
            }
            if (this.localLock.getHoldCount() > 1) {
                this.localLock.unlock();
                return;
            }
            try {
                if (!isAcquiredInThisProcess()) {
                    throw new IllegalStateException("Lock was released in the store due to expiration. " +
                            "The integrity of data protected by this lock may have been compromised.");
                }

                if (Thread.currentThread().isInterrupted()) {
                    RRedisLockRegistry.this.executor.execute(this::removeLockKey);
                } else {
                    removeLockKey();
                }

                if (logger.isDebugEnabled()) {
                    logger.debug("Released lock; " + this);
                }
            } catch (Exception e) {
                ReflectionUtils.rethrowRuntimeException(e);
            } finally {
                this.localLock.unlock();
            }
        }

        private void removeLockKey() {
            RRedisLockRegistry.this.redisTemplate.delete(this.lockKey);
        }

        @Override
        public Condition newCondition() {
            throw new UnsupportedOperationException("Conditions are not supported");
        }

        public boolean isAcquiredInThisProcess() {
            return RRedisLockRegistry.this.clientId.equals(
                    RRedisLockRegistry.this.redisTemplate.boundValueOps(this.lockKey).get());
        }

        @Override
        public String toString() {
            SimpleDateFormat dateFormat = new SimpleDateFormat("YYYY-MM-dd@HH:mm:ss.SSS");
            return "RedisLock [lockKey=" + this.lockKey
                    + ",lockedAt=" + dateFormat.format(new Date(this.lockedAt))
                    + ", clientId=" + RRedisLockRegistry.this.clientId
                    + "]";
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + getOuterType().hashCode();
            result = prime * result + ((this.lockKey == null) ? 0 : this.lockKey.hashCode());
            result = prime * result + (int) (this.lockedAt ^ (this.lockedAt >>> 32));
            result = prime * result + RRedisLockRegistry.this.clientId.hashCode();
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            RRedisLockRegistry.RedisLock other = (RRedisLockRegistry.RedisLock) obj;
            if (!getOuterType().equals(other.getOuterType())) {
                return false;
            }
            if (!this.lockKey.equals(other.lockKey)) {
                return false;
            }
            return this.lockedAt == other.lockedAt;
        }

        private RRedisLockRegistry getOuterType() {
            return RRedisLockRegistry.this;
        }

    }

}
