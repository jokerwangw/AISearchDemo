package com.cmcc.cmvideo.search.presenters;

import com.cmcc.cmvideo.base.BasePresenter;
import com.cmcc.cmvideo.base.BaseView;
import com.cmcc.cmvideo.search.model.SearchByAIBean;

import java.util.List;

/**
 * Created by Yyw on 2018/6/11.
 * Describe:
 */

public interface LookMorePresenter  extends BasePresenter {
    interface View extends BaseView {
        void showInitList();
    }

    void initListItem();
}
