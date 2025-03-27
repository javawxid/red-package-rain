package com.yunxi.user.model.vo.req.censor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;

@ApiModel("图像审核的实体类")
public class ImgCensorReqVO {

    @ApiModelProperty(value = "必填，和imgUrl二选一" +
            "待审核图像Base64编码字符串，\n" +
            "以图像文件形式请求时必填，\n" +
            "图像要求base64后\n" +
            "大于等于5kb，小于等于4M，\n" +
            "最短边大于等于128像素，\n" +
            "小于等于4096像素，\n" +
            "支持的图片格式：PNG、JPG、JPEG、BMP、GIF（仅对首帧进行审核）、Webp、TIFF")
    public String image;

    @ApiModelProperty(value = "图像URL地址，\n" +
            "以URL形式请求，\n" +
            "图像Url需要做UrlEncode，\n" +
            "图像要求base64后大于等于5kb，\n" +
            "小于等于4M，\n" +
            "最短边大于等于128像素，\n" +
            "小于等于4096像素\n" +
            "支持的图片格式：PNG、JPG、JPEG、BMP、GIF（仅对首帧进行审核）、Webp、TIFF")
    public String imgUrl;

    @ApiModelProperty(value = "图片类型0:静态图片（PNG、JPG、JPEG、BMP、GIF（仅对首帧进行审核）、Webp、TIFF），1:GIF动态图片")
    public String image_type;


    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
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
