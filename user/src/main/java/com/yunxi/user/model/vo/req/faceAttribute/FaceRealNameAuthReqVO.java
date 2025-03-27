package com.yunxi.user.model.vo.req.faceAttribute;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;

@ApiModel("人脸实名认证的实体类")
public class FaceRealNameAuthReqVO {

    @ApiModelProperty(value = "APP端类型，配合采集SDK使用时须传入\n" +
            "ios：iOS端采集SDK\n" +
            "android：安卓端采集SDK")
    private String app;


    @ApiModelProperty(value = "SDK安全级别，配合采集SDK使用时须传入，默认common\n" +
            "common : 配合4.1/4.1.5版本采集SDK，人脸图片未进行加密处理\n" +
            "lite: 配合5.2版本SDK")
    private String sec_level;


    @ApiModelProperty(value = "使用5.2版本SDK请求时必填\n" +
            "skey：从SDK获取的密钥信息skey")
    private String skey;


    @ApiModelProperty(value = "使用5.2版本SDK请求时必填\n" +
            "deviceId：从SDK 获取的密钥信息deviceId")
    private String x_device_id;

    @ApiModelProperty(value = "使用5.2版本SDK请求时必填，\n" +
            "SDK输出的加密数据")
    private String data;


    @ApiModelProperty(value = "身份证件号")
    @NotNull(message = "id_card_number不能为null")
    private String id_card_number;

    @ApiModelProperty(value = "姓名(需要是 utf8 编码)")
    @NotNull(message = "name不能为null")
    private String name;

    @ApiModelProperty(value = "活体控制参数\n" +
            "NONE: 不进行控制\n" +
            "LOW:较低的活体要求(高通过率 低攻击拒绝率)\n" +
            "NORMAL: 一般的活体要求(平衡的攻击拒绝率, 通过率)\n" +
            "HIGH: 较高的活体要求(高攻击拒绝率 低通过率)\n" +
            "默认为NONE")
    private String liveness_control;

    @ApiModelProperty(value = "合成图控制参数\n" +
            "NONE: 不进行控制\n" +
            "LOW:较低的合成图阈值数值，由于合成图判定逻辑为大于阈值视为合成图攻击，该项代表低通过率、高攻击拒绝率\n" +
            "NORMAL: 一般的合成图阈值数值，由于合成图判定逻辑为大于阈值视为合成图攻击，该项代表平衡的攻击拒绝率, 通过率\n" +
            "HIGH: 较高的合成图阈值数值，由于合成图判定逻辑为大于阈值视为合成图攻击，该项代表高通过率、低攻击拒绝率)\n" +
            "默认为NONE")
    private String spoofing_control;

    @ApiModelProperty(value = "质量控制参数\n" +
            "NONE: 不进行控制\n" +
            "LOW:较低的质量要求\n" +
            "NORMAL: 一般的质量要求\n" +
            "HIGH: 较高的质量要求\n" +
            "默认为NONE")
    private String quality_control;

    @ApiModelProperty(value = "图片信息(数据大小应小于10M 分辨率应小于1920*1080)，5.2版本SDK请求时已包含在加密数据data中，无需额外传入")
    @NotNull(message = "image不能为null")
    private String image;

    @ApiModelProperty(value = "图片类型\n" +
            "BASE64 : 图片的base64值\n" +
            "URL : 图片的 URL\n" +
            "FACE_TOKEN : 人脸标识\n" +
            "默认 BASE64")
    private String image_type;


    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public String getSec_level() {
        return sec_level;
    }

    public void setSec_level(String sec_level) {
        this.sec_level = sec_level;
    }

    public String getSkey() {
        return skey;
    }

    public void setSkey(String skey) {
        this.skey = skey;
    }

    public String getX_device_id() {
        return x_device_id;
    }

    public void setX_device_id(String x_device_id) {
        this.x_device_id = x_device_id;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getId_card_number() {
        return id_card_number;
    }

    public void setId_card_number(String id_card_number) {
        this.id_card_number = id_card_number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLiveness_control() {
        return liveness_control;
    }

    public void setLiveness_control(String liveness_control) {
        this.liveness_control = liveness_control;
    }

    public String getSpoofing_control() {
        return spoofing_control;
    }

    public void setSpoofing_control(String spoofing_control) {
        this.spoofing_control = spoofing_control;
    }

    public String getQuality_control() {
        return quality_control;
    }

    public void setQuality_control(String quality_control) {
        this.quality_control = quality_control;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getImage_type() {
        return image_type;
    }

    public void setImage_type(String image_type) {
        this.image_type = image_type;
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
