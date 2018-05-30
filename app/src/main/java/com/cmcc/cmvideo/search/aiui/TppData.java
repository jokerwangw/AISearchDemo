package com.cmcc.cmvideo.search.aiui;

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
    public static class DetailsListBean{
        public String area;
        public String name;
        public List<String> language;
        public String detail;
        public String id;
        public List<String> tag;
        public String category;
        public String image;
        public List<SubserialsBean> subserials;
    }
    public static class SubserialsBean{
        public String name;
        public String id;
    }
}
