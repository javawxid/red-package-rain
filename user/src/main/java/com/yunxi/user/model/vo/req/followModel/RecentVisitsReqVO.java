package com.yunxi.user.model.vo.req.followModel;

import com.yunxi.user.model.response.BaseRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel(value = "近期访问的实体类",description ="近期访问的注释")
public class RecentVisitsReqVO extends BaseRequest {

    @ApiModelProperty(value = "用户Id")
    @NotNull(message = "userId不能为null")
    private String userId;

}
