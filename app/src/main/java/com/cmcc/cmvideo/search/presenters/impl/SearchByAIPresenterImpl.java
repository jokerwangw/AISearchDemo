package com.cmcc.cmvideo.search.presenters.impl;

import android.content.Context;
import android.os.Looper;
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
import com.cmcc.cmvideo.search.model.SearchByAIRefreshUIEventBean;
import com.cmcc.cmvideo.search.presenters.SearchByAIPresenter;
import com.cmcc.cmvideo.utils.AiuiConstants;
import com.google.gson.Gson;
import com.iflytek.aiui.AIUIConstant;
import com.iflytek.aiui.AIUIEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Handler;

import static com.cmcc.cmvideo.utils.Constants.*;

/**
 * Created by Yyw on 2018/5/21.
 * Describe:
 */

public class SearchByAIPresenterImpl extends AbstractPresenter implements SearchByAIPresenter, AIUIService.AIUIEventListener, InitSearchByAIListInteractor.Callback, UpdateUserRequestListInteractor.Callback, UpdateAIResponseListInteractor.Callback {
    private final String TAG = "SearchByAIPresenterImpl";
    private Context mContext;
    private SearchByAIPresenter.View mView;
    private IAIUIService aiuiService;
    private Gson gson;
    private String intent;
    private NlpData mData = null;
    private android.os.Handler mHandler;
    private long startTime = 0;
    private final int TIME_OUT = 5000;

