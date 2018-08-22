package com.cmcc.cmvideo.search.aiui;

import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.cmcc.cmvideo.BuildConfig;
import com.cmcc.cmvideo.search.aiui.bean.ControlEventBean;
import com.cmcc.cmvideo.search.aiui.bean.NlpData;
import com.cmcc.cmvideo.search.aiui.impl.LiveEnum;
import com.cmcc.cmvideo.search.model.SearchByAIBean;
import com.cmcc.cmvideo.search.model.SearchByAIEventBean;
import com.cmcc.cmvideo.util.AiResponse;
import com.cmcc.cmvideo.util.AiuiConstants;
import com.google.gson.Gson;
import com.iflytek.aiui.AIUIConstant;
import com.iflytek.aiui.AIUIEvent;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.cmcc.cmvideo.util.AiuiConstants.MessageFrom.MESSAGE_FROM_AI;
import static com.cmcc.cmvideo.util.AiuiConstants.MessageFrom.MESSAGE_FROM_USER;
import static com.cmcc.cmvideo.util.AiuiConstants.MessageType.MESSAGE_TYPE_CAN_ASK_AI;
import static com.cmcc.cmvideo.util.AiuiConstants.MessageType.MESSAGE_TYPE_NORMAL;

public class AIUISemanticProcessor implements AIUIService.AIUIEventListener {
    private long startTime = 0;
    private final int TIME_OUT = 5000;
    private int mCurrentState = AIUIConstant.STATE_IDLE;
    private boolean isAvailableVideo = false;
    //最后一次语义的状态
    private String lastNlpState = "";
    private Gson gson;
    private IAIUIService aiuiService;
    private android.os.Handler mHandler;

    public String getLastNlpState() {
        return lastNlpState;
    }

    public AIUISemanticProcessor(IAIUIService service) {
        aiuiService = service;
        gson = new Gson();
        mHandler = new android.os.Handler(Looper.getMainLooper());
    }

    /**
     * 耳机接入状态
     *
     * @param isConnect
     */
    public void setIsMicConnect(Boolean isConnect) {
        isAvailableVideo = isConnect;
    }

    @Override
    public void onResult(String iatResult, String nlpReslult, String tppResult) {
        if (!TextUtils.isEmpty(nlpReslult)) {
            onNlpResult(nlpReslult);
        }
        if (!TextUtils.isEmpty(tppResult)) {
            onTppResult(tppResult);
        }
    }

