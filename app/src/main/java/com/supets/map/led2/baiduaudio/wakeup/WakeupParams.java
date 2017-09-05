package com.supets.map.led2.baiduaudio.wakeup;

import android.content.Context;

import com.baidu.speech.asr.SpeechConstant;

import java.util.HashMap;
import java.util.Map;

public class WakeupParams {

    private final String TAG = "WakeupParams";
    private Context context;

    public WakeupParams(Context context) {
        this.context = context;
    }

    public Map<String, Object> fetch() {
        Map<String, Object> params = new HashMap<>();
        params.put(SpeechConstant.WP_WORDS_FILE, "assets:///WakeUp.bin");

        params.put(SpeechConstant.ACCEPT_AUDIO_DATA, true);
        params.put(SpeechConstant.ACCEPT_AUDIO_VOLUME, true);
        //params.put(SpeechConstant.IN_FILE, "assets:///test/16k_test.pcm");
        return params;
    }
}
