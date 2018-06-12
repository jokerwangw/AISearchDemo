package com.cmcc.cmvideo.search.presenters.impl;

import android.content.Context;
import android.os.Bundle;
import android.os.Looper;

import com.cmcc.cmvideo.base.AbstractPresenter;
import com.cmcc.cmvideo.base.Executor;
import com.cmcc.cmvideo.base.MainThread;
import com.cmcc.cmvideo.search.aiui.bean.TppData;
import com.cmcc.cmvideo.search.presenters.LookMorePresenter;
import com.cmcc.cmvideo.search.presenters.SearchByAIPresenter;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import static com.cmcc.cmvideo.search.LookMoreActivity.KEY_MORE_DATE;

/**
 * Created by Yyw on 2018/6/11.
 * Describe:
 */

public class LookMorePresenterImpl extends AbstractPresenter implements LookMorePresenter {
    private LookMorePresenter.View mView;
    private Context mContext;

    public LookMorePresenterImpl(Executor executor, MainThread mainThread, LookMorePresenter.View view, Context context) {
        super(executor, mainThread);
        mView = view;
        mContext = context;
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
    public void initListItem(Bundle bundle) {
        if (bundle == null){
            return;
        }

        List<TppData.DetailsListBean> detailsListBeanArrayList = (ArrayList<TppData.DetailsListBean>) bundle.getSerializable(KEY_MORE_DATE);
        if (null != detailsListBeanArrayList) {
            mView.showInitList(detailsListBeanArrayList);
        }
    }
}
