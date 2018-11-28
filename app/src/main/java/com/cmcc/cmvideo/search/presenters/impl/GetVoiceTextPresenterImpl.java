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
import com.cmcc.cmvideo.search.aiui.bean.NlpData;
import com.cmcc.cmvideo.search.presenters.GetVoiceTextPresenter;
import com.google.gson.Gson;
import com.iflytek.aiui.AIUIEvent;

/**
 * Created by Yyw on 2018/11/13.
 * Describe:
 */
public class GetVoiceTextPresenterImpl extends AbstractPresenter implements GetVoiceTextPresenter, AIUIControlService.AIUIEventListener {
    private View mView;
    private IAIUIControlService iaiuiControlService = null;
    private boolean isFirst = false;

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
        if (!TextUtils.isEmpty(tppResult)) {
            onTppResult(tppResult);
        }
    }

    @Override
    public void onEvent(AIUIEvent event) {
    }

    @Override
    public void startRecording() {
        if (iaiuiControlService != null) {
            isFirst = true;
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
}
