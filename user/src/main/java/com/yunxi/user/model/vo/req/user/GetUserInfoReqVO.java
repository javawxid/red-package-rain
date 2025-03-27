package com.yunxi.user.model.vo.req.user;

import com.yunxi.user.model.response.BaseRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;

@ApiModel(value = "用户信息展示给页面的实体类",description ="用户实体类的注释")
public class GetUserInfoReqVO extends BaseRequest {

    @ApiModelProperty(value = "用户Id",example = "")
    @NotNull(message = "userId不能为null")
    private String userId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }


}
