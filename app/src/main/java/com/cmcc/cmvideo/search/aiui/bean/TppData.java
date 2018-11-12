package com.cmcc.cmvideo.search.aiui.bean;

import java.io.Serializable;
import java.util.List;

public class TppData {
    public LxResultBean lxresult;

    public static class LxResultBean {
        public String code;
        public String codedesc;
        public boolean satisfy;
        public DataBean data;
    }

    public static class DataBean {
        public String correct;
        public String service;
        public List<DetailsListBean> detailslist;

    }

    public static class DetailsListBean implements Serializable {
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

        //跳转路由对象
        public ActionBean action;
    }

    public static class SubserialsBean implements Serializable {
        //第一集名称
        public String name;
        //影片ID
        public String id;
    }


    /**
     * 体育赛事模块
     */

    /**
     * match : {"CompetitionBroadCastText":"现在进行到了9轮","matchList":[{"matchEventInfo":[{"CompetitionCountDownTime":-1946220000,"CompetitionStatus":2,"_id":"130000076380","competitionId":"4","competitionLogo":"http://image.suning.cn/uimg/SDSP/competition/4.png?v=20181031","competitionName":"西甲","competitionType":"1","confrontTeamOneid":"7724250","confrontTeamOneimage":"http://image.suning.cn/uimg/SDSP/team/250.png?v=1498717895433","confrontTeamOnename":"巴塞罗那","confrontTeamOnescore":"4","confrontTeamTwoid":"7724244","confrontTeamTwoimage":"http://image.suning.cn/uimg/SDSP/team/244.png?v=1498717892078","confrontTeamTwoname":"塞维利亚","confrontTeamTwoscore":"2","endTime":"1540069200000","keyword":"10月21日02:45","matchStartTime":"02:45","pID":"648227431","projectId":"151","roundId":"9","seasonId":"1273","seasonName":"2018/2019","stageId":"2878","stageRoundName":"联赛第9轮","startTime":"1540061100000","time":"2018.10.21","title":"18/19赛季西甲第9轮全场集锦：巴塞罗那4:2塞维利亚","type":"1","winner":"7724250"}],"matchEventTime":"2018.10.21"}]}
     */

    public MatchBean match;

    public static class MatchBean {
        /**
         * CompetitionBroadCastText : 现在进行到了9轮
         * matchList : [{"matchEventInfo":[{"CompetitionCountDownTime":-1946220000,"CompetitionStatus":2,"_id":"130000076380","competitionId":"4","competitionLogo":"http://image.suning.cn/uimg/SDSP/competition/4.png?v=20181031","competitionName":"西甲","competitionType":"1","confrontTeamOneid":"7724250","confrontTeamOneimage":"http://image.suning.cn/uimg/SDSP/team/250.png?v=1498717895433","confrontTeamOnename":"巴塞罗那","confrontTeamOnescore":"4","confrontTeamTwoid":"7724244","confrontTeamTwoimage":"http://image.suning.cn/uimg/SDSP/team/244.png?v=1498717892078","confrontTeamTwoname":"塞维利亚","confrontTeamTwoscore":"2","endTime":"1540069200000","keyword":"10月21日02:45","matchStartTime":"02:45","pID":"648227431","projectId":"151","roundId":"9","seasonId":"1273","seasonName":"2018/2019","stageId":"2878","stageRoundName":"联赛第9轮","startTime":"1540061100000","time":"2018.10.21","title":"18/19赛季西甲第9轮全场集锦：巴塞罗那4:2塞维利亚","type":"1","winner":"7724250"}],"matchEventTime":"2018.10.21"}]
         */

        //播报语
        public String CompetitionBroadCastText;
        //赛事列表
        public List<MatchBean.MatchListBean> matchList;


        public static class MatchListBean {
            /**
             * matchEventInfo : [{"CompetitionCountDownTime":-1946220000,"CompetitionStatus":2,"_id":"130000076380","competitionId":"4","competitionLogo":"http://image.suning.cn/uimg/SDSP/competition/4.png?v=20181031","competitionName":"西甲","competitionType":"1","confrontTeamOneid":"7724250","confrontTeamOneimage":"http://image.suning.cn/uimg/SDSP/team/250.png?v=1498717895433","confrontTeamOnename":"巴塞罗那","confrontTeamOnescore":"4","confrontTeamTwoid":"7724244","confrontTeamTwoimage":"http://image.suning.cn/uimg/SDSP/team/244.png?v=1498717892078","confrontTeamTwoname":"塞维利亚","confrontTeamTwoscore":"2","endTime":"1540069200000","keyword":"10月21日02:45","matchStartTime":"02:45","pID":"648227431","projectId":"151","roundId":"9","seasonId":"1273","seasonName":"2018/2019","stageId":"2878","stageRoundName":"联赛第9轮","startTime":"1540061100000","time":"2018.10.21","title":"18/19赛季西甲第9轮全场集锦：巴塞罗那4:2塞维利亚","type":"1","winner":"7724250"}]
             * matchEventTime : 2018.10.21
             */

            public String matchEventTime;
            public List<MatchBean.MatchListBean.MatchEventInfoBean> matchEventInfo;


            public static class MatchEventInfoBean {
                /**
                 * CompetitionCountDownTime : -1946220000
                 * CompetitionStatus : 2
                 * mgdbId : 130000076380
                 * competitionId : 4
                 * competitionLogo : http://image.suning.cn/uimg/SDSP/competition/4.png?v=20181031
                 * competitionName : 西甲
                 * competitionType : 1
                 * confrontTeamOneid : 7724250
                 * confrontTeamOneimage : http://image.suning.cn/uimg/SDSP/team/250.png?v=1498717895433
                 * confrontTeamOnename : 巴塞罗那
                 * confrontTeamOnescore : 4
                 * confrontTeamTwoid : 7724244
                 * confrontTeamTwoimage : http://image.suning.cn/uimg/SDSP/team/244.png?v=1498717892078
                 * confrontTeamTwoname : 塞维利亚
                 * confrontTeamTwoscore : 2
                 * endTime : 1540069200000
                 * keyword : 10月21日02:45
                 * matchStartTime : 02:45
                 * pID : 648227431
                 * projectId : 151
                 * roundId : 9
                 * seasonId : 1273
                 * seasonName : 2018/2019
                 * stageId : 2878
                 * stageRoundName : 联赛第9轮
                 * startTime : 1540061100000
                 * time : 2018.10.21
                 * title : 18/19赛季西甲第9轮全场集锦：巴塞罗那4:2塞维利亚
                 * type : 1
                 * winner : 7724250
                 * CompetitionTimeDesc : 7天赛事日期标识 默认当天是4 ，前一天-1，前两天是-2 ；后一天是1 ，后两天是2
                 */

                public int CompetitionCountDownTime;
                public int CompetitionStatus;
                public String mgdbId;
                public String competitionId;
                public String competitionLogo;
                public String competitionName;
                public String competitionType;
                public String confrontTeamOneid;
                public String confrontTeamOneimage;
                public String confrontTeamOnename;
                public String confrontTeamOnescore;
                public String confrontTeamTwoid;
                public String confrontTeamTwoimage;
                public String confrontTeamTwoname;
                public String confrontTeamTwoscore;
                public String endTime;
                public String keyword;
                public String matchStartTime;
                public String pID;
                public String projectId;
                public String roundId;
                public String seasonId;
                public String seasonName;
                public String stageId;
                public String stageRoundName;
                public String startTime;
                public String time;
                public String title;
                public String type;
                public String winner;
                public String CompetitionTimeDesc;

            }
        }
    }
}
