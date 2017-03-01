package com.tos.launcher.lockscreen.utils;
import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.text.TextUtils;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Toast;

/**
 * 保证锁屏服务类
 * Created by xff on 2017/3/1.
 */

public class LockAccessibilityService extends AccessibilityService {

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        AccessibilityServiceInfo serviceInfo =getServiceInfo();
        if(serviceInfo!=null){
            String launcherName=getLauncherPackageName(getApplicationContext());
            if(!TextUtils.isEmpty(launcherName)) {
                serviceInfo.packageNames = new String[]{launcherName};
                Toast.makeText(getApplicationContext(), "launcherName:" + launcherName, Toast.LENGTH_SHORT).show();
                setServiceInfo(serviceInfo);
            }
        }
    }

    public static String getLauncherPackageName(Context context) {
        final Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        final ResolveInfo res = context.getPackageManager().resolveActivity(intent, 0);
        if (res.activityInfo == null) {
            // should not happen. A home is always installed, isn't it?
            return null;
        }
        if (res.activityInfo.packageName.equals("android")) {
            // 有多个桌面程序存在，且未指定默认项时；
            return null;
        } else {
            return res.activityInfo.packageName;
        }
    }


    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        switch (event.getEventType()){
            case AccessibilityEvent.TYPE_VIEW_CLICKED:
                Toast.makeText(getApplicationContext(),"TYPE_VIEW_CLICKED",Toast.LENGTH_SHORT).show();
                startLockScreenService();
                break;
            case AccessibilityEvent.TYPE_VIEW_FOCUSED:
                Toast.makeText(getApplicationContext(),"TYPE_VIEW_FOCUSED",Toast.LENGTH_SHORT).show();
                startLockScreenService();
            break;
            case AccessibilityEvent.TYPE_VIEW_SCROLLED:
                Toast.makeText(getApplicationContext(),"TYPE_VIEW_SCROLLED",Toast.LENGTH_SHORT).show();
                startLockScreenService();
                break;
        }

    }

    private void startLockScreenService(){
        if (LockscreenUtils.serviceIsRunning(getApplicationContext(), LockscreenService.class)) {
            Toast.makeText(getApplicationContext(), "LockscreenService服务已经在运行！", Toast.LENGTH_SHORT).show();
        } else {
            // start service for observing intents
            startService(new Intent(getApplicationContext(), LockscreenService.class));
        }
    }

    @Override
    public void onInterrupt() {

    }
}
