package com.cmcc.cmvideo.search.aiui;

import android.content.Context;

import com.cmcc.cmvideo.search.aiui.bean.NavigationBean;
import com.cmcc.cmvideo.search.aiui.bean.NlpData;
import com.cmcc.cmvideo.search.aiui.bean.TppData;
import com.cmcc.cmvideo.search.aiui.impl.LiveEnum;

public interface INavigation {
    /**
     * 播放视频
     * @param navBean
     */
    void playVideo(NavigationBean<TppData.DetailsListBean> navBean);
    /**
     * 播放剧集
     * @param navBean
     */
    void playEpisode(NavigationBean<TppData.SubserialsBean> navBean);

    /**
     * 会员业务
     */
    void toMember();

    /**
     * 流量业务
     */
    void toInternet();

    /**
     * 活动打折业务业务
     */
    void toActivity();
    /**
     * 购票业务
     */
    void toTicket();

    /**
     * G客业务，如：上传视频
     */
    void toGcustomer();

    /**
     * Live模块分类跳转
     * @param liveType 直播台类型
     */
    void toLive(LiveEnum liveType);

    /**
     * Live模块分类跳转
     * 直播模块详情
     */
    void toLive(NlpData mData);

    /**
     * 是否插入耳机
     */
    void isHeadset(boolean isConnectEarphone);
}
