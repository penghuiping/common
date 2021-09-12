package com.php25.common.ws;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.php25.common.core.util.RandomUtil;
import com.php25.common.core.util.StringUtil;
import com.php25.common.mq.Message;
import com.php25.common.mq.MessageQueueManager;
import com.php25.common.redis.RList;
import com.php25.common.redis.RedisManager;
import com.php25.common.timer.Job;
import com.php25.common.timer.Timer;
import com.php25.common.ws.config.Constants;
import com.php25.common.ws.protocal.BaseMsg;
import com.php25.common.ws.protocal.SecurityAuthentication;
import com.php25.common.ws.serializer.InternalMsgSerializer;
import com.php25.common.ws.serializer.VueMsgSerializer;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
public class SessionContext implements InitializingBean, ApplicationListener<ContextClosedEvent> {
    /**
     * 此session缓存用于缓存自定义的sessionId与WebSocket对应关系,全局应用sessionId
     */
    private final ConcurrentHashMap<String, ExpirationSocketSession> sessions = new ConcurrentHashMap<>(1024);

    /**
     * 此session缓存用于缓存原来的sessionId与WebSocket对应关系,单个应用内部webSocketId
     */
    private final ConcurrentHashMap<String, ExpirationSocketSession> _sessions = new ConcurrentHashMap<>(1024);

    private final Timer timer;

    private final RetryMsgManager retryMsgManager;

    private final RedisManager redisService;

    private final MessageQueueManager messageQueueManager;

    private final String serverId;

    private final SecurityAuthentication securityAuthentication;

    private final ExecutorService executorService;

    private final MsgDispatcher msgDispatcher;

    private final VueMsgSerializer vueMsgSerializer = new VueMsgSerializer();

    private final InternalMsgSerializer internalMsgSerializer = new InternalMsgSerializer();

    public String getServerId() {
        return serverId;
    }

    public SessionContext(RetryMsgManager retryMsgManager,
                          RedisManager redisService,
                          SecurityAuthentication securityAuthentication,
                          String serverId,
                          MsgDispatcher msgDispatcher,
                          Timer timer,
                          MessageQueueManager messageQueueManager) {
        this.retryMsgManager = retryMsgManager;
        this.redisService = redisService;
        this.serverId = serverId;
        this.securityAuthentication = securityAuthentication;
        this.msgDispatcher = msgDispatcher;
        this.timer = timer;
        this.messageQueueManager = messageQueueManager;
        int cpuNum = Runtime.getRuntime().availableProcessors();
        this.executorService = new ThreadPoolExecutor(1, 2 * cpuNum,
                60L, TimeUnit.SECONDS,
                new SynchronousQueue<>(), new ThreadFactoryBuilder().setNameFormat("ws-worker-thread-%d").build(), new ThreadPoolExecutor.AbortPolicy());
    }


    @Override
    public void onApplicationEvent(@NotNull ContextClosedEvent contextClosedEvent) {
        cleanAll();
        try {
            this.executorService.shutdown();
            boolean res = this.executorService.awaitTermination(3, TimeUnit.SECONDS);
            if (res) {
                log.info("关闭ws:ws-worker-thread-pool成功");
            }
        } catch (InterruptedException e) {
            log.error("关闭ws:ws-worker-thread-pool出错", e);
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.messageQueueManager.subscribe("ws_session", serverId, true, message -> {
            for (String key : sessions.keySet()) {
                try {
                    WebSocketSession socketSession = sessions.get(key).getWebSocketSession();
                    socketSession.sendMessage(new TextMessage(message.getBody().toString()));
                } catch (Exception e) {
                    log.info("通过websocket发送消息失败,消息为:{}", message.getBody().toString(), e);
                }
            }
        });
    }

    protected void init(SidUid sidUid) {
        //先判断uid原来是否存在，存在就关闭原有连接，使用新的连接
        SidUid sidUid1 = redisService.string().get(Constants.prefix + sidUid.getUserId(), SidUid.class);
        if (sidUid1 != null) {
            clean(sidUid1.getSessionId());
            close(sidUid1.getSessionId());
        }
        redisService.string().set(Constants.prefix + sidUid.getUserId(), sidUid, 3600L);
        redisService.string().set(Constants.prefix + sidUid.getSessionId(), sidUid, 3600L);
    }

    protected void clean(String sid) {
        SidUid sidUid = redisService.string().get(Constants.prefix + sid, SidUid.class);
        if (null != sidUid) {
            redisService.remove(Constants.prefix + sidUid.getUserId());
        }
        redisService.remove(Constants.prefix + sid);
    }

    public RetryMsgManager getRetryMsgManager() {
        return retryMsgManager;
    }

    public void cleanAll() {
        log.info("GlobalSession clean all...");
        Iterator<Map.Entry<String, ExpirationSocketSession>> iterator = sessions.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, ExpirationSocketSession> entry = iterator.next();
            ExpirationSocketSession socketSession = entry.getValue();
            clean(socketSession.getSessionId());
            socketSession.stop();
        }
    }


