package com.cmcc.cmvideo.util;

import retrofit2.http.PUT;

/**
 * @Author lhluo
 * @description Aiui常量
 * @date 2018/5/30
 */
public class AiuiConstants {
    public static class MessageFrom {
        /*** 消息来自AI*/
        public final static String MESSAGE_FROM_AI = "message_from_ai";
        /*** 消息来自用户*/
        public final static String MESSAGE_FROM_USER = "message_from_user";
    }

    public static class MessageType {
        /*** 聊天类消息*/
        public final static int MESSAGE_TYPE_NORMAL = 0;
        /*** 可以这样问AI*/
        public final static int MESSAGE_TYPE_CAN_ASK_AI = 1;
        /*** AI预约电影成功的类型*/
        public final static int MESSAGE_TYPE_APPOINTMENT = 2;
        /*** 大家都在看*/
        public final static int MESSAGE_TYPE_EVERYONE_IS_WATCHING = 3;
        /*** 我想看XXXX*/
        public final static int MESSAGE_TYPE_I_WANT_TO_SEE = 8;
        /*** 猜你喜欢*/
        public final static int MESSAGE_TYPE_GUESS_WHAT_YOU_LIKE = 4;
        /*** 猜你喜欢_列表横向展示*/
        public final static int MESSAGE_TYPE_GUESS_WHAT_YOU_LIKE_LIST_HORIZONTAL = 6;
        /*** 猜你喜欢_列表垂直展示*/
        public final static int MESSAGE_TYPE_GUESS_WHAT_YOU_LIKE_LIST_VERTICAL = 7;
        /*** 最新影讯*/
        public final static int MESSAGE_TYPE_THE_LATEST_VIDEO = 5;
        /*** 体育赛事列表*/
        public final static int MESSAGE_TYPE_LIST_OF_SPORTS = 9;
        /*** 体育赛事视频*/
        public final static int MESSAGE_TYPE_VIDEO_OF_SPORTS = 10;

    }

    /*** 图片地址*/
    public final static String IMG_BASE_URL = "http://wapx.cmvideo.cn:8080/publish/poms";

    public final static String MICRO_MESSAGE = "小咪为您服务";
    public final static String MICRO_ENTER_MESSAGE = "主淫，主淫，需要我为你做什么";
    public final static String MICRO_DISABLE_MESSAGE = "主淫，没有麦，臣妾办不到啊";


    /**
     * 技能名(闲聊、频道、跳转充值页、展示流量、购票页、系统设置、投屏播放)
     */
    public final static String QA_SERVICE = "openQA";
    public final static String VIDEO_SERVICE = "video";
    public final static String USER_VIDEO_SERVICE = "LINGXI2018.user_video";
    public final static String VIEWCMD_SERVICE = "viewCmd";
    //指令控制技能名  如：开启/关闭语音助手、投屏指令
    public final static String CONTROL_MIGU = "LINGXI2018.control_migu";
    public final static String QUERY_MIGU = "LINGXI2018.businessQuery";
    //商店技能控制技能
    public final static String VIDEO_CMD = "cmd";

    //商店技能 世界杯
    public final static String WORLD_CUP_SERVICE = "AIUI.WorldCup";

    //笑话技能
    public final static String JOKE_SERVICE = "joke";


    //百科词条查询技能
    public final static String QUERY_ENCYCLO_SERVICE = "LEIQIAO.cyclopedia";
    //意图名 百科词条
    public final static String KEYWORD_QUERY_INTENT = "KEYWORD_QUERY";

    //天气查询技能
    public final static String QUERY_WEATHER_SERVICE = "weather";


    //世界杯意图名
    public final static String WORLD_CUP_QUERY_OPEN = "QUERY_OPEN";
    public final static String WORLD_CUP_SERCH_BY_DATE = "SERCH_BY_DATE";
    public final static String WORLD_CUP_QUERY_TEAMS = "QUERY_TEAMS";

    public final static String WORLD_CUP_QUERY_WITH_SESSION = "QUERY_WITH_SESSION";
    public final static String WORLD_CUP_QUERY_IMPTGAME = "QUERY_IMPTGAME";
    public final static String WORLD_CUP_TEAM_PLAYERS = "TEAM_PLAYERS";
    public final static String WORLD_CUP_SEARCH_BY_TEAM_INTENT = "SERCH_BY_TEAM";

