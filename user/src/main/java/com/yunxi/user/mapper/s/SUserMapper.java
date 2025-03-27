package com.yunxi.user.mapper.s;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yunxi.user.model.po.TbUser;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SUserMapper extends BaseMapper<TbUser> {

    Integer batchInsert(@Param("userList") List<TbUser> userList);

    List<TbUser> selectListLimit(@Param("offset") Long offset,@Param("pageSize") Long pageSize);

    TbUser selectUserByUserName(@Param("username") String username);
}
