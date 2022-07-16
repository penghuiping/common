/**
 * <h1>通信协议模块</h1>
 *
 * <h2>一. 格式</h2>
 * <ul>
 * <li>1. 通信协议格式使用json方式</li>
 * </ul>
 *
 * <h2>二. 握手身份认证</h2>
 * <ul>
 * <li>1. 客户端连接以后，需要进行身份验证，服务端主动向客户端推送要求身份验证的请求</li>
 * {@link com.php25.common.ws.protocal.RequestAuthInfo}</br>
 * {"msg_id":"uuid","reply_msg_id":"指明应答的消息id","timestamps":"时间戳","action":"request_auth_info"}
 *
 * <li>2. 客户端在收到服务端推送的身份验证请求以后,需要发送验证用的身份令牌</li>
 * {@link com.php25.common.ws.protocal.SubmitAuthInfo}</br>
 * {"msg_id":"uuid","reply_msg_id":"指明应答的消息id","timestamps":"时间戳","action":"submit_auth_info","token":"身份令牌"}
 *
 * <li>3. 服务端在收到客户端令牌以后进行身份验证，完成后返回客户端用户id(uid)</li>
 * {@link com.php25.common.ws.protocal.ReplyAuthInfo}</br>
 * {"msg_id":"uuid","reply_msg_id":"指明应答的消息id","timestamps":"时间戳","action":"reply_auth_info","uid":"用户id"}
 * </ul>
 *
 * <h2>三. ACK机制</h2>
 * <ul>
 * <li>1. 客户端在收到消息以后，需要给服务端ack回应,否则如果消息支持重发，服务端将会重发消息，直到到达最大重发次数</li>
 * {@link com.php25.common.ws.protocal.Ack}</br>
 * {"msg_id":"uuid","reply_msg_id":"指明应答的消息id","timestamps":"时间戳","action":"ack"}
 * </ul>
 *
 * <h2>四. 连接心跳检测</h2>
 * <ul>
 * <li>1. 客户端需要定时向服务端发送心跳，用来告诉服务端客户端还在线</li>
 * {@link com.php25.common.ws.protocal.Ping}</br>
 * {"msg_id":"uuid","reply_msg_id":"指明应答的消息id","timestamps":"时间戳","action":"ping"}
 *
 * <li>2. 服务端回应客户端ping</li>
 * {@link com.php25.common.ws.protocal.Ping}</br>
 * {"msg_id":"uuid","reply_msg_id":"指明应答的消息id","timestamps":"时间戳","action":"pong"}
 * </ul>
 *
 * @author penghuiping
 * @date 2021/9/19 20:13
 */
package com.php25.common.ws.protocal;
