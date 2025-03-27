package com.yunxi.user.model.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zhiweiLiao
 * @Description
 * @Date create in 2022/10/28 0028 14:57
 */
@Data
public class ProductLine extends PrintFriendliness{

    @ApiModelProperty("产品编码")
    private String productLineCode;
}
