package com.example.redpackagerain.producer;

import com.example.redpackagerain.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.apache.rocketmq.spring.support.RocketMQHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;

@Slf4j
@Component
public class MessageProducer {

    @Resource
    private RocketMQTemplate rocketMQTemplate;

    /**
     * 普通发送MQ消息，但不保证高可用
     * 就可靠性而言，RocketMQ的事务消息比Kafka更可靠。RocketMQ的事务消息是通过半消息的方式实现的，即将消息发送到Broker后，但是不立即将消息投递给消费者，而是先将消息持久化到磁盘后返回给生产者一个确认，然后由生产者根据自己的业务逻辑决定是否要将消息发送给消费者。这样可以确保消息不会丢失，因为只有当消息成功被消费者消费后，才会将消息从磁盘删除。
     * 而Kafka的消息可靠性依赖于配置的参数，如果将`request.required.acks`配置为-1或all，可以最大限度保证消息不丢失，但是会降低Kafka的吞吐量。如果将`request.required.acks`配置为1，虽然能保证Leader存活时不会丢失消息，但是如果Leader挂了，恰好选了一个没有ACK的follower，那也会丢消息。
     * 就性能而言，Kafka的性能一般会比RocketMQ高一些。Kafka采用了多分区多副本的机制，可以同时从多个副本读取数据，提高吞吐量。而RocketMQ的消息顺序性比较强，需要严格按照消息的顺序进行消费，因此在某些场景下可能会降低性能。
     * 综合来说，如果对于消息的可靠性有较高的要求，可以选择RocketMQ的事务消息。如果对吞吐量要求较高，可以选择Kafka。
     * 普通消息的生产者和消费者可以拆分成两个微服务，因为它们不需要保证消息发送和本地事务的原子性，也不需要跨服务进行事务协调。普通消息主要关注消息的传递和消费，对生产者和消费者的位置没有严格的要求。
     * @param topic
     * @param msg
     */
    public void sendMessage(String topic,String msg){
        log.info("发送消息，主题为" + topic + "消息为" + msg);
        this.rocketMQTemplate.convertAndSend(topic,msg);
    }


    /**
     * 添加事务id，保证消息不会重复消费
     * @param topic 只允许事务主题
     * @param msg
     * @param transactionId
     * @param tag
     */
    public void sendMessageInTransaction(String topic,String msg,String transactionId,String tag) {
        //尝试在Header中加入一些自定义的属性。
        Message<String> message = MessageBuilder.withPayload(msg)
                .setHeader(RocketMQHeaders.TRANSACTION_ID,transactionId)
                .setHeader(RocketMQHeaders.TAGS,tag)
                .build();
        String destination = StringUtil.StringAppend(topic,":",tag);
        //这里发送事务消息时，还是会转换成RocketMQ的Message对象，再调用RocketMQ的API完成事务消息机制。
        SendResult sendResult = rocketMQTemplate.sendMessageInTransaction(destination, message,destination);
        log.info("事务消息发送结果：" + sendResult);
    }

    /**
     * 在事务消息中，生产者和消费者需要在同一个微服务里
     * 原子性保证：事务消息要求消息发送和本地事务的执行具有原子性，即要么都成功，要么都失败。这意味着消息发送和本地事务的执行必须在同一个微服务中进行，以确保它们能够作为一个整体进行提交或回滚。如果将生产者和消费者拆分到两个不同的微服务中，原子性将无法得到保证。
     * 分布式事务管理：事务消息涉及分布式事务管理，需要有一个统一的协调者来管理全局事务。在 RocketMQ 中，这个协调者通常是 RocketMQ 的 broker。如果生产者和消费者在不同的微服务中，那么就需要跨服务进行事务协调，这会增加复杂性和出错的可能性。
     * 消息顺序性：在某些场景下，事务消息需要保证消息的顺序性。如果生产者和消费者在不同的微服务中，那么消息的顺序性将无法得到保证。
     * @param topic 只允许事务主题
     * @param msg
     * @param tag
     */
    public void sendMessageInTransaction(String topic,String msg,String tag) {
        //尝试在Header中加入一些自定义的属性。
        Message<String> message = MessageBuilder.withPayload(msg)
                .setHeader(RocketMQHeaders.TAGS,tag)
                .build();
        String destination = StringUtil.StringAppend(topic,":",tag);
        //这里发送事务消息时，还是会转换成RocketMQ的Message对象，再调用RocketMQ的API完成事务消息机制。
        SendResult sendResult = rocketMQTemplate.sendMessageInTransaction(destination, message,destination);
        log.info("事务消息发送结果：" + sendResult);
    }

    /**
     *
     * @param topic 只允许事务主题
     * @param msg
     */
    public void sendMessageInTransaction(String topic,String msg) {
        Message<String> message = MessageBuilder.withPayload(msg).build();
        String destination = StringUtil.StringAppend(topic,":",msg);
        SendResult sendResult = rocketMQTemplate.sendMessageInTransaction(destination, message,destination);
        log.info("事务消息发送结果：" + sendResult);
    }
}
