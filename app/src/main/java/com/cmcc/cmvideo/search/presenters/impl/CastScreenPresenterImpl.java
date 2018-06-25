package com.cmcc.cmvideo.search.presenters.impl;

import android.content.Context;

import com.cmcc.cmvideo.base.AbstractPresenter;
import com.cmcc.cmvideo.base.Executor;
import com.cmcc.cmvideo.base.MainThread;
import com.cmcc.cmvideo.search.presenters.CastScreenPresenter;

/**
 * Created by Yyw on 2018/6/23.
 * Describe:
 */

public class CastScreenPresenterImpl extends AbstractPresenter implements CastScreenPresenter {
    public CastScreenPresenterImpl(Executor executor, MainThread mainThread, CastScreenPresenter.View view, Context context) {
        super(executor, mainThread);
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
}
