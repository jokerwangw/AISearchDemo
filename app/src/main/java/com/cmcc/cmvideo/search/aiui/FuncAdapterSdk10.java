package com.cmcc.cmvideo.search.aiui;

import android.content.Context;
import android.media.AudioManager;
import android.view.View;

public class FuncAdapterSdk10 {

    @SuppressWarnings("unused")
    private static int avoidValue = 0;

    // avoid 1.5-2.2 system error,can't delete
    public void avoidSystemError(int value) {
        avoidValue = value;
    }

    public static boolean Lock(Context context, AudioManager.OnAudioFocusChangeListener listener)
    {
        try{
            AudioManager audioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
            audioManager.requestAudioFocus(listener, AudioManager.STREAM_MUSIC,
                    AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
            return true;
        }catch(Exception e){
        }
        return false;
    }

    /**
     * 释放Audio Focus，恢复后台音乐播放
     */
    public static boolean UnLock(Context context, AudioManager.OnAudioFocusChangeListener listener)
    {
        try{
            AudioManager audioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
            audioManager.abandonAudioFocus(listener);
            return true;
        }catch(Exception e){
        }
        return false;
    }


    /**
     * 关闭硬件加速器
     * 4.0以上android默认开启硬件加速器，导致clipPath等方法不可用
     */
    public static void CloseHardWareAccelerate(View view)
    {
        //设置硬件软加速.
        view.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }
}
