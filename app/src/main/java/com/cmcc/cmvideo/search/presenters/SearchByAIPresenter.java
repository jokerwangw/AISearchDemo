package com.cmcc.cmvideo.search.presenters;


import com.cmcc.cmvideo.base.BasePresenter;
import com.cmcc.cmvideo.base.BaseView;
import com.cmcc.cmvideo.search.aiui.IAIUIService;
import com.cmcc.cmvideo.search.aiui.bean.TppData;
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
         * @param position           刷新item第position个
         * @param searchByAIBeanList
         */
        void showSportsVideoList(int position, List<SearchByAIBean> searchByAIBeanList);
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

    /**
     * 刚启动时解析AIUIService 发来的数据
     *
     * @param jsonData
     */
    void analysisDefaultData(String jsonData);

    /**
     * 跳转播放
     *
     * @param type
     * @param isClickLookMore
     * @param detailsListBean
     * @param deailsJson
     * @param position
     */
    void turnToPlayVideo(int type, boolean isClickLookMore, TppData.DetailsListBean detailsListBean, String deailsJson, int position);

    /**
     * 查看更多
     */
    void lookMore(String title, String itemNlp);

    /**
     * 点击前一天
     *
     * @param position 刷新item第position个
     */
    void clickTheDayBefore(int position);

    /**
     * 点击后一天
     *
     * @param position 刷新item第position个
     */
    void clickTheNextDay(int position);
}
