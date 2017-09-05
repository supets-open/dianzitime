package com.supets.map.led2.baiduaudio.recog;

import android.os.Handler;
import android.os.Message;

import com.supets.map.led2.baiduaudio.IStatus;


public class MessageStatusRecogListener extends StatusRecogListener {
    private Handler handler;

    private long speechEndTime;

    private boolean needTime = true;

    public MessageStatusRecogListener(Handler handler) {
        this.handler = handler;
    }

    @Override
    public void onAsrReady() {
        super.onAsrReady();
        sendStatusMessage("引擎就绪，可以开始说话。");
    }

    @Override
    public void onAsrBegin() {
        super.onAsrBegin();
        sendStatusMessage("检测到用户说话");
    }

    @Override
    public void onAsrEnd() {
        super.onAsrEnd();
        speechEndTime = System.currentTimeMillis();
        sendMessage("检测到用户说话结束");
    }

    @Override
    public void onAsrPartialResult(String[] results, RecogResult recogResult) {
        sendStatusMessage("临时识别结果，结果是“" + results[0] + "”；原始json：" + recogResult.getOrigalJson());
        super.onAsrPartialResult(results, recogResult);
    }

    @Override
    public void onAsrFinalResult(String[] results, RecogResult recogResult) {
        super.onAsrFinalResult(results, recogResult);
        String message =  "识别结束，结果是”" + results[0];
        sendStatusMessage( message + "“；原始json：" + recogResult.getOrigalJson());
        if (speechEndTime > 0){
            long diffTime = System.currentTimeMillis() - speechEndTime;
            message += "。说话结束到识别结束耗时【" + diffTime + "ms】";

        }
        speechEndTime = 0;
        sendMessage(message, status,true);
    }

    @Override
    public void onAsrFinishError(int errorCode, String errorMessage, String descMessage) {
        super.onAsrFinishError(errorCode, errorMessage, descMessage);
        sendStatusMessage("识别错误, 错误码：" + errorCode + "；错误消息:" + errorMessage + "；描述信息：" + descMessage);
        long diffTime = System.currentTimeMillis() - speechEndTime;
        speechEndTime = 0;
        sendMessage("识别错误：" + errorMessage + "说话结束到识别结束耗时【" + diffTime + "ms】",status,true);
        speechEndTime = 0;
    }

    @Override
    public void onAsrOnlineNluResult(String nluResult) {
        super.onAsrOnlineNluResult(nluResult);
        if (!nluResult.isEmpty()){
            sendStatusMessage("原始语义识别结果json：" + nluResult);
        }
    }

    @Override
    public void onAsrFinish(RecogResult recogResult) {
        super.onAsrFinish(recogResult);
        sendStatusMessage("识别一段话结束。如果是长语音的情况会继续识别下段话。");

    }

    /**
     * 长语音识别结束
     */
    @Override
    public void onAsrLongFinish() {
        super.onAsrLongFinish();
        sendStatusMessage("长语音识别结束。");
    }

    @Override
    public void onAsrExit() {
        super.onAsrExit();
        sendStatusMessage("识别引擎结束并空闲中");
    }

    private void sendMessage(String message) {
        sendMessage(message, IStatus.WHAT_MESSAGE_STATUS);
    }

    private void sendStatusMessage(String message) {
        sendMessage(message, status);
    }

    private void sendMessage(String message, int what) {
        sendMessage(message, what, false);
    }


    private void sendMessage(String message, int what, boolean highlight) {
        if (needTime && what != IStatus.STATUS_FINISHED) {
            message += "  ;time=" + System.currentTimeMillis();
        }
        Message msg = Message.obtain();
        msg.what = what;
        msg.arg1 = status;
        if (highlight) {
            msg.arg2 = 1;
        }
        msg.obj = message + "\n";
        handler.sendMessage(msg);
    }
}
