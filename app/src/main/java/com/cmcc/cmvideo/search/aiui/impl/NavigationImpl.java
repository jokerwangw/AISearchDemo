package com.cmcc.cmvideo.search.aiui.impl;

import com.cmcc.cmvideo.search.aiui.INavigation;
import com.cmcc.cmvideo.search.aiui.bean.NavigationBean;
import com.cmcc.cmvideo.search.aiui.bean.TppData;

public class NavigationImpl implements INavigation {

    /**
     * 播放电影
     * @param videoBean 电影数据
     */
    @Override
    public void playVideo(NavigationBean<TppData.DetailsListBean> videoBean) {

    }

    /**
     * 播放剧集
     * @param epiSodeBean 剧集数据
     */
    @Override
    public void playEpisode(NavigationBean<TppData.SubserialsBean> epiSodeBean) {

    }

    /**
     * 跳转到会员业务页面
     */
    @Override
    public void toMember() {

    }

    /**
     * 跳转到流量业务页面
     */
    @Override
    public void toInternet() {

    }

    /**
     * 跳转到活动打折业务
     */
    @Override
    public void toActivity() {

    }

    /**
     * 跳转到购票业务
     */
    @Override
    public void toTicket() {

    }

    /**
     * G客业务，如：上传视频
     */
    @Override
    public void toGcustomer() {

    }

    /**
     * 频道查询 如：我要看CCTV5体育 / 湖南卫视
     * @param liveId 频道ID
     * @param liveName 频道名称
     */
    @Override
    public void toLive(String liveId, String liveName) {

    }

    /**
     * Live模块分类跳转
     * @param liveType 直播台类型
     */
    @Override
    public void toLive(LiveEnum liveType) {
        switch (liveType){
            case CCTV:
                //跳转 到 央视
                break;
            case START_TV:
                //跳转 到 卫视
                break;
            case FILMS:
                //跳转 到 影视
                break;
            case CHILDREN:
                //跳转 到 少儿
                break;
            case NEWS:
                //跳转 到 新闻
                break;
            case DOCUMENTARY:
                //跳转 到 纪录片
                break;
            case TURN_TV:
                //跳转 到 轮播台
                break;
            case AREA_TV:
                //跳转 到 地方台
                break;
            case FEATURE_TV:
                //跳转 到 特色台
                break;
            case SPORTS_TV:
                //跳转 到 体育频道
                break;
            case UNKNOWN:
                //跳转到直播大类页面
                break;
        }
    }
}
