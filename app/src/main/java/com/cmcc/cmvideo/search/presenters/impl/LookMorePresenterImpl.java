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
import com.cmcc.cmvideo.search.aiui.bean.TppData;
import com.cmcc.cmvideo.search.presenters.LookMorePresenter;
import com.cmcc.cmvideo.util.AiuiConstants;
import com.cmcc.cmvideo.util.T;
import com.google.gson.Gson;
import com.iflytek.aiui.AIUIEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Created by Yyw on 2018/6/11.
 * Describe:
 */

public class LookMorePresenterImpl extends AbstractPresenter implements LookMorePresenter ,AIUIService.AIUIEventListener {
    private LookMorePresenter.View mView;
    private Context mContext;
    private Gson gson;
    private List<TppData.DetailsListBean> LookMoreData = new ArrayList();
    private IAIUIService aiuiService;
    private int pageNum = 1;
    private int pageSize = 15;

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
        this.aiuiService = service;
        aiuiService.addAIUIEventListener(this);
    }

    @Override
    public void loadMore() {
        pageNum++;
        aiuiService.getLookMorePage("查看更多", pageNum, pageSize, false,"");
    }

    @Override
    public void refresh() {
        pageNum = 1;
    }

    @Override
    public void loadData(String pageData) {
        receiveLookMoreData(pageData);
    }

    @Override
    public void onResult(String iatResult, String nlpReslult, String tppResult) {
        if (!TextUtils.isEmpty(tppResult)) {
            onTppResult(tppResult);
        }
    }

    private void onTppResult(String tppResult){
        receiveLookMoreData(tppResult);
    }



    @Override
    public void onEvent(AIUIEvent event) {

    }

    public void receiveLookMoreData(String  moreData) {
        NlpData nlpData = gson.fromJson(moreData, NlpData.class);
        //只有是视频搜索的技能才跳转
        if (AiuiConstants.VIDEO_SERVICE.equals(nlpData.service)
                || (nlpData.moreResults != null && nlpData.moreResults.size() > 0 &&
                AiuiConstants.VIDEO_SERVICE.equals(nlpData.moreResults.get(0).service))
                || AiuiConstants.USER_VIDEO_SERVICE.equals(nlpData.service)) {
            mView.exit();
            return;
        }
        if (null != nlpData.data && null != nlpData.data.lxresult) {
            if (nlpData.data.lxresult.satisfy) {
                LookMoreData.addAll(nlpData.data.lxresult.data.detailslist);
                mView.showInitList(LookMoreData);
            } else {
                mView.noMoreDataWithTip();
                return;
            }
        }
        if (nlpData.data == null
                || nlpData.data.lxresult == null
                || nlpData.data.lxresult.data.detailslist.size() == 0) {

            mView.noMoreData();
            return;
        }
        mView.loadComplate();
    }
}
