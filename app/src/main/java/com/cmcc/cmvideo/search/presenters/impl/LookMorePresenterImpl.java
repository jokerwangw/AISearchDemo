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

public class LookMorePresenterImpl extends AbstractPresenter implements LookMorePresenter {
    private LookMorePresenter.View mView;
    private Context mContext;
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
    }

    @Override
    public void onError(String message) {
    }

    @Override
    public void setDetailsJson(String text) {
        onTppResult(text);
    }


    public void onTppResult(String result) {
        if (TextUtils.isEmpty(result)){
            return;
        }

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
        mView.showInitList(blurDetailsList(nlpData.data.lxresult.data.detailslist));
    }
    //前三个 保持不变后面的数据随机排序，产品需求 为了造成每次查看更多好像都换新的数据的假象
    private List<TppData.DetailsListBean> blurDetailsList(List<TppData.DetailsListBean> detailslist){
        if(detailslist ==null||detailslist.size()<4){
            return detailslist;
        }

        List<TppData.DetailsListBean> headList =  detailslist.subList(0,3);
        List<TppData.DetailsListBean> blurList = detailslist.subList(3,detailslist.size());
        List<TppData.DetailsListBean> sourceList = new ArrayList<>();
        int count = blurList.size();
        Random random  =  new Random();
        for(int i=0;i<count;i++){
            int index = random.nextInt(count);
            Collections.swap(blurList,0,index);
        }
        sourceList.addAll(headList);
        sourceList.addAll(blurList);
        return sourceList;
    }
}
