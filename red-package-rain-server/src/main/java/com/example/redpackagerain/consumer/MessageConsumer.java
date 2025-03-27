package com.example.redpackagerain.consumer;

import com.alibaba.fastjson.JSONObject;
import com.example.redpackagerain.constant.RedPackageRainConstant;
import com.example.redpackagerain.entity.RobRedPackageLog;
import com.example.redpackagerain.enums.LogEnum;
import com.example.redpackagerain.factory.LogFactory;
import com.example.redpackagerain.util.RedisUtil;
import com.example.redpackagerain.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.ConcurrentMap;

/**
 * 注意下@RocketMQMessageListener这个注解的其他属性
 * 默认情况下，当你实现了 RocketMQListener 并覆盖 onMessage 方法时，如果 onMessage 方法正常执行完毕且没有抛出异常，RocketMQ 会认为消息已经被成功消费，并自动发送 ACK。
    consumerGroup(): 消费者组名。这是 RocketMQ 中用于标识一组消费者的名称。
    topic(): 消息主题。消费者订阅此主题以接收消息。
    selectorType(): 选择器类型。默认为 SelectorType.TAG，表示使用标签选择器。
    selectorExpression(): 选择器表达式。默认为 "*"，表示匹配所有消息。
    consumeMode(): 消费模式。默认为 ConsumeMode.CONCURRENTLY，表示并发消费。 ConsumeMode.ORDERLY顺序
    messageModel(): 消息模型。默认为 MessageModel.CLUSTERING，表示集群消费。
    consumeThreadMax(): 最大消费线程数。默认为 64。
    consumeTimeout(): 消费超时时间。默认为 15L（单位可能是毫秒或其他，具体取决于 RocketMQ 的实现）。
    accessKey() 和 secretKey(): 用于身份验证的访问键和秘密键。
    enableMsgTrace(): 是否启用消息追踪。默认为 true。
    customizedTraceTopic(): 自定义追踪主题。
    nameServer(): RocketMQ 的名称服务器地址。
    accessChannel(): 访问通道。

 **/
@Slf4j
@Service
@Component
@Transactional
@RocketMQMessageListener(consumerGroup = "RedPackageRainGroup", topic = "RED_PACKAGE_RAIN_TOPIC",consumeMode= ConsumeMode.ORDERLY)
public class MessageConsumer implements RocketMQListener<String> {
    @Autowired
    private RedisUtil redisUtil;

    /**
     * 在某些情况下，想要更精细地控制消息的确认。虽然 RocketMQ Spring Boot Starter 不直接支持手动 ACK，但可以通过抛出异常或者使用 @Transactional 注解（如果你的消息处理涉及到数据库操作）来告知 RocketMQ 消息处理失败，从而触发重试机制。
     * @param message
     */
    @Override
    public void onMessage(String message) {
        try {
            String recievedMessageCount = String.valueOf(redisUtil.recievedMessage());
            // 处理消息
            log.info(StringUtil.StringAppend("RedPackageRainGroup1,收到第",recievedMessageCount,"条消息！！！收到消息: ",message));
            //字符串转Map存入对象
            ConcurrentMap<String, Object> messageMap = StringUtil.stringToMap(message);
            String messageId = (String) messageMap.get(RedPackageRainConstant.RED_PACKAGE_MESSAGE_ID);
            // 没有消费才执行
            if (!StringUtils.isEmpty(messageId) && !StringUtils.isEmpty(message) && RedisUtil.getLock(messageId,message,10)) {
                RobRedPackageLog robRedPackageLog = LogFactory.getLogContext(LogEnum.ROBREDPACKAGELOG);
                String userId = (String) messageMap.get(RedPackageRainConstant.RED_PACKAGE_USER_ID);
                String redPackageId = (String) messageMap.get(RedPackageRainConstant.RED_PACKAGE_REDPACKAGE_ID);
                String activityId = (String) messageMap.get(RedPackageRainConstant.RED_PACKAGE_ACTIVITY_ID);
                String partRedPackage = (String) messageMap.get(RedPackageRainConstant.RED_PACKAGE_VALUE);
                String createTime = (String) messageMap.get(RedPackageRainConstant.CREATE_TIME);
                // 获取相关参数，进行自定义业务
                robRedPackageLog.setUserId(userId);
                robRedPackageLog.setRedPackageId(redPackageId);
                robRedPackageLog.setActivityId(activityId);
                robRedPackageLog.setPartRedPackage(partRedPackage);
                robRedPackageLog.setMessage(message);
                robRedPackageLog.setCreateTime(StringUtil.currentTimeToDate(createTime));
                //设置存入缓存的key
                String redPackageLog = StringUtil.StringAppend(RedPackageRainConstant.RED_PACKAGE_LOG, recievedMessageCount);
                //暂时存入缓存由定时任务取出批量插入数据库减少数据库的压力 todo 字符串存入缓存会将时分秒去掉，需要调整一下，这里我暂时没调整
                redisUtil.leftPushAll(redPackageLog, JSONObject.toJSONString(robRedPackageLog));
                //TODO 额外的业务逻辑
                // 校验抢红包的用户是否正常，这里需要判断是否存在多台设备操作的情况，例如一台设备先抢红包，抢红包有一段时间，这段时间另一台设备进行违规操作，在入账前进行判断，如果违规则不能入账
                // 异步汇总红包通知用户服务红包入账，可以跑异步风控模型，判断用户无异常情况后，抢到的红包金额也没问题，ip也没问题等一系列校验通过之后，没问题就入账
                // 异步统计抢了多少个红包，发了多少钱

                // 已消费
                RedisUtil.releaseLock(messageId, message);//释放锁
            }
        } catch (Exception e) {
            // 如果处理消息时出现异常，Spring 的事务管理器会回滚事务，RocketMQ 会认为消息处理失败并触发重试
            throw new RuntimeException("Message processing failed", e);
        }
    }
}
