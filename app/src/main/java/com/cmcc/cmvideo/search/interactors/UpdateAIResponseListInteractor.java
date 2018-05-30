package com.cmcc.cmvideo.search.interactors;

import com.cmcc.cmvideo.base.Interactor;
import com.cmcc.cmvideo.search.model.SearchByAIBean;

import java.util.List;

/**
 * Created by Yyw on 2018/5/22.
 * Describe:
 */

public interface UpdateAIResponseListInteractor extends Interactor {
    interface Callback {
        void onUpdateAIResponseListData(List<SearchByAIBean> searchByAIBeanList);
    }
}
