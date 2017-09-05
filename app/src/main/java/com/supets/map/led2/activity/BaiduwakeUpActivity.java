package com.supets.map.led2.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.supets.map.led2.baiduaudio.IStatus;
import com.supets.map.led2.baiduaudio.wakeup.IWakeupListener;
import com.supets.map.led2.baiduaudio.wakeup.MyWakeup;
import com.supets.map.led2.baiduaudio.wakeup.SimpleWakeupListener;
import com.supets.map.led2.baiduaudio.wakeup.WakeUpResult;
import com.supets.map.led2.baiduaudio.wakeup.WakeupParams;

import java.util.Map;

public class BaiduwakeUpActivity extends Activity implements IStatus{


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private MyWakeup myWakeup;


    public void init() {
        IWakeupListener listener = new SimpleWakeupListener(){
            @Override
            public void onSuccess(String word, WakeUpResult result) {
                super.onSuccess(word, result);
                Toast.makeText(BaiduwakeUpActivity.this, "唤醒成功"+word, Toast.LENGTH_SHORT).show();
            }



        };
        myWakeup = new MyWakeup(this, listener);
    }

    public void start() {
        WakeupParams wakeupParams = new WakeupParams(this);
        Map<String,Object> params = wakeupParams.fetch();
        myWakeup.start(params);
    }

    public void stop() {
        myWakeup.stop();
    }

    @Override
    protected void onDestroy() {
        release();
        super.onDestroy();
    }

    public void release() {
        myWakeup.release();
    }


}
