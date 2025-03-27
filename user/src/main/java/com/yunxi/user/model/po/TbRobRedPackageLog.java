package com.yunxi.user.model.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.sql.Date;

@Data
@TableName("tb_red_package_log")
public class TbRobRedPackageLog {

    @TableId(value = "id",type = IdType.AUTO)
    private Long id;
    private String redPackageId;
    private Long userId;
    private String activityId;
    private String partRedPackage;
    private String message;
    private String messageId;
    private Date createTime;


}
