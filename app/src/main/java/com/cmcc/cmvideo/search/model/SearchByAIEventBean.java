package com.cmcc.cmvideo.search.model;
import java.util.List;

/**
 * Created by Yyw on 2018/5/22.
 * Describe:
 */

public class SearchByAIEventBean extends EventBusMessage{
    private List<SearchByAIBean> searchByAIBeanList;

    public SearchByAIEventBean(List<SearchByAIBean> searchByAIBeanList) {
        this.searchByAIBeanList = searchByAIBeanList;
    }

    public List<SearchByAIBean> getSearchByAIBeanList() {
        return searchByAIBeanList;
    }
}
