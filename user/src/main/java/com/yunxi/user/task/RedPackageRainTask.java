package com.yunxi.user.task;

import com.yunxi.user.service.RobRedPackageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RedPackageRainTask {

    @Autowired
    RobRedPackageService robRedPackageService;

    // 添加定时任务
    @Scheduled(fixedRate =3000)  // cron表达式
    public void doUploadFileTask(){
        robRedPackageService.saveRedPackageLog();
    }
}
