package com.cmcc.cmvideo.search.presenters;

import android.os.Bundle;

import com.cmcc.cmvideo.base.BasePresenter;
import com.cmcc.cmvideo.base.BaseView;
import com.cmcc.cmvideo.search.aiui.IAIUIService;
import com.cmcc.cmvideo.search.aiui.bean.TppData;
import com.cmcc.cmvideo.search.model.SearchByAIBean;

import java.util.List;

/**
 * Created by Yyw on 2018/6/11.
 * Describe:
 */

public interface LookMorePresenter extends BasePresenter {
    interface View extends BaseView {
        /**
         * 回调显示数据
         *
         * @param detailsListBeanArrayList
         */
        void showInitList(List<TppData.DetailsListBean> detailsListBeanArrayList);
    }

    /**
     * setAIUIService
     *
     * @param service
     */
    void setAIUIService(IAIUIService service);

    /**
     * setSpeechText
     *
     * @param text
     */
    void setSpeechText(String text);
}
