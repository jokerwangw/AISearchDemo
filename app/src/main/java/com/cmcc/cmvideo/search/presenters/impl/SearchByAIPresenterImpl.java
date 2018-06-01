package com.cmcc.cmvideo.search.presenters.impl;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

import com.cmcc.cmvideo.base.AbstractPresenter;
import com.cmcc.cmvideo.base.Executor;
import com.cmcc.cmvideo.base.MainThread;
import com.cmcc.cmvideo.search.aiui.AIUIService;
import com.cmcc.cmvideo.search.aiui.IAIUIService;
import com.cmcc.cmvideo.search.aiui.Logger;
import com.cmcc.cmvideo.search.aiui.bean.NlpData;
import com.cmcc.cmvideo.search.aiui.bean.TppData;
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
import com.iflytek.aiui.AIUIConstant;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.cmcc.cmvideo.utils.Constants.MESSAGE_FROM_AI;
import static com.cmcc.cmvideo.utils.Constants.MESSAGE_FROM_USER;
import static com.cmcc.cmvideo.utils.Constants.MESSAGE_TYPE_EVERYONE_IS_WATCHING;
import static com.cmcc.cmvideo.utils.Constants.MESSAGE_TYPE_GUESS_WHAT_YOU_LIKE;
import static com.cmcc.cmvideo.utils.Constants.MESSAGE_TYPE_NORMAL;
import static com.cmcc.cmvideo.utils.Constants.MESSAGE_TYPE_THE_LATEST_VIDEO;

/**
 * Created by Yyw on 2018/5/21.
 * Describe:
 */