    public SearchByAIPresenterImpl(Executor executor, MainThread mainThread, SearchByAIPresenter.View view, Context context) {
        super(executor, mainThread);
        mView = view;
        mContext = context;
        gson = new Gson();
        mHandler = new android.os.Handler(Looper.getMainLooper());
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
        Map<String, String> map = new HashMap<String, String>() {{
            put("msisdn", "13764279837");
            put("user_id", "553782460");
            put("client_id", "897ddadc222ec9c20651da355daee9cc");
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
        sendMessage(result, MESSAGE_TYPE_NORMAL, MESSAGE_FROM_USER);
    }

    public void onNlpResult(String result) {
        if (TextUtils.isEmpty(result)) return;
        mData = gson.fromJson(result, NlpData.class);
        if (mData.rc == 4) {
            //播报
            if ((System.currentTimeMillis() - startTime) > TIME_OUT) {
                // 超过5秒表示 且rc=4（无法解析出语义） ，可显示推荐说法卡片
                sendMessage("", MESSAGE_TYPE_CAN_ASK_AI, MESSAGE_FROM_AI);
            } else {
                aiuiService.tts(AiuiConstants.ERROR_MESSAGE, null);
            }
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
            case AiuiConstants.CONTROL_MIGU:
                //指令控制  如：打开语音助手/投屏播放
                intentControl(mData, intent);
                break;

        }

    }

    public void onTppResult(String result) {
        if (TextUtils.isEmpty(result)) return;
        NlpData nlpData = gson.fromJson(result, NlpData.class);
        //判断是否解出了语义，并且当前技能是video
        if (nlpData.rc == 4 || !"video".equals(nlpData.service)) return;

        if (nlpData.data == null && nlpData.answer != null && !TextUtils.isEmpty(nlpData.answer.text)) {
            //没有影片数据且存在answer 则播报
            aiuiService.tts(nlpData.answer.text, null);
            sendMessage(nlpData.answer.text, MESSAGE_TYPE_NORMAL, MESSAGE_FROM_AI);
            return;
        }
        //语义后处理没有返回数据则直接退出
        if (nlpData.data.lxresult == null) return;

        switch (nlpData.semantic.get(0).intent) {
            case AiuiConstants.QUERY_INTENT:
                Map<String, String> map = formatSlotsToMap(nlpData.semantic.get(0).slots);
                int messageType = MESSAGE_TYPE_NORMAL;
                if (nlpData.data.lxresult.data.detailslist != null &&
                        nlpData.data.lxresult.data.detailslist.size() > 0) {
                    while (true) {
                        //TODO 判断意图是使用哪个卡片展示
                        if (map.containsKey(AiuiConstants.VIDEO_CATEGORY)) { //猜你喜欢
                            String category = map.get(AiuiConstants.VIDEO_CATEGORY);
                            boolean hasSubserials = hasSubserials(nlpData);
                            if (category.equals("电影")) {
                                messageType = MESSAGE_TYPE_GUESS_WHAT_YOU_LIKE;
                            } else if ((category.equals("电视剧") || category.equals("纪实") || category.equals("动漫")) && hasSubserials) {
                                messageType = MESSAGE_TYPE_GUESS_WHAT_YOU_LIKE_LIST_HORIZONTAL;
                            } else if(category.equals("综艺")&& hasSubserials){
                                messageType = MESSAGE_TYPE_GUESS_WHAT_YOU_LIKE_LIST_VERTICAL;
                            }
                            break;
                        }
                        if (map.containsKey(AiuiConstants.VIDEO_TIME_DESCR)) { // 有时间 的表示最近好看的电影
                            messageType = MESSAGE_TYPE_THE_LATEST_VIDEO;
                            break;
                        }
                        if (map.containsKey(AiuiConstants.VIDEO_TAG) || map.containsKey(AiuiConstants.VIDEO_NAME)) {  //带标签的表示大家都在看
                            messageType = MESSAGE_TYPE_EVERYONE_IS_WATCHING;
                            break;
                        }
                        break;
                    }
                }
                sendMessage(nlpData.answer != null ? nlpData.answer.text : "", messageType, MESSAGE_FROM_AI, nlpData.data.lxresult.data.detailslist);
                break;
        }
    }

    /**
     * 是否存在剧集数据
     *
     * @param nlpData
     * @return
     */
    private boolean hasSubserials(NlpData nlpData) {
        if (nlpData.data.lxresult.data.detailslist == null || nlpData.data.lxresult.data.detailslist.size() == 0)
            return false;
        if (nlpData.data.lxresult.data.detailslist.get(0).subserials == null || nlpData.data.lxresult.data.detailslist.get(0).subserials.size() == 0)
            return false;
        return true;
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
        sendMessage(nlpData.getAnswer().text, MESSAGE_TYPE_NORMAL, MESSAGE_FROM_AI);
    }

    @Override
    public void onResult(String iatResult, String nlpReslult, String tppResult) {
        onIatResult(iatResult);
        onNlpResult(nlpReslult);
        onTppResult(tppResult);
    }

    @Override
    public void onEvent(AIUIEvent event) {
        switch (event.eventType) {
            case AIUIConstant.EVENT_WAKEUP:
                //TODO AIUI 被唤醒
                break;
            case AIUIConstant.EVENT_SLEEP:
                //TODO AIUI 进入休眠 ，可以更新UI
                break;
            case AIUIConstant.EVENT_ERROR:
                if (event.arg1 == 10120) {
                    // TODO 网络有点问题 ，超时
                }
                break;
            case AIUIConstant.EVENT_START_RECORD:
                // 录音开始就发送延时消息，当五秒内在sendMessage()方法中都没有移除消息时就说明 5秒超时了
                mHandler.postDelayed(runnable, TIME_OUT);
                startTime = System.currentTimeMillis();
                break;
            case AIUIConstant.EVENT_STOP_RECORD:
                break;
            case AIUIConstant.EVENT_VAD:
                Logger.debug(TAG, "arg【" + event.arg1 + "】【" + event.arg2 + "】");
                //用arg1标识前后端点或者音量信息:0(前端点)、1(音量)、2(后端点)、3（前端点超时）。
                //当arg1取值为1时，arg2为音量大小。
                if (event.arg1 == 0) {
                    //检测到前端点表示正在录音
                    mHandler.removeCallbacks(runnable);
                }
                break;
        }
        if (null != event) {
            EventBus.getDefault().post(new SearchByAIRefreshUIEventBean(event));
        }
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            // TODO 显示功能引导页面
            sendMessage("", MESSAGE_TYPE_CAN_ASK_AI, MESSAGE_FROM_AI);
        }
    };

    //SlotsBean key-value 数据转换成Map 类型数据方便查找
    private Map<String, String> formatSlotsToMap(List<NlpData.SlotsBean> slotsBeans) {
        Map<String, String> map = new HashMap<>();
        if (slotsBeans == null || slotsBeans.size() == 0) return map;
        for (NlpData.SlotsBean slot : slotsBeans) {
            map.put(slot.name, slot.value);
        }
        return map;
    }

    /**
     * 发送消息更新UI
     *
     * @param msg         消息内容
     * @param messageType 消息内容（普通闲聊内容，影片内容）
     * @param msgFrom     消息来源，
     */
    private void sendMessage(String msg, int messageType, String msgFrom) {
        sendMessage(msg, messageType, msgFrom, null);
    }

    /**
     * 发送消息更新UI
     *
     * @param msg         消息内容
     * @param messageType 消息内容（普通闲聊内容，影片内容）
     * @param msgFrom     消息来源，
     * @param videoList   影片内容影片数据，
     */
    private void sendMessage(String msg, int messageType, String msgFrom, List<TppData.DetailsListBean> videoList) {
        List<SearchByAIBean> messageList = new ArrayList<SearchByAIBean>();
        messageList.add(new SearchByAIBean(msg, messageType, msgFrom, videoList));
        EventBus.getDefault().post(new SearchByAIEventBean(messageList));
    }
}
