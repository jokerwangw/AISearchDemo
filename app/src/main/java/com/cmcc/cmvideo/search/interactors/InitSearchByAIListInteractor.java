package com.cmcc.cmvideo.search.interactors;


import com.cmcc.cmvideo.base.Interactor;
import com.cmcc.cmvideo.search.model.SearchByAIBean;

import java.util.List;

/**
 * Created by Yyw on 2018/5/21.
 * Describe:
 */

public interface InitSearchByAIListInteractor extends Interactor {
    interface Callback{
        void onInitSearchByAIListData(List<SearchByAIBean> searchByAIBeanList);
    }
}
