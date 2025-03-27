package com.yunxi.user.model.vo.req.followModel;

import com.yunxi.user.model.response.BaseRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel(value = "关注的实体类",description ="关注的注释")
public class FollowReqVO extends BaseRequest {

    @ApiModelProperty(value = "用户Id")
    @NotNull(message = "userId不能为null")
    private String userId;

    @ApiModelProperty(value = "被访问者的用户Id")
    @NotNull(message = "intervieweeId不能为null")
    private String intervieweeId;

    @ApiModelProperty(value = "关注类型")
    @NotNull(message = "type不能为null")
    private String type;
}
