package com.yunxi.user.mapper.s;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yunxi.user.model.po.TbRobRedPackageLog;
import org.apache.ibatis.annotations.Param;

import java.util.List;


public interface SRobRedPackageLogMapper extends BaseMapper<TbRobRedPackageLog> {

    Integer batchInsert(@Param("robRedPackageLogList") List<TbRobRedPackageLog> robRedPackageLogList);
    int insert(TbRobRedPackageLog tbRobRedPackageLog);

    List<TbRobRedPackageLog> selectRobRedPackageLog(Long userId);
}
