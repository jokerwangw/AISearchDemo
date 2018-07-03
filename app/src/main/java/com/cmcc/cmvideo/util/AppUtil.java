package com.cmcc.cmvideo.util;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

/**
 * Created by Yyw on 2018/7/3.
 * Describe:
 */

public class AppUtil {
    /**
     * 获取版本号
     *
     * @return
     */
    public static int getVersionCode(Context context) {
        //获取包管理器
        PackageManager manager = context.getPackageManager();
        try {
            //通过当前的包名获取包的信息
            //获取包对象信息
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            return info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 获取坂本明
     *
     * @return
     */
    public static String getVersionName(Context context) {
        PackageManager manager = context.getPackageManager();
        try {
            //第二个参数代表额外的信息，例如获取当前应用中的所有的Activity
            PackageInfo packageInfo = manager.getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }
}
