package com.cmcc.cmvideo.search.aiui.bean;

import java.io.Serializable;
import java.util.List;

public class TppData {
    public LxResultBean lxresult;
    public static class LxResultBean{
        public String code;
        public String codedesc;
        public DataBean data;
    }
    public static class DataBean{
        public String correct;
        public String service;
        public List<DetailsListBean> detailslist;

    }
    public static class DetailsListBean implements Serializable{
        //区域
        public String area;
        //片名
        public String name;
        //演员列表
        public List<String> actor;
        //支持语言列表
        public List<String> language;
        //影片介绍
        public String detail;
        //影片ID
        public String id;
        //影片标签
        public List<String> tag;
        //分类
        public String category;
        //导演
        public List<String> director;
        //图片 4种类型
        public String image;
        //上映时间
        public String releasetime;
        //剧集 第一集，第二集...
        public List<SubserialsBean> subserials;
    }
    public static class SubserialsBean implements Serializable{
        //第一集名称
        public String name;
        //影片ID
        public String id;
    }
}