    public final static String WORLD_CUP_QUERY_FIRST_GAME = "QUERY_FIRST_GAME";
    public final static String WORLD_CUP_QUERY_GROUPS = "QUERY_GROUPS";

    public final static String WORLD_CUP_QUERY_GPGM_OVER = "QUERY_GPGM_OVER";
    public final static String WORLD_CUP_QUERY_TEAM_GROUP = "QUERY_TEAM_GROUP";
    public final static String WORLD_CUP_SERCH_BY_TEAMS_INTENT = "SERCH_BY_TEAMS";
    public final static String WORLD_CUP_QUERY_WITH_GROUP = "QUERY_WITH_GROUP";
    public final static String WORLD_CUP_QUERY_ALLINFO = "QUERY_ALLINFO";

    public final static String WORLD_CUP_QUERY_CHAMPION = "QUERY_CHAMPION";
    public final static String WORLD_CUP_I_LIKE_TEAM = "I_LIKE_TEAM";


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
    public final static String VIDEO_INDEX = "episode";
    //换一集
    public final static String VIDEO_CHANGE = "changeEpisode";
    //快进（退）、快进（退）多长时间
    public final static String VIDEO_FASTWORD = "fastForward";
    public final static String VIDEO_BACKWORD = "fastBackward";
    //快进到or快退到多少时刻
    public final static String VIDEO_FASTWORD_TO = "fastForwardTo";

    //音量增加
    public final static String VIDEO_VOLUME_PLUS = "volume_plus";
    public final static String VIDEO_VOLUME_MINUS = "volume_minus";
    public final static String VIDEO_MUTE = "mute";
    public final static String VIDEO_VOLUME_MAX = "volume_max";


    public final static String HOURS = "hour";
    public final static String MINUTE = "minute";
    public final static String SECOND = "second";


    //视频控制指令类型  播放  暂停  下一集  上一集  打开语音助手  关闭语音助手  投屏播放
    public final static int VDO_PAUSE = 1;
    public final static int VDO_PLAY = 2;
    public final static int VDO_NEXT = 3;
    public final static int VDO_PREVIOUS = 4;
    public final static int VDO_OPEN = 5;
    public final static int VDO_CLOSE = 6;
    public final static int VDO_SCREEN = 7;
    //快进 快退 快进（快退） 换一集  第几集
    public final static int VDO_FASTWORD = 8;
    public final static int VDO_BACKWORD = 9;
    public final static int VDO_FASTWORD_TO = 10;
    public final static int VDO_CHANGE = 11;
    public final static int VDO_WHICH_EPISODE = 12;

    //音量增加  音量减小  静音  最大音量
    public final static int VOLUME_PLUS = 13;
    public final static int VOLUME_MINUS = 14;
    public final static int VOLUME_MUTE = 15;
    public final static int VOLUME_MAX = 16;



    //自定义直播技能
    public final static String VIDEO_ON_SERVICE = "LINGXI2018.onlive";
    public final static String VIDEO_CHANNEL_INTENT = "channel_query";
    public final static String VIDEO_VERITY_INTENT = "variety_query";

    //直播类别normalValue
    public final static String CCTV = "央视";
    public final static String START_TV = "卫视";
    public final static String FILMS = "影视";
    public final static String CHILDREN = "少儿";
    public final static String NEWS = "新闻";
    public final static String DOCUMENTARY = "纪录片";
    public final static String TURN_TV = "轮播台";
    public final static String AREA_TV = "地方台";
    public final static String FEATURE_TV = "特色台";
    public final static String SPORTS_TV = "体育";


    //video 意图下各句子分词
    public final static String VIDEO_AREA = "area";
    public final static String VIDEO_CATEGORY = "category";
    public final static String VIDEO_SCORE_DESCR = "scoreDescr";
    public final static String VIDEO_TIME_DESCR = "timeDescr";
    public final static String VIDEO_POPULAR = "popularity";
    public final static String VIDEO_TAG = "tag";
    public final static String VIDEO_NAME = "name";
    public final static String VIDEO_ARTIST = "artist";
    public final static String VIDEO_TIME = "datetime.dateOrig";
}
