package com.cmcc.cmvideo.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;

import com.cmcc.cmvideo.base.ApplicationContext;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

/**
 * 网络工具类
 *
 * @author zhanghongxing
 * @date 2017/10/18
 */

public class NetworkUtil {

    public static final int SIM_TYPE_UNKNOWN = 0;
    public static final int SIM_TYPE_CHINA_MOBILE = 1;
    public static final int SIM_TYPE_CHINA_UNICOM = 2;
    public static final int SIM_TYPE_CHINA_TELECOM = 3;

    private static final int NETWORK_TYPE_UNAVAILABLE = -1;
    private static final int NETWORK_TYPE_WIFI = -101;
    private static final int NETWORK_CLASS_WIFI = -101;
    private static final int NETWORK_CLASS_UNAVAILABLE = -1;
    /**
     * Unknown network class.
     */
    private static final int NETWORK_CLASS_UNKNOWN = 0;
    /**
     * Class of broadly defined "2G" networks.
     */
    private static final int NETWORK_CLASS_2_G = 1;
    /**
     * Class of broadly defined "3G" networks.
     */
    private static final int NETWORK_CLASS_3_G = 2;
    /**
     * Class of broadly defined "4G" networks.
     */
    private static final int NETWORK_CLASS_4_G = 3;

    // 适配低版本手机
    /**
     * Network type is unknown
     */
    public static final int NETWORK_TYPE_UNKNOWN = 0;
    /**
     * Current network is GPRS
     */
    public static final int NETWORK_TYPE_GPRS = 1;
    /**
     * Current network is EDGE
     */
    public static final int NETWORK_TYPE_EDGE = 2;
    /**
     * Current network is UMTS
     */
    public static final int NETWORK_TYPE_UMTS = 3;
    /**
     * Current network is CDMA: Either IS95A or IS95B
     */
    public static final int NETWORK_TYPE_CDMA = 4;
    /**
     * Current network is EVDO revision 0
     */
    public static final int NETWORK_TYPE_EVDO_0 = 5;
    /**
     * Current network is EVDO revision A
     */
    public static final int NETWORK_TYPE_EVDO_A = 6;
    /**
     * Current network is 1xRTT
     */
    public static final int NETWORK_TYPE_1XRTT = 7;
    /**
     * Current network is HSDPA
     */
    public static final int NETWORK_TYPE_HSDPA = 8;
    /**
     * Current network is HSUPA
     */
    public static final int NETWORK_TYPE_HSUPA = 9;
    /**
     * Current network is HSPA
     */
    public static final int NETWORK_TYPE_HSPA = 10;
    /**
     * Current network is iDen
     */
    public static final int NETWORK_TYPE_IDEN = 11;
    /**
     * Current network is EVDO revision B
     */
    public static final int NETWORK_TYPE_EVDO_B = 12;
    /**
     * Current network is LTE
     */
    public static final int NETWORK_TYPE_LTE = 13;
    /**
     * Current network is eHRPD
     */
    public static final int NETWORK_TYPE_EHRPD = 14;
    /**
     * Current network is HSPA+
     */
    public static final int NETWORK_TYPE_HSPAP = 15;

    /**
     * 获取网络状态是否可用
     *
     * @param context
     * @return
     */
    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            Context appContext = context.getApplicationContext();

            // 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
            ConnectivityManager connectivityManager =
                    (ConnectivityManager) appContext.getSystemService(Context.CONNECTIVITY_SERVICE);

