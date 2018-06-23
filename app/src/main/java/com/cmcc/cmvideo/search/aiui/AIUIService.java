package com.cmcc.cmvideo.search.aiui;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.cmcc.cmvideo.search.aiui.bean.ControlEventBean;
import com.cmcc.cmvideo.search.aiui.bean.IatBean;
import com.cmcc.cmvideo.search.aiui.bean.MicBean;
import com.cmcc.cmvideo.search.aiui.bean.NlpData;
import com.cmcc.cmvideo.search.aiui.bean.TppData;
import com.cmcc.cmvideo.search.model.SearchByAIBean;
import com.cmcc.cmvideo.search.model.SearchByAIEventBean;
import com.cmcc.cmvideo.search.presenters.impl.SearchByAIPresenterImpl;
import com.cmcc.cmvideo.util.AiResponse;
import com.cmcc.cmvideo.util.AiuiConstants;
import com.cmcc.cmvideo.util.LogUtil;
import com.google.gson.Gson;
import com.iflytek.aiui.AIUIAgent;
import com.iflytek.aiui.AIUIConstant;
import com.iflytek.aiui.AIUIEvent;
import com.iflytek.aiui.AIUIListener;
import com.iflytek.aiui.AIUIMessage;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.SynthesizerListener;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.cmcc.cmvideo.util.Constants.MESSAGE_FROM_AI;
import static com.cmcc.cmvideo.util.Constants.MESSAGE_FROM_USER;
import static com.cmcc.cmvideo.util.Constants.MESSAGE_TYPE_CAN_ASK_AI;
import static com.cmcc.cmvideo.util.Constants.MESSAGE_TYPE_NORMAL;
import static com.iflytek.aiui.AIUIConstant.CMD_SET_PARAMS;

public class AIUIService extends Service {
    private static final String TAG = "AIUIService";
    private AIUIServiceImpl aiuiService;
    private AIUIAgent mAIUIAgent;
    private int mCurrentState = AIUIConstant.STATE_IDLE;
    private AIUIEventListenerManager eventListenerManager;
    private Map<String, String> userInfoMap;
    private AudioManager audoManager;
    private boolean isIvwModel = false;
    private boolean hasSetLookMorePageSize = false;
    private boolean hasSyncData = false;
    private boolean hasClearData = false;
    private boolean hasCancelRecordAudio = false;
    private Gson gson;
    private String intent = null;
    private NlpData mData = null;
    private long startTime = 0;
    private final int TIME_OUT = 5000;
    //是否是在投屏状态或者插入耳机状态
    private boolean isAvailableVideo = false;
    private Map<String, String> solts = null;
    private String controlVdoTimeHour = null, controlVdoTimeMinu = null, controlVdoTimeSecon = null;


    @Override
    public void onCreate() {
        super.onCreate();
        isAvailableVideo = false;
        gson = new Gson();
        aiuiService = new AIUIServiceImpl();
        eventListenerManager = new AIUIEventListenerManager();
        init();
        Logger.debug("AIUIService has onCreated!");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        if (mAIUIAgent != null) {
            mAIUIAgent.destroy();
        }
        SpeechUtility.getUtility().destroy();
        if (null != mReceiver) {
            unregisterReceiver(mReceiver);
        }
        super.onDestroy();
        Logger.debug("AIUIService has onDestroy!");
    }

