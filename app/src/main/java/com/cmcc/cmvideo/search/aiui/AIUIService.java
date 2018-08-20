package com.cmcc.cmvideo.search.aiui;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetManager;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.cmcc.cmvideo.search.SearchByAIActivity;
import com.cmcc.cmvideo.search.aiui.bean.IatBean;
import com.cmcc.cmvideo.search.aiui.impl.NavigationImpl;
import com.cmcc.cmvideo.search.model.LookMoreEventDataBean;
import com.cmcc.cmvideo.util.AiResponse;
import com.cmcc.cmvideo.util.AiuiConstants;
import com.cmcc.cmvideo.util.FileUtil;
import com.cmcc.cmvideo.util.ServiceUtils;
import com.google.gson.Gson;
import com.iflytek.aiui.AIUIAgent;
import com.iflytek.aiui.AIUIConstant;
import com.iflytek.aiui.AIUIEvent;
import com.iflytek.aiui.AIUIListener;
import com.iflytek.aiui.AIUIMessage;
import com.iflytek.cloud.SpeechUtility;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class AIUIService extends Service {
    public static final String AIUI_SERVICE_NAME = "com.cmcc.cmvideo.search.aiui.AIUIService";
    private static final String TAG = "AIUIService";
    private AIUIServiceImpl aiuiService;
    private INavigation navigation;
    private AIUIAgent mAIUIAgent;
    private int mCurrentState = AIUIConstant.STATE_IDLE;
    private AIUIEventListenerManager eventListenerManager;
    private Map<String, String> userInfoMap;
    private boolean isIvwModel = false;
    private boolean hasSetLookMorePageSize = false;
    private boolean hasSyncData = false;
    private boolean hasClearData = false;
    private boolean hasCancelRecordAudio = false;
    private boolean isAvailableVideo = false;
    private AIUISemanticProcessor semanticProcessor;
    private boolean uiAttached = false;

    private byte[] fileData;
    //是否正在tts播报中
    private boolean isTtsing = false;


    @Override
    public void onCreate() {
        super.onCreate();
        aiuiService = new AIUIServiceImpl();
        navigation = new NavigationImpl(this);
        eventListenerManager = new AIUIEventListenerManager();
        semanticProcessor = new AIUISemanticProcessor(aiuiService);
        eventListenerManager.addAIUIEventListener(semanticProcessor);
        init();
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
    }


    //SDK 初始化
    private void init() {
        //AIUI初始化
        mAIUIAgent = AIUIAgent.createAgent(this, getAIUIParams(), aiuiListener);
        mAIUIAgent.sendMessage(new AIUIMessage(AIUIConstant.CMD_START, 0, 0, "", null));
        //MSC初始化（登陆）
        SpeechUtility.createUtility(this, "appid=5aceb703");

        //        SpeechUtility.createUtility(AIUIService.this, String.format("engine_start=ivw,delay_init=0,appid=%s", "5aceb703"));
        sendBroadcast();
        setUserData();
        Logger.debug(">>>>>>>>>>>onCreate>>>>>>>");
    }

    /**
     * 发送检测耳机状态广播
     */
    private void sendBroadcast() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_HEADSET_PLUG);
        registerReceiver(mReceiver, intentFilter);
    }


    private void setUserData() {
        Map<String, String> map = new HashMap<String, String>() {{
            put("msisdn", "13764279837");
            put("user_id", "553782460");
            put("client_id", "897ddadc222ec9c20651da355daee9cc");
        }};
        userInfoMap = map;
        AIUIService.this.setUserParam();
    }


    private void ivwMode() {
        Logger.debug(">>>>>>>>>ivwMode=========>>>>>>>>");
        setParam("5000", "ivw", "continuous", "sdk");
        fileData = FileUtil.readFileFromAssets(AIUIService.this, "wav/migumigu.wav");
        AIUIMessage writeMsg = new AIUIMessage(AIUIConstant.CMD_WRITE, 0, 0, "data_type=audio,sample_rate=16000", fileData);
        sendMessage(writeMsg);
        sendMessage(new AIUIMessage(AIUIConstant.CMD_START_RECORD, 0, 0, "data_type=audio,sample_rate=16000", null));
        setUserData();
        isIvwModel = true;
        AIUIMessage writeStopMsg = new AIUIMessage(AIUIConstant.CMD_STOP_WRITE, 0, 0, "data_type=audio,sample_rate=16000", fileData);
        sendMessage(writeStopMsg);
    }

    /**
     * 设置模式参数
     *
     * @param interactTime
     * @param wakeupMode
     * @param interactMode
     * @param dataSource
     */
    private void setParam(String interactTime, String wakeupMode, String interactMode, String dataSource) {

        try {
            JSONObject objectJson = new JSONObject();
            JSONObject paramJson = new JSONObject();
            JSONObject timeJson = new JSONObject();

            timeJson.put("interact_timeout", interactTime);
            timeJson.put("result_timeout", "5000");
            paramJson.put("wakeup_mode", wakeupMode);
            paramJson.put("interact_mode", interactMode);
            paramJson.put("data_source", dataSource);
            objectJson.put("speech", paramJson);
            objectJson.put("interact", timeJson);
            sendMessage(new AIUIMessage(AIUIConstant.CMD_SET_PARAMS, 0, 0, objectJson.toString(), null));

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    /**
     * 标准模式
     */
    private void standardMode() {
        AIUIService.this.cancelTts();
        sendMessage(new AIUIMessage(AIUIConstant.CMD_STOP_RECORD, 0, 0, "data_type=audio,sample_rate=16000", null));
        setParam("60000", "off", "oneshot", "sdk");
        isIvwModel = false;

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return aiuiService;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    private class AIUIServiceImpl extends Binder implements IAIUIService {
        @Override
        public void setInteractMode(boolean isOnShot) {
            if (isOnShot) {
                String setParams = "{\"speech\":{\"interact_mode\":\"oneshot\"}}";
                AIUIMessage setMsg = new AIUIMessage(AIUIConstant.CMD_SET_PARAMS, 0, 0, setParams, null);
                mAIUIAgent.sendMessage(setMsg);
            } else {
                String setParams = "{\"speech\":{\"interact_mode\":\"continuous\"}}";
                AIUIMessage setMsg = new AIUIMessage(AIUIConstant.CMD_SET_PARAMS, 0, 0, setParams, null);
                mAIUIAgent.sendMessage(setMsg);
            }
        }

        @Override
        public void tts(String ttsText) {
            AIUIService.this.tts(ttsText);
        }

        @Override
        public void cancelTts() {
            //取消语音合成
            AIUIService.this.cancelTts();

        }

        @Override
        public void startRecordAudio() {
            if (!isIvwModel) {
                hasCancelRecordAudio = false;
                if (hasSetLookMorePageSize) {
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
        public void setAttached(boolean isAttached) {
            uiAttached = isAttached;
        }

        @Override
        public void showAiUi(String data) {
            if (!uiAttached) {
                Intent intent = new Intent(AIUIService.this, SearchByAIActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("TPP_DATA", data);
                AIUIService.this.startActivity(intent);
            }
        }

        @Override
        public String getLastNlpState() {
            return semanticProcessor.getLastNlpState();
        }

        @Override
        public INavigation getNavigation() {
            return navigation;
        }

        @Override
        public void onResume(boolean flag) {
            hasSetLookMorePageSize = flag;
        }

        @Override
        public void cancelRecordAudio() {
            hasCancelRecordAudio = true;
            semanticProcessor.cancelRecordAudio();
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
                                String json = new String(event.data.getByteArray(cnt_id), "utf-8");
                                JSONObject cntJson = null;
                                if (!TextUtils.isEmpty(json)) {
                                    cntJson = new JSONObject(json);
                                }
                                if (cntJson == null)
                                    return;
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

                                    eventListenerManager.onResult(null, resultStr, null);
                                } else {
                                    String resultStr = cntJson.optString("intent");
                                    if (resultStr.equals("{}")) {
                                        return;
                                    }

                                    String jsonResultStr = cntJson.toString();
                                    //                                    LogUtil.e("TPP===", resultStr);
                                    Logger.debug("TPP 【" + jsonResultStr + "】");
                                    EventBus.getDefault().post(new LookMoreEventDataBean(jsonResultStr));
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
                case AIUIConstant.CMD_STOP_RECORD: {
                }
                break;
                case AIUIConstant.EVENT_ERROR:
                    break;
                case AIUIConstant.EVENT_WAKEUP:
                    if (isIvwModel) {
                        if (!isTtsing) {
                            tts(AiuiConstants.MICRO_MESSAGE);
                        }
                    }
                    break;
                case AIUIConstant.EVENT_PRE_SLEEP:
                    Logger.debug(">>>>>>>>>>>>EVENT_PRE_SLEEP>>>>>>>>>>>>");
                    //如果正在合成
                    if (isTtsing) {
                        mAIUIAgent.sendMessage(new AIUIMessage(AIUIConstant.CMD_WAKEUP, 0, 0, "", null));
                    }
                    break;
                case AIUIConstant.EVENT_SLEEP:
                    Logger.debug(">>>>>>>>>>>>EVENT_SLEEP>>>>>>>>>>>>");
                    if (isIvwModel) {
                        if (!isTtsing) {
                            tts(AiResponse.getInstance().getSleep().response);
                        }
                    }
                    break;
                case AIUIConstant.EVENT_VAD:
                    //                Logger.debug("arg【" + event.arg1 + "】【" + event.arg2 + "】");
                    //用arg1标识前后端点或者音量信息:0(前端点)、1(音量)、2(后端点)、3（前端点超时）。
                    //当arg1取值为1时，arg2为音量大小。
                    if (event.arg1 == 0) {
                        //检测到前端点表示正在录音
                        AIUIService.this.cancelTts();
                    }
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
                case AIUIConstant.EVENT_TTS: {
                    switch (event.arg1) {
                        case AIUIConstant.TTS_SPEAK_BEGIN:
                            isTtsing = true;
                            Logger.debug("==========TTS_SPEAK_BEGIN");
                            // 停止后台音频播放
                            FuncAdapter.Lock(AIUIService.this, null);
                            break;
                        case AIUIConstant.TTS_SPEAK_COMPLETED:
                            isTtsing = false;
                            Logger.debug("=============TTS_SPEAK_COMPLETED");
                            // 开启后台音频播放
                            FuncAdapter.UnLock(AIUIService.this, null);
                            break;
                        case AIUIConstant.TTS_SPEAK_PAUSED:
                            Logger.debug("暂停播放");
                            break;

                        case AIUIConstant.TTS_SPEAK_RESUMED:
                            Logger.debug("恢复播放");
                            break;

                        default:
                            break;
                    }
                }
                break;
                default:
                    break;
            }
            //AIUI状态分发给各客户端监听
            eventListenerManager.onEvent(event);
        }
    };


    private void cancelTts() {
        sendMessage(new AIUIMessage(AIUIConstant.CMD_TTS, AIUIConstant.CANCEL, 0, "", null));
    }


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
        params.append(",ent=xtts");//引擎，默认aisound，如果需要较好的效果，可设置成xtts
        //开始合成
        Logger.debug("合成参数【" + params.toString() + "】");
        sendMessage(new AIUIMessage(AIUIConstant.CMD_TTS, AIUIConstant.START, 0, params.toString(), ttsData));
    }

    //设置页码
    private void setPageInfo(String pageIndex, String pageSize) {
        if (userInfoMap != null) {
            userInfoMap.put("pageindex", pageIndex);
            userInfoMap.put("pagesize", pageSize);
            setUserParam();
        }
    }

    //生效动态实体
    public void effectDynamicEntity() {
        try {
            JSONObject params = new JSONObject();
            JSONObject audioParams = new JSONObject();
            audioParams.put("pers_param", "{\"uid\":\"\"}");
            params.put("audioparams", audioParams);
            AIUIMessage setMsg = new AIUIMessage(AIUIConstant.CMD_SET_PARAMS, 0, 0, params.toString(), null);
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
            mAIUIAgent.sendMessage(syncAthenaMessage);
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

    //发送AIUI消息
    private void sendMessage(final AIUIMessage message) {
        if (mAIUIAgent != null) {
            //确保AIUI处于唤醒状态
            if (mCurrentState != AIUIConstant.STATE_WORKING) {
                mAIUIAgent.sendMessage(new AIUIMessage(AIUIConstant.CMD_WAKEUP, 0, 0, "", null));
            }

            mAIUIAgent.sendMessage(message);

        }
    }

    //同步用户数据
    private void setUserParam() {
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
                sendMessage(new AIUIMessage(AIUIConstant.CMD_SET_PARAMS, 0, 0, objectJson.toString(), null));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    //获取AIUI参数
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
                        semanticProcessor.setIsMicConnect(false);
                        navigation.isHeadset(false);
                        //切换为外放模式
                        //                        PlayerManager.getInstance().changeToReceiver();
                        if (isIvwModel) {
                            standardMode();
                        }
                    } else if (intent.getIntExtra("state", 0) == 1) {
                        isAvailableVideo = true;
                        navigation.isHeadset(true);
                        semanticProcessor.setIsMicConnect(true);
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


    private static String oldTtsMsg;
    private static long time;

    private void showTts(String ttsMsg) {
        if (!ttsMsg.equals(oldTtsMsg)) { // 当显示的内容不一样时，即断定为不是同一个Toast
            if (isIvwModel) {
                tts(ttsMsg);
            }
        } else {
            time = System.currentTimeMillis();
            // 显示内容一样时，只有间隔时间大于2秒时才显示
            if (System.currentTimeMillis() - time > 5000) {
                tts(ttsMsg);
                time = System.currentTimeMillis();
            }
        }
        oldTtsMsg = ttsMsg;

    }


}
