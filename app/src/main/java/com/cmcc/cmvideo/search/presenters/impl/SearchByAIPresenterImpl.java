package com.cmcc.cmvideo.search.presenters.impl;

import android.content.Context;
import android.text.TextUtils;

import com.cmcc.cmvideo.base.AbstractPresenter;
import com.cmcc.cmvideo.base.Executor;
import com.cmcc.cmvideo.base.MainThread;
import com.cmcc.cmvideo.search.aiui.AIUIService;
import com.cmcc.cmvideo.search.aiui.IAIUIService;
import com.cmcc.cmvideo.search.aiui.Logger;
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
import com.cmcc.cmvideo.utils.AiuiConstants;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import static com.cmcc.cmvideo.utils.Constants.MESSAGE_FROM_AI;
import static com.cmcc.cmvideo.utils.Constants.MESSAGE_FROM_USER;
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
    private String intent;

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
        Logger.debug("听写用户输入数据=====" + result);
        List<SearchByAIBean> userRequestList = new ArrayList<SearchByAIBean>();
        userRequestList.add(new SearchByAIBean(result, MESSAGE_TYPE_NORMAL, MESSAGE_FROM_USER));
        EventBus.getDefault().post(new SearchByAIEventBean(userRequestList));
    }

    @Override
    public void onNlpResult(String result) {
        if (TextUtils.isEmpty(result)) return;
        NlpData nlpData = gson.fromJson(result, NlpData.class);
        Logger.debug("nlp " + result + "应答码==="
                + nlpData.service + "-------------"
                + nlpData.answer.getText());
        if (4 == nlpData.rc) {
            //播报
            aiuiService.tts(AiuiConstants.ERROR_MESSAGE, null);
            return;
        }
        String service = nlpData.service;

        if (null != nlpData.semantic) {
            intent = nlpData.semantic.get(0).getIntent();
        }

        switch (service) {
            case AiuiConstants.QA_SERVICE:
                //闲聊
                intentQa(result);
                break;
            case AiuiConstants.CHANNEL_SERVICE:
                //频道 如想看央视5台

                break;
            case AiuiConstants.QUERY_MIGU:
                //业务查询与办理
                intentQuery(intent);
                break;


            case AiuiConstants.CONTROL_MIGU:
                //指令控制  如：打开语音助手/投屏播放
                intentControl(intent);
                break;

        }

    }

    @Override
    public void onTppResult(String result) {
        if (TextUtils.isEmpty(result))
            return;
        NlpData nlpData = gson.fromJson(result, NlpData.class);
        if (nlpData.rc == 4
                || !"video".equals(nlpData.service)
                || nlpData.data == null
                || nlpData.data.lxresult == null)
            return;
        if (nlpData.data.lxresult.data.detailslist != null && nlpData.data.lxresult.data.detailslist.size() > 0) {
            aiuiService.tts("为你找到" + nlpData.data.lxresult.data.detailslist.size() + "个结果", null);
            //TODO 展示图片列表
        } else if (nlpData.answer != null && !TextUtils.isEmpty(nlpData.answer.getText())) {
            aiuiService.tts(nlpData.answer.getText(), null);
            final List<SearchByAIBean> responseList = new ArrayList<SearchByAIBean>();
            responseList.add(new SearchByAIBean(nlpData.answer.getText(), MESSAGE_TYPE_NORMAL, MESSAGE_FROM_AI));
            EventBus.getDefault().post(new SearchByAIEventBean(responseList));
        }
    }


    /**
     * 处理查询指令
     *
     * @param intent
     */
    private void intentQuery(String intent) {
        switch (intent) {
            case AiuiConstants.MEMBER_INTENT:
                //会员业务

                break;
            case AiuiConstants.INTERNET_INTENT:
                //流量业务

                break;
            case AiuiConstants.TICKET_INTENT:
                //购票业务

                break;
            case AiuiConstants.ACYIVITY_INTENT:
                //活动打折业务

                break;
            case AiuiConstants.GCUSTOMER_INTENT:
                //G客业务，如：上传视频

                break;
        }


    }


    /**
     * 处理控制指令
     *
     * @param intent
     */
    private void intentControl(String intent) {
        switch (intent) {
            case AiuiConstants.CONTROL_INTENT:
                // TODO: 2018/5/30 控制指令跳转
                Logger.debug("控制指令intent===" + intent);

                break;
            case AiuiConstants.SREEN_INTENT:
                // TODO: 2018/5/30 投屏跳转
                Logger.debug("投屏指令intent===" + intent);
                break;
        }

    }

    /**
     * 处理闲聊技能
     */
    private void intentQa(String nlpHandle) {
        NlpData nlpData = gson.fromJson(nlpHandle, NlpData.class);
        aiuiService.tts(nlpData.getAnswer().getText(), null);
        List<SearchByAIBean> userRequestList = new ArrayList<SearchByAIBean>();
        userRequestList.add(new SearchByAIBean(nlpData.getAnswer().getText(), MESSAGE_TYPE_NORMAL, MESSAGE_FROM_AI));
        EventBus.getDefault().post(new SearchByAIEventBean(userRequestList));
        Logger.debug("闲聊数据Q&A ==== " + nlpData.text + "-----" + nlpData.getAnswer().getText());
    }


}
