package com.supets.map.led2.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.SeekBar;

import com.supets.map.led2.R;
import com.supets.map.led2.utils.ScreenLightUtils;
import com.supets.map.led2.utils.WakeScreenUtils;
import com.supets.map.led2.view.BcdTimeView2;
import com.supets.map.led2.view.TimeCallBack;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends BaiduSoundActivity implements TimeCallBack {

    private static final int UPDATE_TIME = 0X1100;
    private BcdTimeView2 mTimeLcd;
    private View mTimeLay;
    private ViewGroup mSetLay, mLightLay;
    private View mClose;
    private View mSet;

    private int lightvaule = 128;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WakeScreenUtils.keepScreen(this);
        setContentView(R.layout.activity_main);
        mTimeLcd = findViewById(R.id.lcd1602);
        mTimeLay = findViewById(R.id.clocklay);
        mSetLay = findViewById(R.id.setlay);
        mLightLay = findViewById(R.id.lightlay);
        mClose = findViewById(R.id.close);
        mSet = findViewById(R.id.set);

        mTimeLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int visible = mSetLay.getVisibility() == View.GONE ? View.VISIBLE : View.GONE;

                mSetLay.setVisibility(visible);
                mLightLay.setVisibility(visible);

                if (mSetLay.getVisibility() == View.VISIBLE) {
                    anim(mSetLay, R.anim.toptenter);
                    anim(mLightLay, R.anim.bottomenter);
                } else {
                    anim(mSetLay, R.anim.topexit);
                    anim(mLightLay, R.anim.bottomexit);
                }
            }
        });


        mClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ColorPickerActivity.class);
                startActivity(intent);
            }
        });

        SeekBar seekBar = findViewById(R.id.mSeekBar);
        seekBar.setMax(255);
        seekBar.setProgress(lightvaule);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int vaule, boolean b) {
                lightvaule = vaule;
                ScreenLightUtils.setLight(MainActivity.this, vaule);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        ScreenLightUtils.setLight(MainActivity.this, lightvaule);
    }

    private void anim(View view, int animid) {
        Animation animation = AnimationUtils.loadAnimation(this, animid);
        view.setAnimation(animation);
        animation.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startTimer();
        start();//开启唤醒
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopTimer();
        stop();//停止唤醒
    }

    private Timer mTimer;
    private TimerTask mTimerTask;
    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            if (mTimeLcd != null) {
                mTimeLcd.postInvalidate();
            }
            return false;
        }
    });

    private void startTimer() {
        if (mTimer == null) {
            mTimer = new Timer();
        }

        if (mTimerTask == null) {
            mTimerTask = new TimerTask() {
                @Override
                public void run() {
                    mTimeLcd.updateData(MainActivity.this);
                    sendMessage(UPDATE_TIME);
                }
            };
        }

        if (mTimer != null && mTimerTask != null)
            mTimer.schedule(mTimerTask, 1000, 1000);
    }

    private void stopTimer() {

        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }

        if (mTimerTask != null) {
            mTimerTask.cancel();
            mTimerTask = null;
        }
    }

    public void sendMessage(int id) {
        if (mHandler != null) {
            Message message = Message.obtain(mHandler, id);
            mHandler.sendMessage(message);
        }
    }

    @Override
    public void onTime(boolean wholeDian, boolean am, int time) {

        if (isOpenBaoshi) {

            if (wholeDian) {
                String temp = "";

                if (am && (time == 0)) {
                    temp = "晚上12点整";
                } else if (!am && (time == 0)) {
                    temp = "中午12点整";
                } else if (am && (time < 6)) {
                    // temp = "凌晨" + time + "点整";
                } else if (am && (time < 9)) {
                    if (time == 6) {
                        temp = "早上" + time + "点整";
                    }
                    if (time == 7) {
                        temp = "早上" + time + "点整";
                    }
                    if (time == 8) {
                        temp = "早上" + time + "点整";
                    }

                } else if (am && (time <= 11)) {
                    temp = "上午" + time + "点整";
                } else if (!am && (time < 7)) {
                    temp = "下午" + time + "点整";
                } else if (!am && (time < 9)) {
                    temp = "晚上" + time + "点整";
                } else if (!am && (time <= 11)) {
                    temp = "晚上" + time + "点整";
                }
                playSound(temp);
            }

        }
    }

    boolean isOpenBaoshi = true;

    @Override
    public void autoCmd(String word) {
        super.autoCmd(word);
        if ("增大亮度".equals(word)) {
            lightvaule += 10;
            lightvaule = lightvaule > 255 ? 255 : lightvaule;
            ScreenLightUtils.setLight(this, lightvaule);
            playSound("当前亮度" + lightvaule);
        }
        if ("减小亮度".equals(word)) {
            lightvaule -= 10;
            lightvaule = lightvaule < 0 ? 0 : lightvaule;
            ScreenLightUtils.setLight(this, lightvaule);
            playSound("当前亮度" + lightvaule);
        }

        if ("关闭语音".equals(word)) {
            isOpenBaoshi = false;
            playSound("整点报时关闭");
        }
        if ("语音播放".equals(word)) {
            isOpenBaoshi = true;
            playSound("整点报时打开");
            playSound("现在时间" + mTimeLcd.getData());
        }
    }
}

