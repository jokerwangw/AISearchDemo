package com.cmcc.cmvideo.base;

import android.app.Application;
import android.text.TextUtils;

/**
 * Created by Yyw on 2018/6/4.
 * Describe:
 */

public class ApplicationContext {
    public static Application application;

    public static void init(Application applicationParam) {
        application = applicationParam;
    }
}
