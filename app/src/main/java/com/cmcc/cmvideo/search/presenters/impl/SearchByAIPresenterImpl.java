package com.cmcc.cmvideo.search.presenters.impl;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.cmcc.cmvideo.BuildConfig;
import com.cmcc.cmvideo.base.AbstractPresenter;
import com.cmcc.cmvideo.base.Executor;
import com.cmcc.cmvideo.base.MainThread;
import com.cmcc.cmvideo.search.LookMoreActivity;
import com.cmcc.cmvideo.search.aiui.AIUIService;
import com.cmcc.cmvideo.search.aiui.IAIUIService;
import com.cmcc.cmvideo.search.aiui.Logger;
import com.cmcc.cmvideo.search.aiui.bean.MicBean;
import com.cmcc.cmvideo.search.aiui.bean.NlpData;
import com.cmcc.cmvideo.search.aiui.bean.TppData;
import com.cmcc.cmvideo.search.interactors.InitSearchByAIListInteractor;
import com.cmcc.cmvideo.search.interactors.impl.InitSearchByAIListInteractorImpl;
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
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.cmcc.cmvideo.util.Constants.*;

/**
 * Created by Yyw on 2018/5/21.
 * Describe:
 */

public class SearchByAIPresenterImpl extends AbstractPresenter implements SearchByAIPresenter, AIUIService.AIUIEventListener, InitSearchByAIListInteractor.Callback {
    private final String TAG = "SearchByAIPresenterImpl";
    private int lastResponseVideoMessageType = MESSAGE_TYPE_NORMAL;
    private int mCurrentState = AIUIConstant.STATE_IDLE;
    private long startTime = 0;
    private final int TIME_OUT = 5000;
    private Context mContext;
    private SearchByAIPresenter.View mView;
    private IAIUIService aiuiService;
    private Gson gson;
    private String intent;
    private NlpData mData = null;
    private android.os.Handler mHandler;
    private String lastResponseVideoTitle = "";
    private String lastRequestVideoText = "";
    private SearchByAIBean lastVideoSearchByAIBean = null;
    //最后一次语义的状态
    private String lastNlpState = "";
    //是否是在投屏状态或者插入耳机状态
    private boolean isAvailableVideo = false;

