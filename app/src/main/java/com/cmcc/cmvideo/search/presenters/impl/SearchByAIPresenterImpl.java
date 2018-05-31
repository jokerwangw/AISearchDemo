package com.cmcc.cmvideo.search.presenters.impl;

import android.content.Context;
import android.text.TextUtils;

import com.cmcc.cmvideo.base.AbstractPresenter;
import com.cmcc.cmvideo.base.Executor;
import com.cmcc.cmvideo.base.MainThread;
import com.cmcc.cmvideo.search.aiui.AIUIService;
import com.cmcc.cmvideo.search.aiui.IAIUIService;
import com.cmcc.cmvideo.search.aiui.bean.NlpData;
import com.cmcc.cmvideo.search.interactors.InitSearchByAIListInteractor;
import com.cmcc.cmvideo.search.interactors.UpdateAIResponseListInteractor;
import com.cmcc.cmvideo.search.interactors.UpdateUserRequestListInteractor;
import com.cmcc.cmvideo.search.interactors.impl.InitSearchByAIListInteractorImpl;
import com.cmcc.cmvideo.search.interactors.impl.UpdateAIResponseListInteractorImpl;
import com.cmcc.cmvideo.search.interactors.impl.UpdateUserRequestListInteractorImpl;
import com.cmcc.cmvideo.search.model.SearchByAIBean;
import com.cmcc.cmvideo.search.model.SearchByAIEventBean;
import com.cmcc.cmvideo.search.presenters.SearchByAIPresenter;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import static com.cmcc.cmvideo.utils.Constants.MESSAGE_FROM_AI;
import static com.cmcc.cmvideo.utils.Constants.MESSAGE_TYPE_NORMAL;

/**
 * Created by Yyw on 2018/5/21.
 * Describe:
 */

public class SearchByAIPresenterImpl extends AbstractPresenter implements
        SearchByAIPresenter, AIUIService.ResultDispatchListener,
        InitSearchByAIListInteractor.Callback,
        UpdateUserRequestListInteractor.Callback,
        UpdateAIResponseListInteractor.Callback {

    private Context mContext;
    private SearchByAIPresenter.View mView;
    private IAIUIService aiuiService;
    private Gson gson;

    public SearchByAIPresenterImpl(
            Executor executor,
            MainThread mainThread,
            SearchByAIPresenter.View view,
            Context context) {
        super(executor, mainThread);
        mView = view;
        mContext = context;
        gson = new Gson();
    }

    @Override
    public void resume() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void stop() {
    }

    @Override
    public void destroy() {
    }

    @Override
    public void onError(String message) {
    }

    /**
     * 这里做初始化数据的操作
     */
    @Override
    public void initListSearchItem() {
        InitSearchByAIListInteractor initSearchByAIListInteractor = new InitSearchByAIListInteractorImpl(mExecutor, mMainThread, this);
        initSearchByAIListInteractor.execute();
    }

    /**
     * 这里做用户发送请求数据的操作
     */
    @Override
    public void updateUserRequestListItem() {
        UpdateUserRequestListInteractor updateUserRequestListInteractor = new UpdateUserRequestListInteractorImpl(mExecutor, mMainThread, this);
        updateUserRequestListInteractor.execute();
    }

    /**
     * 这里做AI响应用户请求数据的操作
     *
     * @param order
     */
    @Override
    public void updateAIResponseListItem(String order) {
        UpdateAIResponseListInteractor updateAIResponseListInteractor = new UpdateAIResponseListInteractorImpl(mExecutor, mMainThread, order, this);
        updateAIResponseListInteractor.execute();
    }

    @Override
    public void setAIUIService(IAIUIService service) {
        aiuiService = service;
        aiuiService.setResultDispatchListener(this);
    }

    @Override
    public void onInitSearchByAIListData(List<SearchByAIBean> searchByAIBeanList) {
        mView.showInitList(searchByAIBeanList);
    }


    @Override
    public void onUpdateUserRequestListData(List<SearchByAIBean> searchByAIBeanList) {
        if (null != searchByAIBeanList && searchByAIBeanList.size() != 0) {
            EventBus.getDefault().post(new SearchByAIEventBean(searchByAIBeanList));
            updateAIResponseListItem(searchByAIBeanList.get(searchByAIBeanList.size() - 1).getMessage());
        }
    }

    @Override
    public void onUpdateAIResponseListData(List<SearchByAIBean> searchByAIBeanList) {
        EventBus.getDefault().post(new SearchByAIEventBean(searchByAIBeanList));
    }

    @Override
    public void onIatResult(String result) {

    }

    @Override
    public void onNlpResult(String result) {

    }

    @Override
    public void onTppResult(String result) {
        if(TextUtils.isEmpty(result))
            return;
        NlpData nlpData =  gson.fromJson(result,NlpData.class);
        if(nlpData.rc==4
                ||!"video".equals(nlpData.service)
                ||nlpData.data ==null
                ||nlpData.data.lxresult == null)
            return;
        if(nlpData.data.lxresult.data.detailslist!=null&&nlpData.data.lxresult.data.detailslist.size()>0){
            aiuiService.tts("为你找到"+nlpData.data.lxresult.data.detailslist.size()+"个结果",null);
            //TODO 展示图片列表
        }else if(nlpData.answer!=null&&!TextUtils.isEmpty(nlpData.answer.getText())){
            aiuiService.tts(nlpData.answer.getText(),null);
            final List<SearchByAIBean> responseList = new ArrayList<SearchByAIBean>();
            responseList.add(new SearchByAIBean(nlpData.answer.getText(), MESSAGE_TYPE_NORMAL, MESSAGE_FROM_AI));
            EventBus.getDefault().post(new SearchByAIEventBean(responseList));
        }
    }
}