    @Override
    public void onEvent(AIUIEvent event) {
        switch (event.eventType) {
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
                //Logger.debug("arg【" + event.arg1 + "】【" + event.arg2 + "】");
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
    }

    private void onNlpResult(String nlpResult) {
        String service = null;
        NlpData mData = gson.fromJson(nlpResult, NlpData.class);

        if (null != mData.service) {
            service = mData.service;
            if (!AiuiConstants.VIEWCMD_SERVICE.equals(service) && !aiuiService.isTextUnderRequest()) {
                sendMessage(mData.text, MESSAGE_TYPE_NORMAL, MESSAGE_FROM_USER);
            }
        }

        if (mData.rc == 4) {
            //播报
            sendMessage(mData.text, MESSAGE_TYPE_NORMAL, MESSAGE_FROM_USER);
            if (isAvailableVideo) {
                AiResponse.Response response = AiResponse.getInstance().getResultResponse();
                aiuiService.tts(response.response);
                sendMessage(response.response, MESSAGE_TYPE_NORMAL, MESSAGE_FROM_AI);
            } else {
                if ((System.currentTimeMillis() - startTime) > TIME_OUT) {
                    // 超过5秒表示 且rc=4（无法解析出语义） ，可显示推荐说法卡片
                    sendMessage("", MESSAGE_TYPE_CAN_ASK_AI, MESSAGE_FROM_AI);
                } else {
                    AiResponse.Response response = AiResponse.getInstance().getResultResponse();
                    aiuiService.tts(response.response);
                    sendMessage(response.response, MESSAGE_TYPE_NORMAL, MESSAGE_FROM_AI);
                }
            }
            return;
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

        try {
            //解析出当前语义状态，以便上传同步客户端状态，实现多伦对话
            JSONObject jsonObject = new JSONObject(nlpResult);
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

        service = mData.service;

        switch (service) {
            case AiuiConstants.QA_SERVICE:
                //闲聊
                intentQa(mData);
                break;
            case AiuiConstants.QUERY_MIGU:
                //业务查询与办理
                intentQuery(mData);
                break;
            case AiuiConstants.CONTROL_MIGU:
                //指令控制  如：打开语音助手/投屏播放
                controlCmdIntent(mData);
                break;
            case AiuiConstants.VIDEO_CMD:
                //视频播放、暂停、下一集、上一集
                controlCmdIntent(mData);
                break;
            //            case AiuiConstants.VIDEO_ON_SERVICE:
            //                //直播模块
            //                intentOnLive(mData);
            //                break;
            case AiuiConstants.WORLD_CUP_SERVICE:
                //伪球迷必备
                intentWorldCup(mData);
                break;
            default:
                break;
        }
    }

    private void onTppResult(String tppResult) {
        NlpData mData = gson.fromJson(tppResult, NlpData.class);

        if (AiuiConstants.VIDEO_SERVICE.equals(mData.service)) {
            aiuiService.showAiUi(tppResult);
        }

        if (mData.moreResults != null && mData.moreResults.size() > 0 &&
                AiuiConstants.VIDEO_SERVICE.equals(mData.moreResults.get(0).service)) {
            aiuiService.showAiUi(tppResult);
        }

        if (AiuiConstants.USER_VIDEO_SERVICE.equals(mData.service)) {
            aiuiService.showAiUi(tppResult);
        }

        //如果是直播技能LINGXI2018.onlive并且意图是channel_query的就解析
        if (AiuiConstants.VIDEO_ON_SERVICE.equals(mData.service)) {
            //直播模块
            intentOnLive(mData);
        }

    }

    /**
     * 控制指令 播放、暂停、下一集、上一集
     */
    private void controlCmdIntent(NlpData mData) {
        String service = mData.service;
        if (mData.rc == 4) {
            return;
        }
        switch (service) {
            case AiuiConstants.VIDEO_CMD:
                //耳机插入场景下执行控制指令
                if (isAvailableVideo) {
                    //视频播放、暂停、下一集、上一集  换一集  快进 快退  快进到xxx
                    intentVideoControl(mData);
                }
                break;
            case AiuiConstants.CONTROL_MIGU:
                //指令控制  如：打开语音助手/投屏播放
                intentControl(mData);
                break;
            default:
                break;
        }
    }

    /**
     * 处理控制指令
     *
     * @param mData
     */
    private void intentControl(NlpData mData) {
        String intent = mData.semantic.get(0).intent;
        switch (intent) {
            case AiuiConstants.CONTROL_INTENT:
                //控制指令跳转
                if (null != mData.semantic && null != mData.semantic.get(0) && null != mData.semantic.get(0).getSlots()) {
                    if (mData.semantic.get(0).getSlots().size() == 1) {
                        String norMalValue = mData.semantic.get(0).getSlots().get(0).normValue;
                        if (norMalValue.equals("open")) {
                            EventBus.getDefault().post(new ControlEventBean(AiuiConstants.VDO_OPEN));
                            Logger.debug("VDO_OPEN=============");
                        } else if (norMalValue.equals("close")) {
                            EventBus.getDefault().post(new ControlEventBean(AiuiConstants.VDO_CLOSE));
                            Logger.debug("VDO_CLOSE============");
                        }
                    }
                }
                //                aiuiService.tts("正在为您" + mData.text);
                break;
            case AiuiConstants.SREEN_INTENT:
                // 投屏跳转
                isAvailableVideo = true;
                EventBus.getDefault().post(new ControlEventBean(AiuiConstants.VDO_SCREEN));
                //                aiuiService.tts("正在为您" + mData.text);
                break;
            default:
                break;
        }
    }

    /**
     * 处理视频播放cmd技能
     */
    private void intentVideoControl(NlpData mData) {
        String intent = mData.semantic.get(0).intent;
        if (AiuiConstants.VIDEO_CMD_INTENT.equals(intent)) {
            Map<String, String> solts = formatSlotsToMap(mData.semantic.get(0).slots);
            if (solts.containsKey(AiuiConstants.VIDEO_INSTYPE)) {
                switch (solts.get(AiuiConstants.VIDEO_INSTYPE)) {
                    case AiuiConstants.VIDEO_PAUSE:
                        //暂停
                        Logger.debug("暂停===" + intent);
                        EventBus.getDefault().post(new ControlEventBean(AiuiConstants.VDO_PAUSE));
                        break;
                    case AiuiConstants.VIDEO_PLAY:
                        //播放
                        EventBus.getDefault().post(new ControlEventBean(AiuiConstants.VDO_PLAY));
                        Logger.debug("播放===" + intent);
                        break;
                    case AiuiConstants.VIDEO_PREVIOUS:
                        //上一集
                        EventBus.getDefault().post(new ControlEventBean(AiuiConstants.VDO_PREVIOUS));
                        Logger.debug("上一集===" + intent);
                        break;
                    case AiuiConstants.VIDEO_NEXT:
                        //下一集
                        EventBus.getDefault().post(new ControlEventBean(AiuiConstants.VDO_NEXT));
                        Logger.debug("下一集===" + intent);
                        break;
                    case AiuiConstants.VIDEO_CHANGE:
                        //换一集
                        EventBus.getDefault().post(new ControlEventBean(AiuiConstants.VDO_CHANGE));
                        Logger.debug("换一集===" + intent);
                        break;
                    case AiuiConstants.VIDEO_FASTWORD:
                        //快进
                        swControl(mData);
                        Logger.debug("快进===" + intent);
                        break;
                    case AiuiConstants.VIDEO_BACKWORD:
                        //快退
                        swControl(mData);
                        Logger.debug("快退===" + intent);
                        break;
                    case AiuiConstants.VIDEO_FASTWORD_TO:
                        //快进or快退到某个时间
                        swControl(mData);
                        Logger.debug("快进到XXXXX===" + intent);
                        break;
                    case AiuiConstants.VIDEO_INDEX:
                        //第几集
                        EventBus.getDefault().post(new ControlEventBean(AiuiConstants.VDO_WHICH_EPISODE));
                        break;
                    default:
                        break;
                }
            }
        }
    }

    /**
     * 视频播放指令控制
     *
     * @param mData
     */
    private void swControl(NlpData mData) {
        Map<String, String> solts = formatSlotsToMap(mData.semantic.get(0).slots);
        if (solts.containsKey(AiuiConstants.VIDEO_INSTYPE)) {
            //快进 or 快退
            if (solts.size() == 1) {
                if (AiuiConstants.VIDEO_FASTWORD.equals(solts.get(AiuiConstants.VIDEO_INSTYPE))) {
                    //快进
                    Logger.debug("快进========" + solts.get(AiuiConstants.VIDEO_INSTYPE));
                    EventBus.getDefault().post(new ControlEventBean(AiuiConstants.VDO_FASTWORD));

                } else if (AiuiConstants.VIDEO_BACKWORD.equals(solts.get(AiuiConstants.VIDEO_INSTYPE))) {
                    //快退
                    Logger.debug("快退========" + solts.get(AiuiConstants.VIDEO_INSTYPE));
                    EventBus.getDefault().post(new ControlEventBean(AiuiConstants.VDO_BACKWORD));

                }
            } else if (solts.size() == 2 || solts.size() == 3 || solts.size() == 4) {
                //快进1小时  、1分钟  、1秒  单值  两值  三值
                formatControlTime(solts);
            }
        }
    }

    /**
     * 控制视频时间
     */
    private void formatControlTime(Map<String, String> solts) {
        String controlVdoTimeHour = null, controlVdoTimeMinu = null, controlVdoTimeSecon = null;

        if (null != solts.get(AiuiConstants.HOURS) && solts.containsKey(AiuiConstants.HOURS)) {
            //小时
            controlVdoTimeHour = solts.get(AiuiConstants.HOURS);
        }

        if (null != solts.get(AiuiConstants.MINUTE) && solts.containsKey(AiuiConstants.MINUTE)) {
            //分钟
            controlVdoTimeMinu = solts.get(AiuiConstants.MINUTE);
        }

        if (null != solts.get(AiuiConstants.SECOND) && solts.containsKey(AiuiConstants.SECOND)) {
            //秒
            controlVdoTimeSecon = solts.get(AiuiConstants.SECOND);
        }

        if (AiuiConstants.VIDEO_FASTWORD.equals(solts.get(AiuiConstants.VIDEO_INSTYPE)) || AiuiConstants.VIDEO_BACKWORD.equals(solts.get(AiuiConstants.VIDEO_INSTYPE))) {
            EventBus.getDefault().post(new ControlEventBean(AiuiConstants.VDO_BACKWORD, controlVdoTimeHour, controlVdoTimeMinu, controlVdoTimeSecon));

        } else if (AiuiConstants.VIDEO_FASTWORD_TO.equals(solts.get(AiuiConstants.VIDEO_INSTYPE))) {
            EventBus.getDefault().post(new ControlEventBean(AiuiConstants.VDO_FASTWORD_TO, controlVdoTimeHour, controlVdoTimeMinu, controlVdoTimeSecon));
        }
        Logger.debug("时间打印====" + controlVdoTimeHour + "=====" + controlVdoTimeMinu + "----" + controlVdoTimeSecon);
    }

    /**
     * 处理商店技能世界杯
     *
     * @param nlpData
     */
    private void intentWorldCup(NlpData nlpData) {
        if ((nlpData.answer != null && !TextUtils.isEmpty(nlpData.answer.text))) {
            aiuiService.tts(nlpData.getAnswer().text);
            sendMessage(nlpData.getAnswer().text, MESSAGE_TYPE_NORMAL, MESSAGE_FROM_AI);
        }
    }

    /**
     * 处理直播模块指令 各意图
     *
     * @param mData
     */
    private void intentOnLive(NlpData mData) {
        String intent = mData.semantic.get(0).intent;
        switch (intent) {
            case AiuiConstants.VIDEO_CHANNEL_INTENT:
                //频道查询 如：我要看CCTV5体育 / 湖南卫视
                aiuiService.getNavigation().toLive(mData);
                break;
            case AiuiConstants.VIDEO_VERITY_INTENT:
                //多样直播视频查询 如：我要看直播 、体育直播
                if (!TextUtils.isEmpty(mData.text)) {
                    if ("我要看直播".equals(mData.text)) {
                        //直接跳转到直播模块
                        aiuiService.getNavigation().toLive(LiveEnum.UNKNOWN);
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
        Logger.debug("直播模块分类：" + textNormal);
        switch (textNormal) {
            case AiuiConstants.CCTV:
                aiuiService.getNavigation().toLive(LiveEnum.CCTV);
                break;
            case AiuiConstants.START_TV:
                aiuiService.getNavigation().toLive(LiveEnum.START_TV);
                break;
            case AiuiConstants.FILMS:
                aiuiService.getNavigation().toLive(LiveEnum.FILMS);
                break;
            case AiuiConstants.CHILDREN:
                aiuiService.getNavigation().toLive(LiveEnum.CHILDREN);
                break;
            case AiuiConstants.NEWS:
                aiuiService.getNavigation().toLive(LiveEnum.NEWS);
                break;
            case AiuiConstants.DOCUMENTARY:
                aiuiService.getNavigation().toLive(LiveEnum.DOCUMENTARY);
                break;
            case AiuiConstants.TURN_TV:
                aiuiService.getNavigation().toLive(LiveEnum.TURN_TV);
                break;
            case AiuiConstants.AREA_TV:
                aiuiService.getNavigation().toLive(LiveEnum.AREA_TV);
                break;
            case AiuiConstants.FEATURE_TV:
                aiuiService.getNavigation().toLive(LiveEnum.FEATURE_TV);
                break;
            case AiuiConstants.SPORTS_TV:
                aiuiService.getNavigation().toLive(LiveEnum.SPORTS_TV);
                break;
            default:
                break;
        }
    }

    /**
     * 处理查询指令
     *
     * @param mData
     */
    private void intentQuery(NlpData mData) {
        String intent = mData.semantic.get(0).intent;
        Logger.debug("查询指令：" + intent);
        switch (intent) {
            case AiuiConstants.MEMBER_INTENT:
                //会员业务
                aiuiService.getNavigation().toMember();
                break;
            case AiuiConstants.INTERNET_INTENT:
                //流量业务
                aiuiService.getNavigation().toInternet();
                break;
            case AiuiConstants.TICKET_INTENT:
                //购票业务
                aiuiService.getNavigation().toTicket();
                break;
            case AiuiConstants.ACYIVITY_INTENT:
                //活动打折业务
                aiuiService.getNavigation().toActivity();
                break;
            case AiuiConstants.GCUSTOMER_INTENT:
                //G客业务，如：上传视频
                aiuiService.getNavigation().toGcustomer();
                break;
            default:
                break;
        }
    }

    /**
     * 处理闲聊技能
     */
    private void intentQa(NlpData nlpData) {
        if ((nlpData.answer != null && !TextUtils.isEmpty(nlpData.answer.text))) {
            aiuiService.tts(nlpData.getAnswer().text);
            sendMessage(nlpData.getAnswer().text, MESSAGE_TYPE_NORMAL, MESSAGE_FROM_AI);
        }
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
        List<SearchByAIBean> messageList = new ArrayList<SearchByAIBean>();
        messageList.add(new SearchByAIBean(msg, messageType, msgFrom, null));
        EventBus.getDefault().post(new SearchByAIEventBean(messageList));
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            //显示功能引导页面
            sendMessage("", MESSAGE_TYPE_CAN_ASK_AI, MESSAGE_FROM_AI);
        }
    };

    public void cancelRecordAudio() {
        mHandler.removeCallbacks(runnable);
    }
}
