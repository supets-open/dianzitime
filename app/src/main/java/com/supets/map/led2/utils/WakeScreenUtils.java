package com.supets.map.led2.utils;

import android.app.Activity;
import android.content.Context;
import android.os.PowerManager;
import android.view.WindowManager;

public class WakeScreenUtils {

    private PowerManager.WakeLock wakeLock;

    public void stop() {
        if (wakeLock != null) {
            wakeLock.release();
            wakeLock = null;
        }
    }

    //加权限：<uses-permission android:name="android.permission.WAKE_LOCK"/>
    public void keep(Context mContext) {
        PowerManager pm = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "TAG");
        wakeLock.acquire();
    }

    public static void keepScreen(Activity activity){
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

}
