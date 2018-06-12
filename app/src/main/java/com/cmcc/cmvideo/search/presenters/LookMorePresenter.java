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

public interface LookMorePresenter  extends BasePresenter {
    interface View extends BaseView {
        void showInitList(List<TppData.DetailsListBean> detailsListBeanArrayList);
    }
    void setAIUIService(IAIUIService service);
    void setSpeechText(String text);
}
