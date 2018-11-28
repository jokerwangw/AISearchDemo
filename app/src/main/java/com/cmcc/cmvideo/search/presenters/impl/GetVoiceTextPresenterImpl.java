package com.cmcc.cmvideo.search.presenters.impl;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.cmcc.cmvideo.base.AbstractPresenter;
import com.cmcc.cmvideo.base.Executor;
import com.cmcc.cmvideo.base.MainThread;
import com.cmcc.cmvideo.search.aiui.AIUIControlService;
import com.cmcc.cmvideo.search.aiui.AIUIService;
import com.cmcc.cmvideo.search.aiui.IAIUIControlService;
import com.cmcc.cmvideo.search.aiui.IAIUIService;
import com.cmcc.cmvideo.search.aiui.Logger;
import com.cmcc.cmvideo.search.aiui.bean.NlpData;
import com.cmcc.cmvideo.search.presenters.GetVoiceTextPresenter;
import com.google.gson.Gson;
import com.iflytek.aiui.AIUIEvent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Yyw on 2018/11/13.
 * Describe:
 */
public class GetVoiceTextPresenterImpl extends AbstractPresenter implements GetVoiceTextPresenter, AIUIControlService.AIUIEventListener {
    private View mView;
    private IAIUIControlService iaiuiControlService = null;

    public GetVoiceTextPresenterImpl(Executor executor, MainThread mainThread, View view, Context context) {
        super(executor, mainThread);
        this.mView = view;
    }

    @Override
    public void resume() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void stop() {
    }

    @Override
    public void destroy() {
        iaiuiControlService.removeAIUIEventListener(this);
    }

    @Override
    public void onError(String message) {
    }

    @Override
    public void setAIUIService(IAIUIControlService service) {
        iaiuiControlService = service;
    }

    @SuppressLint("LongLogTag")
    @Override
    public void onResult(String iatResult, String nlpReslult, String tppResult) {
        Log.d("GetVoiceTextPresenterImpl", "iatResult====" + iatResult);
        if (!TextUtils.isEmpty(nlpReslult)) {
            onNlpResult(nlpReslult);
        }
    }

    @Override
    public void onEvent(AIUIEvent event) {
    }

    @Override
    public void startRecording() {
        if (iaiuiControlService != null) {
            iaiuiControlService.startRecordAudio();
            iaiuiControlService.addAIUIEventListener(this);
        }
    }

    @Override
    public void stopRecording() {
        if (iaiuiControlService != null) {
            iaiuiControlService.stopRecordAudio();
        }
    }

    private void onTppResult(String tppResult) {
        NlpData nlpData = new Gson().fromJson(tppResult, NlpData.class);
        if (null != nlpData && !TextUtils.isEmpty(nlpData.text)) {
            mView.showVoiceText(false, nlpData.text);
        } else {
            mView.showVoiceText(true, "error");
        }
    }

    private void onNlpResult(String nlpReslult) {
        try {
            if (TextUtils.isEmpty(nlpReslult)) {
                mView.showVoiceText(true, "error");
                return;
            }

            int rc = 4;
            String text = "";
            JSONObject jsonObject = new JSONObject(nlpReslult);

            if (jsonObject.has("rc")) {
                rc = jsonObject.optInt("rc");
            }

            if (jsonObject.has("text")) {
                text = jsonObject.optString("text");
            }

            if (0 == rc) {
                if (jsonObject.has("semantic")) {
                    JSONArray jsonArraySemantic = jsonObject.getJSONArray("semantic");
                    if (null == jsonArraySemantic || jsonArraySemantic.length() == 0) {
                        mView.showVoiceText(false, text);
                        return;
                    }

                    String template = "";
                    JSONObject semantic = jsonArraySemantic.getJSONObject(0);
                    if (null != semantic && semantic.has("template") && semantic.has("slots") && null != semantic.getJSONArray("slots")) {
                        template = semantic.optString("template");
                        JSONArray slots = semantic.getJSONArray("slots");

                        for (int i = 0; i < slots.length(); i++) {
                            JSONObject slot = slots.getJSONObject(i);
                            template = template.replaceAll("\\{" + slot.getString("name") + "\\}", slot.getString("normValue"));
                        }

                        mView.showVoiceText(false, template);
                    } else {
                        mView.showVoiceText(false, text);
                    }
                } else {
                    mView.showVoiceText(false, text);
                }
            } else {
                mView.showVoiceText(false, text);
            }
        } catch (Exception e) {
            Logger.error(e);
        }
    }
}
