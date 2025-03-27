package com.yunxi.user.model.vo.req.faceAttribute;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;

@ApiModel("人脸编辑的实体类")
public class FaceAttributeEditReqVO {

    @ApiModelProperty(value = "原始图片信息 图片的分辨率要求在256*256以上、在4096*4096以下 大小在4M下 人脸区域要求在64*64以上")
    @NotNull(message = "image不能为null")
    private String image;

    @ApiModelProperty(value = "图片类型\n" +
            "BASE64:图片的base64值;\n" +
            "URL:图片的 URL( 下载图片时可能由于网络等原因导致下载图片时间过长)\n" +
            "FACE_TOKEN: 人脸标识")
    @NotNull(message = "image_type不能为null")
    private String image_type;

    @ApiModelProperty(value = "人脸编辑方式\n" +
            "TO_KID: V1版本变小孩\n" +
            "TO_OLD: V1版本变老人\n" +
            "TO_FEMALE: V1版本变女生\n" +
            "TO_MALE: V1版本变男生\n" +
            "V2_AGE：V2版本年龄变换，选择该项后可通过target参数指定年龄\n" +
            "V2_GENDER： v2版本性别变换，选择该项后需通过target进一步指定要转换的性别")
    @NotNull(message = "action_type不能为null")
    private String action_type;

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

    public String getAction_type() {
        return action_type;
    }

    public void setAction_type(String action_type) {
        this.action_type = action_type;
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
