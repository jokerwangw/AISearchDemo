package com.cmcc.cmvideo.util;

import android.content.Context;
import android.media.AudioManager;
import android.os.Build;
import android.util.Log;

import com.cmcc.cmvideo.BuildConfig;
import com.cmcc.cmvideo.MyApplication;
import com.cmcc.cmvideo.base.ApplicationContext;

/**
 * @Author lhluo
 * @date 2018/6/6
 */
public class PlayerManager {
    private AudioManager audioManager;
    private static PlayerManager instance = null;

    public PlayerManager() {
        audioManager = (AudioManager) ApplicationContext.application.getSystemService(Context.AUDIO_SERVICE);

    }

    public static synchronized PlayerManager getInstance() {
        if (instance == null) {
            synchronized (PlayerManager.class) {
                if (instance == null) {
                    instance = new PlayerManager();
                }
            }
        }
        return instance;
    }

    /**
     * 切换到外放
     */
    public void changeToSpeaker(){
        if (BuildConfig.DEBUG) Log.d("MainActivity", "外放模式");
        audioManager.setMode(AudioManager.MODE_NORMAL);
        audioManager.setSpeakerphoneOn(true);
    }

    /**
     * 切换到耳机模式
     */
    public void changeToHeadset(){
        if (BuildConfig.DEBUG) Log.d("MainActivity", "耳机模式");
        audioManager.setSpeakerphoneOn(false);
    }

    /**
     * 切换到听筒模式
     */
    public void changeToReceiver(){
        audioManager.setSpeakerphoneOn(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
            audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
        } else {
            audioManager.setMode(AudioManager.MODE_IN_CALL);
        }
    }

}
