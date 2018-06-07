package com.cmcc.cmvideo.search.presenters.impl;

import android.content.Context;
import android.content.res.Resources;
import android.os.Looper;
import android.text.TextUtils;

import com.cmcc.cmvideo.base.AbstractPresenter;
import com.cmcc.cmvideo.base.Executor;
import com.cmcc.cmvideo.base.MainThread;
import com.cmcc.cmvideo.search.aiui.AIUIService;
import com.cmcc.cmvideo.search.aiui.FuncAdapter;
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
import com.cmcc.cmvideo.util.AiResponse;
import com.cmcc.cmvideo.util.AiuiConstants;
import com.google.gson.Gson;
import com.iflytek.aiui.AIUIConstant;
import com.iflytek.aiui.AIUIEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static com.cmcc.cmvideo.util.Constants.*;

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
    private List<TppData.DetailsListBean> lastVideoList = null;
    private int lastResponseVideoMessageType = MESSAGE_TYPE_NORMAL;
    private int mCurrentState = AIUIConstant.STATE_IDLE;

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
        //上传用户数据
        aiuiService.setUserParam(map);
        //清理所见即可说的数据
        aiuiService.clearSpeakableData();
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
        String service = mData.service;
        if(!AiuiConstants.VIEWCMD_SERVICE.equals(service)) {
            Logger.debug("听写用户输入数据=====" + mData.text);
            sendMessage( mData.text, MESSAGE_TYPE_NORMAL, MESSAGE_FROM_USER);
        }
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
            case AiuiConstants.VIEWCMD_SERVICE:
                //viewCmd
                intentViewCmd(result);
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
        if (nlpData.rc == 4
                || !("video".equals(nlpData.service)
                ||"LINGXI2018.user_video".equals(nlpData.service)))
        {
            if(nlpData.moreResults ==null) {
                return;
            }
            nlpData = nlpData.moreResults.get(0);
            if (nlpData.rc == 4
                    || !("video".equals(nlpData.service)
                    || "LINGXI2018.user_video".equals(nlpData.service))){
                return;
            }
        }

        if (    (
                    nlpData.data == null
                    ||nlpData.data.lxresult==null
                    ||nlpData.data.lxresult.data.detailslist.size()==0
                )
                &&
                nlpData.answer != null
                && !TextUtils.isEmpty(nlpData.answer.text)) {
            //没有影片数据且存在answer 则播报
            aiuiService.tts(nlpData.answer.text, null);
            sendMessage(nlpData.answer.text, MESSAGE_TYPE_NORMAL, MESSAGE_FROM_AI);
            return;
        }
        //语义后处理没有返回数据则直接退出
        if (nlpData.data.lxresult == null
                ||!"000000".equals(nlpData.data.lxresult.code))
            return;
        AiResponse.Response responseTts = null;
        Map<String, String> map = formatSlotsToMap(nlpData.semantic.get(0).slots);
        switch (nlpData.semantic.get(0).intent) {
            case AiuiConstants.QUERY_INTENT:
                int messageType = MESSAGE_TYPE_NORMAL;
                if (nlpData.data.lxresult.data.detailslist != null &&
                        nlpData.data.lxresult.data.detailslist.size() > 0) {
                    while (true) {
                        // 判断意图是使用哪个卡片展示
                        if (map.size() ==1&&map.containsKey(AiuiConstants.VIDEO_CATEGORY)) { //猜你喜欢
                            AiResponse.Response response = AiResponse.getInstance().getGuessWhatYouLike();
                            String category = map.get(AiuiConstants.VIDEO_CATEGORY);
                            boolean hasSubserials = hasSubserials(nlpData);
                            if (category.equals("电影")) {
                                messageType = MESSAGE_TYPE_GUESS_WHAT_YOU_LIKE;
                            } else if ((category.equals("电视剧") || category.equals("纪实") || category.equals("动漫")) && hasSubserials) {
                                messageType = MESSAGE_TYPE_GUESS_WHAT_YOU_LIKE_LIST_HORIZONTAL;
                            } else if (category.equals("综艺") && hasSubserials) {
                                messageType = MESSAGE_TYPE_GUESS_WHAT_YOU_LIKE_LIST_VERTICAL;
                            }
                            //用户问的是电影 ，文字部分就是电影 ；用户没有指定某分类，文字部分就影是视频
                            if(response.respType == AiResponse.RespType.VIDEO_TYPE&&messageType ==MESSAGE_TYPE_GUESS_WHAT_YOU_LIKE){
                                response.response = String.format(response.response,"电影");
                            }
                            //用户问的是电影 ，文字部分就是电影 ；用户没有指定某分类，文字部分就影是视频
                            if(response.respType == AiResponse.RespType.VIDEO_TYPE&&messageType !=MESSAGE_TYPE_GUESS_WHAT_YOU_LIKE){
                                response.response = String.format(response.response,"视频");
                            }
                            //播报电影名称的反馈语
                            if(response.respType == AiResponse.RespType.VIDEO_NAME){
                                response.response = String.format(response.response,nlpData.data.lxresult.data.detailslist.get(0).name);
                            }
                            responseTts = response;
                            break;
                        }
                        if (map.containsKey(AiuiConstants.VIDEO_TIME_DESCR)) { // 有时间 的表示最近好看的电影
                            AiResponse.Response response = AiResponse.getInstance().getNewVideo();
                            messageType = MESSAGE_TYPE_THE_LATEST_VIDEO;
                            if(response.respType == AiResponse.RespType.VIDEO_TYPE){
                                //用户问的是电影 ，文字部分就是电影 ；用户没有指定某分类，文字部分就影是视频
                                if(map.containsKey(AiuiConstants.VIDEO_CATEGORY)&&map.get(AiuiConstants.VIDEO_CATEGORY).equals("电影")) {
                                    response.response = String.format(response.response, "电影");
                                }else if(map.containsKey(AiuiConstants.VIDEO_CATEGORY)){
                                    response.response = String.format(response.response, "视频");
                                }
                            }else if(response.respType == AiResponse.RespType.VIDEO_NAME){
                                response.response = String.format(response.response, nlpData.data.lxresult.data.detailslist.get(0).name);
                            }
                            responseTts = response;
                            break;
                        }
                        if (map.containsKey(AiuiConstants.VIDEO_TAG) || map.containsKey(AiuiConstants.VIDEO_NAME)) {  //带标签的表示大家都在看
                            AiResponse.Response response = AiResponse.getInstance().getEveryoneSee();
                            messageType = MESSAGE_TYPE_EVERYONE_IS_WATCHING;
                            if(response.respType == AiResponse.RespType.VIDEO_TYPE){
                                //用户问的是电影 ，文字部分就是电影 ；用户没有指定某分类，文字部分就影是视频
                                if(map.containsKey(AiuiConstants.VIDEO_CATEGORY)&&map.get(AiuiConstants.VIDEO_CATEGORY).equals("电影")) {
                                    response.response = String.format(response.response, "电影");
                                }else{
                                    response.response = String.format(response.response, "视频");
                                }
                            }
                            responseTts = response;
                            break;
                        }
                        break;
                    }
                }
                sendMessage(nlpData.answer != null ? nlpData.answer.text : "", messageType, MESSAGE_FROM_AI, nlpData.data.lxresult.data.detailslist);
                break;
            case AiuiConstants.HOTVIDEO_INTENT:
                AiResponse.Response response = AiResponse.getInstance().getEveryoneSee();
                if(response.respType == AiResponse.RespType.VIDEO_TYPE){
                    //用户问的是电影 ，文字部分就是电影 ；用户没有指定某分类，文字部分就影是视频
                    if(map.containsKey(AiuiConstants.VIDEO_CATEGORY)&&map.get(AiuiConstants.VIDEO_CATEGORY).equals("电影")) {
                        response.response = String.format(response.response, "电影");
                    }else{
                        response.response = String.format(response.response, "视频");
                    }
                }
                responseTts = response;
                sendMessage(nlpData.answer != null ? nlpData.answer.text : "", MESSAGE_TYPE_EVERYONE_IS_WATCHING, MESSAGE_FROM_AI,
                        nlpData.data.lxresult.data.detailslist);
                break;
        }
        if(responseTts!=null){
            aiuiService.tts(responseTts.response,null);
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
        if ((nlpData.answer != null && !TextUtils.isEmpty(nlpData.answer.text))) {
            aiuiService.tts(nlpData.getAnswer().text, null);
            sendMessage(nlpData.getAnswer().text, MESSAGE_TYPE_NORMAL, MESSAGE_FROM_AI);
        }
    }
    /**
     * 处理ViewCmd技能
     */
    private void intentViewCmd(String nlpHandle) {
        NlpData nlpData = gson.fromJson(nlpHandle, NlpData.class);
        if(AiuiConstants.VIEWCMD_INTENT.equals(nlpData.semantic.get(0).intent)){
            Map<String,String> solts = formatSlotsToMap(nlpData.semantic.get(0).slots);
            if(solts.containsKey(AiuiConstants.VIEWCMD_SERVICE)){
                switch (solts.get(AiuiConstants.VIEWCMD_SERVICE)){
                    case "下一页":
                        break;
                    case "查看更多":
                        break;
                    default:
                        String[] names = solts.get(AiuiConstants.VIEWCMD_SERVICE).split("\\|");
                        if(names ==null) {
                            return;
                        }
                        if(lastVideoList==null){
                            // names不为空，但是lastVideoList 为null
                            // 说明最后一次lastVideo是没有的，
                            // 这时要清理下为lastVideoList所设置的所见即可说
                            aiuiService.clearSpeakableData();
                            return;
                        }
                        List<TppData.DetailsListBean> selectedVideoList = new ArrayList<>();
                        for(int i = 0 ;i<names.length;i++){
                            for (TppData.DetailsListBean bean:lastVideoList){
                                Logger.debug("name【"+names[i]+"】beanName【"+bean.name+"】");
                                if(names[i].equals(bean.name)){
                                    // 找到多个匹配结果
                                    selectedVideoList.add(bean);
                                    break;
                                }
                            }
                        }

                        if(selectedVideoList.size()==0) return;
                        lastVideoList = selectedVideoList;
                        if(selectedVideoList.size() ==1){
                            //TODO 打开当前影片
                            aiuiService.tts("正在为你打开,"+selectedVideoList.get(0).name,null);
                        }else {
                            //更新UI为筛选出的Video列表
                            sendMessage("",lastResponseVideoMessageType,MESSAGE_FROM_AI,lastVideoList);
                            aiuiService.tts("为你找到"+selectedVideoList.size()+"个结果",null);
                        }

                        break;
                }
            }
        }
    }
    @Override
    public void onResult(String iatResult, String nlpReslult, String tppResult) {
        //onIatResult(iatResult);
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
            case AIUIConstant.EVENT_TTS: {
                switch (event.arg1) {
                    case AIUIConstant.TTS_SPEAK_BEGIN:
                        // 停止后台音频播放
                        mView.requestAudioFocus();
                        Logger.debug("开始播报");
                        break;
                    case AIUIConstant.TTS_SPEAK_PROGRESS:
                        Logger.debug(" 播报进度为" + event.data.getInt("percent"));     // 播放进度
                        break;
                    case AIUIConstant.TTS_SPEAK_PAUSED:
                        Logger.debug("暂停播报");
                        break;
                    case AIUIConstant.TTS_SPEAK_RESUMED:
                        Logger.debug("恢复播报");
                        break;
                    case AIUIConstant.TTS_SPEAK_COMPLETED:
                        // 开启后台音频播放
                        mView.abandonAudioFocus();
                        Logger.debug("播报完成");
                        break;

                    default:
                        break;
                }
            } break;
            case AIUIConstant.EVENT_START_RECORD:
                if(mCurrentState==AIUIConstant.STATE_WORKING) {
                    // 录音开始就发送延时消息，当五秒内在sendMessage()方法中都没有移除消息时就说明 5秒超时了
                    mHandler.postDelayed(runnable, TIME_OUT);
                    startTime = System.currentTimeMillis();
                }
                break;
            case AIUIConstant.EVENT_STOP_RECORD:
                break;
            case AIUIConstant.EVENT_VAD:
                Logger.debug("arg【" + event.arg1 + "】【" + event.arg2 + "】");
                //用arg1标识前后端点或者音量信息:0(前端点)、1(音量)、2(后端点)、3（前端点超时）。
                //当arg1取值为1时，arg2为音量大小。
                if (event.arg1 == 0) {
                    //检测到前端点表示正在录音
                    mHandler.removeCallbacks(runnable);
                }
                break;
            case AIUIConstant.EVENT_STATE:
                mCurrentState = event.arg1;
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
        if(videoList!=null&&videoList.size()>0){
            lastResponseVideoMessageType = messageType;
            lastVideoList = videoList;
            //服务端返回数据就去同步所见即可说
            String hotInfo = "查看更多|换一个";
            for(TppData.DetailsListBean bean:videoList){
                hotInfo+=bean.name+"|";
            }
            aiuiService.syncSpeakableData(hotInfo.substring(0,hotInfo.lastIndexOf("|")));
        }
        List<SearchByAIBean> messageList = new ArrayList<SearchByAIBean>();
        messageList.add(new SearchByAIBean(msg, messageType, msgFrom, videoList));
        EventBus.getDefault().post(new SearchByAIEventBean(messageList));
    }
}
