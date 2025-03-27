package com.yunxi.user.model.vo.req.censor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;

@ApiModel("音频审核的实体类")
public class VoiceCensorReqVO {

    @ApiModelProperty(value = "音频文件的url地址")
    public String url;

    @ApiModelProperty(value = "音频文件的base64编码，与url二选一，若都有按base64调用")
    public String base64;

    @ApiModelProperty(value = "是否返回音频识别结果 true:是;false:否 默认为true")
    public Boolean rawText;

    @ApiModelProperty(value = "rawText是否拆句，true:拆句;false:不拆句返回整段文本 默认为true")
    public String split;

    @ApiModelProperty(value = "用户信息标识，限长64位字符长度")
    public String account;

    @ApiModelProperty(value = "音频信息标识，限长128位字符长度")
    public String audioId;

    @ApiModelProperty(value = "音频文件的格式，pcm、wav、amr、m4a，推荐pcm格式")
    @NotNull(message = "fmt不能为null")
    public String fmt;

    @ApiModelProperty(value = "音频采样率[16000] ]")
    @NotNull(message = "rate不能为null")
    public String rate;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getBase64() {
        return base64;
    }

    public void setBase64(String base64) {
        this.base64 = base64;
    }

    public Boolean getRawText() {
        return rawText;
    }

    public void setRawText(Boolean rawText) {
        this.rawText = rawText;
    }

    public String getSplit() {
        return split;
    }

    public void setSplit(String split) {
        this.split = split;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getAudioId() {
        return audioId;
    }

    public void setAudioId(String audioId) {
        this.audioId = audioId;
    }

    public String getFmt() {
        return fmt;
    }

    public void setFmt(String fmt) {
        this.fmt = fmt;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
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
