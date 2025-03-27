package com.yunxi.user.algorithem;

import org.apache.shardingsphere.api.sharding.complex.ComplexKeysShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.complex.ComplexKeysShardingValue;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @Author: liaozhiwei
 * @Description: complex复杂分片策略 多字段的分库分表 库策略
 */
public class RobRedPackageLogComplexDSSharding implements ComplexKeysShardingAlgorithm<Long> {
    // select t.id,t.red_package_id,t.user_id,t.activity_id,t.part_red_package t from tb_red_package_log t where  create_time between ? and ? and user_id = ?  or activity_id = ?
    @Override
    public Collection<String> doSharding(Collection<String> availableTargetNames, ComplexKeysShardingValue<Long> shardingValue) {
        Collection<Long> userIdCol = shardingValue.getColumnNameAndShardingValuesMap().get("user_id");// user_id = ?
        List<String> res = new ArrayList<>();
        for(Long userId: userIdCol){
            BigInteger userIdB = BigInteger.valueOf(userId);
            //实现库策略 tb_red_package_log_{userID%2+1}
            BigInteger target = (userIdB.mod(new BigInteger("2"))).add(new BigInteger("1"));
            res.add("m"+target);
        }
        return res;
    }
}
