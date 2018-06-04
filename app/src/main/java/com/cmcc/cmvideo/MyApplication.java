package com.cmcc.cmvideo;

import android.app.Application;

import com.cmcc.cmvideo.base.ApplicationContext;

/**
 * Created by Yyw on 2018/5/29.
 * Describe:
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        ApplicationContext.init(this);
    }
}
