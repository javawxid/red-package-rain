package com.example.redpackagerain.entity;

import lombok.Data;
import java.util.Date;


@Data
public class RobRedPackageLog {

    private Long id;
    private String redPackageId;
    private String activityId;
    private String userId;
    private String partRedPackage;
    private String messageId;
    private String message;
    private Date createTime;

}
