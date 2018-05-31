package com.cmcc.cmvideo.search.aiui.bean;

import java.util.List;


public class IatBean {

    /**
     * sn : 2
     * ls : false
     * bg : 0
     * ed : 0
     * ws : [{"bg":0,"cw":[{"sc":0,"w":""}]},{"bg":0,"cw":[{"sc":0,"w":"你好"}]},{"bg":0,"cw":[{"sc":0,"w":"吗"}]}]
     */

    public int sn;
    public boolean ls;
    public int bg;
    public int ed;
    public List<WsBean> ws;

    public static class WsBean {
        /**
         * bg : 0
         * cw : [{"sc":0,"w":""}]
         */

        public int bg;
        public List<CwBean> cw;

        public static class CwBean {
            /**
             * sc : 0
             * w :
             */

            public int sc;
            public String w;
        }
    }
}
