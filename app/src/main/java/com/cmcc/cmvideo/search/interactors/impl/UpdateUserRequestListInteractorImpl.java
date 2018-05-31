package com.cmcc.cmvideo.search.interactors.impl;

import com.cmcc.cmvideo.base.AbstractInteractor;
import com.cmcc.cmvideo.base.BaseObject;
import com.cmcc.cmvideo.base.Executor;
import com.cmcc.cmvideo.base.MainThread;
import com.cmcc.cmvideo.search.interactors.UpdateUserRequestListInteractor;
import com.cmcc.cmvideo.search.model.SearchByAIBean;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.cmcc.cmvideo.utils.Constants.MESSAGE_FROM_USER;
import static com.cmcc.cmvideo.utils.Constants.MESSAGE_TYPE_NORMAL;

/**
 * Created by Yyw on 2018/5/22.
 * Describe:
 */

public class UpdateUserRequestListInteractorImpl extends AbstractInteractor implements UpdateUserRequestListInteractor {
    private Callback mCallback;

    public UpdateUserRequestListInteractorImpl(Executor threadExecutor, MainThread mainThread, UpdateUserRequestListInteractor.Callback callback) {
        super(threadExecutor, mainThread);
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
        final List<SearchByAIBean> userRequestList = new ArrayList<SearchByAIBean>();
        userRequestList.add(new SearchByAIBean("我要看海贼王", MESSAGE_TYPE_NORMAL, MESSAGE_FROM_USER));

        mMainThread.post(new Runnable() {
            @Override
            public void run() {
                mCallback.onUpdateUserRequestListData(userRequestList);
            }
        });
    }
}
