package com.yunxi.user.model.vo.req.faceAttribute;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;

@ApiModel("人脸融合的实体类")
public class FaceMergeReqVO {

    @ApiModelProperty(value = "模板图信息，要求被融合的人脸边缘需要与图片边缘保持一定距离")
    @NotNull(message = "image_template不能为null")
    private ImageTemplateReqVO image_template;

    @ApiModelProperty(value = "目标图信息，要求图片为清晰正脸")
    @NotNull(message = "image_target不能为null")
    private imageTargetReqVO image_target;

    @ApiModelProperty(value = "融合度 关系到融合图与目标图的相似度 越高则越相似\n" +
            "LOW:较低的融合度\n" +
            "NORMAL: 一般的融合度\n" +
            "HIGH: 较高的融合度\n" +
            "COMPLETE: 完全融合\n" +
            "默认COMPLETE")
    private String merge_degree;

    public ImageTemplateReqVO getImage_template() {
        return image_template;
    }

    public void setImage_template(ImageTemplateReqVO image_template) {
        this.image_template = image_template;
    }

    public imageTargetReqVO getImage_target() {
        return image_target;
    }

    public void setImage_target(imageTargetReqVO image_target) {
        this.image_target = image_target;
    }

    public String getMerge_degree() {
        return merge_degree;
    }

    public void setMerge_degree(String merge_degree) {
        this.merge_degree = merge_degree;
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
