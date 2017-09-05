package com.supets.map.led2.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

import com.supets.map.led2.baiduaudio.recog.IRecogListener;
import com.supets.map.led2.baiduaudio.IStatus;
import com.supets.map.led2.baiduaudio.recog.MessageStatusRecogListener;
import com.supets.map.led2.baiduaudio.recog.MyRecognizer;
import com.supets.map.led2.baiduaudio.params.CommonRecogParams;
import com.supets.map.led2.baiduaudio.params.OfflineRecogParams;

import java.util.Map;

public abstract class BaiduVoiceRecogActivity extends Activity implements IStatus {

    private MyRecognizer myRecognizer;
    boolean enableoffline = false;
    private Handler handler;

    protected CommonRecogParams apiParams;//仅仅用于生成调用START的json字符串

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initRecog();
    }

    public void initRecog() {
        handler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                handleMsg(msg);
            }
        };
        IRecogListener listener = new MessageStatusRecogListener(handler);
        myRecognizer = new MyRecognizer(this, listener);
        apiParams = getApiParams();
        if (enableoffline) {
            myRecognizer.loadOfflineEngine(OfflineRecogParams.fetchOfflineParams());
        }
    }

    protected abstract void handleMsg(Message msg);

    protected abstract CommonRecogParams getApiParams();

    public void start() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        Map<String, Object> params = apiParams.fetch(sp);
        myRecognizer.start(params);
    }

    public void stop() {
        myRecognizer.stop(); // 发送停止录音事件，提前结束录音等待识别结果
    }

    public void cancel() {
        myRecognizer.cancel(); // 取消本次识别，取消后将立即停止不会返回识别结果
    }

    public void release() {
        myRecognizer.release(); // 含有离线引擎卸载
    }

    @Override
    protected void onDestroy() {
        release();
        super.onDestroy();
    }
}
