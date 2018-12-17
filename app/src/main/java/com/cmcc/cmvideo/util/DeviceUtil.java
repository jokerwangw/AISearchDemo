package com.cmcc.cmvideo.util;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.format.Formatter;
import android.view.Display;
import android.view.ViewConfiguration;
import android.view.WindowManager;

import com.cmcc.cmvideo.base.ApplicationContext;

import java.io.File;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;
import java.util.UUID;

/**
 * Created by Yyw on 2018/12/17.
 * Describe:
 */
public class DeviceUtil {

    private static int screenWidth = 0;
    private static int sceenHeight = 0;

    static {
        sceenHeight = getDeviceHeight();
        screenWidth = getScreenWidth();
    }

    public static int fastGetScreenWidth() {
        return getDeviceWidth();
    }

    public static int fastGetScreenHeight() {
        return getDeviceHeight();
    }

    /**
     * 获取屏幕高度
     *
     * @return 屏幕的高（单位：像素）
     */
    @SuppressWarnings("deprecation")
    public static int getScreenHeight() {
        Display display = ((WindowManager) ApplicationContext.application
                .getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        return display.getHeight();
    }

    /**
     * 获取屏幕宽度
     *
     * @return 屏幕的宽（单位：像素）
     */
    @SuppressWarnings("deprecation")
    public static int getScreenWidth() {
        Display display = ((WindowManager) ApplicationContext.application
                .getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        return display.getWidth();
    }

    public static int getDeviceWidth() {
        Context context = ApplicationContext.application;
        return Math.min(context.getResources().getDisplayMetrics().widthPixels,
                context.getResources().getDisplayMetrics().heightPixels);
    }

    public static int getDeviceHeight() {
        Context context = ApplicationContext.application;
        return Math.max(context.getResources().getDisplayMetrics().widthPixels,
                context.getResources().getDisplayMetrics().heightPixels);
    }

}
