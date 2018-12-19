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
    public static void error(String error){
        Log.e(TAG, error);
    }
    public static void error(String tag,String error){
        Log.e(tag, error);
    }
    public static void error(Throwable e){

        error(getStackTraceInfo(e));
    }
    public static void error(String tag,Throwable e){
        error(tag, getStackTraceInfo(e));
    }

    private static String getStackTraceInfo(Throwable e){
        if(e==null){
            return "未知错误";
        }
        StringBuilder sb = new StringBuilder();
        StackTraceElement[] stackTraceElements =  e.getStackTrace();
        if(stackTraceElements == null||stackTraceElements.length == 0){
            return "未知错误";
        }
        for(int i = 0;i<stackTraceElements.length;i++){
            sb.append(stackTraceElements[i].toString()).append("\r\n");
        }
        return sb.toString();
    }
}
