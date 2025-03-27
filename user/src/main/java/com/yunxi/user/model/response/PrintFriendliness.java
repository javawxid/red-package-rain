package com.yunxi.user.model.response;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

import java.io.Serializable;

/**
 * @author zhiweiLiao
 * @Description 友好打印
 * @Date create in 2022/10/28 0028 14:56
 */
public class PrintFriendliness implements Serializable {

    private static final long serialVersionUID = -9140385409591586152L;

    public String toString() {
        return String.format("%s:%s", this.getClass().getSimpleName(), JSON.toJSONString(this, new SerializerFeature[]{SerializerFeature.WriteDateUseDateFormat, SerializerFeature.SkipTransientField}));
    }

}
