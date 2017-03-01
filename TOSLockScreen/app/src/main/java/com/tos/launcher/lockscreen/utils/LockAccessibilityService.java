package com.tos.launcher.lockscreen.utils;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Toast;

/**
 * Created by xff on 2017/3/1.
 */

public class LockAccessibilityService extends AccessibilityService {

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();

    }

    /**
     * 页面变化回调事件
     * @param event event.getEventType() 当前事件的类型;
     *              event.getClassName() 当前类的名称;
     *              event.getSource() 当前页面中的节点信息；
     *              event.getPackageName() 事件源所在的包名
     */
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        Toast.makeText(getApplicationContext(),"event:"+event.getPackageName()+","+event.getEventType(),Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onInterrupt() {

    }
}
