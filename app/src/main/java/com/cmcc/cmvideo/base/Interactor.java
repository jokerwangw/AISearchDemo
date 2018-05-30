package com.cmcc.cmvideo.base;

/**
 * Created by Yyw on 2018/5/29.
 * Describe:
 */

public interface Interactor {

    /**
     * This is the main method that starts an interactor. It will make sure that the interactor operation is done on a
     * background thread.
     */
    void execute();
}
