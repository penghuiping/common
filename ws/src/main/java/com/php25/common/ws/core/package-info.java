/**
 * <h1>作用</h1>
 * <p>
 * ws模块内核,ws模块是用于解决在服务器多实例部署的情况下，解决websocket在不同服务器中相互之间通信问题，
 * 可以解决websocket的水平扩展问题
 * </p>
 * <h1>原理</h1>
 * <ul>
 * <li>1. 把websocket的连接状态记录在redis中，并使用redis的list做跨服务器实例通信</li>
 * <li>2. 接收消息数据流图---websocket方式(从客户端上报的消息)</li>
 *   {@link com.php25.common.ws.core.WebsocketHandler}<br/>
 *   ↓<br/>
 *   {@link com.php25.common.ws.core.ExpirationSocketSession}<br/>
 *   ↓<br/>
 *   {@link com.php25.common.ws.core.MsgDispatcher}<br/>
 *   ↓<br/>
 *   {@link com.php25.common.ws.handler.MsgHandler}
 * <li>3. 接收消息数据流图----redis订阅方式</li>
 *   {@link com.php25.common.ws.core.RedisQueueSubscriber}<br/>
 *   ↓<br/>
 *   {@link com.php25.common.ws.core.ExpirationSocketSession}<br/>
 *   ↓<br/>
 *   {@link com.php25.common.ws.core.MsgDispatcher}<br/>
 *   ↓<br/>
 *   {@link com.php25.common.ws.handler.MsgHandler}<br/>
 * <li>4. 发送数据数据流图---目标websocket在本机直接向客户端发送</li>
 *   {@link com.php25.common.ws.core.SessionContext}<br/>
 *   ↓<br/>
 *   {@link org.springframework.web.socket.WebSocketSession}<br/>
 * <li>5. 发送数据数据流图---目标websocket不在本机，先经过redis寻址，在向目标机的websocket发送</li>
 *   {@link com.php25.common.ws.core.SessionContext}<br/>
 *   ↓<br/>
 *   {@link com.php25.common.redis.RedisManager}<br/>
 *   ↓<br/>
 *   跳转至步骤3<br/>
 * </ul>
 *
 * @author penghuiping
 * @date 2021/9/19 20:34
 */
package com.php25.common.ws.core;
