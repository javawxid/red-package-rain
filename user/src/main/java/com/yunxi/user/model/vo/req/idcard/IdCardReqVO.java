package com.yunxi.user.model.vo.req.idcard;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;

@ApiModel("人脸融合的实体类")
public class IdCardReqVO {

    @ApiModelProperty(value = "必填，和url二选一；图像数据，base64编码后进行urlencode，要求base64编码和urlencode后大小不超过4M，最短边至少15px，最长边最大4096px,支持jpg/jpeg/png/bmp格式")
    private String image;
    @ApiModelProperty(value = "必填，和image二选一；图片完整URL，URL长度不超过1024字节，URL对应的图片base64编码后大小不超过4M，最短边至少15px，最长边最大4096px,支持jpg/jpeg/png/bmp格式，当image字段存在时url字段失效请注意关闭URL防盗链")
    private String url;

    @ApiModelProperty(value = "front/back;-front：身份证含照片的一面 -back：身份证带国徽的一面 自动检测身份证正反面，如果传参指定方向与图片相反，支持正常识别，返回参数image_status字段为\"reversed_side\"")
    @NotNull(message = "id_card_side不能为null")
    private String id_card_side;

    @ApiModelProperty(value = "是否开启身份证风险类型(身份证复印件、临时身份证、身份证翻拍、修改过的身份证)检测功能，默认不开启，即：false。\n" +
            "- true：开启，请查看返回参数risk_type；\n" +
            "- false：不开启")
    private String detect_risk;

    @ApiModelProperty(value = "是否开启身份证质量类型(边框/四角不完整、头像或关键字段被遮挡/马赛克)检测功能，默认不开启，即：false。\n" +
            "- true：开启，请查看返回参数card_quality；\n" +
            "- false：不开启")
    private String detect_quality;

    @ApiModelProperty(value = "是否检测头像内容，默认不检测。可选值：true-检测头像并返回头像的 base64 编码及位置信息")
    private String detect_photo;


    @ApiModelProperty(value = "是否检测身份证进行裁剪，默认不检测。可选值：true-检测身份证并返回证照的 base64 编码及位置信息")
    private String detect_card;

    @ApiModelProperty(value = " \t- false：默认值不进行图像方向自动矫正\n" +
            "- true: 开启图像方向自动矫正功能，可对旋转 90/180/270 度的图片进行自动矫正并识别")
    private String detect_direction;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getId_card_side() {
        return id_card_side;
    }

    public void setId_card_side(String id_card_side) {
        this.id_card_side = id_card_side;
    }

    public String getDetect_risk() {
        return detect_risk;
    }

    public void setDetect_risk(String detect_risk) {
        this.detect_risk = detect_risk;
    }

    public String getDetect_quality() {
        return detect_quality;
    }

    public void setDetect_quality(String detect_quality) {
        this.detect_quality = detect_quality;
    }

    public String getDetect_photo() {
        return detect_photo;
    }

    public void setDetect_photo(String detect_photo) {
        this.detect_photo = detect_photo;
    }

    public String getDetect_card() {
        return detect_card;
    }

    public void setDetect_card(String detect_card) {
        this.detect_card = detect_card;
    }

    public String getDetect_direction() {
        return detect_direction;
    }

    public void setDetect_direction(String detect_direction) {
        this.detect_direction = detect_direction;
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
