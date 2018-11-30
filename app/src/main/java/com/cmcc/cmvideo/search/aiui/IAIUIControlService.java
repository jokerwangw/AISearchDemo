package com.cmcc.cmvideo.search.aiui;

/**
 * Created by Yyw on 2018/11/18.
 * Describe:
 */
public interface IAIUIControlService {
    /**
     * 开始录音
     */
    void startRecordAudio();

    /**
     * 停止录音
     */
    void stopRecordAudio();

    /**
     * 添加AIUIService中事件监听
     *
     * @param resultDispatchListener 监听对象
     */
    void addAIUIEventListener(AIUIControlService.AIUIEventListener resultDispatchListener);

    /**
     * 移除AIUIService中事件监听
     *
     * @param resultDispatchListener 监听对象
     */
    void removeAIUIEventListener(AIUIControlService.AIUIEventListener resultDispatchListener);

}