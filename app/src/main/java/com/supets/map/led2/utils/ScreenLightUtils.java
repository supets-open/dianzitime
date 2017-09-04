package com.supets.map.led2.utils;

import android.app.Activity;
import android.net.Uri;
import android.provider.Settings;
import android.view.WindowManager;

/**
 * led2
 *
 * @user lihongjiang
 * @description
 * @date 2017/9/4
 * @updatetime 2017/9/4
 */

public class ScreenLightUtils {

    /**
     * 一种是通过WindowManager去设置当前界面的亮度——注意，是当前界面，不是系统的亮度
     * 需要注意其中的context的类型是Activity，不能是Context。
     * 这种方式的特点，是**只在当前设置的界面生效**，离开此界面后，屏幕亮度受亮度自动调节的开关控制。
     * 换句话说，用这种方式设置当前界面的亮度时，会使亮度自动调节失效。
     * 只有离开此界面，亮度自动调节继续生效。这种方式适用某些特殊的，需要高亮显示界面。
     *
     * @param context
     * @param brightness
     */
    public static void setLight(Activity context, int brightness) {
        WindowManager.LayoutParams lp = context.getWindow().getAttributes();
        lp.screenBrightness = (float) brightness * (1f / 255f);
        context.getWindow().setAttributes(lp);
    }

    /**
     * 第二种方式时通过修改系统数据库来设置亮度
     * 这种方式的特点是可以修改系统亮度，即使退出当前界面，设置的亮度值依然生效。
     * 这种方式设置的亮度值受亮度自动调节开关的影响。
     * <p>
     * 即开关关闭时，此值生效；开关关闭时，此值其实并没有什么卵用。
     * 需要注意的时，设置时需要向系统数据库写入数据，因此需要相应的权限才行。
     * <p>
     *
     * @param activity
     * @param brightness
     */
    public static void saveBrightness(Activity activity, int brightness) {
        Uri uri = Settings.System.getUriFor(Settings.System.SCREEN_BRIGHTNESS);
        Settings.System.putInt(activity.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, brightness);
        activity.getContentResolver().notifyChange(uri, null);
    }
}
