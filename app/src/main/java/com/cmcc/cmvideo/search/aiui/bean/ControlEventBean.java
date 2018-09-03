package com.cmcc.cmvideo.search.aiui.bean;

/**
 * @Author lhluo
 * @description 控制指令实体控制 eventbus使用
 * @date 2018/6/19
 */
public class ControlEventBean {
    /**
     * 视频控制指令类型  播放  暂停  下一集  上一集  打开语音助手  关闭语音助手  投屏播放
     * public final static int VDO_PAUSE = 1;
     * public final static int VDO_PLAY = 2;
     * public final static int VDO_NEXT = 3;
     * public final static int VDO_PREVIOUS = 4;
     * public final static int VDO_OPEN = 5;
     * public final static int VDO_CLOSE = 6;
     * public final static int VDO_SCREEN = 7;
     * <p>
     * <p>
     * public final static int VDO_FASTWORD = 8;
     * public final static int VDO_BACKWORD = 9;
     * public final static int VDO_FASTWORD_TO = 10;
     * public final static int VDO_CHANGE = 11;
     * 快进 快退 快进（快退） 换一集  第几集
     */

    private int controlType;
    private String hour;
    private String minute;
    private String second;

    //第几集 的 几
    private String episode;

    public ControlEventBean() {
    }

    public ControlEventBean(int controlType) {
        this.controlType = controlType;
    }

    public ControlEventBean(int controlType, String episode) {
        this.controlType = controlType;
        this.episode = episode;
    }


    public ControlEventBean(int controlType, String hour, String minute, String second) {
        this.controlType = controlType;
        this.hour = hour;
        this.minute = minute;
        this.second = second;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public String getMinute() {
        return minute;
    }

    public void setMinute(String minute) {
        this.minute = minute;
    }

    public String getSecond() {
        return second;
    }

    public void setSecond(String second) {
        this.second = second;
    }

    public int getControlType() {
        return controlType;
    }

    public void setControlType(int controlType) {
        this.controlType = controlType;
    }
}
