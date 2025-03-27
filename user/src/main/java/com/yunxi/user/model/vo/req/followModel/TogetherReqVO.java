package com.yunxi.user.model.vo.req.followModel;

import com.yunxi.user.model.response.BaseRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel(value = "共同关注的人的实体类",description ="共同关注的人的注释")
public class TogetherReqVO extends BaseRequest {

    @ApiModelProperty(value = "用户Id")
    @NotNull(message = "userId不能为null")
    private String userId;

    @ApiModelProperty(value = "被访问者的用户Id")
    @NotNull(message = "intervieweeId不能为null")
    private String intervieweeId;
}
