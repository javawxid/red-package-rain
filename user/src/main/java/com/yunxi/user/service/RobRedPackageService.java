package com.yunxi.user.service;

import com.google.common.collect.Lists;
import com.yunxi.user.constants.RedPackageRainConstant;
//import com.yunxi.user.dynamic.DynamicDataSourceContextHolder;
import com.yunxi.user.mapper.s.SRobRedPackageLogMapper;
import com.yunxi.user.model.po.TbRobRedPackageLog;
import com.yunxi.user.util.RedisUtil;
import com.yunxi.user.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.transaction.Transactional;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class RobRedPackageService {

    @Autowired
    private SRobRedPackageLogMapper sRobRedPackageLogMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private RedisUtil redisUtil;

    private static final int BATCH_SIZE = 1000; // 定义每批处理的数据量

//    @GlobalTransactional(name="saveRedPackageLog",rollbackFor=Exception.class)
    public void saveRedPackageLog() {
//        log.info("当前 XID: {}", RootContext.getXID());
        // 获取计数器初始值
        Long initialCount = redisUtil.getInitialCount();
//        log.info(StringUtil.StringAppend("=============保存红包雨日志的定时任务，时间是：", StringUtil.getCurrentTime(),"执行第：",String.valueOf(initialCount),"次任务"));
        //从缓存中取出全部数据 没有一个固定的 ArrayList 长度会导致 OOM。在某些情况下，一个包含数千个元素的 ArrayList 就可能导致 OOM，而在其他情况下，一个包含数百万个元素的 ArrayList 也可能正常运行。
        //这里解决办法有几种：
        // 第一种，一次只处理一批数据，剩下的重新保存到redis中，等到第二次定时任务触发，重新执行。
        // 第二种：将定时任务时间间隔设置短一些，例如设置成3秒，这样robRedPackageLogList的长度就不会太大，保证数据库在可以承受的范围内即可。
        // 第三种：不要一次性取出这么多数据，分批次取出，进行处理。
        List<TbRobRedPackageLog> robRedPackageLogList = new ArrayList<>();
        Long start = 1L; // 起始索引
        Long end = Math.min(start + BATCH_SIZE - 1, initialCount); // 结束索引，确保不超过initialCount
        while (start <= initialCount) {
            // 分批次从Redis中获取数据
            for (Long i = start; i <= end; i++) {
                TbRobRedPackageLog robRedPackageLog = redisUtil.rangeredPackageLog(RedPackageRainConstant.RED_PACKAGE_LOG + i);
                if (!Objects.isNull(robRedPackageLog)) {
                    robRedPackageLogList.add(robRedPackageLog);
                }
            }
            // 处理当前批次的数据（1000次）
            processRobRedPackageLogList(robRedPackageLogList);
            // 清空列表，准备下一批数据的处理
            robRedPackageLogList.clear();
            // 更新起始和结束索引
            start = end + 1;
            end = Math.min(start + BATCH_SIZE - 1, initialCount);
        }
        // 保存红包雨活动相关的数据，通常情况下，根据业务不同，需要保存的数据类型也各不相同，例如用户行为数据、交易数据，这里我只保存红包日志数据，去除掉了我在公司开发的那部分业务逻辑，只保留通用的日志，避免被人告了，哈哈
        // 红包日志不只是可以用来查询每个用户抢了多少红包，还会用于大数据的处理，大数据开发岗会对这些数据进行清洗、整合和预处理，以便为算法模型提供高质量的训练和输入数据。如果公司有一定规模还会构建数据仓库或数据湖，以便团队能够方便地对数据进行查询和分析。
        Boolean result = userService.updateRedPackage(robRedPackageLogList);
        if(!result){
            // 解决 feign调用用户服务导致Seata失效的处理
            throw new RuntimeException("用户服务异常了");
        }

        //TODO 额外的业务逻辑：
        // 异步跑风控规则或者模型：调用算法模型进行风险评估，算法模型会根据用户的行为数据和其他相关信息，给出一个风险评分或标签。
        // 如果风险评分超过预设的阈值，或者标签为异常行为，则Java后端服务会拒绝该该用户后续继续参加抢红包活动并且该用户抢到的红包不能入账，并可能采取进一步的风控措施，如限制用户账户、记录异常违规日志等。
        // 异步汇总红包通知用户服务红包入账，自定义业务逻辑

    }

    //需要DataSourceConfig.java和JdbcTemplateConfig.java的配置
/*    @Autowired
    private JdbcTemplate jdbcTemplate;
    //使用原生的批量插入
    @Transactional
    *//**
     * 只会插入多张表里
     *//*
    public boolean batchInsertTbRobRedPackageLog(List<TbRobRedPackageLog> logs) {
        String sql = "INSERT INTO tb_red_package_log (red_package_id,user_id,activity_id,part_red_package,create_time)  VALUES (?, ?, ?, ?, ?)";
        // 对业务代码有入侵
        DynamicDataSourceContextHolder.setDataSourceType("customShardingDataSource");
        int[] batchUpdate = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                TbRobRedPackageLog tbRobRedPackageLog = logs.get(i);
                ps.setString(1, tbRobRedPackageLog.getRedPackageId());
                ps.setLong(2, tbRobRedPackageLog.getUserId());
                ps.setString(3, tbRobRedPackageLog.getActivityId());
                ps.setString(4, tbRobRedPackageLog.getPartRedPackage());
                // 使用 SimpleDateFormat 来格式化时间
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                String currentTimeStr = dateFormat.format(new java.util.Date());
                Date sqlDate = Date.valueOf(currentTimeStr.substring(0, 10)); // 只取日期部分
                ps.setDate(5, sqlDate);
            }
            @Override
            public int getBatchSize() {
                return logs.size();
            }
        });
        DynamicDataSourceContextHolder.clearDataSourceType();
        return Arrays.stream(batchUpdate).allMatch(count -> count > 0);
    }*/
    /**
     * 批量插入 由分批次批量插入到分库分表历史数据归档到使用原生批量插入优化分库分表插入提升性能
     * @param robRedPackageLogList
     */
    public void processRobRedPackageLogList(List<TbRobRedPackageLog> robRedPackageLogList) {
        if (!CollectionUtils.isEmpty(robRedPackageLogList)) {
            // 将集合拆分，分批次插入
            List<List<TbRobRedPackageLog>> robRedPackageLogPart = Lists.partition(robRedPackageLogList, 50);
            for (List<TbRobRedPackageLog> logs : robRedPackageLogPart) {
/*                //使用原生批量插入
                boolean result = batchInsertTbRobRedPackageLog(logs);
                if (result) {
                    log.info(StringUtil.StringAppend("=============插入库的红包日志成功：", StringUtil.listToString(logs)));
                }else {
                    log.info("=============插入失败");
                }*/
                // 第一种方式，对业务代码有入侵，需要调整改用注解WR
//                DynamicDataSourceContextHolder.setDataSourceType("customShardingDataSource");
                //正常批量插入,第二种方式直接用对应的mapper
//                Integer integer = sRobRedPackageLogMapper.insert(logs.get(0));
                Integer integer = sRobRedPackageLogMapper.batchInsert(logs);
//                DynamicDataSourceContextHolder.clearDataSourceType();
                if (integer > 0) {
                    log.info(StringUtil.StringAppend("=============插入库的红包日志成功：", StringUtil.listToString(logs)));
                }else {
                    log.info("=============插入失败");
                }
            }
        }
    }
}
