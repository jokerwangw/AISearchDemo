package com.cmcc.cmvideo.search.presenters;


import com.cmcc.cmvideo.base.BasePresenter;
import com.cmcc.cmvideo.base.BaseView;
import com.cmcc.cmvideo.search.aiui.IAIUIService;
import com.cmcc.cmvideo.search.model.SearchByAIBean;

import java.util.List;

/**
 * Created by Yyw on 2018/5/21.
 * Describe:
 */

public interface SearchByAIPresenter extends BasePresenter {
    interface View extends BaseView {
        /**
         * 回调显示数据
         *
         * @param searchByAIBeanList
         */
        void showInitList(List<SearchByAIBean> searchByAIBeanList);

        /**
         * requestAudioFocus
         */
        void requestAudioFocus();

        /**
         * abandonAudioFocus
         */
        void abandonAudioFocus();
    }

    /**
     * 初始数据
     */
    void initListSearchItem();

    /**
     * 传参
     *
     * @param service
     */
    void setAIUIService(IAIUIService service);

    /**
     * cancelRecordAudio
     */
    void cancelRecordAudio();
}
