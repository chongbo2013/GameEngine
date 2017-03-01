package com.tos.launcher.lockscreen;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import com.tos.launcher.lockscreen.utils.LockAccessibilityService;
import com.tos.launcher.lockscreen.utils.LockscreenService;
import com.tos.launcher.lockscreen.utils.LockscreenUtils;

import java.util.List;

/**
 * 锁屏设置主入口
 * Created by xff on 2017/2/28.
 */

public class MainActivity extends Activity {


    /**
     * 前往设置界面开启服务
     */
    private void startAccessibilityService() {
        new AlertDialog.Builder(this)
                .setTitle("开启辅助功能")
                .setIcon(R.mipmap.ic_launcher)
                .setMessage("使用此项功能需要您开启辅助功能")
                .setPositiveButton("立即开启", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 隐式调用系统设置界面
                        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                        startActivity(intent);
                    }
                }).create().show();
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //启动辅助服务
        if (LockscreenUtils.serviceIsRunning(getApplicationContext(), LockAccessibilityService.class)) {
            Toast.makeText(getApplicationContext(), "LockAccessibilityService服务已经在运行！", Toast.LENGTH_SHORT).show();
        } else {
            startAccessibilityService();
        }
        //启动锁屏服务
        if (LockscreenUtils.serviceIsRunning(getApplicationContext(), LockscreenService.class)) {
            Toast.makeText(getApplicationContext(), "LockscreenService服务已经在运行！", Toast.LENGTH_SHORT).show();
        } else {
            // start service for observing intents
            startService(new Intent(getApplicationContext(), LockscreenService.class));
        }
    }


}
