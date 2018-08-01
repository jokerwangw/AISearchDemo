package com.cmcc.cmvideo.search.presenters.impl;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.cmcc.cmvideo.BuildConfig;
import com.cmcc.cmvideo.MainActivity;
import com.cmcc.cmvideo.base.AbstractPresenter;
import com.cmcc.cmvideo.base.Executor;
import com.cmcc.cmvideo.base.MainThread;
import com.cmcc.cmvideo.search.LookMoreActivity;
import com.cmcc.cmvideo.search.aiui.AIUIService;
import com.cmcc.cmvideo.search.aiui.IAIUIService;
import com.cmcc.cmvideo.search.aiui.Logger;
import com.cmcc.cmvideo.search.aiui.bean.MicBean;
import com.cmcc.cmvideo.search.aiui.bean.NavigationBean;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.cmcc.cmvideo.util.AiuiConstants.MessageFrom.MESSAGE_FROM_AI;
import static com.cmcc.cmvideo.util.AiuiConstants.MessageType.MESSAGE_TYPE_EVERYONE_IS_WATCHING;
import static com.cmcc.cmvideo.util.AiuiConstants.MessageType.MESSAGE_TYPE_GUESS_WHAT_YOU_LIKE;
import static com.cmcc.cmvideo.util.AiuiConstants.MessageType.MESSAGE_TYPE_GUESS_WHAT_YOU_LIKE_LIST_HORIZONTAL;
import static com.cmcc.cmvideo.util.AiuiConstants.MessageType.MESSAGE_TYPE_GUESS_WHAT_YOU_LIKE_LIST_VERTICAL;
import static com.cmcc.cmvideo.util.AiuiConstants.MessageType.MESSAGE_TYPE_NORMAL;

/**
 * Created by Yyw on 2018/5/21.
 * Describe:
 */

public class SearchByAIPresenterImpl extends AbstractPresenter implements SearchByAIPresenter, AIUIService.AIUIEventListener, InitSearchByAIListInteractor.Callback {
    private final String TAG = "SearchByAIPresenterImpl";
    private final int TIME_OUT = 5000;
    private Context mContext;
    private SearchByAIPresenter.View mView;
    private IAIUIService aiuiService;
    private Gson gson;
    private String lastResponseVideoTitle = "";
    private String lastVideoData = "";
    private SearchByAIBean lastVideoSearchByAIBean = null;

    public enum CategoryType {
        // 电影，电视剧，记录片，卡通，综艺
        MOVIE, TV, DOC, CARTOON, VARIETY
    }

