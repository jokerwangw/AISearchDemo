package com.cmcc.cmvideo.search.presenters.impl;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.cmcc.cmvideo.base.AbstractPresenter;
import com.cmcc.cmvideo.base.Executor;
import com.cmcc.cmvideo.base.MainThread;
import com.cmcc.cmvideo.search.aiui.AIUIService;
import com.cmcc.cmvideo.search.aiui.IAIUIService;
import com.cmcc.cmvideo.search.aiui.bean.NlpData;
import com.cmcc.cmvideo.search.presenters.GetVoiceTextPresenter;
import com.google.gson.Gson;
import com.iflytek.aiui.AIUIEvent;

/**
 * Created by Yyw on 2018/11/13.
 * Describe:
 */
public class GetVoiceTextPresenterImpl extends AbstractPresenter implements GetVoiceTextPresenter, AIUIService.AIUIEventListener {
    private View mView;
    private IAIUIService aiuiService = null;
    private boolean isFirst = false;

    public GetVoiceTextPresenterImpl(Executor executor, MainThread mainThread, View view, Context context) {
        super(executor, mainThread);
        this.mView = view;
    }

    @Override
    public void resume() {
        if (aiuiService != null) {
            aiuiService.resetLastNlp();
        }
    }

    @Override
    public void pause() {
    }

    @Override
    public void stop() {
    }

    @Override
    public void destroy() {
        aiuiService.setAttached(false);
        aiuiService.removeAIUIEventListener(this);
    }

    @Override
    public void onError(String message) {
    }

    @Override
    public void setAIUIService(IAIUIService service) {
        aiuiService = service;
        aiuiService.setAttached(true);
        aiuiService.addAIUIEventListener(this);
        //暂时就这么做吧
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                //清理所见即可说的数据
                aiuiService.clearSpeakableData();
            }
        }, 500);
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
        if (aiuiService != null) {
            isFirst = true;
            aiuiService.startRecordAudio();
        }
    }

    @Override
    public void stopRecording() {
        if (aiuiService != null) {
            aiuiService.stopRecordAudio();
        }
    }

    private void onTppResult(String tppResult) {
        NlpData nlpData = new Gson().fromJson(tppResult, NlpData.class);
        if (null != nlpData) {
            mView.showVoiceText(false, nlpData.text);
        } else {
            mView.showVoiceText(true, "error");
        }
    }
}
