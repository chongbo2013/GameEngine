package com.tos.launcher.lockscreen.utils;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

/**
 * 锁屏工具封装
 */
public class LockscreenUtils {


    /**
     * 判断服务是否启动
     * @param context
     * @param className
     * @return
     */
    public static boolean serviceIsRunning(Context context,Class className) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> services = am.getRunningServices(Short.MAX_VALUE);
        for (ActivityManager.RunningServiceInfo info : services) {
            if (info.service.getClassName().endsWith(className.getName())) {
                return true;
            }
        }
        return false;
    }

}