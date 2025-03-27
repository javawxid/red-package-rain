package com.yunxi.user.algorithem;

import org.apache.shardingsphere.api.sharding.complex.ComplexKeysShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.complex.ComplexKeysShardingValue;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @Author: liaozhiwei
 * @Description: complex复杂分片策略 多字段的分库分表 表策略
 */
public class RobRedPackageLogComplexTableSharding implements ComplexKeysShardingAlgorithm<String> {
    //   select t.id,t.red_package_id,t.user_id,t.activity_id,t.part_red_package  from tb_red_package_log where  create_time between ? and ? and user_id = ?  or activity_id = ?
    @Override
    public Collection<String> doSharding(Collection<String> availableTargetNames, ComplexKeysShardingValue<String> shardingValue) {
        Map<String, Collection<String>> columnValueMap = shardingValue.getColumnNameAndShardingValuesMap();
        Collection<String> redPackageIds = columnValueMap.get("red_package_id");
        List<String> res = new ArrayList<>();
        for (String redPackageId : redPackageIds) {
            String year = redPackageId.substring(0,4);
            String month = redPackageId.substring(4,6);
            if (Integer.valueOf(month) > 12) {
                month = redPackageId.substring(4,5);
            }
            String redPackageLabStr = redPackageId.substring(6);
            BigInteger redPackageLab = BigInteger.valueOf(Long.valueOf(redPackageLabStr));
            //根据年份、月份和取模结果进行分表
            String tableName = "tb_red_package_log_" + year + "_" + month + "_" + (redPackageLab.mod(new BigInteger("2"))).add(new BigInteger("1"));
            if (availableTargetNames.contains(tableName)) {
                res.add(tableName);
            }
        }
        return res;
    }
}
