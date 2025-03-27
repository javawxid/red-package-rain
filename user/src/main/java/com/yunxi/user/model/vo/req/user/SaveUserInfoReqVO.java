package com.yunxi.user.model.vo.req.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.yunxi.user.model.response.BaseRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "用户信息保存或者更新的实体类")
public class SaveUserInfoReqVO extends BaseRequest {

    @ApiModelProperty(value = "用户Id")
    private Integer id;
    @ApiModelProperty(value = "用户昵称")
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private String nickename;
    @ApiModelProperty(value = "用户名")
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private String username;
    @ApiModelProperty(value = "年龄")
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private Integer age;
    @ApiModelProperty(value = "爱好")
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private String hobby;
    @ApiModelProperty(value = "身高")
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private Integer height;
    @ApiModelProperty(value = "体重")
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private Integer weight;
    @ApiModelProperty(value = "地址")
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private String address;
    @ApiModelProperty(value = "最高学历 程序需要校验学历是否合法")
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private String education;
    @ApiModelProperty(value = "收入")
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private String income;
    @ApiModelProperty(value = "行业 需要程序枚举定义每个数值代表的行业")
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private Integer industry;
    @ApiModelProperty(value = "职业")
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private String occupation;
    @ApiModelProperty(value = "自我介绍")
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private String selfIntroduction;
    @ApiModelProperty(value = "籍贯")
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private String nativeplace;
    @ApiModelProperty(value = "照片")
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private String photo;
    @ApiModelProperty(value = "性别")
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private Integer sex;
    @ApiModelProperty(value = "邮箱")
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private String email;
    @ApiModelProperty(value = "婚姻状况 0未婚 1已婚 2离异")
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private Integer maritalStatus;
    @ApiModelProperty(value = "脱单目标")
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private String departuretarget;
    @ApiModelProperty(value = "手机号")
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private String mobile;


}
