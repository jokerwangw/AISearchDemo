package com.cmcc.cmvideo.util;

import android.app.ActivityManager;
import android.content.Context;
import android.media.AudioManager;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.cmcc.cmvideo.util.DigitUtil.getNumbers;

/**
 * Created by Yyw on 2018/6/25.
 * Describe:
 */

public class AIUIUtils {
    /**
     * 判断服务是否开启
     * @return
     */
    public static boolean isServiceRunning(Context context, String serviceName) {
        if (TextUtils.isEmpty(serviceName)) {
            return false;
        }
        ActivityManager myManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ArrayList<ActivityManager.RunningServiceInfo> runningService = (ArrayList<ActivityManager.RunningServiceInfo>) myManager.getRunningServices(Integer.MAX_VALUE);
        for (int i = 0; i < runningService.size(); i++) {
            if (runningService.get(i).service.getClassName().toString().equals(serviceName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 设置语音播放的模式
     * @param ctx
     * @param mode
     */
    public static void setAudioMode(Context ctx, int mode) throws Exception {
        if (mode != AudioManager.MODE_NORMAL && mode != AudioManager.MODE_IN_COMMUNICATION) {
            return;
        }
        AudioManager audioManager = (AudioManager) ctx.getSystemService(Context.AUDIO_SERVICE);
        if (mode == AudioManager.MODE_NORMAL) {
            //打开扬声器
            audioManager.setSpeakerphoneOn(true);
        } else if (mode == AudioManager.MODE_IN_COMMUNICATION) {
            //关闭扬声器
            audioManager.setSpeakerphoneOn(false);
        }
        audioManager.setMode(mode);
    }

    /**
     * 阿拉伯数字转大写
     * @param s
     * @return
     */
    public static String transition(String s) {
        String tag = "";
        tag = s;
        if (!HasDigit(tag)) {
            return tag;
        }

        String num = getNumbers(tag);
        String zh = NumberToWord.toChinese(num);
        tag = tag.replaceFirst(num, zh);
        if (HasDigit(tag)) {
            return transition(tag);
        } else {
            return tag;
        }
    }

    /**
     * 判断一个字符串是否含有数字
     * @param content
     * @return
     */
    public static boolean HasDigit(String content) {
        boolean flag = false;
        Pattern p = Pattern.compile(".*\\d+.*");
        Matcher m = p.matcher(content);
        if (m.matches()) {
            flag = true;
        }
        return flag;
    }
}
