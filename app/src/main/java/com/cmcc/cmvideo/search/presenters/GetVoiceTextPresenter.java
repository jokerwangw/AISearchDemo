package com.cmcc.cmvideo.search.presenters;

import com.cmcc.cmvideo.base.BasePresenter;
import com.cmcc.cmvideo.base.BaseView;
import com.cmcc.cmvideo.search.aiui.IAIUIControlService;
import com.cmcc.cmvideo.search.aiui.IAIUIService;
import com.cmcc.cmvideo.search.model.SearchByAIBean;

import java.util.List;

/**
 * Created by Yyw on 2018/11/13.
 * Describe:
 */
public interface GetVoiceTextPresenter extends BasePresenter {
    interface View extends BaseView {
        /**
         * 回调显示语音文本
         *
         * @param isError
         * @param voiceText
         */
        void showVoiceText(boolean isError, String voiceText);
    }

    /**
     * 传参
     *
     * @param service
     */
    void setAIUIService(IAIUIControlService service);

    /**
     * 开始录音
     */
    void startRecording();

    /**
     * 结束录音
     */
    void stopRecording();
}
