package com.php25.common.ws;

import com.php25.common.redis.RedisManager;
import com.php25.common.redis.RedisManagerImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.BoundListOperations;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import javax.annotation.PreDestroy;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.ExecutorService;

@Slf4j
public class GlobalSession {
    private final ConcurrentHashMap<String, ExpirationSocketSession> sessions = new ConcurrentHashMap<>(1024);

    private final DelayQueue<ExpirationSocketSession> expireSessionQueue = new DelayQueue<>();

    private final InnerMsgRetryQueue msgRetry;

    private final RedisManager redisService;

    private final String serverId;

    private final SecurityAuthentication securityAuthentication;

    private final ExecutorService executorService;

    private final MsgDispatcher msgDispatcher;

    private final VueMsgSerializer vueMsgSerializer = new VueMsgSerializer();

    private final InternalMsgSerializer internalMsgSerializer = new InternalMsgSerializer();

    public String getServerId() {
        return serverId;
    }

    public GlobalSession(InnerMsgRetryQueue msgRetry,
                         RedisManager redisService,
                         SecurityAuthentication securityAuthentication,
                         String serverId,
                         ExecutorService executorService,
                         MsgDispatcher msgDispatcher) {
        this.msgRetry = msgRetry;
        this.redisService = redisService;
        this.serverId = serverId;
        this.securityAuthentication = securityAuthentication;
        this.executorService = executorService;
        this.msgDispatcher = msgDispatcher;
    }


    public void dispatchAck(String action, BaseRetryMsg srcMsg) {
        msgDispatcher.dispatchAck(action, srcMsg);
    }

    protected void init(SidUid sidUid) {
        //先判断uid原来是否存在，存在就无法创建新连接
        SidUid sidUid1 = redisService.string().get(Constants.prefix + sidUid.getUserId(), SidUid.class);
        if (sidUid1 != null) {
            throw new IllegalStateException("已存在以后连接无法创建新连接");
        }
        redisService.string().set(Constants.prefix + sidUid.getUserId(), sidUid, 3600 * 24L);
        redisService.string().set(Constants.prefix + sidUid.getSessionId(), sidUid, 3600 * 24L);
    }

    protected void clean(String sid) {
        SidUid sidUid = redisService.string().get(Constants.prefix + sid, SidUid.class);
        if (null != sidUid) {
            redisService.remove(Constants.prefix + sidUid.getUserId());
        }
        redisService.remove(Constants.prefix + sid);
    }

    @PreDestroy
    public void cleanAll() {
        log.info("GlobalSession clean all...");
        Iterator<Map.Entry<String, ExpirationSocketSession>> iterator = sessions.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, ExpirationSocketSession> entry = iterator.next();
            ExpirationSocketSession socketSession = entry.getValue();
            clean(socketSession.getSessionId());
        }
    }


    protected void create(WebSocketSession webSocketSession) {
        ExpirationSocketSession expirationSocketSession = new ExpirationSocketSession();
        expirationSocketSession.setTimestamp(System.currentTimeMillis());
        expirationSocketSession.setWebSocketSession(webSocketSession);
        expirationSocketSession.setSessionId(webSocketSession.getId());
        expirationSocketSession.setExecutorService(executorService);
        expirationSocketSession.setMsgDispatcher(msgDispatcher);
        sessions.put(webSocketSession.getId(), expirationSocketSession);
        expireSessionQueue.put(expirationSocketSession);
    }


    protected void close(String sid) {
        ExpirationSocketSession expirationSocketSession = sessions.remove(sid);
        expirationSocketSession.setSessionId(sid);
        expireSessionQueue.remove(expirationSocketSession);
        expirationSocketSession.stop();
    }


    protected WebSocketSession get(String sid) {
        ExpirationSocketSession expirationSocketSession = sessions.get(sid);
        if (null != expirationSocketSession) {
            return expirationSocketSession.getWebSocketSession();
        }
        return null;
    }

    protected ExpirationSocketSession getExpirationSocketSession(String sid) {
        return sessions.get(sid);
    }


    protected void updateExpireTime(String sid) {
        ExpirationSocketSession expirationSocketSession = sessions.get(sid);
        expirationSocketSession.setTimestamp(System.currentTimeMillis());
        expireSessionQueue.remove(expirationSocketSession);
        expireSessionQueue.add(expirationSocketSession);
    }

    public String getSid(String uid) {
        SidUid sidUid = redisService.string().get(Constants.prefix + uid, SidUid.class);
        if (null != sidUid) {
            return sidUid.getSessionId();
        }
        return null;
    }

    public void send(BaseRetryMsg baseRetryMsg) {
        this.send(baseRetryMsg, true);
    }

    public void send(BaseRetryMsg baseRetryMsg, Boolean retry) {
        if (baseRetryMsg instanceof ConnectionCreate || baseRetryMsg instanceof ConnectionClose) {
            msgRetry.put(baseRetryMsg);
            return;
        }

        String sid = baseRetryMsg.getSessionId();
        try {
            //现看看sid是否本地存在
            if (this.sessions.containsKey(sid)) {
                //本地存在,直接通过本地session发送
                WebSocketSession socketSession = sessions.get(sid).getWebSocketSession();
                socketSession.sendMessage(new TextMessage(vueMsgSerializer.from(baseRetryMsg)));
                if (retry) {
                    msgRetry.put(baseRetryMsg);
                }
            } else {
                //获取远程session
                RedisManagerImpl redisManagerImpl = (RedisManagerImpl) redisService;
                SidUid sidUid = redisService.string().get(Constants.prefix + sid, SidUid.class);
                String serverId = sidUid.getServerId();
                BoundListOperations<String, String> listOperations = redisManagerImpl.getRedisTemplate().boundListOps(Constants.prefix + serverId);
                listOperations.leftPush(internalMsgSerializer.from(baseRetryMsg));
            }
        } catch (Exception e) {
            log.info("通过websocket发送消息失败,sid:{}", sid, e);
        }
    }

    public String authenticate(String token) {
        return this.securityAuthentication.authenticate(token);
    }

    public void revokeRetry(BaseRetryMsg baseRetryMsg) {
        //这里interval必须要大于0才能从重试队列中移除
        baseRetryMsg.setInterval(1);
        msgRetry.remove(baseRetryMsg);
    }

    public BaseRetryMsg getMsg(String msgId, String action) {
        return this.msgRetry.get(msgId, action);
    }

    protected DelayQueue<ExpirationSocketSession> getAllExpirationSessions() {
        return this.expireSessionQueue;
    }

    protected String generateUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
