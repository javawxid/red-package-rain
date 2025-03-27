package com.yunxi.user.model.vo.req.user;

import com.yunxi.user.model.response.BaseRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import javax.validation.constraints.NotNull;

@Data
@ApiModel(value = "用户信息展示给页面的实体类",description ="用户实体类的注释")
public class GetFollowListReqVO extends BaseRequest {

    @ApiModelProperty(value = "用户Id")
    @NotNull(message = "userId不能为null")
    private String userId;

    @ApiModelProperty(value = "类型")
    @NotNull(message = "type不能为null")
    private String type;


}
