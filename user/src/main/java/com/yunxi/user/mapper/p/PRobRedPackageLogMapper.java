package com.yunxi.user.mapper.p;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yunxi.user.model.po.TbRobRedPackageLog;
import org.apache.ibatis.annotations.Param;

import java.util.List;


public interface PRobRedPackageLogMapper extends BaseMapper<TbRobRedPackageLog> {

    Integer batchInsert(@Param("robRedPackageLogList") List<TbRobRedPackageLog> robRedPackageLogList);
}
