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

import com.cmcc.cmvideo.search.aiui.bean.IatBean;
import com.cmcc.cmvideo.search.aiui.bean.MicBean;
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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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

    @Override
    public void onCreate() {
        super.onCreate();
        aiuiService = new AIUIServiceImpl();
        eventListenerManager = new AIUIEventListenerManager();
        init();
        Logger.debug("AIUIService has onCreated!");
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
            sendMessage(new AIUIMessage(AIUIConstant.CMD_SET_PARAMS, 0, 0, objectJson.toString(), null));
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
            sendMessage(new AIUIMessage(AIUIConstant.CMD_SET_PARAMS, 0, 0, objectJson.toString(), null));
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

        @Override
        public void clearSpeakableData() {
            AIUIService.this.clearSpeakableData();
        }

        @Override
        public void syncSpeakableData(String stateKey, String hotInfo) {
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
                    Logger.debug("CMD_START_RECORD===========");
                    break;
                case AIUIConstant.EVENT_ERROR:
                    //                    Logger.debug("EVENT_ERROR===========" + event.arg1 + "  " + event.info);
                    break;
                case AIUIConstant.EVENT_WAKEUP:
                    //                    Logger.debug("EVENT_WAKEUP==========arg1【" + event.arg1 + "】arg2【" + event.arg2 + "】info【" + event.info + "】");
                    break;
                case AIUIConstant.EVENT_SLEEP:
                    Logger.debug("EVENT_SLEEP===========");
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
            sendMessage(new AIUIMessage(AIUIConstant.CMD_SET_PARAMS, 0, 0, objectJson.toString(), null));
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
}