    public SearchByAIPresenterImpl(Executor executor, MainThread mainThread, SearchByAIPresenter.View view, Context context) {
        super(executor, mainThread);
        mView = view;
        mContext = context;
        gson = new Gson();
        mHandler = new android.os.Handler(Looper.getMainLooper());
        EventBus.getDefault().register(this);
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
        EventBus.getDefault().unregister(this);
        aiuiService.removeAIUIEventListener(this);
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

    @Override
    public void setAIUIService(IAIUIService service) {
        isAvailableVideo = false;
        aiuiService = service;
        Map<String, String> map = new HashMap<String, String>() {{
            put("msisdn", "13764279837");
            put("user_id", "553782460");
            put("client_id", "897ddadc222ec9c20651da355daee9cc");
        }};
        aiuiService.addAIUIEventListener(this);
        //上传用户数据
        aiuiService.setUserParam(map);
        //TODO  暂时就这么做吧
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                //清理所见即可说的数据
                aiuiService.clearSpeakableData();
            }
        }, 500);
    }

    @Override
    public void onInitSearchByAIListData(List<SearchByAIBean> searchByAIBeanList) {
        mView.showInitList(searchByAIBeanList);
    }

    public void onIatResult(String result) {
        if (TextUtils.isEmpty(result)) {
            return;
        }
        sendMessage(result, MESSAGE_TYPE_NORMAL, MESSAGE_FROM_USER);
    }

    private void onNlpResult(String result) {
        if (TextUtils.isEmpty(result)) {
            return;
        }
        mData = gson.fromJson(result, NlpData.class);
        String service = mData.service;
        if (!AiuiConstants.VIEWCMD_SERVICE.equals(service)) {
            Logger.debug("听写用户输入数据=====" + mData.text);
            sendMessage(mData.text, MESSAGE_TYPE_NORMAL, MESSAGE_FROM_USER);
        }


        //如 我想看电影 是开放问答的技能，此时会返回moreResults字段，但是这个是要走后处理，所以moreResults里面如果是video就直接返回
        //如果包含moreResults且service是video则直接返回，如果是viewCmd则要发送消息
        if (null != mData && null != mData.moreResults) {
            if ("video".equals(mData.service) && "video".equals(mData.moreResults.get(0).service)) {
                return;
            }
            if ("openQA".equals(mData.service) && "video".equals(mData.moreResults.get(0).service)
                    || "video".equals(mData.service) && "openQA".equals(mData.moreResults.get(0).service)) {
                return;
            }
            if ("video".equals(mData.service)) {
                mData = mData.moreResults.get(0);
            }
        }

        if (mData.rc == 4) {
            //播报
            if ((System.currentTimeMillis() - startTime) > TIME_OUT) {
                // 超过5秒表示 且rc=4（无法解析出语义） ，可显示推荐说法卡片
                sendMessage("", MESSAGE_TYPE_CAN_ASK_AI, MESSAGE_FROM_AI);
            } else {
                AiResponse.Response response = AiResponse.getInstance().getResultResponse();
                aiuiService.tts(response.response);
                sendMessage(response.response, MESSAGE_TYPE_NORMAL, MESSAGE_FROM_AI);
            }
            return;
        }

        try {
            //解析出当前语义状态，以便上传同步客户端状态，实现多伦对话
            JSONObject jsonObject = new JSONObject(result);
            if (jsonObject.has("state")) {
                JSONObject stateObj = jsonObject.getJSONObject("state");
                Iterator<String> keysIterator = stateObj.keys();
                while (keysIterator.hasNext()) {
                    lastNlpState = keysIterator.next();
                    Logger.debug("lastNlpState 【" + lastNlpState + "】");
                }
            }
            if (!"fg::viewCmd::default::default".equals(lastNlpState)) {
                aiuiService.syncSpeakableData(lastNlpState, "");
            }
        } catch (JSONException e) {
            e.printStackTrace();
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
//                intentControl(mData, intent);
                break;

            case AiuiConstants.VIDEO_CMD:
                //视频播放、暂停、下一集、上一集
//                if (isAvailableVideo) {
//                    intentVideoControl(mData, intent);
//                }
                intentCmd(mData);
                break;
            case AiuiConstants.VIDEO_ON_SERVICE:
                //直播模块
                intentOnLive(mData, intent);
                break;
            case AiuiConstants.WORLD_CUP_SERVICE:
                //伪球迷必备
                intentWorldCup(result);
                break;
            default:
                break;
        }

    }


    /**
     * 处理商店技能世界杯
     *
     * @param nlpHandle
     */
    private void intentWorldCup(String nlpHandle) {
        NlpData nlpData = gson.fromJson(nlpHandle, NlpData.class);
        if (null != nlpData.semantic && null != nlpData.semantic.get(0) && null != nlpData.semantic.get(0).intent) {
            switch (nlpData.semantic.get(0).intent) {
                case AiuiConstants.WORLD_CUP_QUERY_OPEN:
                    break;
                case AiuiConstants.WORLD_CUP_SERCH_BY_DATE:
                    break;
                case AiuiConstants.WORLD_CUP_QUERY_TEAMS:
                    break;
                case AiuiConstants.WORLD_CUP_QUERY_WITH_SESSION:
                    break;
                case AiuiConstants.WORLD_CUP_QUERY_IMPTGAME:
                    break;
                case AiuiConstants.WORLD_CUP_TEAM_PLAYERS:
                    break;
                case AiuiConstants.WORLD_CUP_SEARCH_BY_TEAM_INTENT:
                    break;
                case AiuiConstants.WORLD_CUP_QUERY_FIRST_GAME:
                    break;
                case AiuiConstants.WORLD_CUP_QUERY_GROUPS:
                    break;
                case AiuiConstants.WORLD_CUP_QUERY_GPGM_OVER:
                    break;
                case AiuiConstants.WORLD_CUP_QUERY_TEAM_GROUP:
                    break;
                case AiuiConstants.WORLD_CUP_SERCH_BY_TEAMS_INTENT:
                    break;
                case AiuiConstants.WORLD_CUP_QUERY_WITH_GROUP:
                    break;
                case AiuiConstants.WORLD_CUP_QUERY_ALLINFO:
                    break;
                case AiuiConstants.WORLD_CUP_QUERY_CHAMPION:
                    break;
                case AiuiConstants.WORLD_CUP_I_LIKE_TEAM:
                    break;
            }
        }

        if ((nlpData.answer != null && !TextUtils.isEmpty(nlpData.answer.text))) {
            aiuiService.tts(nlpData.getAnswer().text);
            sendMessage(nlpData.getAnswer().text, MESSAGE_TYPE_NORMAL, MESSAGE_FROM_AI);
        }

    }

    private void intentCmd(NlpData mData) {
        Map<String, String> solts = formatSlotsToMap(mData.semantic.get(0).slots);
        switch (mData.semantic.get(0).intent) {
            case AiuiConstants.VIDEO_CMD_INTENT:
                if (solts.containsKey(AiuiConstants.VIDEO_INDEX) && lastSearchIsGuessWhatYouLike()) {
                    int index = Integer.parseInt(solts.get(AiuiConstants.VIDEO_INDEX)) - 1;
                    if (lastVideoSearchByAIBean != null && lastVideoSearchByAIBean.getVideoList() != null && lastVideoSearchByAIBean.getVideoList().size() > 0) {
                        List<TppData.SubserialsBean> subserials = lastVideoSearchByAIBean.getVideoList().get(0).subserials;
                        if (index >= 0 && index < subserials.size()) {
                            String id = subserials.get(index).id;
                            String name = subserials.get(index).name;
                            //TODO 猜你喜欢 选剧集
                            Logger.debug("猜你喜欢 选择 id 【" + id + "】 name 【" + name + "】");
                        }
                    }
                }
                break;
        }
    }

    /**
     * 处理直播模块指令 各意图
     *
     * @param mData
     * @param intent
     */
    private void intentOnLive(NlpData mData, String intent) {
        switch (intent) {
            case AiuiConstants.VIDEO_CHANNEL_INTENT:
                //频道查询 如：我要看CCTV5体育 / 湖南卫视
                Logger.debug("VIDEO_CHANNEL_INTENT=================="
                        + mData.text
                        + mData.semantic.get(0).getSlots().get(0).normValue
                        + mData.semantic.get(0).getSlots().get(0).value);
                break;
            case AiuiConstants.VIDEO_VERITY_INTENT:
                //多样直播视频查询 如：我要看直播 、体育直播
                if (!TextUtils.isEmpty(mData.text)) {
                    if ("我要看直播".equals(mData.text)) {
                        //直接跳转到直播模块
                        Logger.debug("我要看直播===================");
                    } else {
                        //跳转到直播内各大类模块
                        String tx = mData.semantic.get(0).getSlots().get(0).normValue;
                        intentToVerity(tx);
                    }
                }
                break;
            default:
                break;
        }
    }

    /**
     * 直播模块分类  跳转
     */
    private void intentToVerity(String textNormal) {
        switch (textNormal) {
            case AiuiConstants.CCTV:
                //央视
                Logger.debug("央视==========");
                break;
            case AiuiConstants.START_TV:
                Logger.debug("卫视==========");
                break;
            case AiuiConstants.FILMS:
                Logger.debug("影视==========");
                break;
            case AiuiConstants.CHILDREN:
                Logger.debug("少儿==========");
                break;
            case AiuiConstants.NEWS:
                Logger.debug("新闻==========");
                break;
            case AiuiConstants.DOCUMENTARY:
                Logger.debug("纪录片========");
                break;
            case AiuiConstants.TURN_TV:
                Logger.debug("地方台========");
                break;
            case AiuiConstants.AREA_TV:
                Logger.debug("地方台========");
            case AiuiConstants.FEATURE_TV:
                Logger.debug("特色台========");
                break;
            case AiuiConstants.SPORTS_TV:
                Logger.debug("体育==========");
                break;
            default:
                break;
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMicEvent(MicBean event) {
        if (event.isConnect()) {
            isAvailableVideo = true;
            if (BuildConfig.DEBUG) {
                Log.d("aiui_log", "耳机接入");
            }
        } else {
            isAvailableVideo = false;
            if (BuildConfig.DEBUG) {
                Log.d("aiui_log", "耳机未接入");
            }
        }
    }

    /**
     * 处理视频播放cmd技能
     */
    private void intentVideoControl(NlpData mData, String intent) {
        if (mData.semantic == null || mData.semantic.size() == 0) {
            return;
        }

        if (AiuiConstants.VIDEO_CMD_INTENT.equals(mData.semantic.get(0).intent)) {
            Map<String, String> solts = formatSlotsToMap(mData.semantic.get(0).slots);
            if (solts.containsKey(AiuiConstants.VIDEO_INSTYPE)) {
                switch (solts.get(AiuiConstants.VIDEO_INSTYPE)) {
                    case AiuiConstants.VIDEO_PAUSE:
                        //暂停
                        Logger.debug("暂停===" + intent);
                        break;
                    case AiuiConstants.VIDEO_PLAY:
                        //播放
                        Logger.debug("播放===" + intent);
                        break;
                    case AiuiConstants.VIDEO_PREVIOUS:
                        //上一集
                        Logger.debug("上一集===" + intent);
                        break;
                    case AiuiConstants.VIDEO_NEXT:
                        //下一集
                        Logger.debug("下一集===" + intent);
                        break;
                    default:
                        break;
                }

            }
        }

    }

    public void onTppResult(String result) {
        if (TextUtils.isEmpty(result)) {
            return;
        }
        NlpData nlpData = gson.fromJson(result, NlpData.class);
        //判断是否解出了语义，并且当前技能是video
        if (nlpData.rc == 4
                || !("video".equals(nlpData.service)
                || "LINGXI2018.user_video".equals(nlpData.service))) {
            if (nlpData.moreResults == null) {
                return;
            }
            if(AiuiConstants.VIEWCMD_SERVICE.equals(nlpData.service)
                    &&AiuiConstants.VIDEO_SERVICE.equals(nlpData.moreResults.get(0).service)){
                return;
            }
            nlpData = nlpData.moreResults.get(0);
            if (nlpData.rc == 4
                    || !("video".equals(nlpData.service)
                    || "LINGXI2018.user_video".equals(nlpData.service))) {
                return;
            }
        }

        if (nlpData.moreResults != null) {
            if ("QUERY".equals(nlpData.moreResults.get(0).semantic.get(0).intent)) {
                nlpData = nlpData.moreResults.get(0);
            }
        }

        //语义后处理没有返回数据则直接退出
        if (!hasVideoData(nlpData)
                || !nlpData.data.lxresult.code.equals("000000")
                ) {
            //没有影片数据且存在answer 则播报  随机播报一条反馈语言
            AiResponse.Response response = AiResponse.getInstance().getNetWorkStatus();
            aiuiService.tts(response.response);
            sendMessage(response.response, MESSAGE_TYPE_NORMAL, MESSAGE_FROM_AI);
            return;
        }

        AiResponse.Response responseTts = null;
        Map<String, String> map = formatSlotsToMap(nlpData.semantic.get(0).slots);
        lastRequestVideoText = nlpData.text;
        lastResponseVideoTitle = makeCardTitle(map);
        String msg = nlpData.answer != null ? nlpData.answer.text : "";
        switch (nlpData.semantic.get(0).intent) {
            case AiuiConstants.VIDEO_CMD_INTENT:
            case AiuiConstants.QUERY_INTENT:
                int messageType = MESSAGE_TYPE_NORMAL;
                /*  暂时去除该模块判断 后续会加上
                if (map.containsKey(AiuiConstants.VIDEO_TIME_DESCR)) { // 有时间 的表示最近好看的电影
                    AiResponse.Response response = AiResponse.getInstance().getNewVideo();
                    messageType = MESSAGE_TYPE_THE_LATEST_VIDEO;
                    if (response.respType == AiResponse.RespType.VIDEO_TYPE) {
                        //用户问的是电影 ，文字部分就是电影 ；用户没有指定某分类，文字部分就影是视频
                        if (map.containsKey(AiuiConstants.VIDEO_CATEGORY) && map.get(AiuiConstants.VIDEO_CATEGORY).equals("电影")) {
                            response.response = String.format(response.response, "电影");
                        } else if (map.containsKey(AiuiConstants.VIDEO_CATEGORY)) {
                            response.response = String.format(response.response, "视频");
                        }
                    } else if (response.respType == AiResponse.RespType.VIDEO_NAME) {
                        response.response = String.format(response.response, nlpData.data.lxresult.data.detailslist.get(0).name);
                    }
                    responseTts = response;
                    break;
                }
                */

                // 判断意图是使用哪个卡片展示
                if (isCategory(map)) { //猜你喜欢
                    AiResponse.Response response = AiResponse.getInstance().getGuessWhatYouLike();
                    boolean hasSubserials = true;//hasSubserials(nlpData);
                    if (checkCategory(map, CategoryType.MOVIE)) {
                        messageType = MESSAGE_TYPE_GUESS_WHAT_YOU_LIKE;
                    } else if ((checkCategory(map, CategoryType.TV) || checkCategory(map, CategoryType.DOC) || checkCategory(map, CategoryType.CARTOON)) && hasSubserials) {
                        messageType = MESSAGE_TYPE_GUESS_WHAT_YOU_LIKE_LIST_HORIZONTAL;
                    } else if (checkCategory(map, CategoryType.VARIETY) && hasSubserials) {
                        messageType = MESSAGE_TYPE_GUESS_WHAT_YOU_LIKE_LIST_VERTICAL;
                    }
                    //用户问的是电影 ，文字部分就是电影 ；用户没有指定某分类，文字部分就影是视频
                    if (response.respType == AiResponse.RespType.VIDEO_TYPE && checkCategory(map, CategoryType.MOVIE)) {
                        response.response = String.format(response.response, "电影");
                    }
                    //用户问的是电影 ，文字部分就是电影 ；用户没有指定某分类，文字部分就影是视频
                    if (response.respType == AiResponse.RespType.VIDEO_TYPE && !checkCategory(map, CategoryType.MOVIE)) {
                        response.response = String.format(response.response, "视频");
                    }
                    //播报电影名称的反馈语
                    if (response.respType == AiResponse.RespType.VIDEO_NAME) {
                        response.response = String.format(response.response, nlpData.data.lxresult.data.detailslist.get(0).name);
                    }
                    responseTts = response;
                } else {
                    AiResponse.Response response = AiResponse.getInstance().getEveryoneSee();
                    messageType = MESSAGE_TYPE_EVERYONE_IS_WATCHING;
                    if (response.respType == AiResponse.RespType.VIDEO_TYPE) {
                        //用户问的是电影 ，文字部分就是电影 ；用户没有指定某分类，文字部分就影是视频
                        if (map.containsKey(AiuiConstants.VIDEO_CATEGORY) && map.get(AiuiConstants.VIDEO_CATEGORY).equals("电影")) {
                            response.response = String.format(response.response, "电影");
                        } else {
                            response.response = String.format(response.response, "视频");
                        }
                    }
                    responseTts = response;
                }
                if (messageType == MESSAGE_TYPE_NORMAL
                        && nlpData.answer != null
                        && !TextUtils.isEmpty(nlpData.answer.text)) {
                    aiuiService.tts(nlpData.answer.text);
                }
                if (hasVideoData(nlpData)) {
                    msg = lastResponseVideoTitle;
                }
                sendMessage(msg, messageType, MESSAGE_FROM_AI, nlpData.data.lxresult.data.detailslist);
                break;
            case AiuiConstants.HOTVIDEO_INTENT:
                AiResponse.Response response = AiResponse.getInstance().getEveryoneSee();
                if (response.respType == AiResponse.RespType.VIDEO_TYPE) {
                    //用户问的是电影 ，文字部分就是电影 ；用户没有指定某分类，文字部分就影是视频
                    if (map.containsKey(AiuiConstants.VIDEO_CATEGORY) && map.get(AiuiConstants.VIDEO_CATEGORY).equals("电影")) {
                        response.response = String.format(response.response, "电影");
                    } else {
                        response.response = String.format(response.response, "视频");
                    }
                }
                responseTts = response;
                if (hasVideoData(nlpData)) {
                    msg = lastResponseVideoTitle;
                }
                sendMessage(msg, MESSAGE_TYPE_EVERYONE_IS_WATCHING, MESSAGE_FROM_AI, nlpData.data.lxresult.data.detailslist);
                break;
            default:
                break;
        }
        if (responseTts != null) {
            aiuiService.tts(responseTts.response);
        }
    }

    /**
     * 获得卡片标题
     *
     * @param map
     * @return
     */
    private String makeCardTitle(Map<String, String> map) {
        String cardTitle = "";
        if (map == null) {
            return cardTitle;
        }
        if (map.containsKey(AiuiConstants.VIDEO_NAME)) {
            return map.get(AiuiConstants.VIDEO_NAME);
        }
        if (map.containsKey(AiuiConstants.VIDEO_ARTIST)) {
            cardTitle += "“" + map.get(AiuiConstants.VIDEO_ARTIST).replace("|","、") + "”" + "的";
        }
        if (map.containsKey(AiuiConstants.VIDEO_TIME)) {
            cardTitle += "“" + map.get(AiuiConstants.VIDEO_TIME).replace("|","、") + "”" + "的";
        }
        if (map.containsKey(AiuiConstants.VIDEO_TIME_DESCR)) {
            cardTitle += "“" + map.get(AiuiConstants.VIDEO_TIME_DESCR).replace("|","、") + "”";
        }
        if (map.containsKey(AiuiConstants.VIDEO_AREA)) {
            cardTitle += "“" + map.get(AiuiConstants.VIDEO_AREA).replace("|","、") + "”";
        }
        boolean hasCatgeory = false;
        if (map.containsKey(AiuiConstants.VIDEO_TAG)) {
            String tag = map.get(AiuiConstants.VIDEO_TAG);
            String[] tags = tag.split("\\|");
            boolean hasMoreTags = false;
            for(int i = 0;i<tags.length;i++){
                switch (tags[i]) {
                    case "综艺":
                        hasCatgeory = true;
                        cardTitle += hasMoreTags?"和综艺节目":"综艺节目";
                        break;
                    case "动画":
                        hasCatgeory = true;
                        cardTitle += hasMoreTags?"和动画片":"动画片";
                        break;
                    case "纪录":
                        hasCatgeory = true;
                        cardTitle += hasMoreTags?"和纪录片":"纪录片";
                        break;
                    default:
                        cardTitle += hasMoreTags?"“" + tag + "”":"“" + tag + "”";
                        break;
                }
                hasMoreTags = true;
            }
        }
        if (map.containsKey(AiuiConstants.VIDEO_CATEGORY)&&!hasCatgeory) {
             cardTitle += map.get(AiuiConstants.VIDEO_CATEGORY).equals("片") ? "电影" : map.get(AiuiConstants.VIDEO_CATEGORY);
        }
        return cardTitle;
    }

    /**
     * 是否存在剧集数据
     *
     * @param detail
     * @return
     */
    private boolean hasSubserials(TppData.DetailsListBean detail) {
        if (detail.subserials == null || detail.subserials.size() == 0) {
            return false;
        }
        return true;
    }

    private boolean hasVideoData(NlpData nlpData) {
        if (nlpData == null
                || nlpData.data == null
                || nlpData.data.lxresult == null
                || nlpData.data.lxresult.data == null
                || nlpData.data.lxresult.data.detailslist == null
                || nlpData.data.lxresult.data.detailslist.size() == 0)
            return false;
        return true;
    }

    /**
     * 判断当前类目是什么
     *
     * @param solts
     * @param categoryType
     * @return
     */
    private boolean checkCategory(Map<String, String> solts, CategoryType categoryType) {
        if (solts == null || solts.size() > 2)
            return false;
        String cate = "";
        if (solts.size() == 1) {
            if (solts.containsKey(AiuiConstants.VIDEO_CATEGORY))
                cate = solts.get(AiuiConstants.VIDEO_CATEGORY);
            if (solts.containsKey(AiuiConstants.VIDEO_TAG))
                cate = solts.get(AiuiConstants.VIDEO_TAG);
        } else if (solts.size() == 2) {
            String category = solts.get(AiuiConstants.VIDEO_CATEGORY);
            if ("片".equals(category) || "节目".equals(category) || "影视".equals(category)) {
                cate = solts.get(AiuiConstants.VIDEO_TAG);
            }
        }
        switch (categoryType) {
            case TV:
                return "电视剧".equals(cate);
            case DOC:
                return "纪录".equals(cate) || "纪实".equals(cate);
            case MOVIE:
                return "电影".equals(cate) || "片".equals(cate);
            case CARTOON:
                return "卡通".equals(cate) || "动漫".equals(cate);
            case VARIETY:
                return "综艺".equals(cate);
            default:
                return false;
        }
    }

    /**
     * 判断当前类目是什么
     *
     * @param solts
     * @return
     */
    private boolean isCategory(Map<String, String> solts) {
        if (solts == null || solts.size() > 2)
            return false;
        boolean reslult = false;
        if (solts.size() == 1) {
            reslult = solts.containsKey(AiuiConstants.VIDEO_CATEGORY) || solts.containsKey(AiuiConstants.VIDEO_TAG);
        }
        if (solts.size() == 2) {
            reslult = solts.containsKey(AiuiConstants.VIDEO_CATEGORY) && solts.containsKey(AiuiConstants.VIDEO_TAG);
        }
        if (reslult) {
            String cate = "";
            if (solts.size() == 1) {
                if (solts.containsKey(AiuiConstants.VIDEO_CATEGORY))
                    cate = solts.get(AiuiConstants.VIDEO_CATEGORY);
                if (solts.containsKey(AiuiConstants.VIDEO_TAG))
                    cate = solts.get(AiuiConstants.VIDEO_TAG);
            } else if (solts.size() == 2) {
                String category = solts.get(AiuiConstants.VIDEO_CATEGORY);
                if ("片".equals(category) || "节目".equals(category) || "影视".equals(category)) {
                    cate = solts.get(AiuiConstants.VIDEO_TAG);
                }
            }
            return TextUtils.isEmpty(cate) ? false : "电视剧,纪录,纪实,电影,片,卡通,动漫,综艺".contains(cate);
        }
        return false;
    }

    /**
     * 判断最后一次搜索是不是猜你喜欢的内容
     *
     * @return
     */
    private boolean lastSearchIsGuessWhatYouLike() {
        if (lastVideoSearchByAIBean == null)
            return false;

        return
                lastVideoSearchByAIBean.getMessageType() == MESSAGE_TYPE_GUESS_WHAT_YOU_LIKE
                        || lastVideoSearchByAIBean.getMessageType() == MESSAGE_TYPE_GUESS_WHAT_YOU_LIKE_LIST_HORIZONTAL
                        || lastVideoSearchByAIBean.getMessageType() == MESSAGE_TYPE_GUESS_WHAT_YOU_LIKE_LIST_VERTICAL;
    }

    /**
     * 过滤所见即可说 同步的内容。避免同步失效
     *
     * @return
     */
    private String filterSyncName(String name) {
        return name;
//        if(TextUtils.isEmpty(name))
//            return "";
//        return name.replaceAll("[：！。，《》\\s]","");
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
            default:
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
                aiuiService.tts("正在为您" + mData.text);
                break;
            case AiuiConstants.SREEN_INTENT:
                // TODO: 2018/5/30 投屏跳转
                isAvailableVideo = true;
                aiuiService.tts("正在为您" + mData.text);
                break;
            default:
                break;
        }

    }

    /**
     * 处理闲聊技能
     */
    private void intentQa(String nlpHandle) {
        NlpData nlpData = gson.fromJson(nlpHandle, NlpData.class);
        if ((nlpData.answer != null && !TextUtils.isEmpty(nlpData.answer.text))) {
            aiuiService.tts(nlpData.getAnswer().text);
            sendMessage(nlpData.getAnswer().text, MESSAGE_TYPE_NORMAL, MESSAGE_FROM_AI);
        }
    }

    /**
     * 处理ViewCmd技能
     */
    private void intentViewCmd(String nlpHandle) {
        NlpData nlpData = gson.fromJson(nlpHandle, NlpData.class);
        if (AiuiConstants.VIEWCMD_INTENT.equals(nlpData.semantic.get(0).intent)) {
            switch (nlpData.semantic.get(0).slots.get(0).name) {
                case "LOOK_MORE":
                    Intent intent = new Intent(mContext, LookMoreActivity.class);
                    intent.putExtra(LookMoreActivity.KEY_MORE_DATE_SPEECH_TEXT, lastRequestVideoText);
                    intent.putExtra(LookMoreActivity.KEY_TITLE, lastResponseVideoTitle);
                    mContext.startActivity(intent);
                    break;
                case "CHANGE":
                    if (lastVideoSearchByAIBean != null) {
                        if (lastVideoSearchByAIBean.getVideoList().size() > 1) {
                            lastVideoSearchByAIBean.getVideoList().remove(0);
                            sendMessage(lastVideoSearchByAIBean);
                        }
                    }
                    break;
                case "HORIZONTAL_EPISODE":
                    if (!lastSearchIsGuessWhatYouLike()) return;
                    Logger.debug(nlpData.semantic.get(0).slots.get(0).value);
                    break;
                case "VERTICAL_EPISODE":
                    if (!lastSearchIsGuessWhatYouLike()) return;
                    if (lastVideoSearchByAIBean.getVideoList() == null || lastVideoSearchByAIBean.getVideoList().size() == 0)
                        return;
                    String[] subNames = nlpData.semantic.get(0).slots.get(0).value.split("\\|");
                    if (subNames == null) {
                        return;
                    }
                    TppData.DetailsListBean detail = lastVideoSearchByAIBean.getVideoList().get(0);
                    if (!hasSubserials(detail)) {
                        // names不为空，但是lastVideoList 为null
                        // 说明最后一次lastVideo是没有的，
                        // 这时要清理下为lastVideoList所设置的所见即可说
                        aiuiService.clearSpeakableData();
                        return;
                    }
                    List<TppData.SubserialsBean> selectedSubserialsList = new ArrayList<>();
                    for (int i = 0; i < subNames.length; i++) {
                        for (TppData.SubserialsBean sub : detail.subserials) {
                            Logger.debug("name【" + subNames[i] + "】beanName【" + sub.name + "】");
                            if (sub.name.contains(subNames[i])) {
                                // 找到多个匹配结果
                                selectedSubserialsList.add(sub);
                                break;
                            }
                        }
                    }
                    if (selectedSubserialsList.size() == 0) {
                        return;
                    }
                    //TODO 打开当前影片
                    aiuiService.tts("正在为你打开," + selectedSubserialsList.get(0).name);
                    break;
                case "VIDEO_NAME":
                    String[] names = nlpData.semantic.get(0).slots.get(0).value.split("\\|");
                    if (names == null) {
                        return;
                    }
                    if (lastVideoSearchByAIBean != null && lastVideoSearchByAIBean.getVideoList() == null) {
                        // names不为空，但是lastVideoList 为null
                        // 说明最后一次lastVideo是没有的，
                        // 这时要清理下为lastVideoList所设置的所见即可说
                        aiuiService.clearSpeakableData();
                        return;
                    }
                    List<TppData.DetailsListBean> selectedVideoList = new ArrayList<>();
                    for (int i = 0; i < names.length; i++) {
                        for (TppData.DetailsListBean bean : lastVideoSearchByAIBean.getVideoList()) {
                            Logger.debug("name【" + names[i] + "】beanName【" + bean.name + "】");
                            if (names[i].equals(bean.name)) {
                                // 找到多个匹配结果
                                selectedVideoList.add(bean);
                                break;
                            }
                        }
                    }
                    if (selectedVideoList.size() == 0) {
                        return;
                    }
                    if (selectedVideoList.size() == 1) {
                        //TODO 打开当前影片
                        aiuiService.tts("正在为你打开," + selectedVideoList.get(0).name);
                    } else {
                        lastVideoSearchByAIBean.setVideoList(selectedVideoList);
                        //更新UI为筛选出的Video列表
                        sendMessage(lastVideoSearchByAIBean);
                        aiuiService.tts("为你找到" + selectedVideoList.size() + "个结果");
                    }
                    break;
                case "FIRST":
                    TppData.DetailsListBean detail1 = lastVideoSearchByAIBean.getVideoList().get(0);
                    if (lastSearchIsGuessWhatYouLike() && hasSubserials(detail1)) {
                        //TODO 打开第1 集 detail1.subserials.get(0).id
                        aiuiService.tts("正在为你打开," + detail1.subserials.get(0).name);
                    } else {
                        //TODO 打开第1 个电影 lastVideoSearchByAIBean.getVideoList().get(0).id
                        aiuiService.tts("正在为你打开," + lastVideoSearchByAIBean.getVideoList().get(0).name);
                    }

                    break;
                case "SECOND":
                    TppData.DetailsListBean detail2 = lastVideoSearchByAIBean.getVideoList().get(0);
                    if (lastSearchIsGuessWhatYouLike() && hasSubserials(detail2) && detail2.subserials.size() >= 2) {
                        //TODO 打开第2 集 detail2.subserials.get(1).id
                        aiuiService.tts("正在为你打开," + detail2.subserials.get(1).name);
                    } else {
                        //TODO 打开第2 个电影
                        aiuiService.tts("正在为你打开," + lastVideoSearchByAIBean.getVideoList().get(1).name);
                    }
                    break;
                case "THIRD":
                    TppData.DetailsListBean detail3 = lastVideoSearchByAIBean.getVideoList().get(0);
                    if (lastSearchIsGuessWhatYouLike() && hasSubserials(detail3) && detail3.subserials.size() >= 3) {
                        //TODO 打开第3 集 detail2.subserials.get(2).id
                        aiuiService.tts("正在为你打开," + detail3.subserials.get(2).name);
                    } else {
                        //TODO 打开第3 个电影
                        aiuiService.tts("正在为你打开," + lastVideoSearchByAIBean.getVideoList().get(2).name);
                    }
                    break;
            }
        }
    }

    @Override
    public void onResult(String iatResult, String nlpReslult, String tppResult) {
        //onIatResult(iatResult);
        if (!aiuiService.isLookMorePageData()) {
            onNlpResult(nlpReslult);
            onTppResult(tppResult);
        }
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
                        Logger.debug("+++++++++++++++开始播报");
                        break;
                    case AIUIConstant.TTS_SPEAK_PROGRESS:
                        // 播放进度
                        break;
                    case AIUIConstant.TTS_SPEAK_PAUSED:
                        Logger.debug("+++++++++++++++++暂停播报");
                        break;
                    case AIUIConstant.TTS_SPEAK_RESUMED:
                        Logger.debug("++++++++++++++++++恢复播报");
                        break;
                    case AIUIConstant.TTS_SPEAK_COMPLETED:
                        // 开启后台音频播放
                        mView.abandonAudioFocus();
                        Logger.debug("+++++++++++++++播报完成");
                        break;

                    default:
                        break;
                }
            }
            break;
            case AIUIConstant.EVENT_START_RECORD:
                if (mCurrentState == AIUIConstant.STATE_WORKING) {
                    // 录音开始就发送延时消息，当五秒内在sendMessage()方法中都没有移除消息时就说明 5秒超时了
                    mHandler.postDelayed(runnable, TIME_OUT);
                    startTime = System.currentTimeMillis();
                }
                break;
            case AIUIConstant.EVENT_STOP_RECORD:
                break;
            case AIUIConstant.EVENT_VAD:
                //                Logger.debug("arg【" + event.arg1 + "】【" + event.arg2 + "】");
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
            default:
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

    //取消录音
    @Override
    public void cancelRecordAudio() {
        aiuiService.cancelRecordAudio();
        mHandler.removeCallbacks(runnable);
    }

    //SlotsBean key-value 数据转换成Map 类型数据方便查找
    private Map<String, String> formatSlotsToMap(List<NlpData.SlotsBean> slotsBeans) {
        Map<String, String> map = new HashMap<>();
        if (slotsBeans == null || slotsBeans.size() == 0) {
            return map;
        }
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
        SearchByAIBean searchByAIBean = new SearchByAIBean(msg, messageType, msgFrom, videoList);
        if (videoList != null && videoList.size() > 0) {
            lastVideoSearchByAIBean = searchByAIBean;
            //服务端返回数据就去同步所见即可说
            syncSpeakableData(searchByAIBean.getMessageType(), videoList);
        }
        List<SearchByAIBean> messageList = new ArrayList<SearchByAIBean>();
        searchByAIBean.setSpeechText(lastRequestVideoText);
        messageList.add(searchByAIBean);
        EventBus.getDefault().post(new SearchByAIEventBean(messageList));
    }

    /**
     * 发送消息更新UI
     */
    private void sendMessage(SearchByAIBean searchByAIBean) {
        if (searchByAIBean != null && searchByAIBean.getVideoList() != null && searchByAIBean.getVideoList().size() > 0) {
            //服务端返回数据就去同步所见即可说
            syncSpeakableData(searchByAIBean.getMessageType(), searchByAIBean.getVideoList());
        }
        List<SearchByAIBean> messageList = new ArrayList<SearchByAIBean>();
        messageList.add(searchByAIBean);
        EventBus.getDefault().post(new SearchByAIEventBean(messageList));
    }

    public enum CategoryType {
        // 电影，电视剧，记录片，卡通，综艺
        MOVIE, TV, DOC, CARTOON, VARIETY
    }

    /**
     * 同步所见即可说数据
     *
     * @param messageType
     * @param beans
     */
    private void syncSpeakableData(int messageType, List<TppData.DetailsListBean> beans) {
        Map<String, String> syncMap = new HashMap<>();
        if (lastSearchIsGuessWhatYouLike()) {
            syncMap.put("CHANGE", "换一个|下一个");
        }
        if (messageType == MESSAGE_TYPE_GUESS_WHAT_YOU_LIKE_LIST_HORIZONTAL) {
            if (hasSubserials(beans.get(0))) {
                String episode = "第%s集";
                StringBuilder hotInfo = new StringBuilder();
                for (int i = 1; i <= beans.get(0).subserials.size(); i++) {
                    hotInfo.append(String.format(episode, i)).append("|");
                }
                syncMap.put("HORIZONTAL_EPISODE", hotInfo.substring(0, hotInfo.lastIndexOf("|")));
            }
        } else if (messageType == MESSAGE_TYPE_GUESS_WHAT_YOU_LIKE_LIST_VERTICAL) {
            if (hasSubserials(beans.get(0))) {
                StringBuilder hotInfo = new StringBuilder();
                for (int i = 0; i < beans.get(0).subserials.size(); i++) {
                    hotInfo.append(filterSyncName(beans.get(0).subserials.get(i).name)).append("|");
                }
                syncMap.put("FIRST", "第一个");
                syncMap.put("SECOND", "第二个|中间那个");
                syncMap.put("THIRD", "第三个|最后一个");
                syncMap.put("VERTICAL_EPISODE", hotInfo.substring(0, hotInfo.lastIndexOf("|")));
            }
        } else if (messageType == MESSAGE_TYPE_GUESS_WHAT_YOU_LIKE) {
        } else {
            StringBuilder hotInfo = new StringBuilder();
            for (TppData.DetailsListBean bean : beans) {
                hotInfo.append(bean.name).append("|");
            }
            syncMap.put("LOOK_MORE", "查看更多");
            syncMap.put("FIRST", "第一个");
            syncMap.put("SECOND", "第二个|中间那个");
            syncMap.put("THIRD", "第三个|最后一个");
            syncMap.put("VIDEO_NAME", hotInfo.substring(0, hotInfo.lastIndexOf("|")));
        }
        aiuiService.syncSpeakableData(lastNlpState, syncMap);
    }
}
