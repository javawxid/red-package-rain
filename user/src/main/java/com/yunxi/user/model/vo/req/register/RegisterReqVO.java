package com.yunxi.user.model.vo.req.register;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel("用户注册的实体类")
public class RegisterReqVO {

    @ApiModelProperty(value = "用户名")
    @NotNull(message = "username不能为null")
    private String username;

    @ApiModelProperty(value = "用户密码")
    @NotNull(message = "password不能为null")
    private String password;


    /**
     * 自身内容能以可读方式输出
     * @return
     */
    @Override
    public String toString() {
        return this.getClass().getSimpleName() + ":"
                + JSON.toJSONString(this, SerializerFeature.WriteDateUseDateFormat, SerializerFeature.SkipTransientField);
    }
}
