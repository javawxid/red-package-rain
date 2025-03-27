package com.example.redpackagerain.consumer;

import com.alibaba.fastjson.JSONObject;
import com.example.redpackagerain.constant.RedPackageRainConstant;
import com.example.redpackagerain.entity.RedPackageRainVo;
import com.example.redpackagerain.entity.RobRedPackageLog;
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

import java.util.List;
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
@RocketMQMessageListener(consumerGroup = "MessageConsumerMergeRequestGroup2", topic = "RED_PACKAGE_RAIN_MERGERE_QUEST_TOPIC",consumeMode= ConsumeMode.CONCURRENTLY)
public class MessageConsumerMergeRequest2 implements RocketMQListener<String> {
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
            log.info(StringUtil.StringAppend("MessageConsumerMergeRequestGroup2,收到第",recievedMessageCount,"条消息！！！收到消息: ",message));
            ConcurrentMap<String, Object> messageMap = StringUtil.parseStringToMap(message);
            String messageId = (String) messageMap.get(RedPackageRainConstant.RED_PACKAGE_MESSAGE_ID);
            String redPackageSum = (String) messageMap.get(RedPackageRainConstant.RED_PACKAGE_SUM);
            // 判断是否已消费，为空表示没有消费过
            if (!StringUtils.isEmpty(messageId) && !StringUtils.isEmpty(message) && RedisUtil.getLock(RedPackageRainConstant.RED_PACKAGE_MESSAGE_ID + messageId,message,3600)) {
                log.info("MessageConsumerMergeRequestGroup2处理消息：" + messageId);
                List<RedPackageRainVo> list = (List<RedPackageRainVo>)messageMap.get("list");
                for (RedPackageRainVo redPackageRainVo : list) {
                    RobRedPackageLog robRedPackageLog = LogFactory.getRobRedPackageLog();
                    String redPackageId = redPackageRainVo.getRedPackageId();
                    String activityId = redPackageRainVo.getActivityId();
                    String userId = redPackageRainVo.getUserId();
                    robRedPackageLog.setUserId(userId);
                    robRedPackageLog.setRedPackageId(redPackageId);
                    robRedPackageLog.setActivityId(activityId);
                    robRedPackageLog.setMessage(message);
                    robRedPackageLog.setMessageId(messageId);
                    robRedPackageLog.setPartRedPackage(redPackageSum);
                    //设置存入缓存的key
                    String redPackageLog = StringUtil.StringAppend(RedPackageRainConstant.RED_PACKAGE_MERGEREQUEST_LOG, recievedMessageCount);
                    // 移除列表左侧的元素（左弹出） 先移除然后添加保证始终都是最新的数据
                    redisUtil.leftPop(redPackageLog);
                    //暂时存入缓存由定时任务取出批量插入数据库减少数据库的压力
                    redisUtil.leftPushAll(redPackageLog, JSONObject.toJSONString(robRedPackageLog));
                }
                // 获取相关参数 todo 进行自定义业务
                //TODO 额外的业务逻辑
                // 校验抢红包的用户是否正常，这里需要判断是否存在多台设备操作的情况，例如一台设备先抢红包，抢红包有一段时间，这段时间另一台设备进行违规操作，在入账前进行判断，如果违规则不能入账
                // 异步汇总红包通知用户服务红包入账，可以跑异步风控模型，判断用户无异常情况后，抢到的红包金额也没问题，ip也没问题等一系列校验通过之后，没问题就入账
                // 异步统计抢了多少个红包，发了多少钱

                // 表示已消费
                RedisUtil.releaseLock(messageId, message);//释放锁
            }
        } catch (Exception e) {
            // 如果处理消息时出现异常，Spring 的事务管理器会回滚事务，RocketMQ 会认为消息处理失败并触发重试
            throw new RuntimeException("消息处理失败，异常：", e);
        }
    }
}
