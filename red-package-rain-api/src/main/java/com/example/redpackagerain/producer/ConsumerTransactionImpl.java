package com.example.redpackagerain.producer;

import com.example.redpackagerain.util.theadpool.ThreadPoolConstant;
import com.example.redpackagerain.service.ThreadPooService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.spring.annotation.RocketMQTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionState;
import org.apache.rocketmq.spring.support.RocketMQHeaders;
import org.apache.rocketmq.spring.support.RocketMQUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.stereotype.Component;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 关于@RocketMQTransactionListener 这个注解，2.0.4版本中，是需要指定txProducerGroup指向一个消息发送者组。不同的组可以有不同的事务消息逻辑。
 * 但是到了2.1.1版本，只能指定rocketMQTemplateBeanMame，也就是说如果你有多个发送者组需要有不同的事务消息逻辑，那就需要定义多个RocketMQTemplate。
 * 而且这个版本中，虽然重现了我们在原生API中的事务消息逻辑，但是测试过程中还是发现一些奇怪的特性，用的时候要注意点。
 **/

@Slf4j
@Component
@RocketMQTransactionListener
public class ConsumerTransactionImpl implements RocketMQLocalTransactionListener {

    private ConcurrentHashMap<Object, Message> localTrans = new ConcurrentHashMap<>();

    @Autowired
    private ThreadPooService threadPooService;

    /**
     * 这个方法用于执行本地事务。当生产者发送一条事务消息时，RocketMQ会先调用这个方法执行本地事务。开发者需要在这个方法中编写自己的本地事务逻辑。
     * @param msg
     * @param arg
     * @return
     */
    @Override
    public RocketMQLocalTransactionState executeLocalTransaction(Message msg, Object arg) {
        Object transId = msg.getHeaders().get(RocketMQHeaders.PREFIX +  RocketMQHeaders.TRANSACTION_ID);
        String destination = arg.toString();
        localTrans.put(transId,msg);
        //这个msg的实现类是GenericMessage，里面实现了toString方法.在Header中自定义的RocketMQHeaders.TAGS属性，到这里就没了。
        // 但是RocketMQHeaders.TRANSACTION_ID这个属性就还在。而message的Header里面会默认保存RocketMQHeaders里的属性，但是都会加上一个RocketMQHeaders.PREFIX前缀
        log.info("executeLocalTransaction方法的消息 ="+msg);
        //转成RocketMQ的Message对象
        org.apache.rocketmq.common.message.Message message = RocketMQUtil.convertToRocketMessage(new StringMessageConverter(),"UTF-8",destination, msg);
        String tag = message.getTags();
        //处理线程池参数的业务  TODO 可以使用策略模式改造
        if(StringUtils.equals(tag, ThreadPoolConstant.TAG_THREADPOOL_PARAMETER)){
            if (threadPooService.generateMonitoringInfo(message)) {
                //表示本地事务执行成功，消息应该被提交。
                return RocketMQLocalTransactionState.COMMIT;
            }else {
                //表示本地事务执行失败，消息应该被回滚。
                return RocketMQLocalTransactionState.ROLLBACK;
            }
         // 处理线程池被拒绝的任务
        }else if(StringUtils.equals(tag, ThreadPoolConstant.TAG_THREADPOOL_TASK)){
            if (threadPooService.manualIntervention(message)) {
                //表示本地事务执行成功，消息应该被提交。
                return RocketMQLocalTransactionState.COMMIT;
            }else {
                //表示本地事务执行失败，消息应该被回滚。
                return RocketMQLocalTransactionState.ROLLBACK;
            }
        }
        //表示本地事务状态不确定，需要进行二次检查。RocketMQ将在稍后的某个时间点调用checkLocalTransaction方法进行二次检查。
        return RocketMQLocalTransactionState.UNKNOWN;
    }

    /**
     * 这个方法用于检查本地事务的执行状态。当executeLocalTransaction方法返回TransactionState.UNKNOW时，RocketMQ会在稍后的某个时间点（通常是消息发送后的某个时间间隔）调用这个方法。
     * 开发者需要在这个方法中根据某种机制（例如，查询数据库）来确定本地事务的最终状态。然后返回相应的状态给RocketMQ，以便RocketMQ决定是提交还是回滚这条消息。
     * checkLocalTransaction方法应该尽快返回结果，因为它可能会阻塞消息的生产者线程。如果checkLocalTransaction方法执行时间过长，可能会导致生产者线程阻塞，进而影响应用的性能。
     * 因此，在checkLocalTransaction方法中，应该避免执行耗时的操作，如访问远程数据库或调用外部服务等。
     * @param msg
     * @return
     */
    @Override
    public RocketMQLocalTransactionState checkLocalTransaction(Message msg) {
        String transId = msg.getHeaders().get(RocketMQHeaders.PREFIX+RocketMQHeaders.TRANSACTION_ID).toString();
        Message originalMessage = localTrans.get(transId);
        //这里能够获取到自定义的transaction_id属性
        log.info("checkLocalTransaction方法的消息 ="+originalMessage);
        //获取标签时，自定义的RocketMQHeaders.TAGS拿不到，但是框架会封装成一个带RocketMQHeaders.PREFIX的属性
        String tag = msg.getHeaders().get(RocketMQHeaders.PREFIX + RocketMQHeaders.TAGS).toString();

        if(StringUtils.contains(tag,"TagC")){
            //表示本地事务最终执行成功，消息应该被提交。
            return RocketMQLocalTransactionState.COMMIT;
        }else if(StringUtils.contains(tag,"TagD")){
            //表示本地事务最终执行失败，消息应该被回滚。
            return RocketMQLocalTransactionState.ROLLBACK;
        }else{
            //开发者应该确保 checkLocalTransaction 方法能够尽快并准确地返回本地事务的最终状态（COMMIT_MESSAGE 或 ROLLBACK_MESSAGE）。
            // 如果事务状态确实需要一段时间才能确定，可以考虑使用某种机制（如数据库锁、定时任务等）来在 checkLocalTransaction 方法被调用时能够快速地获取到最终状态。
            // 这样可以减少不必要的重试和潜在的消息回滚，提高系统的可靠性和性能。
            // 如果一直为UNKNOWN会每隔一段时间都会执行checkLocalTransaction方法
            return RocketMQLocalTransactionState.UNKNOWN;
        }
    }
}