public class SearchByAIPresenterImpl extends AbstractPresenter implements
        SearchByAIPresenter, AIUIService.AIUIEventListener,
        InitSearchByAIListInteractor.Callback,
        UpdateUserRequestListInteractor.Callback,
        UpdateAIResponseListInteractor.Callback {

    private Context mContext;
    private SearchByAIPresenter.View mView;
    private IAIUIService aiuiService;
    private Gson gson;
    private String intent;
    private NlpData mData = null;

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
        Map<String,String> map = new HashMap<String,String>(){{
            put("msisdn","13764279837");
            put("user_id","553782460");
            put("client_id","897ddadc222ec9c20651da355daee9cc");
        }};
        aiuiService.setAIUIEventListener(this);
        aiuiService.setUserParam(map);
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

    public void onIatResult(String result) {
        if (TextUtils.isEmpty(result)) return;
        Logger.debug("听写用户输入数据=====" + result);
        List<SearchByAIBean> userRequestList = new ArrayList<SearchByAIBean>();
        userRequestList.add(new SearchByAIBean(result, MESSAGE_TYPE_NORMAL, MESSAGE_FROM_USER));
        EventBus.getDefault().post(new SearchByAIEventBean(userRequestList));
    }

    public void onNlpResult(String result) {
        if (TextUtils.isEmpty(result)) return;
        mData = gson.fromJson(result, NlpData.class);
        if (mData.rc == 4) {
            //播报
            aiuiService.tts(AiuiConstants.ERROR_MESSAGE, null);
            return;
        }
        String service = mData.service;

        if (null != mData.semantic) {
            intent = mData.semantic.get(0).getIntent();
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
            case AiuiConstants.VIDEO_SERVICE:
                intentVideo(mData);
                break;
            case AiuiConstants.CONTROL_MIGU:
                //指令控制  如：打开语音助手/投屏播放
                intentControl(mData, intent);
                break;

        }

    }

    public void onTppResult(String result) {
        if (TextUtils.isEmpty(result))
            return;
        NlpData nlpData = gson.fromJson(result, NlpData.class);
        if (nlpData.rc == 4
                || !"video".equals(nlpData.service)
                || nlpData.data == null
                || nlpData.data.lxresult == null)
            return;

        switch (nlpData.semantic.get(0).intent){
            case AiuiConstants.QUERY_INTENT:
                Map<String,String> map = formatSlotsToMap(nlpData.semantic.get(0).slots);
                int messageType = MESSAGE_TYPE_NORMAL;
                while (true){
                    //TODO 判断意图是使用哪个卡片展示
                    if(map.containsKey(AiuiConstants.VIDEO_CATEGORY)&&
                            map.get(AiuiConstants.VIDEO_CATEGORY).equals("电影")){ //猜你喜欢
                        messageType = MESSAGE_TYPE_GUESS_WHAT_YOU_LIKE;
                        break;
                    }
                    if(map.containsKey(AiuiConstants.VIDEO_TIME_DESCR)){ // 有时间 的表示最近好看的电影
                        messageType = MESSAGE_TYPE_THE_LATEST_VIDEO;
                        break;
                    }
                    if(map.containsKey(AiuiConstants.VIDEO_TAG)){  //带标签的表示大家都在看
                        messageType = MESSAGE_TYPE_EVERYONE_IS_WATCHING;
                        break;
                    }
                    break;
                }

                if (nlpData.data.lxresult.data.detailslist != null && nlpData.data.lxresult.data.detailslist.size() > 0) {
                    aiuiService.tts("为你找到" + nlpData.data.lxresult.data.detailslist.size() + "个结果", null);
                    final List<SearchByAIBean> responseList = new ArrayList<SearchByAIBean>();
                    for(TppData.DetailsListBean detail: nlpData.data.lxresult.data.detailslist){
                        //TODO 添加影片到列表
                        responseList.add(new SearchByAIBean(nlpData.answer.text, messageType, MESSAGE_FROM_AI));
                    }
                    EventBus.getDefault().post(new SearchByAIEventBean(responseList));
                }
                break;
        }
    }


    /**
     * 处理查询指令
     *
     * @param intent
     */
    private void intentQuery(String intent) {
        switch (intent) {
            // TODO: 2018/5/31 跳转页面操作
            case AiuiConstants.MEMBER_INTENT:
                //会员业务
                Logger.debug("会员业务意图===" + intent);


                break;
            case AiuiConstants.INTERNET_INTENT:
                //流量业务
                Logger.debug("流量意图===" + intent);


                break;
            case AiuiConstants.TICKET_INTENT:
                //购票业务
                Logger.debug("购票意图===" + intent);


                break;
            case AiuiConstants.ACYIVITY_INTENT:
                //活动打折业务
                Logger.debug("活动意图===" + intent);

                break;
            case AiuiConstants.GCUSTOMER_INTENT:
                //G客业务，如：上传视频
                Logger.debug("G客意图===" + intent);
                break;
        }


    }


    /**
     * 处理控制指令
     *
     * @param intent
     */
    private void intentControl(NlpData mData, String intent) {
        switch (intent) {
            case AiuiConstants.CONTROL_INTENT:
                // TODO: 2018/5/30 控制指令跳转
                aiuiService.tts("正在为您" + mData.text, null);
                break;
            case AiuiConstants.SREEN_INTENT:
                // TODO: 2018/5/30 投屏跳转
                aiuiService.tts("正在为您" + mData.text, null);
                break;
        }

    }

    /**
     * 处理闲聊技能
     */
    private void intentQa(String nlpHandle) {
        NlpData nlpData = gson.fromJson(nlpHandle, NlpData.class);
        aiuiService.tts(nlpData.getAnswer().text, null);
        List<SearchByAIBean> userRequestList = new ArrayList<SearchByAIBean>();
        userRequestList.add(new SearchByAIBean(nlpData.getAnswer().text, MESSAGE_TYPE_NORMAL, MESSAGE_FROM_AI));
        EventBus.getDefault().post(new SearchByAIEventBean(userRequestList));
    }

    /**
     * 处理视频技能
     */
    private void intentVideo(NlpData nlpData) {
        if (nlpData.answer != null && !TextUtils.isEmpty(nlpData.answer.text)) {
            aiuiService.tts(nlpData.answer.text, null);
            final List<SearchByAIBean> responseList = new ArrayList<SearchByAIBean>();
            responseList.add(new SearchByAIBean(nlpData.answer.text, MESSAGE_TYPE_NORMAL, MESSAGE_FROM_AI));
            EventBus.getDefault().post(new SearchByAIEventBean(responseList));
        }
    }


    @Override
    public void onResult(String iatResult, String nlpReslult, String tppResult) {
        onIatResult(iatResult);
        onNlpResult(nlpReslult);
        onTppResult(tppResult);
    }

    @Override
    public void onEvent(int eventType) {
        switch (eventType){
            case AIUIConstant.EVENT_WAKEUP:
                //TODO AIUI 被唤醒
                break;
            case AIUIConstant.EVENT_SLEEP:
                //TODO AIUI 进入休眠 ，可以更新UI
                break;
        }
    }
    //SlotsBean key-value 数据转换成Map 类型数据方便查找
    private Map<String,String> formatSlotsToMap(List<NlpData.SlotsBean> slotsBeans){
        Map<String,String> map = new HashMap<>();
        if(slotsBeans == null||slotsBeans.size() ==0)
            return map;
        for(NlpData.SlotsBean slot:slotsBeans){
            map.put(slot.name,slot.value);
        }
        return map;
    }
}
