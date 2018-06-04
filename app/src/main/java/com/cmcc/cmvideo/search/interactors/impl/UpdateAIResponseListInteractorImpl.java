package com.cmcc.cmvideo.search.interactors.impl;

import com.cmcc.cmvideo.base.AbstractInteractor;
import com.cmcc.cmvideo.base.BaseObject;
import com.cmcc.cmvideo.base.Executor;
import com.cmcc.cmvideo.base.MainThread;
import com.cmcc.cmvideo.search.interactors.UpdateAIResponseListInteractor;
import com.cmcc.cmvideo.search.model.SearchByAIBean;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.cmcc.cmvideo.util.Constants.MESSAGE_FROM_AI;
import static com.cmcc.cmvideo.util.Constants.MESSAGE_TYPE_NORMAL;

/**
 * Created by Yyw on 2018/5/22.
 * Describe:
 */

public class UpdateAIResponseListInteractorImpl extends AbstractInteractor implements UpdateAIResponseListInteractor {
    private String order;
    private Callback mCallback;

    public UpdateAIResponseListInteractorImpl(Executor threadExecutor, MainThread mainThread, String order, UpdateAIResponseListInteractor.Callback callback) {
        super(threadExecutor, mainThread);
        this.order = order;
        this.mCallback = callback;
    }

    @Override
    public void dataObjectChanged(BaseObject dataObject, int what) {
    }

    @Override
    public void dataObjectFailed(BaseObject dataObject, int what, JSONObject errorMsg) {
    }

    @Override
    public void run() {
        final List<SearchByAIBean> responseList = new ArrayList<SearchByAIBean>();
        responseList.add(new SearchByAIBean("正在识别" + order, MESSAGE_TYPE_NORMAL, MESSAGE_FROM_AI));
        mMainThread.post(new Runnable() {
            @Override
            public void run() {
                mCallback.onUpdateAIResponseListData(responseList);
            }
        });
    }
}
