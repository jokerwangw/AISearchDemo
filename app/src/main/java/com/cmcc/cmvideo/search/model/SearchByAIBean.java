package com.cmcc.cmvideo.search.model;

import com.cmcc.cmvideo.search.aiui.bean.TppData;

import java.util.List;

/**
 * Created by Yyw on 2018/5/22.
 * Describe:
 */

public class SearchByAIBean {
    private String message;
    private String deailsJson;
    private int messageType;
    private String messageFrom;
    private List<TppData.DetailsListBean> videoList;
    private List<TppData.MatchBean> matchList;



    public SearchByAIBean() {
    }

    public SearchByAIBean(String message, int messageType, String messageFrom) {
        this(message, messageType, messageFrom, null);
    }

    public SearchByAIBean(String message, int messageType, String messageFrom, List<TppData.DetailsListBean> videoList) {
        this.message = message;
        this.messageType = messageType;
        this.messageFrom = messageFrom;
        this.videoList = videoList;
    }

    /**
     * 体育赛事模块
     * @param messageType
     * @param matchList
     * @param messageFrom
     */
    public SearchByAIBean(int messageType,  List<TppData.MatchBean> matchList,String messageFrom) {
        this.messageType = messageType;
        this.matchList = matchList;
        this.messageFrom = messageFrom;
    }

    public String getDeailsJson() {
        return deailsJson;
    }

    public void setDeailsJson(String deailsJson) {
        this.deailsJson = deailsJson;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getMessageType() {
        return messageType;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }

    public String getMessageFrom() {
        return messageFrom;
    }

    public void setMessageFrom(String messageFrom) {
        this.messageFrom = messageFrom;
    }

    public List<TppData.DetailsListBean> getVideoList() {
        return videoList;
    }

    public void setVideoList(List<TppData.DetailsListBean> videoList) {
        this.videoList = videoList;
    }

    public List<TppData.MatchBean> getMatchList() {
        return matchList;
    }

    public void setMatchList(List<TppData.MatchBean> matchList) {
        this.matchList = matchList;
    }
}
