package com.example.redpackagerain.factory;

import com.example.redpackagerain.entity.RobRedPackageLog;
import com.example.redpackagerain.enums.LogEnum;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LogFactory {

    public final static Map<LogEnum, RobRedPackageLog> maps = new ConcurrentHashMap<>();

    public static RobRedPackageLog getLogContext(LogEnum payStrategy) {
        RobRedPackageLog packageLog = maps.get(payStrategy);
        if(packageLog == null) {
            RobRedPackageLog robRedPackageLog = new RobRedPackageLog();
            maps.put(payStrategy, robRedPackageLog);
            return maps.get(payStrategy);
        }else {
            return packageLog;
        }
    }
    public static RobRedPackageLog getRobRedPackageLog() {
        RobRedPackageLog packageLog = maps.get(LogEnum.ROBREDPACKAGELOG);
        if(packageLog == null) {
            RobRedPackageLog robRedPackageLog = new RobRedPackageLog();
            maps.put(LogEnum.ROBREDPACKAGELOG, robRedPackageLog);
            return maps.get(LogEnum.ROBREDPACKAGELOG);
        }else {
            return packageLog;
        }
    }
}
