package com.cmcc.cmvideo.search.presenters.impl;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import com.cmcc.cmvideo.base.AbstractPresenter;
import com.cmcc.cmvideo.base.Executor;
import com.cmcc.cmvideo.base.MainThread;
import com.cmcc.cmvideo.search.aiui.AIUIService;
import com.cmcc.cmvideo.search.aiui.IAIUIService;
import com.cmcc.cmvideo.search.aiui.bean.NlpData;
import com.cmcc.cmvideo.search.presenters.LookMorePresenter;
import com.google.gson.Gson;
import com.iflytek.aiui.AIUIEvent;

/**
 * Created by Yyw on 2018/6/11.
 * Describe:
 */

public class LookMorePresenterImpl extends AbstractPresenter implements AIUIService.AIUIEventListener, LookMorePresenter {
    private LookMorePresenter.View mView;
    private Context mContext;
    private IAIUIService aiuiService;
    private String speechText = "";
    private Gson gson;

    public LookMorePresenterImpl(Executor executor, MainThread mainThread, LookMorePresenter.View view, Context context) {
        super(executor, mainThread);
        mView = view;
        mContext = context;
        gson = new Gson();
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
        aiuiService.removeAIUIEventListener(this);
    }

    @Override
    public void onError(String message) {
    }

    @Override
    public void setAIUIService(IAIUIService service) {
        aiuiService = service;
        aiuiService.addAIUIEventListener(this);
        if (!TextUtils.isEmpty(speechText)) {
            aiuiService.getLookMorePage(speechText, 1, 40);
        }
    }

    @Override
    public void setSpeechText(String text) {
        speechText = text;
    }

    @Override
    public void onResult(String iatResult, String nlpReslult, String tppResult) {
        onTppResult(tppResult);
    }

    @Override
    public void onEvent(AIUIEvent event) {

    }

    public void onTppResult(String result) {
        if (TextUtils.isEmpty(result) || !aiuiService.isLookMorePageData())
            return;
        NlpData nlpData = gson.fromJson(result, NlpData.class);
        //判断是否解出了语义，并且当前技能是video
        if (nlpData.rc == 4
                || !("video".equals(nlpData.service)
                || "LINGXI2018.user_video".equals(nlpData.service))) {
            if (nlpData.moreResults == null) {
                return;
            }
            nlpData = nlpData.moreResults.get(0);
            if (nlpData.rc == 4
                    || !("video".equals(nlpData.service)
                    || "LINGXI2018.user_video".equals(nlpData.service))) {
                return;
            }
        }
        //语义后处理没有返回数据则直接退出
        if (
                nlpData.data == null
                        || nlpData.data.lxresult == null
                        || nlpData.data.lxresult.data.detailslist.size() == 0) {
            return;
        }
        mView.showInitList(nlpData.data.lxresult.data.detailslist);
    }
}
