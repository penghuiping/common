package com.php25.common.mq.rabbit;

import com.google.common.base.Charsets;
import com.php25.common.core.util.AssertUtil;
import com.php25.common.core.util.JsonUtil;
import com.php25.common.core.util.StringUtil;
import com.php25.common.mq.Message;
import com.php25.common.mq.MessageHandler;
import com.php25.common.mq.MessageQueueManager;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.DirectMessageListenerContainer;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import java.util.HashMap;

/**
 * @author penghuiping
 * @date 2021/3/10 20:55
 */
public class RabbitMessageQueueManager implements MessageQueueManager, InitializingBean, DisposableBean {

    private final RabbitTemplate rabbitTemplate;

    private final RabbitAdmin rabbitAdmin;

    private final RabbitMessageListener messageListener;

    private final DirectMessageListenerContainer listenerContainer;

    public RabbitMessageQueueManager(RabbitTemplate rabbitTemplate, RabbitMessageListener messageListener) {
        this.rabbitTemplate = rabbitTemplate;
        this.rabbitAdmin = new RabbitAdmin(this.rabbitTemplate);
        this.messageListener = messageListener;
        this.listenerContainer = new DirectMessageListenerContainer();
        this.listenerContainer.setConnectionFactory(this.rabbitTemplate.getConnectionFactory());
        this.listenerContainer.setMessageListener(this.messageListener);
        this.listenerContainer.setConsumersPerQueue(1);
        this.listenerContainer.setAcknowledgeMode(AcknowledgeMode.MANUAL);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.listenerContainer.start();
    }

    @Override
    public void destroy() throws Exception {
        this.listenerContainer.stop();
    }

    @Override
    public Boolean subscribe(String queue, MessageHandler handler) {
        return this.subscribe(queue, queue, handler);
    }

    @Override
    public Boolean subscribe(String queue, String group, MessageHandler handler) {
        return this.subscribe(queue, group, false, handler);
    }

    @Override
    public Boolean subscribe(String queue, String group, Boolean autoDelete, MessageHandler handler) {
        String directQueue = RabbitQueueGroupHelper.getDirectQueueName(queue);
        String fanoutQueue = RabbitQueueGroupHelper.getFanoutQueueName(queue);
        String group0 = RabbitQueueGroupHelper.getGroupName(groupName(queue, group));

        this.rabbitAdmin.declareExchange(new DirectExchange(directQueue));
        this.rabbitAdmin.declareExchange(new FanoutExchange(fanoutQueue));
        this.rabbitAdmin.declareQueue(new Queue(group0, true, false, autoDelete));
        this.rabbitAdmin.declareBinding(new Binding(group0,
                Binding.DestinationType.QUEUE,
                directQueue,
                group0,
                new HashMap<>(16)));
        this.rabbitAdmin.declareBinding(new Binding(group0,
                Binding.DestinationType.QUEUE,
                fanoutQueue,
                group0,
                new HashMap<>(16)));
        messageListener.addHandler(queue, group, handler);
        this.listenerContainer.addQueueNames(group0);
        return true;
    }

    private String groupName(String queue, String group) {
        return queue + ":" + group;
    }

    @Override
    public Boolean send(String queue, Message message) {
        return send(queue, null, message);
    }

    @Override
    public Boolean send(String queue, String group, Message message) {
        AssertUtil.hasText(message.getId(), "messageId不能为空");
        AssertUtil.hasText(queue, "queue不能为空");
        message.setQueue(queue);
        message.setGroup(group);
        if (StringUtil.isBlank(group)) {
            rabbitTemplate.convertAndSend(
                    RabbitQueueGroupHelper.getFanoutQueueName(queue),
                    "",
                    JsonUtil.toJson(message));
        } else {
            rabbitTemplate.convertAndSend(
                    RabbitQueueGroupHelper.getDirectQueueName(queue),
                    RabbitQueueGroupHelper.getGroupName(groupName(queue, group)),
                    JsonUtil.toJson(message));
        }
        return true;
    }

    @Override
    public Boolean delete(String queue) {
        AssertUtil.hasText(queue, "queue不能为空");
        this.delete(queue, queue);
        rabbitAdmin.deleteExchange(RabbitQueueGroupHelper.getDirectQueueName(queue));
        rabbitAdmin.deleteExchange(RabbitQueueGroupHelper.getFanoutQueueName(queue));
        rabbitAdmin.deleteQueue(RabbitQueueGroupHelper.getDlq(queue));
        return true;
    }

    @Override
    public Boolean delete(String queue, String group) {
        AssertUtil.hasText(queue, "queue不能为空");
        AssertUtil.hasText(group, "group不能为空");
        rabbitAdmin.deleteQueue(RabbitQueueGroupHelper.getGroupName(groupName(queue, group)));
        return true;
    }

    @Override
    public Message pullDlq(String queue, Long timeout) {
        String queueName = RabbitQueueGroupHelper.getDlq(queue);
        org.springframework.amqp.core.Message message = rabbitTemplate.receive(queueName, timeout);
        return JsonUtil.fromJson(new String(message.getBody(), Charsets.UTF_8), Message.class);
    }

    @Override
    public Boolean bindDeadLetterQueue(String queue) {
        String queue0 = RabbitQueueGroupHelper.getDirectQueueName(queue);
        String group0 = RabbitQueueGroupHelper.getDlq(queue);
        this.rabbitAdmin.declareExchange(new DirectExchange(queue0));
        this.rabbitAdmin.declareQueue(new Queue(group0));
        this.rabbitAdmin.declareBinding(new Binding(group0,
                Binding.DestinationType.QUEUE,
                queue0,
                "error",
                new HashMap<>(16)));
        return true;
    }
}