            if (connectivityManager != null) {
                // 获取NetworkInfo对象
                NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();

                if (networkInfo != null && networkInfo.length > 0) {
                    for (int i = 0; i < networkInfo.length; i++) {
                        if (networkInfo[i] != null) {
                            // 判断当前网络状态是否为连接状态
                            if (networkInfo[i].getState() == NetworkInfo.State.CONNECTED) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    public static boolean isWifi(Context context) {
        boolean isWifi = getCurrentNetworkNumber(context) == 4;
        return isWifi;
    }

    /**
     * 获取当前网络类型
     *
     * @param ctx
     * @return
     */
    public static int getCurrentNetworkNumber(Context ctx) {
        int networkClass = getNetworkClass(ctx);
        int type = 0;
        switch (networkClass) {
            case NETWORK_CLASS_UNAVAILABLE:
                type = 0;
                break;
            case NETWORK_CLASS_WIFI:
                type = 4;
                break;
            case NETWORK_CLASS_2_G:
                type = 1;
                break;
            case NETWORK_CLASS_3_G:
                type = 2;
                break;
            case NETWORK_CLASS_4_G:
                type = 5;
                break;
            case NETWORK_CLASS_UNKNOWN:
                type = 0;
                break;
            default:
                break;
        }
        return type;
    }

    /**
     * 获取当前网络类型
     *
     * @param ctx
     * @return
     */
    public static String getCurrentNetworkType(Context ctx) {
        int networkClass = getNetworkClass(ctx);
        String type = "未知";
        switch (networkClass) {
            case NETWORK_CLASS_UNAVAILABLE:
                type = "无";
                break;
            case NETWORK_CLASS_WIFI:
                type = "Wi-Fi";
                break;
            case NETWORK_CLASS_2_G:
                type = "2G";
                break;
            case NETWORK_CLASS_3_G:
                type = "3G";
                break;
            case NETWORK_CLASS_4_G:
                type = "4G";
                break;
            case NETWORK_CLASS_UNKNOWN:
                type = "未知";
                break;
            default:
                break;
        }
        return type;
    }

    private static int getNetworkClass(Context ctx) {
        int networkType = NETWORK_TYPE_UNKNOWN;
        try {
            final NetworkInfo network = ((ConnectivityManager) ctx
                    .getSystemService(Context.CONNECTIVITY_SERVICE))
                    .getActiveNetworkInfo();
            if (network != null && network.isAvailable()
                    && network.isConnected()) {
                int type = network.getType();
                if (type == ConnectivityManager.TYPE_WIFI) {
                    networkType = NETWORK_TYPE_WIFI;
                } else if (type == ConnectivityManager.TYPE_MOBILE) {
                    TelephonyManager telephonyManager = (TelephonyManager) ctx.getSystemService(
                            Context.TELEPHONY_SERVICE);
                    networkType = telephonyManager.getNetworkType();
                }
            } else {
                networkType = NETWORK_TYPE_UNAVAILABLE;
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return getNetworkClassByType(networkType);
    }

    private static int getNetworkClassByType(int networkType) {
        switch (networkType) {
            case NETWORK_TYPE_UNAVAILABLE:
                return NETWORK_CLASS_UNAVAILABLE;
            case NETWORK_TYPE_WIFI:
                return NETWORK_CLASS_WIFI;
            case NETWORK_TYPE_GPRS:
            case NETWORK_TYPE_EDGE:
            case NETWORK_TYPE_CDMA:
            case NETWORK_TYPE_1XRTT:
            case NETWORK_TYPE_IDEN:
                return NETWORK_CLASS_2_G;
            case NETWORK_TYPE_UMTS:
            case NETWORK_TYPE_EVDO_0:
            case NETWORK_TYPE_EVDO_A:
            case NETWORK_TYPE_HSDPA:
            case NETWORK_TYPE_HSUPA:
            case NETWORK_TYPE_HSPA:
            case NETWORK_TYPE_EVDO_B:
            case NETWORK_TYPE_EHRPD:
            case NETWORK_TYPE_HSPAP:
                return NETWORK_CLASS_3_G;
            case NETWORK_TYPE_LTE:
                return NETWORK_CLASS_4_G;
            default:
                return NETWORK_CLASS_UNKNOWN;
        }
    }

    public static String getIPAddress(Context context) {
        NetworkInfo info = ((ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            if (info.getType() == ConnectivityManager.TYPE_MOBILE) {//当前使用2G/3G/4G网络
                try {
                    //Enumeration<NetworkInterface> en=NetworkInterface.getNetworkInterfaces();
                    for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                        NetworkInterface intf = en.nextElement();
                        for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                            InetAddress inetAddress = enumIpAddr.nextElement();
                            if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                                return inetAddress.getHostAddress();
                            }
                        }
                    }
                } catch (SocketException e) {
                    e.printStackTrace();
                }

            } else if (info.getType() == ConnectivityManager.TYPE_WIFI) {//当前使用无线网络
                WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                String ipAddress = intIP2StringIP(wifiInfo.getIpAddress());//得到IPV4地址
                return ipAddress;
            }
        } else {
            //当前无网络连接,请在设置中打开网络
        }
        return null;
    }

    /**
     * 将得到的int类型的IP转换为String类型
     *
     * @param ip
     * @return
     */
    public static String intIP2StringIP(int ip) {
        return (ip & 0xFF) + "." +
                ((ip >> 8) & 0xFF) + "." +
                ((ip >> 16) & 0xFF) + "." +
                (ip >> 24 & 0xFF);
    }

    /**
     * 判断网络类型是否属于低速网络，如2G
     *
     * @return
     */
    public static boolean isConnectFast() {
        String connectionType = NetworkUtil.getConnectionTypeName(ApplicationContext.application);
        if ("TYPE_1xRTT".equals(connectionType) || "NETWORK_TYPE_CDMA".equals(connectionType)
                || "NETWORK_TYPE_EDGE".equals(connectionType) || "TYPE_GPRS".equals(connectionType)) {
            return false;
        }
        return true;
    }

    /**
     * 获取网络连接类型
     *
     * @return
     */
    public static String getConnectionTypeName(Context ctx) {
        String netType = "TYPE_UNKNOWN";

        ConnectivityManager connectivityManager = (ConnectivityManager) ctx
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isAvailable()) {
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                netType = "TYPE_WIFI";
            } else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                switch (networkInfo.getSubtype()) {
                    case TelephonyManager.NETWORK_TYPE_1xRTT:
                        netType = "TYPE_1xRTT";
                        break; // ~ 50-100 kbps
                    case TelephonyManager.NETWORK_TYPE_CDMA:
                        netType = "TYPE_CDMA";
                        break; // ~ 14-64 kbps
                    case TelephonyManager.NETWORK_TYPE_EDGE:
                        netType = "TYPE_EDGE";
                        break; // ~ 50-100 kbps
                    case TelephonyManager.NETWORK_TYPE_EVDO_0:
                        netType = "TYPE_EVDO_0";
                        break; // ~ 400-1000 kbps
                    case TelephonyManager.NETWORK_TYPE_EVDO_A:
                        netType = "TYPE_EVDO_A";
                        break; // ~ 600-1400 kbps
                    case TelephonyManager.NETWORK_TYPE_GPRS:
                        netType = "TYPE_GPRS";
                        break; // ~ 100 kbps
                    case TelephonyManager.NETWORK_TYPE_HSDPA:
                        netType = "TYPE_HSDPA";
                        break; // ~ 2-14 Mbps
                    case TelephonyManager.NETWORK_TYPE_HSPA:
                        netType = "TYPE_HSPA";
                        break; // ~ 700-1700 kbps
                    case TelephonyManager.NETWORK_TYPE_HSUPA:
                        netType = "TYPE_HSUPA";
                        break; // ~ 1-23 Mbps
                    case TelephonyManager.NETWORK_TYPE_UMTS:
                        netType = "TYPE_UMTS";
                        break; // ~ 400-7000 kbps
                    case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                        netType = "TYPE_MOBILE";
                        break;
                }
            }
        }
        return netType;
    }

    /**
     * 判断是否处于2g或者3g,4g网络状态
     *
     * @return
     */
    public static boolean is2G3G4G(Context ctx) {
        String connectName = getConnectionTypeName(ctx);
        return !("TYPE_WIFI".equals(connectName));
    }

    /**
     * 判断是否处于移动网络
     *
     * @return
     */
    public static boolean isMobileNet(Context ctx) {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) ctx
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                return true;
            }
        } catch (Exception e) {
        }
        return false;
    }

    /**
     * 判断是否连接wifi
     *
     * @param
     * @return
     */
    public static boolean isWifiNet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) ApplicationContext.application
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifi != null && wifi.isAvailable()) {
            return true;
        } else {
            return false;
        }
    }

    public static String getWifiName() {
        ConnectivityManager connectivityManager = (ConnectivityManager) ApplicationContext.application
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifi != null && wifi.getExtraInfo() != null) {
            return wifi.getExtraInfo();
        } else {
            return "";
        }
    }

    /**
     * 判断是否已经联网
     *
     * @return true已联网 false未联网
     */
    public static boolean isConnectNet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) ApplicationContext.application
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isAvailable()) {
            return true;
        } else {
            return false;
        }
    }

}
