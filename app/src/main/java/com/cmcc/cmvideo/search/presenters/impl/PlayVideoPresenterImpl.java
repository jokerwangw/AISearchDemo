package com.cmcc.cmvideo.search.presenters.impl;

import android.content.Context;

import com.cmcc.cmvideo.base.AbstractPresenter;
import com.cmcc.cmvideo.base.Executor;
import com.cmcc.cmvideo.base.MainThread;
import com.cmcc.cmvideo.search.aiui.bean.ControlEventBean;
import com.cmcc.cmvideo.search.presenters.PlayVideoPresenter;
import com.cmcc.cmvideo.util.AiuiConstants;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by Yyw on 2018/6/23.
 * Describe:
 */

public class PlayVideoPresenterImpl extends AbstractPresenter implements PlayVideoPresenter {
    public PlayVideoPresenterImpl(Executor executor, MainThread mainThread, PlayVideoPresenter.View view, Context context) {
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
