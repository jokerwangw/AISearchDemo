package com.cmcc.cmvideo.search.model;

/**
 * @Author lhluo
 * @description 点击查看更多上次文本
 * @date 2018/8/10
 */
public class LastTextDataBean {
    private String lastText ;

    public LastTextDataBean(String lastText) {
        this.lastText = lastText;
    }

    public String getLastText() {
        return lastText;
    }

    public void setLastText(String lastText) {
        this.lastText = lastText;
    }
}