    public SearchByAIPresenterImpl(Executor executor, MainThread mainThread, SearchByAIPresenter.View view, Context context) {
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
        aiuiService.setAttached(false);
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
    public void onInitSearchByAIListData(List<SearchByAIBean> searchByAIBeanList) {
        mView.showInitList(searchByAIBeanList);
    }

    @Override
    public void setAIUIService(IAIUIService service) {
        aiuiService = service;
        aiuiService.setAttached(true);
        aiuiService.addAIUIEventListener(this);
        //暂时就这么做吧
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                //清理所见即可说的数据
                aiuiService.clearSpeakableData();
            }
        }, 500);
    }

    @Override
    public void analysisDefaultData(final String jsonData) {
        if (!TextUtils.isEmpty(jsonData)) {
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    //暂时用延迟来保证页面显示出来之后再加载数据
                    onTppResult(jsonData);
                }
            }, 500);
        }
    }

    @Override
    public void onEvent(AIUIEvent event) {
        if (null != event) {
            EventBus.getDefault().post(new SearchByAIRefreshUIEventBean(event));
        }
    }

    @Override
    public void onResult(String iatResult, String nlpReslult, String tppResult) {
        //onIatResult(iatResult);
        if (!aiuiService.isLookMorePageData()) {
            if (!TextUtils.isEmpty(nlpReslult)) {
                onNlpResult(nlpReslult);
            }
            if (!TextUtils.isEmpty(tppResult)) {
                onTppResult(tppResult);
            }
        }
    }

    private void onNlpResult(String nlpResult) {
        NlpData mData = gson.fromJson(nlpResult, NlpData.class);
        if (mData.rc == 4) {
            return;
        }

        String service = mData.service;
        switch (service) {
            case AiuiConstants.VIEWCMD_SERVICE:
                intentViewCmd(mData);
                break;
            case AiuiConstants.VIDEO_CMD:
                intentCmd(mData);
                break;
            default:
                break;
        }
    }

    private void onTppResult(String result) {
        NlpData nlpData = gson.fromJson(result, NlpData.class);
        //判断是否解出了语义，并且当前技能是video
        if (nlpData.rc == 4 || !("video".equals(nlpData.service) || "LINGXI2018.user_video".equals(nlpData.service))) {
            if (nlpData.moreResults == null) {
                return;
            }
            if (AiuiConstants.VIEWCMD_SERVICE.equals(nlpData.service) && AiuiConstants.VIDEO_SERVICE.equals(nlpData.moreResults.get(0).service)) {
                return;
            }
            nlpData = nlpData.moreResults.get(0);
            if (nlpData.rc == 4 || !("video".equals(nlpData.service) || "LINGXI2018.user_video".equals(nlpData.service))) {
                return;
            }
        }

        if (nlpData.moreResults != null) {
            if ("QUERY".equals(nlpData.moreResults.get(0).semantic.get(0).intent)) {
                nlpData = nlpData.moreResults.get(0);
            }
        }

        //语义后处理没有返回数据则直接退出
        if (!hasVideoData(nlpData) || !"0000000".equals(nlpData.data.lxresult.code)) {
            //没有影片数据且存在answer 则播报  随机播报一条反馈语言
            AiResponse.Response response = AiResponse.getInstance().getNetWorkStatus();
            aiuiService.tts(response.response);
            sendMessage(response.response, MESSAGE_TYPE_NORMAL, MESSAGE_FROM_AI);
            return;
        }

        AiResponse.Response responseTts = null;
        Map<String, String> map = formatSlotsToMap(nlpData.semantic.get(0).slots);
        lastVideoData = result;
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
                if (isCategory(map)) {
                    AiResponse.Response response = AiResponse.getInstance().getGuessWhatYouLike();
                    //hasSubserials(nlpData);
                    boolean hasSubserials = true;
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
                        if (map.containsKey(AiuiConstants.VIDEO_CATEGORY) && "电影".equals(map.get(AiuiConstants.VIDEO_CATEGORY))) {
                            response.response = String.format(response.response, "电影");
                        } else {
                            response.response = String.format(response.response, "视频");
                        }
                    }
                    responseTts = response;
                }
                if (messageType == MESSAGE_TYPE_NORMAL && nlpData.answer != null && !TextUtils.isEmpty(nlpData.answer.text)) {
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
                    if (map.containsKey(AiuiConstants.VIDEO_CATEGORY) && "电影".equals(map.get(AiuiConstants.VIDEO_CATEGORY))) {
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

    private void intentCmd(NlpData mData) {
        Map<String, String> solts = formatSlotsToMap(mData.semantic.get(0).slots);
        switch (mData.semantic.get(0).intent) {
            case AiuiConstants.VIDEO_CMD_INTENT:
                if (solts.containsKey(AiuiConstants.VIDEO_INDEX) && lastSearchIsGuessWhatYouLike()) {
                    int index = Integer.parseInt(solts.get(AiuiConstants.VIDEO_INDEX)) - 1;
                    if (lastVideoSearchByAIBean != null && lastVideoSearchByAIBean.getVideoList() != null && lastVideoSearchByAIBean.getVideoList().size() > 0) {
                        List<TppData.SubserialsBean> subserials = lastVideoSearchByAIBean.getVideoList().get(0).subserials;
                        Collections.reverse(subserials);
                        if (index >= 0 && index < subserials.size()) {
                            String name = subserials.get(index).name;
                            aiuiService.getNavigation().playEpisode(new NavigationBean(subserials.get(index)));
                            aiuiService.tts("正在为你打开" + name);
                        }
                    }
                }
                break;
            default:
                break;
        }
    }

    /**
     * 获得卡片标题
     *
     * @param map
     * @return
     */
    private String makeCardTitle(Map<String, String> map) {
        StringBuilder cardTitle = new StringBuilder();
        if (map == null) {
            return cardTitle.toString();
        }
        if (map.containsKey(AiuiConstants.VIDEO_NAME)) {
            return map.get(AiuiConstants.VIDEO_NAME);
        }
        if (map.containsKey(AiuiConstants.VIDEO_ARTIST)) {
            cardTitle.append("“").append(map.get(AiuiConstants.VIDEO_ARTIST).replace("|", "、")).append("”").append("的");
        }
        if (map.containsKey(AiuiConstants.VIDEO_POPULAR)) {
            cardTitle.append("“").append(map.get(AiuiConstants.VIDEO_POPULAR).replace("|", "、")).append("”");
        }
        if (map.containsKey(AiuiConstants.VIDEO_AREA)) {
            cardTitle.append("“").append(map.get(AiuiConstants.VIDEO_AREA).replace("|", "、")).append("”");
        }
        boolean hasCatgeory = false;
        if (map.containsKey(AiuiConstants.VIDEO_TAG)) {
            String tag = map.get(AiuiConstants.VIDEO_TAG);
            String[] tags = tag.split("\\|");
            boolean hasMoreTags = false;
            for (String tag1 : tags) {
                switch (tag1) {
                    case "综艺":
                        hasCatgeory = true;
                        cardTitle.append(hasMoreTags ? "和综艺节目" : "综艺节目");
                        break;
                    case "动画":
                        hasCatgeory = true;
                        cardTitle.append(hasMoreTags ? "和动画片" : "动画片");
                        break;
                    case "纪录":
                        hasCatgeory = true;
                        cardTitle.append(hasMoreTags ? "和纪录片" : "纪录片");
                        break;
                    default:
                        cardTitle.append(hasMoreTags ? "、“" + tag1 + "”" : "“" + tag1 + "”");
                        break;
                }
                hasMoreTags = true;
            }
        }
        if (map.containsKey(AiuiConstants.VIDEO_CATEGORY) && !hasCatgeory) {
            cardTitle.append("片".equals(map.get(AiuiConstants.VIDEO_CATEGORY)) ? "电影" : map.get(AiuiConstants.VIDEO_CATEGORY));
        }
        if (TextUtils.isEmpty(cardTitle.toString())) {
            cardTitle = new StringBuilder("影视");
        }
        return cardTitle.toString();
    }

    /**
     * 是否存在剧集数据
     *
     * @param detail
     * @return
     */
    private boolean hasSubserials(TppData.DetailsListBean detail) {
        return !(detail.subserials == null || detail.subserials.size() == 0);
    }

    /**
     * 判断是否存在视频数据
     *
     * @param nlpData
     * @return
     */
    private boolean hasVideoData(NlpData nlpData) {
        return !(nlpData == null
                || nlpData.data == null
                || nlpData.data.lxresult == null
                || nlpData.data.lxresult.data == null
                || nlpData.data.lxresult.data.detailslist == null
                || nlpData.data.lxresult.data.detailslist.size() == 0);
    }

    /**
     * 判断当前类目是什么
     *
     * @param solts
     * @param categoryType
     * @return
     */
    private boolean checkCategory(Map<String, String> solts, CategoryType categoryType) {
        if (solts == null || solts.size() > 2) {
            return false;
        }

        String cate = "";
        if (solts.size() == 1) {
            if (solts.containsKey(AiuiConstants.VIDEO_CATEGORY)) {
                cate = solts.get(AiuiConstants.VIDEO_CATEGORY);
            }

            if (solts.containsKey(AiuiConstants.VIDEO_TAG)) {
                cate = solts.get(AiuiConstants.VIDEO_TAG);
            }

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
                return "电影".equals(cate) || "片".equals(cate) || "影视".equals(cate);
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
        if (solts == null || solts.size() > 2) {
            return false;
        }

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
                if (solts.containsKey(AiuiConstants.VIDEO_CATEGORY)) {
                    cate = solts.get(AiuiConstants.VIDEO_CATEGORY);
                }
                if (solts.containsKey(AiuiConstants.VIDEO_TAG)) {
                    cate = solts.get(AiuiConstants.VIDEO_TAG);
                }
            } else if (solts.size() == 2) {
                String category = solts.get(AiuiConstants.VIDEO_CATEGORY);
                if ("片".equals(category) || "节目".equals(category) || "影视".equals(category)) {
                    cate = solts.get(AiuiConstants.VIDEO_TAG);
                }
            }
            return !TextUtils.isEmpty(cate) && "电视剧,纪录,纪实,电影,影视,片,卡通,动漫,综艺".contains(cate);
        }
        return false;
    }

    /**
     * 判断最后一次搜索是不是猜你喜欢的内容
     *
     * @return
     */
    private boolean lastSearchIsGuessWhatYouLike() {
        return lastVideoSearchByAIBean != null && (lastVideoSearchByAIBean.getMessageType() == MESSAGE_TYPE_GUESS_WHAT_YOU_LIKE || lastVideoSearchByAIBean.getMessageType() == MESSAGE_TYPE_GUESS_WHAT_YOU_LIKE_LIST_HORIZONTAL || lastVideoSearchByAIBean.getMessageType() == MESSAGE_TYPE_GUESS_WHAT_YOU_LIKE_LIST_VERTICAL);
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
     * 处理ViewCmd技能
     */
    private void intentViewCmd(NlpData nlpData) {
        if (AiuiConstants.VIEWCMD_INTENT.equals(nlpData.semantic.get(0).intent)) {
            switch (nlpData.semantic.get(0).slots.get(0).name) {
                case "LOOK_MORE":
                    Intent intent = new Intent(mContext, LookMoreActivity.class);
                    intent.putExtra(LookMoreActivity.KEY_MORE_DATE, lastVideoData);
                    intent.putExtra(LookMoreActivity.KEY_TITLE, lastResponseVideoTitle);
                    mContext.startActivity(intent);
                    break;
                case "CHANGE":
                    if (lastVideoSearchByAIBean != null) {
                        if (lastVideoSearchByAIBean.getVideoList().size() > 1) {
                            lastVideoSearchByAIBean.getVideoList().remove(0);
                            sendMessage(lastVideoSearchByAIBean);
                        } else {
                            aiuiService.tts(AiResponse.getInstance().getChangeResponse().response);
                        }
                    }
                    break;
                case "HORIZONTAL_EPISODE":
                    if (!lastSearchIsGuessWhatYouLike()) {
                        return;
                    }

                    Logger.debug(nlpData.semantic.get(0).slots.get(0).value);
                    break;
                case "VERTICAL_EPISODE":
                    if (!lastSearchIsGuessWhatYouLike()) {
                        return;
                    }
                    if (lastVideoSearchByAIBean.getVideoList() == null || lastVideoSearchByAIBean.getVideoList().size() == 0) {
                        return;
                    }
                    String[] subNames = nlpData.semantic.get(0).slots.get(0).value.split("\\|");
                    TppData.DetailsListBean detail = lastVideoSearchByAIBean.getVideoList().get(0);
                    if (!hasSubserials(detail)) {
                        // names不为空，但是lastVideoList 为null
                        // 说明最后一次lastVideo是没有的，
                        // 这时要清理下为lastVideoList所设置的所见即可说
                        aiuiService.clearSpeakableData();
                        return;
                    }
                    List<TppData.SubserialsBean> selectedSubserialsList = new ArrayList<>();
                    for (String subName : subNames) {
                        for (TppData.SubserialsBean sub : detail.subserials) {
                            Logger.debug("name【" + subName + "】beanName【" + sub.name + "】");
                            if (sub.name.contains(subName)) {
                                // 找到多个匹配结果
                                // 找到多个匹配结果
                                selectedSubserialsList.add(sub);
                                break;
                            }
                        }
                    }
                    if (selectedSubserialsList.size() == 0) {
                        return;
                    }
                    aiuiService.getNavigation().playEpisode(new NavigationBean(selectedSubserialsList.get(0)));
                    aiuiService.tts("正在为你打开," + selectedSubserialsList.get(0).name);
                    break;
                case "VIDEO_NAME":
                    String[] names = nlpData.semantic.get(0).slots.get(0).value.split("\\|");
                    if (lastVideoSearchByAIBean != null && lastVideoSearchByAIBean.getVideoList() == null) {
                        // names不为空，但是lastVideoList 为null
                        // 说明最后一次lastVideo是没有的，
                        // 这时要清理下为lastVideoList所设置的所见即可说
                        aiuiService.clearSpeakableData();
                        return;
                    }
                    List<TppData.DetailsListBean> selectedVideoList = new ArrayList<>();
                    for (String name : names) {
                        for (TppData.DetailsListBean bean : lastVideoSearchByAIBean.getVideoList()) {
                            Logger.debug("name【" + name + "】beanName【" + bean.name + "】");
                            if (name.equals(bean.name)) {
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
                        aiuiService.getNavigation().playEpisode(new NavigationBean(selectedVideoList.get(0)));
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
                        aiuiService.getNavigation().playEpisode(new NavigationBean(detail1.subserials.get(0)));
                        aiuiService.tts("正在为你打开," + detail1.subserials.get(0).name);
                    } else {
                        aiuiService.getNavigation().playVideo(new NavigationBean(lastVideoSearchByAIBean.getVideoList().get(0)));
                        aiuiService.tts("正在为你打开," + lastVideoSearchByAIBean.getVideoList().get(0).name);
                    }

                    break;
                case "SECOND":
                    TppData.DetailsListBean detail2 = lastVideoSearchByAIBean.getVideoList().get(0);
                    if (lastSearchIsGuessWhatYouLike() && hasSubserials(detail2) && detail2.subserials.size() >= 2) {
                        aiuiService.getNavigation().playEpisode(new NavigationBean(detail2.subserials.get(1)));
                        aiuiService.tts("正在为你打开," + detail2.subserials.get(1).name);
                    } else {
                        aiuiService.getNavigation().playVideo(new NavigationBean(lastVideoSearchByAIBean.getVideoList().get(1)));
                        aiuiService.tts("正在为你打开," + lastVideoSearchByAIBean.getVideoList().get(1).name);
                    }
                    break;
                case "THIRD":
                    TppData.DetailsListBean detail3 = lastVideoSearchByAIBean.getVideoList().get(0);
                    if (lastSearchIsGuessWhatYouLike() && hasSubserials(detail3) && detail3.subserials.size() >= 3) {
                        aiuiService.getNavigation().playEpisode(new NavigationBean(detail3.subserials.get(2)));
                        aiuiService.tts("正在为你打开," + detail3.subserials.get(2).name);
                    } else {
                        aiuiService.getNavigation().playVideo(new NavigationBean(lastVideoSearchByAIBean.getVideoList().get(2)));
                        aiuiService.tts("正在为你打开," + lastVideoSearchByAIBean.getVideoList().get(2).name);
                    }
                    break;
                default:
                    break;
            }
        }
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
            syncMap.put("CHANGE", "换一个|下一个|再换一个");
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
        aiuiService.syncSpeakableData(aiuiService.getLastNlpState(), syncMap);
    }

    /**
     * 取消录音
     */
    @Override
    public void cancelRecordAudio() {
        aiuiService.cancelRecordAudio();
    }

    /**
     * SlotsBean key-value 数据转换成Map 类型数据方便查找
     *
     * @param slotsBeans
     * @return
     */
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

    @Override
    public void turnToPlayVideo(int type, boolean isClickLookMore, TppData.DetailsListBean detailsListBean, String deailsJson, int position) {
        switch (type) {
            case MESSAGE_TYPE_EVERYONE_IS_WATCHING:
                if (TextUtils.isEmpty(deailsJson)) {
                    return;
                }
                NlpData nlpData = gson.fromJson(deailsJson, NlpData.class);
                //判断是否解出了语义，并且当前技能是video
                if (nlpData.rc == 4 || !("video".equals(nlpData.service) || "LINGXI2018.user_video".equals(nlpData.service))) {
                    if (nlpData.moreResults == null) {
                        return;
                    }
                    nlpData = nlpData.moreResults.get(0);
                    if (nlpData.rc == 4 || !("video".equals(nlpData.service) || "LINGXI2018.user_video".equals(nlpData.service))) {
                        return;
                    }
                }
                //语义后处理没有返回数据则直接退出
                if (nlpData.data == null
                        || nlpData.data.lxresult == null
                        || nlpData.data.lxresult.data.detailslist.size() == 0
                        || nlpData.data.lxresult.data.detailslist.size() < position) {
                    return;
                }

                playVideo(nlpData.data.lxresult.data.detailslist.get(position).id);
                break;
            case MESSAGE_TYPE_GUESS_WHAT_YOU_LIKE:
                if (null != detailsListBean) {
                    playVideo(detailsListBean.id);
                }
                break;
            case MESSAGE_TYPE_GUESS_WHAT_YOU_LIKE_LIST_HORIZONTAL:
                if (isClickLookMore) {
                    Toast.makeText(mContext, "查看更多", Toast.LENGTH_SHORT).show();
                } else {
                    playVideo(getContentID(detailsListBean, position));
                }
                break;
            case MESSAGE_TYPE_GUESS_WHAT_YOU_LIKE_LIST_VERTICAL:
                if (isClickLookMore) {
                    Toast.makeText(mContext, "查看更多", Toast.LENGTH_SHORT).show();
                } else {
                    if (null != detailsListBean.subserials && detailsListBean.subserials.size() > position) {
                        playVideo(detailsListBean.subserials.get(position).id);
                    }
                }
                break;
            default:
                break;
        }
    }

    /**
     * 播放视频
     *
     * @param contentID
     */
    private void playVideo(String contentID) {
        if (!TextUtils.isEmpty(contentID)) {
        }
    }

    /**
     * 得到剧集id
     *
     * @param detailsListBean
     * @param pos
     * @return
     */
    private String getContentID(TppData.DetailsListBean detailsListBean, int pos) {
        String contentID = "";
        if (null != detailsListBean) {
            List<TppData.SubserialsBean> subserials = detailsListBean.subserials;
            if (null != subserials && subserials.size() > pos) {
                contentID = subserials.get(pos).id;
                //int position = subserials.size() - pos - 1;
                //if (position > -1 && position < subserials.size()) {
                //contentID = subserials.get(position).id;
                //}
            }
        }
        return contentID;
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
        searchByAIBean.setDeailsJson(lastVideoData);
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
}
