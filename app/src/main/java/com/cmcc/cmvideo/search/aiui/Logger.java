package com.cmcc.cmvideo.search.aiui;

import android.util.Log;

public class Logger {
    private static final String TAG ="aiui_log";
    private static boolean isDebug = true;
    public static void debug(String log){
        debug(TAG,log);
    }
    public static void debug(String tag,String log){
        if(isDebug) {
            Log.d(tag, log);
        }
    }
}
