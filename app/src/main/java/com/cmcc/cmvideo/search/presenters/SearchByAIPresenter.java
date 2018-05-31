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
        void showInitList(List<SearchByAIBean> searchByAIBeanList);
    }

    void initListSearchItem();

    void updateUserRequestListItem();

    void updateAIResponseListItem(String order);

    void setAIUIService(IAIUIService service);
}
