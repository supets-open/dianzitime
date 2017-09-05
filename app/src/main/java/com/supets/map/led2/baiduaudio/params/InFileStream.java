package com.supets.map.led2.baiduaudio.params;

import android.app.Activity;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;

public class InFileStream {

    private static Activity context;

    private static final String TAG = "InFileStream";

    public static void setContext(Activity context) {
        InFileStream.context = context;
    }

    public static InputStream create16kStream() {
        InputStream is = null;
        Log.i(TAG, "cmethod call");
        try {
            is = context.getAssets().open("outfile.pcm");
            Log.i(TAG, "create input stream ok" + is.available());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return is;
    }
}