package com.cmcc.cmvideo.search.aiui.bean;

import java.util.HashMap;

/**
 * Created by Yyw on 2018/8/20.
 * Describe:
 */

public class ActionBean {
    /**
     * 类型，区分功能，比如跳转、刷新
     */
    public String type;
    /**
     * 参数
     */
    public ParamBean params;

    /**
     * 时间戳
     */
    public long timeStamp;


    public ActionBean() {
        super();
        params = new ParamBean();
    }

    public static class ParamBean {
        /**
         * 页面的Frame节点
         */
        public String frameID;
        /**
         * 跳转目标页面的ID
         */
        public String pageID;
        /**
         * 节目ID-跳转播放详情必传参数
         */
        public String contentID;
        public String path;
        public String url;
        public String location;
        public String groupId;
        public int index;
        public String imgUrl;
        public HashMap<String, String> extra = new HashMap<>();

        public ParamBean(){}
    }

    public void setTimeStamp() {
        timeStamp = System.currentTimeMillis();
    }
}
