package com.yunxi.user.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @Author: liaozhiwei
 * @Description: TODO
 * @Date: Created in 21:19 2022/8/22
 */
@Component
@FeignClient(value = "red-packet")
public interface RedPacketredPacketFeign {

    @GetMapping("/grabRedPacket")
    Boolean grabRedPacket();
}
