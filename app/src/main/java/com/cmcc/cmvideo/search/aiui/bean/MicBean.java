package com.cmcc.cmvideo.search.aiui.bean;

/**
 * @Author lhluo
 * @description 检测耳机是否接入
 * @date 2018/6/6
 */
public class MicBean {
    private boolean isConnect ;

    public MicBean(boolean isConnect) {
        this.isConnect = isConnect;
    }

    public boolean isConnect() {
        return isConnect;
    }

    public void setConnect(boolean connect) {
        isConnect = connect;
    }
}
