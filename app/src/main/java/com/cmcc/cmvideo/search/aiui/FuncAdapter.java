package com.cmcc.cmvideo.search.aiui;

import android.content.Context;
import android.media.AudioManager;
import android.view.View;

/**
 * 适配不同版本SDK的方法
 * 包括：AudioRequest,HardwareAccelerate
 */
public class FuncAdapter {

    //Android2.3版本
    public static int SDK_GINGERBREAD = 9;

    //Android4.0版本
    public static int SDK_ICECREM = 14;

    /**
     * 获取Audio Focus,暂停后台音乐播放
     * 若之前正在播放的合成语音被AudioManager暂停，则继续播放.
     */
    public static boolean Lock(Context context, AudioManager.OnAudioFocusChangeListener listener) {
        if (android.os.Build.VERSION.SDK_INT >= SDK_GINGERBREAD) {
            FuncAdapterSdk10.Lock(context, listener);
        }
        return false;
    }

    /**
     * 释放Audio Focus，恢复后台音乐播放
     */
    public static boolean UnLock(Context context, AudioManager.OnAudioFocusChangeListener listener) {
        if (android.os.Build.VERSION.SDK_INT >= SDK_GINGERBREAD) {
            return FuncAdapterSdk10.UnLock(context, listener);
        }
        return false;
    }

    /**
     * 关闭硬件加速器
     * 4.0以上android默认开启硬件加速器，导致clipPath等方法不可用
     */
    public static void CloseHardWareAccelerate(View view) {
        if (android.os.Build.VERSION.SDK_INT >= SDK_ICECREM) {
            //设置硬件软加速.
            FuncAdapterSdk10.CloseHardWareAccelerate(view);
        }
    }
}
