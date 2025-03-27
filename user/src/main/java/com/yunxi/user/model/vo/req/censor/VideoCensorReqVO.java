package com.yunxi.user.model.vo.req.censor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;

@ApiModel("视频审核的实体类")
public class VideoCensorReqVO {

    @ApiModelProperty(value = "视频名称")
    @NotNull(message = "name不能为null")
    public String name;

    @ApiModelProperty(value = "视频主URL地址，若主Url无效或抓取失败，则依次抓取备用地址videoUrl2、videoUrl3、videoUrl4，若全部抓取失败则审核失败")
    @NotNull(message = "videoUrl不能为null")
    public String videoUrl;

    @ApiModelProperty(value = "视频在用户平台的唯一ID，方便人工审核结束时数据推送，用户利用此ID唯一锁定一条平台资源，若无可填写视频Url")
    @NotNull(message = "extId不能为null")
    public String extId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getExtId() {
        return extId;
    }

    public void setExtId(String extId) {
        this.extId = extId;
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
