package com.yunxi.user.model.vo.req.censor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@ApiModel("文本审核的实体类")
public class TextCensorReqVO {

    @ApiModelProperty(value = "待审核文本字符串")
    @NotNull(message = "text不能为null")
    @Size(min = 1, max = 20000, message = "20000字节（约等于6666个字符数）")
    public String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

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
