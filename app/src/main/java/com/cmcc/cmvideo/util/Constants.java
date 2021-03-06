package com.cmcc.cmvideo.util;

/**
 * Created by Yyw on 2018/5/29.
 * Describe:常量
 */

public class Constants {
    /*** 消息来自AI*/
    public final static String MESSAGE_FROM_AI = "message_from_ai";
    /*** 消息来自用户*/
    public final static String MESSAGE_FROM_USER = "message_from_user";

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

    /*** 图片地址*/
    public final static String IMG_BASE_URL = "http://wapx.cmvideo.cn:8080/publish/poms";
}
