package com.php25.common.mq.rabbit;

import com.google.common.base.Charsets;
import com.php25.common.core.util.JsonUtil;
import com.php25.common.core.util.StringUtil;
import com.php25.common.mq.MessageHandler;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;

import java.util.HashMap;
import java.util.Map;

/**
 * @author penghuiping
 * @date 2021/3/20 17:10
 */
public class RabbitMessageListener implements ChannelAwareMessageListener {
    private static final Logger log = LoggerFactory.getLogger(RabbitMessageListener.class);

    private final Map<String, MessageHandlerContainer> map = new HashMap<>(128);

    private final RabbitTemplate rabbitTemplate;

    public RabbitMessageListener(RabbitTemplate template) {
        this.rabbitTemplate = template;
    }

    public synchronized void addHandler(String queue, String group, MessageHandler handler) {
        if (StringUtil.isBlank(queue)) {
            return;
        }
        if (StringUtil.isBlank(group)) {
            MessageHandlerContainer messageHandlerContainer = map.get(queue);
            if (null == messageHandlerContainer) {
                messageHandlerContainer = new MessageHandlerContainer();
            }
            messageHandlerContainer.add(handler);
            map.put(queue, messageHandlerContainer);
            return;
        }

        MessageHandlerContainer messageHandlerContainer = map.get(queue + ":" + group);
        if (null == messageHandlerContainer) {
            messageHandlerContainer = new MessageHandlerContainer();
        }
        messageHandlerContainer.add(handler);
        map.put(queue + ":" + group, messageHandlerContainer);
    }

    @Override
    public void onMessage(Message message, Channel channel) throws Exception {
        String queueName = message.getMessageProperties().getReceivedExchange();
        String groupName = message.getMessageProperties().getConsumerQueue();
        String queue = RabbitQueueGroupHelper.getQueue(queueName);
        String group = RabbitQueueGroupHelper.getGroup(groupName);
        com.php25.common.mq.Message message0 = null;
        try {
            message0 = JsonUtil.fromJson(new String(message.getBody(), Charsets.UTF_8), com.php25.common.mq.Message.class);
            MessageHandlerContainer container = map.get(group);
            container.getMessageHandlerRoundRobin().handle(message0);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            rabbitTemplate.convertAndSend(
                    RabbitQueueGroupHelper.getDirectQueueName(queue),
                    "error",
                    JsonUtil.toJson(message));
            log.error("消费消息出错", e);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        }
    }
}
