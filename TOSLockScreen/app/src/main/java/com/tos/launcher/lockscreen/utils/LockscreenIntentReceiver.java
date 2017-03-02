package com.tos.launcher.lockscreen.utils;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.tos.launcher.lockscreen.LockScreen3dActivity;
import com.tos.launcher.lockscreen.MainActivity;


public class LockscreenIntentReceiver extends BroadcastReceiver {

    // Handle actions and display Lockscreen
    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)
                || intent.getAction().equals(Intent.ACTION_SCREEN_ON)
                || intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            start_lockscreen(context);
        }

    }

    // Display lock screen
    private void start_lockscreen(Context context) {
        Intent mIntent = new Intent("com.tos.screenlock");
        mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(mIntent);
    }

}