    /**
     * SDK 初始化
     */
    private void init() {
        //AIUI初始化
        mAIUIAgent = AIUIAgent.createAgent(this, getAIUIParams(), aiuiListener);
        //MSC初始化（登陆）
        SpeechUtility.createUtility(this, "appid=5aceb703");
        //注册耳机是否插入广播
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_HEADSET_PLUG);
        registerReceiver(mReceiver, intentFilter);
    }

    private void ivwMode() {

        try {
            if (SpeechUtility.getUtility() != null) {
                SpeechUtility.getUtility().destroy();
            }
            SpeechUtility.createUtility(AIUIService.this, String.format("engine_start=ivw,delay_init=0,appid=%s", "5aceb703"));
            if (mAIUIAgent == null) {
                mAIUIAgent = AIUIAgent.createAgent(this, getAIUIParams(), aiuiListener);
            }

            JSONObject objectJson = new JSONObject();
            JSONObject paramJson = new JSONObject();
            paramJson.put("wakeup_mode", "ivw");
            objectJson.put("speech", paramJson);
            sendMessage(new AIUIMessage(CMD_SET_PARAMS, 0, 0, objectJson.toString(), null));
            mAIUIAgent.sendMessage(new AIUIMessage(AIUIConstant.CMD_STOP, 0, 0, "", null));
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    //延时启动保障完全停止后 能够重新启动
                    mAIUIAgent.sendMessage(new AIUIMessage(AIUIConstant.CMD_START, 0, 0, "", null));
                    //根据需求文档直接进入working 状态sendMessage中会再发送CMD_WEAKUP
                    //正常应该发送mAIUIAgent.sendMessage(new AIUIMessage(AIUIConstant.CMD_START_RECORD, 0, 0, "data_type=audio,sample_rate=16000", null));
                    //进入的是等待说出“咪咕咪咕” 的带唤醒状态
                    sendMessage(new AIUIMessage(AIUIConstant.CMD_START_RECORD, 0, 0, "data_type=audio,sample_rate=16000", null));
                    tts("小咪为你服务");

                    isIvwModel = true;
                }
            }, 500);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Logger.debug("已启动唤醒模式");
    }

    private void standardMode() {
        try {
            if (mAIUIAgent == null) {
                mAIUIAgent = AIUIAgent.createAgent(this, getAIUIParams(), aiuiListener);
            }

            JSONObject objectJson = new JSONObject();
            JSONObject paramJson = new JSONObject();
            paramJson.put("wakeup_mode", "off");
            objectJson.put("speech", paramJson);
            sendMessage(new AIUIMessage(CMD_SET_PARAMS, 0, 0, objectJson.toString(), null));
            mAIUIAgent.sendMessage(new AIUIMessage(AIUIConstant.CMD_STOP, 0, 0, "", null));
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    //延时启动保障完全停止后 能够重新启动
                    mAIUIAgent.sendMessage(new AIUIMessage(AIUIConstant.CMD_START, 0, 0, "", null));
                    isIvwModel = false;
                }
            }, 500);
            SpeechUtility.createUtility(this, "appid=5aceb703");
            Logger.debug("已启动标准模式");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return aiuiService;
    }

    private class AIUIServiceImpl extends Binder implements IAIUIService {
        @Override
        public void setInteractMode(boolean isOnShot) {
            if (isOnShot) {
                String setParams = "{\"speech\":{\"interact_mode\":\"oneshot\"}}";
                AIUIMessage setMsg = new AIUIMessage(CMD_SET_PARAMS, 0, 0, setParams, null);
                mAIUIAgent.sendMessage(setMsg);
            } else {
                String setParams = "{\"speech\":{\"interact_mode\":\"continuous\"}}";
                AIUIMessage setMsg = new AIUIMessage(CMD_SET_PARAMS, 0, 0, setParams, null);
                mAIUIAgent.sendMessage(setMsg);
            }
        }

        @Override
        public void tts(String ttsText) {
            AIUIService.this.tts(ttsText);
        }

        @Override
        public void startRecordAudio() {
            if (!isIvwModel) {
                hasCancelRecordAudio = false;
                if (hasSetLookMorePageSize) {

                    setPageInfo("1", "3");
                    hasSetLookMorePageSize = false;
                }
                sendMessage(new AIUIMessage(AIUIConstant.CMD_START_RECORD, 0, 0, "data_type=audio,sample_rate=16000", null));
            }
        }

        @Override
        public void stopRecordAudio() {
            if (!isIvwModel) {
                sendMessage(new AIUIMessage(AIUIConstant.CMD_STOP_RECORD, 0, 0, "data_type=audio,sample_rate=16000", null));
            }
        }

        @Override
        public void addAIUIEventListener(AIUIEventListener aiuiEventListener) {
            eventListenerManager.addAIUIEventListener(aiuiEventListener);
        }

        @Override
        public void removeAIUIEventListener(AIUIEventListener aiuiEventListener) {
            eventListenerManager.removeAIUIEventListener(aiuiEventListener);
        }

        @Override
        public void setUserParam(Map<String, String> map) {
            userInfoMap = map;
            AIUIService.this.setUserParam();
        }

        @Override
        public void clearSpeakableData() {
            AIUIService.this.clearSpeakableData();
        }

        @Override
        public void syncSpeakableData(String stateKey, String hotInfo) {
            AIUIService.this.syncSpeakableData(stateKey, hotInfo);
        }

        @Override
        public void syncSpeakableData(String stateKey, Map<String, String> hotInfo) {
            AIUIService.this.syncSpeakableData(stateKey, hotInfo);
        }

        private String lookMoreText;
        private int pageIndex;
        private int pageSize;

        @Override
        public void getLookMorePage(final String lookMoreText, final int pageIndex, final int pageSize) {
            this.lookMoreText = lookMoreText;
            this.pageIndex = pageIndex;
            this.pageSize = pageSize;
            hasSetLookMorePageSize = true;
            if (hasSyncData) {
                //如果有同步所见即可说数据先要清除数据，避免lookMoreText中带了上一次查找的内容而干扰结果返回
                //同时由于clearSpeakableData 是异步的，所以在清楚数据后的getPage在EVENT_CMD_RETURN事件（即清除成功返回）中执行
                AIUIService.this.clearSpeakableData();
                hasSyncData = false;
                hasClearData = true;
            } else {
                getPage();
            }
        }

        @Override
        public void textUnderstander(String text) {
            String params = "data_type=text";
            byte[] textData = text.getBytes();
            AIUIMessage msg = new AIUIMessage(AIUIConstant.CMD_WRITE, 0, 0, params, textData);
            sendMessage(msg);
        }

        @Override
        public boolean isLookMorePageData() {
            return hasSetLookMorePageSize;
        }


        @Override
        public void cancelRecordAudio() {
            hasCancelRecordAudio = true;
            stopRecordAudio();
        }

        public void getPage() {
            setPageInfo(pageIndex + "", pageSize + "");
            String params = "data_type=text";
            byte[] textData = lookMoreText.getBytes();
            AIUIMessage msg = new AIUIMessage(AIUIConstant.CMD_WRITE, 0, 0, params, textData);
            sendMessage(msg);
        }
    }

    /**
     * 控制指令 播放、暂停、下一集、上一集
     */
    private void controlCmdIntent(String result) {
        Logger.debug("controlCmdIntent===========");
        if (TextUtils.isEmpty(result)) {
            return;
        }
        mData = gson.fromJson(result, NlpData.class);
        String service = mData.service;
//        if (!AiuiConstants.VIEWCMD_SERVICE.equals(service)) {
//            Logger.debug("听写用户输入数据=====" + mData.text);
//            sendMessageUI(mData.text, MESSAGE_TYPE_NORMAL, MESSAGE_FROM_USER);
//        }

        //如果包含moreResults且service是video则直接返回，如果是viewCmd则要发送消息
//        if (null != mData && null != mData.moreResults) {
//            mData = mData.moreResults.get(0);
//            if (("video".equals(mData.service))) {
//                Logger.debug("video=================++++++++++++++++++===================" + mData.service);
//                if (AiuiConstants.VIEWCMD_SERVICE.equals(service)) {
//                    Logger.debug("viewCmd=================--------------===================" + service);
//                    sendMessageUI(mData.text, MESSAGE_TYPE_NORMAL, MESSAGE_FROM_USER);
//                } else {
//                    return;
//                }
//            }
//        } else {
//            Logger.debug("video====================================" + service);
//        }

        if (mData.rc == 4) {
            //播报
            if ((System.currentTimeMillis() - startTime) > TIME_OUT) {
                // 超过5秒表示 且rc=4（无法解析出语义） ，可显示推荐说法卡片
                sendMessageUI("", MESSAGE_TYPE_CAN_ASK_AI, MESSAGE_FROM_AI);
            } else {
                AiResponse.Response response = AiResponse.getInstance().getResultResponse();
                aiuiService.tts(response.response);
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
            aiuiService.syncSpeakableData(lastNlpState, "");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (null != mData.semantic) {
            intent = mData.semantic.get(0).getIntent();
        }

        switch (service) {
            case AiuiConstants.VIDEO_CMD:
                //耳机插入场景下执行控制指令
                if (isAvailableVideo) {
                    //视频播放、暂停、下一集、上一集  换一集  快进 快退  快进到xxx
                    intentVideoControl(mData, intent);
                }

                break;
            case AiuiConstants.CONTROL_MIGU:
                //指令控制  如：打开语音助手/投屏播放
                intentControl(mData, intent);
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
                aiuiService.tts("正在为您" + mData.text);
                break;
            case AiuiConstants.SREEN_INTENT:
                // TODO: 2018/5/30 投屏跳转
                isAvailableVideo = true;
                EventBus.getDefault().post(new ControlEventBean(AiuiConstants.VDO_SCREEN));
                aiuiService.tts("正在为您" + mData.text);
                break;
            default:
                break;
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
            solts = formatSlotsToMap(mData.semantic.get(0).slots);
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
        solts = formatSlotsToMap(mData.semantic.get(0).slots);
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
        controlVdoTimeHour = null;
        controlVdoTimeMinu = null;
        controlVdoTimeSecon = null;

        if (null != solts.get(AiuiConstants.HOURS)
                && solts.containsKey(AiuiConstants.HOURS)) {
            //小时
            controlVdoTimeHour = solts.get(AiuiConstants.HOURS);

        }

        if (null != solts.get(AiuiConstants.MINUTE)
                && solts.containsKey(AiuiConstants.MINUTE)) {
            //分钟
            controlVdoTimeMinu = solts.get(AiuiConstants.MINUTE);
        }


        if (null != solts.get(AiuiConstants.SECOND)
                && solts.containsKey(AiuiConstants.SECOND)) {
            //秒
            controlVdoTimeSecon = solts.get(AiuiConstants.SECOND);

        }

        if (AiuiConstants.VIDEO_FASTWORD.equals(solts.get(AiuiConstants.VIDEO_INSTYPE))
                || AiuiConstants.VIDEO_BACKWORD.equals(solts.get(AiuiConstants.VIDEO_INSTYPE))) {
            EventBus.getDefault().post(new ControlEventBean(AiuiConstants.VDO_BACKWORD, controlVdoTimeHour, controlVdoTimeMinu, controlVdoTimeSecon));

        } else if (AiuiConstants.VIDEO_FASTWORD_TO.equals(solts.get(AiuiConstants.VIDEO_INSTYPE))) {
            EventBus.getDefault().post(new ControlEventBean(AiuiConstants.VDO_FASTWORD_TO, controlVdoTimeHour, controlVdoTimeMinu, controlVdoTimeSecon));
        }
        Logger.debug("时间打印====" + controlVdoTimeHour + "=====" + controlVdoTimeMinu + "----" + controlVdoTimeSecon);
    }


    private AIUIListener aiuiListener = new AIUIListener() {
        @Override
        public void onEvent(AIUIEvent event) {
            switch (event.eventType) {
                case AIUIConstant.EVENT_RESULT: {
                    if (hasCancelRecordAudio) {
                        return;
                    }

                    try {
                        JSONObject bizParamJson = new JSONObject(event.info);
                        JSONObject data = bizParamJson.getJSONArray("data").getJSONObject(0);
                        JSONObject params = data.getJSONObject("params");
                        JSONObject content = data.getJSONArray("content").getJSONObject(0);

                        if (content.has("cnt_id")) {
                            String cnt_id = content.getString("cnt_id");
                            if (cnt_id.isEmpty()) {
                                return;
                            }
                            String sub = params.optString("sub");
                            if ("iat".equals(sub) || "nlp".equals(sub) || "tpp".equals(sub)) {
                                // 解析得到语义结果
                                JSONObject cntJson = new JSONObject(new String(event.data.getByteArray(cnt_id), "utf-8"));
                                if ("iat".equals(sub)) {
                                    String iat = cntJson.optString("text");
                                    if (iat.equals("{}") || iat.isEmpty()) {
                                        return;
                                    }

                                    String iatTxt = getIatTxt(iat);
                                    if (TextUtils.isEmpty(iatTxt)) {
                                        return;
                                    }
                                    eventListenerManager.onResult(iatTxt, null, null);

                                } else if ("nlp".equals(sub)) {
                                    String resultStr = cntJson.optString("intent");
                                    if (resultStr.equals("{}") || resultStr.isEmpty()) {
                                        return;
                                    }
                                    Logger.debug("NLP 【" + resultStr + "】");
                                    mData = gson.fromJson(resultStr, NlpData.class);
                                    if (!TextUtils.isEmpty(mData.service)) {
                                        String service = mData.service;
                                        if (AiuiConstants.VIDEO_CMD.equals(service) || AiuiConstants.CONTROL_MIGU.equals(service)) {
                                            controlCmdIntent(resultStr);
                                            return;
                                        }
                                    }
                                    eventListenerManager.onResult(null, resultStr, null);
                                } else {
                                    String resultStr = cntJson.optString("intent");
                                    if (resultStr.equals("{}")) {
                                        return;
                                    }

                                    String jsonResultStr = cntJson.toString();
                                    //                                    LogUtil.e("TPP===", resultStr);
                                    Logger.debug("TPP 【" + jsonResultStr + "】");
                                    eventListenerManager.onResult(null, null, jsonResultStr);
                                }
                            }
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }
                break;
                case AIUIConstant.CMD_START_RECORD:

                    break;
                case AIUIConstant.EVENT_ERROR:

                    break;
                case AIUIConstant.EVENT_WAKEUP:
                    //每次唤醒都同步用户数据
                    setUserParam();
                    break;
                case AIUIConstant.EVENT_SLEEP:

                    break;
                case AIUIConstant.EVENT_STATE:
                    mCurrentState = event.arg1;
                    Logger.debug("state is 【" + mCurrentState + "】");
                    break;
                case AIUIConstant.EVENT_CMD_RETURN:
                    AIUIEvent event1 = event;
                    if (event1.arg1 == AIUIConstant.CMD_SYNC) {
                        int dtype = event.data.getInt("sync_dtype");
                        //arg2表示结果
                        if (0 == event.arg2) {          // 同步成功
                            Logger.debug("sync_dtype is " + dtype);
                            switch (dtype) {
                                case AIUIConstant.SYNC_DATA_SPEAKABLE:
                                    Logger.debug("SYNC_DATA_SPEAKABLE 生效");
                                    effectDynamicEntity();
                                    break;
                                case AIUIConstant.SYNC_DATA_STATUS:
                                    Logger.debug("同步所见即可说数据成功");
                                    if (hasClearData) {
                                        aiuiService.getPage();
                                        hasClearData = false;
                                    }
                                    break;
                                default:
                                    break;
                            }
                        } else {
                            Logger.debug(event.info);
                        }
                    }
                default:
                    break;
            }
            //AIUI状态分发给各客户端监听
            eventListenerManager.onEvent(event);
        }
    };

    private void tts(String ttsText) {
        if (TextUtils.isEmpty(ttsText)) {
            return;
        }
        //转为二进制数据
        byte[] ttsData = new byte[0];
        try {
            ttsData = ttsText.getBytes("utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        StringBuffer params = new StringBuffer();  //构建合成参数
        params.append("vcn=jiajia");  //合成发音人
        params.append(",speed=85");  //合成速度
        params.append(",pitch=30");  //合成音调
        params.append(",volume=100");  //合成音量
        //开始合成
        Logger.debug("合成参数【" + params.toString() + "】");
        sendMessage(new AIUIMessage(AIUIConstant.CMD_TTS, AIUIConstant.START, 0, params.toString(), ttsData));
    }

    //设置页码
    private void setPageInfo(String pageIndex, String pageSize) {
        try {
            JSONObject objectJson = new JSONObject();
            JSONObject paramJson = new JSONObject();
            paramJson.put("pageindex", pageIndex);
            paramJson.put("pagesize", pageSize);
            objectJson.put("userparams", paramJson);
            sendMessage(new AIUIMessage(CMD_SET_PARAMS, 0, 0, objectJson.toString(), null));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //生效动态实体
    public void effectDynamicEntity() {
        try {
            JSONObject params = new JSONObject();
            JSONObject audioParams = new JSONObject();
            audioParams.put("pers_param", "{\"uid\":\"\"}");
            params.put("audioparams", audioParams);
            AIUIMessage setMsg = new AIUIMessage(CMD_SET_PARAMS, 0, 0, params.toString(), null);
            mAIUIAgent.sendMessage(setMsg);
        } catch (JSONException e) {
        }
    }

    //清除所见即可说
    public void clearSpeakableData() {
        try {
            JSONObject data = new JSONObject();
            JSONObject state = new JSONObject();
            state.put("activeStatus", "fg");
            state.put("sceneStatus", "default");
            data.put("video::default", state);
            JSONObject viewCmd = new JSONObject();
            viewCmd.put("activeStatus", "bg");
            viewCmd.put("sceneStatus", "default");
            JSONObject viewCmdData = new JSONObject();
            JSONObject viewCmdHotInfo = new JSONObject();
            viewCmdHotInfo.put("viewCmd", "");
            viewCmdData.put("hotInfo", viewCmdHotInfo);
            viewCmd.put("data", viewCmdData);
            data.put("viewCmd::default", viewCmd);
            String params = data.toString();
            byte[] syncData = params.getBytes("utf-8");
            AIUIMessage syncAthenaMessage = new AIUIMessage(AIUIConstant.CMD_SYNC, AIUIConstant.SYNC_DATA_STATUS, 0, params, syncData);
            sendMessage(syncAthenaMessage);
            Logger.debug("删除状态数据【" + data.toString() + "】");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //同步所见即可说
    public void syncSpeakableData(String stateKey, String hotInfo) {
        try {
            if (TextUtils.isEmpty(stateKey) && TextUtils.isEmpty(hotInfo)) {
                return;
            }

            JSONObject data = new JSONObject();
            if (!TextUtils.isEmpty(stateKey)) {
                String[] statep = stateKey.split("::");
                JSONObject state = new JSONObject();
                state.put("activeStatus", statep[0]);
                state.put("sceneStatus", statep[3]);
                data.put(statep[1] + "::" + statep[2], state);
            }
            if (!TextUtils.isEmpty(hotInfo)) {
                JSONObject viewCmd = new JSONObject();
                viewCmd.put("activeStatus", "bg");
                viewCmd.put("sceneStatus", "default");
                JSONObject viewCmdData = new JSONObject();
                JSONObject viewCmdHotInfo = new JSONObject();
                viewCmdHotInfo.put("viewCmd", hotInfo);
                viewCmdData.put("hotInfo", viewCmdHotInfo);
                viewCmd.put("data", viewCmdData);
                data.put("viewCmd::default", viewCmd);
            }
            String params = data.toString();
            byte[] syncData = params.getBytes("utf-8");
            AIUIMessage syncAthenaMessage = new AIUIMessage(AIUIConstant.CMD_SYNC, AIUIConstant.SYNC_DATA_STATUS, 0, params, syncData);
            mAIUIAgent.sendMessage(syncAthenaMessage);
            hasSyncData = true;
            Logger.debug("同步状态数据【" + data.toString() + "】");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //同步所见即可说
    public void syncSpeakableData(String stateKey, Map<String, String> hotInfo) {
        try {
            if (TextUtils.isEmpty(stateKey) && (hotInfo == null || hotInfo.size() == 0)) {
                return;
            }

            JSONObject data = new JSONObject();
            if (!TextUtils.isEmpty(stateKey)) {
                String[] statep = stateKey.split("::");
                JSONObject state = new JSONObject();
                state.put("activeStatus", statep[0]);
                state.put("sceneStatus", statep[3]);
                data.put(statep[1] + "::" + statep[2], state);
            }
            if (hotInfo != null && hotInfo.size() > 0) {
                JSONObject viewCmd = new JSONObject();
                viewCmd.put("activeStatus", "bg");
                viewCmd.put("sceneStatus", "default");
                JSONObject viewCmdData = new JSONObject();
                JSONObject viewCmdHotInfo = new JSONObject();
                Iterator<Map.Entry<String, String>> iterator = hotInfo.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<String, String> entry = iterator.next();
                    viewCmdHotInfo.put(entry.getKey(), entry.getValue());
                }
                viewCmdData.put("hotInfo", viewCmdHotInfo);
                viewCmd.put("data", viewCmdData);
                data.put("viewCmd::default", viewCmd);
            }
            String params = data.toString();
            byte[] syncData = params.getBytes("utf-8");
            AIUIMessage syncAthenaMessage = new AIUIMessage(AIUIConstant.CMD_SYNC, AIUIConstant.SYNC_DATA_STATUS, 0, params, syncData);
            mAIUIAgent.sendMessage(syncAthenaMessage);
            hasSyncData = true;
            Logger.debug("同步状态数据【" + data.toString() + "】");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送AIUI消息
     *
     * @param message
     */
    private void sendMessage(AIUIMessage message) {
        if (mAIUIAgent != null) {
            //确保AIUI处于唤醒状态
            if (mCurrentState != AIUIConstant.STATE_WORKING) {
                mAIUIAgent.sendMessage(new AIUIMessage(AIUIConstant.CMD_WAKEUP, 0, 0, "", null));
            }
            mAIUIAgent.sendMessage(message);
        }
    }

    /**
     * 同步用户数据
     */
    private void setUserParam(){
        if (userInfoMap != null && userInfoMap.size() > 0) {
            try {
                JSONObject objectJson = new JSONObject();
                JSONObject paramJson = new JSONObject();
                //用户数据添加的初始化参数中
                Iterator<Map.Entry<String, String>> iterator = userInfoMap.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<String, String> item = iterator.next();
                    paramJson.put(item.getKey(), item.getValue());
                }
                objectJson.put("userparams", paramJson);
                sendMessage(new AIUIMessage(CMD_SET_PARAMS, 0, 0, objectJson.toString(), null));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取AIUI参数
     */
    private String getAIUIParams() {
        String params = "";
        AssetManager assetManager = getResources().getAssets();
        try {
            InputStream ins = assetManager.open("cfg/aiui_phone.cfg");
            byte[] buffer = new byte[ins.available()];
            ins.read(buffer);
            ins.close();
            params = new String(buffer);
            JSONObject paramsJson = new JSONObject(params);
            params = paramsJson.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return params;
    }

    public interface AIUIEventListener {
        void onResult(String iatResult, String nlpReslult, String tppResult);

        void onEvent(AIUIEvent event);

    }

    private static Gson mGson;

    private String getIatTxt(String iat) {
        if (mGson == null) {
            mGson = new Gson();
        }
        IatBean iatBean = mGson.fromJson(iat, IatBean.class);
        String text = "";
        List<IatBean.WsBean> ws = iatBean.ws;
        for (int i = 0; i < ws.size(); i++) {
            IatBean.WsBean wsBean = ws.get(i);
            List<IatBean.WsBean.CwBean> cw = wsBean.cw;
            for (int j = 0; j < cw.size(); j++) {
                IatBean.WsBean.CwBean cwBean = cw.get(j);
                text = text + cwBean.w;
            }
        }
        return text;
    }

    /**
     * 检测耳机是否插入
     */
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_HEADSET_PLUG)) {
                if (intent.hasExtra("state")) {
                    if (intent.getIntExtra("state", 0) == 0) {
                        EventBus.getDefault().post(new MicBean(false));
                        //切换为外放模式
                        //PlayerManager.getInstance().changeToReceiver();
                        if (isIvwModel) {
                            standardMode();
                        }
                    } else if (intent.getIntExtra("state", 0) == 1) {
                        EventBus.getDefault().post(new MicBean(true));
                        //切换为耳机模式
                        //PlayerManager.getInstance().changeToHeadset();
                        if (!isIvwModel) {
                            ivwMode();
                        }
                    }
                }
            }
        }
    };

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


    private int lastResponseVideoMessageType = MESSAGE_TYPE_NORMAL;
    private List<TppData.DetailsListBean> lastVideoList = null;
    //最后一次语义的状态
    private String lastNlpState = "";
    private String lastRequestVideoText = "";

    /**
     * 发送消息更新UI
     *
     * @param msg         消息内容
     * @param messageType 消息内容（普通闲聊内容，影片内容）
     * @param msgFrom     消息来源，
     */
    private void sendMessageUI(String msg, int messageType, String msgFrom) {
        sendUIMessage(msg, messageType, msgFrom, null);
    }

    /**
     * 发送消息更新UI
     *
     * @param msg         消息内容
     * @param messageType 消息内容（普通闲聊内容，影片内容）
     * @param msgFrom     消息来源，
     * @param videoList   影片内容影片数据，
     */
    private void sendUIMessage(String msg, int messageType, String msgFrom, List<TppData.DetailsListBean> videoList) {
        if (videoList != null && videoList.size() > 0) {
            lastResponseVideoMessageType = messageType;
            lastVideoList = videoList;
            //服务端返回数据就去同步所见即可说
            StringBuilder hotInfo = new StringBuilder("查看更多|换一个|");
            for (TppData.DetailsListBean bean : videoList) {
                hotInfo.append(bean.name).append("|");
            }
            hotInfo = new StringBuilder(hotInfo.substring(0, hotInfo.lastIndexOf("|")));
            Logger.debug("所见即可说同步数据【" + hotInfo + "】");
            AIUIService.this.syncSpeakableData(lastNlpState, hotInfo.toString());
        }
        List<SearchByAIBean> messageList = new ArrayList<SearchByAIBean>();
        SearchByAIBean searchByAIBean = new SearchByAIBean(msg, messageType, msgFrom, videoList);
        searchByAIBean.setSpeechText(lastRequestVideoText);
        messageList.add(searchByAIBean);
        EventBus.getDefault().post(new SearchByAIEventBean(messageList));
    }
}
