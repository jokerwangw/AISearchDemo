package com.cmcc.cmvideo.search.aiui.bean;

import java.util.List;

public class NlpData {
    public int rc;
    public TppData data;
    public String service;
    public String text;
    public String uuid;
    public String sid;
    public AnswerBean answer;
    public List<SemanticBean> semantic;

    public static class AnswerBean{
        public String text;
    }
    public static class SemanticBean{
        public String intent;
        public List<SlotsBean> slots;
    }
    public static class SlotsBean{
        public String name;
        public String value;
        public String normValue;
    }
}
