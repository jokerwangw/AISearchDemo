package com.cmcc.cmvideo.util;

/**
 * @Author lhluo
 * @description Aiui常量
 * @date 2018/5/30
 */
public class AiuiConstants {
    public final static String ERROR_MESSAGE = "抱歉，不明白你的意思";

    /**
     * 技能名(闲聊、频道、跳转充值页、展示流量、购票页、系统设置、投屏播放)
     */
    public final static String QA_SERVICE = "openQA";
    public final static String VIDEO_SERVICE = "video";
    public final static String VIEWCMD_SERVICE = "viewCmd";
    //指令控制技能名  如：开启/关闭语音助手、投屏指令
    public final static String CONTROL_MIGU = "LINGXI2018.control_migu";
    public final static String QUERY_MIGU = "LINGXI2018.businessQuery";
    public final static String VIDEO_CMD = "cmd";


    public final static String CHANNEL_SERVICE = "tvchannel";
    public final static String MIGU_TV_ORDER = "LINGXI2018.MiGu_TV_Order";
    public final static String MIGU_TV = "LINGXI2018.MiGu_tv";
    public final static String MIGU_SHIXUN = "LINGXI2018.MiGu_shixun";
    public final static String MIGU_USER_VIDEO = "LINGXI2018.user_video";
    public final static String MIGU_OPERATION_ORDER = "LINGXI2018.operation_order";
    public final static String MIGU_SCREEN = "LINGXI2018.MIGU";

    //意图名
    public final static String CONTROL_INTENT = "Assistant";
    public final static String SREEN_INTENT = "Screen";
    public final static String QUERY_INTENT = "QUERY";
    public final static String HOTVIDEO_INTENT = "hotVideo";
    public final static String MEMBER_INTENT = "Member";
    public final static String INTERNET_INTENT = "InternetTraffic";
    public final static String TICKET_INTENT = "Ticket";
    public final static String ACYIVITY_INTENT = "Activity";
    public final static String GCUSTOMER_INTENT = "Gcustomer";
    public final static String VIEWCMD_INTENT = "VIEWCMD";
    //视频播放控制
    public final static String VIDEO_CMD_INTENT = "INSTRUCTION";
    public final static String VIDEO_INSTYPE = "insType";
    public final static String VIDEO_PAUSE = "pause";
    public final static String VIDEO_PLAY = "play";
    public final static String VIDEO_PREVIOUS = "previousEpisode";
    public final static String VIDEO_NEXT = "nextEpisode";

    //自定义直播技能
    public final static String VIDEO_ON_SERVICE = "LINGXI2018.onlive";
    public final static String VIDEO_CHANNEL_INTENT = "channel_query";
    public final static String VIDEO_VERITY_INTENT = "variety_query";



    //video 意图下各句子分词
    public final static String VIDEO_AREA = "area";
    public final static String VIDEO_CATEGORY = "category";
    public final static String VIDEO_SCORE_DESCR = "scoreDescr";
    public final static String VIDEO_TIME_DESCR = "timeDescr";
    public final static String VIDEO_TAG = "tag";
    public final static String VIDEO_NAME = "name";
    public final static String VIDEO_ARTIST = "artist";
}
