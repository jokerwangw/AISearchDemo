package com.cmcc.cmvideo.search.aiui;

import android.os.Bundle;
import android.text.TextUtils;

import com.cmcc.cmvideo.search.model.ChatBean;
import com.cmcc.cmvideo.utils.AiuiConstants;
import com.google.gson.Gson;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SynthesizerListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class NlpHandle implements IHandle, SynthesizerListener {
    private IAIUIService aiuiService;
    private Gson mGson;
    private String service;

    public NlpHandle(IAIUIService service) {
        aiuiService = service;
    }

    public void handle(String nlpHandle) {
        if (TextUtils.isEmpty(nlpHandle)) return;
        try {
            JSONObject jsonObject = new JSONObject(nlpHandle);
            int rc = 0;
            rc = jsonObject.getInt("rc");
            if (rc == 4){
                //播报
                aiuiService.tts(AiuiConstants.ERROR_MESSAGE, this);
                return;
            }

            service = jsonObject.getString("service");


        } catch (JSONException e) {
            e.printStackTrace();
        }
        switch (service) {
            case AiuiConstants.QA_SERVICE:
                //闲聊
                intentQa(nlpHandle);
                break;
            case AiuiConstants.CHANNEL_SERVICE:
                //频道 如想看央视5台

                break;
            case AiuiConstants.QUERY_MIGU:
                //业务查询与办理
                intentQuery(nlpHandle);

                break;


            case AiuiConstants.CONTROL_MIGU:
                //指令控制  如：打开语音助手/投屏播放
                intentControl(nlpHandle);

                break;

        }


    }


    /**
     * 处理查询指令
     * @param nlpHandle
     */
    private void intentQuery(String nlpHandle) {



    }


    /**
     * 处理控制指令
     *
     * @param nlpHandle
     */
    private void intentControl(String nlpHandle) {
        try {
            JSONObject jsonObject = new JSONObject(nlpHandle);
            JSONArray jsonArray = jsonObject.getJSONArray("semantic");
            if (jsonArray == null || jsonArray.isNull(0))
                return;

            JSONObject intentJSONObj = jsonArray.getJSONObject(0);
            JSONArray slotsArray = intentJSONObj.getJSONArray("slots");
            String intent = intentJSONObj.getString("intent");
            switch (intent) {
                case AiuiConstants.CONTROL_INTENT:
                    // TODO: 2018/5/30 控制指令跳转
                    Logger.debug("控制指令intent===" + intent);

                    break;
                case AiuiConstants.SREEN_INTENT:
                    // TODO: 2018/5/30 投屏跳转
                    Logger.debug("投屏指令intent===" + intent);
                    break;
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Logger.debug("解析发生错误");
        }

    }

    /**
     * 处理闲聊技能
     */
    private void intentQa(String nlpHandle) {
        ChatBean chatBean = mGson.fromJson(nlpHandle, ChatBean.class);
        Logger.debug("闲聊数据Q&A ==== " + chatBean.getText() + "-----" + chatBean.getAnswer().getText());
    }

    @Override
    public void onSpeakBegin() {

    }

    @Override
    public void onBufferProgress(int i, int i1, int i2, String s) {

    }

    @Override
    public void onSpeakPaused() {

    }

    @Override
    public void onSpeakResumed() {

    }

    @Override
    public void onSpeakProgress(int i, int i1, int i2) {

    }

    @Override
    public void onCompleted(SpeechError speechError) {

    }

    @Override
    public void onEvent(int i, int i1, int i2, Bundle bundle) {

    }
}
