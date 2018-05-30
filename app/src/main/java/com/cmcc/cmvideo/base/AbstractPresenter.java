package com.cmcc.cmvideo.base;

/**
 * Created by Yyw on 2018/5/29.
 * Describe:
 */

public abstract class AbstractPresenter {
    protected Executor mExecutor;
    protected MainThread mMainThread;

    public AbstractPresenter(Executor executor, MainThread mainThread) {
        mExecutor = executor;
        mMainThread = mainThread;
    }
}