    protected void create(WebSocketSession webSocketSession) {
        ExpirationSocketSession expirationSocketSession = new ExpirationSocketSession(generateUUID(), webSocketSession, executorService, msgDispatcher);
        sessions.put(expirationSocketSession.getSessionId(), expirationSocketSession);
        _sessions.put(webSocketSession.getId(), expirationSocketSession);

        SessionExpiredCallback callback = new SessionExpiredCallback(expirationSocketSession.getSessionId());
        long executeTime = System.currentTimeMillis() + 30000L;
        Job job = new Job(expirationSocketSession.getSessionId(), executeTime, callback);
        timer.add(job);
    }


    protected void close(String sid) {
        ExpirationSocketSession expirationSocketSession = sessions.remove(sid);
        expirationSocketSession.setSessionId(sid);
        expirationSocketSession.stop();
        _sessions.remove(expirationSocketSession.getWebSocketSession().getId());
        timer.stop(sid);
    }


    protected WebSocketSession get(String sid) {
        ExpirationSocketSession expirationSocketSession = sessions.get(sid);
        if (null != expirationSocketSession) {
            return expirationSocketSession.getWebSocketSession();
        }
        return null;
    }

    public ExpirationSocketSession getExpirationSocketSession(String sid) {
        return sessions.get(sid);
    }

    protected ExpirationSocketSession getExpirationSocketSession(WebSocketSession webSocketSession) {
        return _sessions.get(webSocketSession.getId());
    }


    protected void updateExpireTime(String sid) {
        ExpirationSocketSession expirationSocketSession = sessions.get(sid);
        expirationSocketSession.refreshTime();
        timer.stop(sid);
        long executeTime = System.currentTimeMillis() + 30000L;
        Job job = new Job(expirationSocketSession.getSessionId(), executeTime, expirationSocketSession.getCallback());
        timer.add(job);
    }

    public String getSid(String uid) {
        SidUid sidUid = redisService.string().get(Constants.prefix + uid, SidUid.class);
        if (null != sidUid) {
            return sidUid.getSessionId();
        }
        return null;
    }


    public void send(BaseMsg baseMsg) {
        String sid = baseMsg.getSessionId();
        try {
            if (StringUtil.isBlank(sid)) {
                //没有指定sid,则认为进行全局广播，并且广播消息不会重试
                Message message = new Message(RandomUtil.randomUUID(), vueMsgSerializer.from(baseMsg));
                messageQueueManager.send("ws_session", message);
            } else {
                //现看看sid是否本地存在
                if (this.sessions.containsKey(sid)) {
                    //本地存在,直接通过本地session发送
                    WebSocketSession socketSession = sessions.get(sid).getWebSocketSession();
                    socketSession.sendMessage(new TextMessage(vueMsgSerializer.from(baseMsg)));
                } else {
                    //获取远程session,并往redis推送
                    SidUid sidUid = redisService.string().get(Constants.prefix + sid, SidUid.class);
                    String serverId = sidUid.getServerId();
                    RList<String> rList = redisService.list(Constants.prefix + serverId, String.class);
                    rList.leftPush(internalMsgSerializer.from(baseMsg));
                }
            }
        } catch (Exception e) {
            log.info("通过websocket发送消息失败,sid:{}", sid, e);
        }
    }

    public String authenticate(String token) {
        return this.securityAuthentication.authenticate(token);
    }

    protected ConcurrentHashMap<String, ExpirationSocketSession> getAllExpirationSessions() {
        return this.sessions;
    }

    protected String generateUUID() {
        return RandomUtil.randomUUID().replace("_", "");
    }

    public void stats() {
        log.info("globalSession sessions:{}", sessions.size());
        log.info("globalSession _sessions:{}", sessions.size());
    }
}
