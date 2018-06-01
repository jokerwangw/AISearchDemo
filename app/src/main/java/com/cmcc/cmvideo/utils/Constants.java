package com.cmcc.cmvideo.utils;

/**
 * Created by Yyw on 2018/5/29.
 * Describe:
 */

public class Constants {

    public final static String MESSAGE_FROM_AI = "message_from_ai";//消息来自AI
    public final static String MESSAGE_FROM_USER = "message_from_user";//消息来自用户

    public final static int MESSAGE_TYPE_NORMAL = 0;//聊天类消息
    public final static int MESSAGE_TYPE_CAN_ASK_AI = 1;//可以这样问AI
    public final static int MESSAGE_TYPE_APPOINTMENT = 2;//AI预约电影成功的类型
    public final static int MESSAGE_TYPE_EVERYONE_IS_WATCHING = 3;//大家都在看
    public final static int MESSAGE_TYPE_GUESS_WHAT_YOU_LIKE = 4;//猜你喜欢
    public final static int MESSAGE_TYPE_THE_LATEST_VIDEO = 5;//最新影讯
}
