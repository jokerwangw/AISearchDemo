package com.cmcc.cmvideo.base;

/**
 * Created by Yyw on 2018/5/29.
 * Describe:
 */

public interface MainThread {

    /**
     * Make runnable operation run in the main thread.
     *
     * @param runnable The runnable to run.
     */
    void post(final Runnable runnable);
}
