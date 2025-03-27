package com.yunxi.user.model.vo.rsp.followModel;

import com.yunxi.user.model.response.BaseRequest;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel(value = "我可能认识的人的实体类",description ="我可能认识的人的注释")
public class MightKnowRspVO extends BaseRequest {

    private String userId;


}
