package com.yunxi.user.model.vo.req.faceAttribute;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;
@ApiModel("模板图信息实体类，要求被融合的人脸边缘需要与图片边缘保持一定距离的")
public class ImageTemplateReqVO {

    @ApiModelProperty(value = "模板图信息 图片的分辨率要求在1920x1080以下")
    @NotNull(message = "image不能为null")
    private String image;

    @ApiModelProperty(value = "图片类型\n" +
            "BASE64:图片的base64值;\n" +
            "URL:图片的 URL( 下载图片时可能由于网络等原因导致下载图片时间过长)\n" +
            "FACE_TOKEN: 人脸标识")
    @NotNull(message = "image_type不能为null")
    private String image_type;

    @ApiModelProperty(value = "质量控制\n" +
            "NONE: 不进行控制\n" +
            "LOW:较低的质量要求 NORMAL: 一般的质量要求\n" +
            "HIGH: 较高的质量要求\n" +
            "默认NONE")
    private String quality_control;


    @ApiModelProperty(value = "指定模板图中进行人脸融合的人脸框位置 不指定时则默认使用最大的人脸\n" +
            "格式形如: {\\\"left\\\": 111.4,\\\"top\\\": 96.56,\\\"width\\\": 98,\\\"height\\\": 98,\\\"rotation\\\": 3}\n" +
            "当image_type为FACE_TOKEN时, 此参数无效, 会使用FACE_TOKEN对应的人脸")
    private String face_location;

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

    public String getQuality_control() {
        return quality_control;
    }

    public void setQuality_control(String quality_control) {
        this.quality_control = quality_control;
    }

    public String getFace_location() {
        return face_location;
    }

    public void setFace_location(String face_location) {
        this.face_location = face_location;
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
