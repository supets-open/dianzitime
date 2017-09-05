package com.supets.map.led2.activity;

import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.supets.map.led2.R;
import com.supets.map.led2.baiduaudio.params.CommonRecogParams;
import com.supets.map.led2.baiduaudio.params.OfflineRecogParams;

public class BaiduASRUIactivity extends BaiduVoiceRecogActivity {

    protected TextView txtLog;
    protected Button btn;
    protected Button setting;
    protected TextView txtResult;

    public int status = STATUS_NONE;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_yuyin);

        txtResult = findViewById(R.id.txtResult);
        txtLog = findViewById(R.id.txtLog);
        btn = findViewById(R.id.btn);
        setting = findViewById(R.id.setting);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uistart();
            }
        });
    }

    protected void handleMsg(Message msg) {
        switch (msg.what) { // 处理MessageStatusRecogListener中的状态回调
            case STATUS_FINISHED:
                if (msg.arg2 == 1)
                    txtResult.setText(msg.obj.toString());
                //故意不写break
            case STATUS_NONE:
            case STATUS_READY:
            case STATUS_SPEAKING:
            case STATUS_RECOGNITION:
                status = msg.what;
                updateBtnTextByStatus();
                break;
        }
    }

    @Override
    protected CommonRecogParams getApiParams() {
        return new OfflineRecogParams(this);
    }

    private void updateBtnTextByStatus() {
        switch (status) {
            case STATUS_NONE:
                btn.setText("开始录音");
                btn.setEnabled(true);
                break;
            case STATUS_WAITING_READY:
            case STATUS_READY:
            case STATUS_SPEAKING:
            case STATUS_RECOGNITION:
                btn.setText("停止录音");
                btn.setEnabled(true);
                break;

            case STATUS_STOPPED:
                btn.setText("取消整个识别过程");
                btn.setEnabled(true);
                break;
        }
    }

    public void uistart() {
        switch (status) {
            case STATUS_NONE: // 初始状态
                start();
                status = STATUS_WAITING_READY;
                updateBtnTextByStatus();
                txtLog.setText("");
                txtResult.setText("");
                break;
            case STATUS_WAITING_READY: // 调用本类的start方法后，即输入START事件后，等待引擎准备完毕。
            case STATUS_READY: // 引擎准备完毕。
            case STATUS_SPEAKING:
            case STATUS_FINISHED:// 长语音情况
            case STATUS_RECOGNITION:
                stop();
                status = STATUS_STOPPED; // 引擎识别中
                updateBtnTextByStatus();
                break;
            case STATUS_STOPPED: // 引擎识别中
                cancel();
                status = STATUS_NONE; // 识别结束，回到初始状态
                updateBtnTextByStatus();
                break;
        }
    }

}
