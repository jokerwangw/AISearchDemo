package com.cmcc.cmvideo.search.model;

/**
 * Created by Yyw on 2018/5/22.
 * Describe:
 */

public class SearchByAIBean {
    private String message;
    private int messageType;
    private String messageFrom;

    public SearchByAIBean() {
    }

    public SearchByAIBean(String message, int messageType, String messageFrom) {
        this.message = message;
        this.messageType = messageType;
        this.messageFrom = messageFrom;
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
}
