package com.supets.map.led2.storge;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.supets.map.led2.R;
import com.supets.map.led2.utils.App;

public class TimeConfig extends BasePref {

    private static final String TIME_COLOR = "TIME_COLOR";

    private static final String Name = "timeconfig";

    public static int getTimeColor() {
        SharedPreferences preferences = getPref(Name);
        int defaultVaule = App.INSTANCE.getResources().getColor(R.color.bcd_color);
        return preferences.getInt(TIME_COLOR, defaultVaule);
    }

    public static void setTimeColor(int colorvaule) {
        Editor editor = edit(Name);
        editor = editor.putInt(TIME_COLOR, colorvaule);
        editor.commit();
    }

    public static void clear() {
        BasePref.clear(Name);
    }

}
