package com.cmcc.cmvideo.search.aiui;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetManager;
import android.media.AudioDeviceInfo;
import android.media.AudioManager;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.cmcc.cmvideo.search.aiui.bean.IatBean;
import com.cmcc.cmvideo.search.aiui.bean.MicBean;
import com.cmcc.cmvideo.search.aiui.bean.NlpData;
import com.cmcc.cmvideo.util.PlayerManager;
import com.google.gson.Gson;
import com.iflytek.aiui.AIUIAgent;
import com.iflytek.aiui.AIUIConstant;
import com.iflytek.aiui.AIUIEvent;
import com.iflytek.aiui.AIUIListener;
import com.iflytek.aiui.AIUIMessage;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.SynthesizerListener;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
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
    private static final String TAG = "AIUIService";

    private AIUIServiceImpl aiuiService;
    private AIUIAgent mAIUIAgent;
    private int mCurrentState = AIUIConstant.STATE_IDLE;
    private SpeechSynthesizer mTTs;
    private AIUIEventListener eventListener;
    private Map<String, String> userInfoMap;
    private AudioManager audoManager;

    @Override
    public void onCreate() {
        super.onCreate();
        aiuiService = new AIUIServiceImpl();
        init();
    }

    @Override
    public void onDestroy() {
        if (mAIUIAgent != null)
            mAIUIAgent.destroy();
        if (mTTs != null) {
            if (mTTs.isSpeaking())
                mTTs.stopSpeaking();
            mTTs.destroy();
        }
        SpeechUtility.getUtility().destroy();
        if (null != mReceiver) {
            unregisterReceiver(mReceiver);
        }
        super.onDestroy();
    }

    /**
     * SDK 初始化
     */
    private void init() {
        //AIUI初始化
        mAIUIAgent = AIUIAgent.createAgent(this, getAIUIParams(), aiuiListener);
        //MSC初始化（登陆）
        SpeechUtility.createUtility(this, "appid=5aceb703");
        //TTS 初始化MSC中的TTS 功能
        mTTs = SpeechSynthesizer.createSynthesizer(this, new InitListener() {
            @Override
            public void onInit(int i) {
                if (i != 0) {
                    Logger.debug("合成初始化失败");
                } else {
                    Logger.debug("合成初始化成功");
                }
            }
        });

        //注册耳机是否插入广播
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_HEADSET_PLUG);
        registerReceiver(mReceiver, intentFilter);
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return aiuiService;
    }

    private class AIUIServiceImpl extends Binder implements IAIUIService {
        @Override
        public void tts(String ttsText, SynthesizerListener synthesizerListener) {
            ttsStartSpeaking(ttsText, synthesizerListener);
            //AIUIService.this.tts(ttsText);
        }

        @Override
        public void startRecordAudio() {
            sendMessage(new AIUIMessage(AIUIConstant.CMD_START_RECORD, 0, 0, "data_type=audio,sample_rate=16000", null));
        }

        @Override
        public void stopRecordAudio() {
            sendMessage(new AIUIMessage(AIUIConstant.CMD_STOP_RECORD, 0, 0, "data_type=audio,sample_rate=16000", null));
        }

        @Override
        public void setAIUIEventListener(AIUIEventListener aiuiEventListener) {
            eventListener = aiuiEventListener;
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
        public void syncSpeakableData(String hotInfo) {
            AIUIService.this.syncSpeakableData(hotInfo);
        }
    }


    private AIUIListener aiuiListener = new AIUIListener() {
        @Override
        public void onEvent(AIUIEvent event) {
            switch (event.eventType) {
                case AIUIConstant.EVENT_RESULT: {
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
                            JSONObject cntJson = new JSONObject(new String(event.data.getByteArray(cnt_id), "utf-8"));

                            String sub = params.optString("sub");
                            Logger.debug("SUB 【" + sub + "】 【" + cntJson.toString() + "】");
                            if ("iat".equals(sub) || "nlp".equals(sub) || "tpp".equals(sub)) {
                                // 解析得到语义结果

                                if ("iat".equals(sub)) {
                                    String iat = cntJson.optString("text");
                                    if (iat.equals("{}") || iat.isEmpty()) {
                                        return;
                                    }

                                    String iatTxt = getIatTxt(iat);
                                    if (TextUtils.isEmpty(iatTxt)) {
                                        return;
                                    }
                                    if (eventListener != null) {
                                        eventListener.onResult(iatTxt, null, null);
                                    }

                                } else if ("nlp".equals(sub)) {
                                    String resultStr = cntJson.optString("intent");
                                    if (resultStr.equals("{}") || resultStr.isEmpty())
                                        return;
                                    Logger.debug("NLP 【" + resultStr + "】");
                                    if (eventListener != null)
                                        eventListener.onResult(null, resultStr, null);
                                } else {
                                    String resultStr = cntJson.optString("intent");
                                    if (resultStr.equals("{}"))
                                        return;
                                    String jsonResultStr = cntJson.toString();
                                    Logger.debug("TPP 【" + jsonResultStr + "】");
                                    if (eventListener != null)
                                        eventListener.onResult(null, null, jsonResultStr);
                                }
                            }
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }
                break;
                case AIUIConstant.EVENT_WAKEUP:

                    break;
                case AIUIConstant.EVENT_STATE:
                    mCurrentState = event.arg1;
                    break;
                case AIUIConstant.EVENT_CMD_RETURN:
                    AIUIEvent event1 = event;
                    if (event1.arg1 == AIUIConstant.CMD_SYNC) {
                        int dtype = event.data.getInt("sync_dtype");

                        //arg2表示结果
                        if (0 == event.arg2) {          // 同步成功
                            Logger.debug("sync_dtype is "+dtype);
                            switch (dtype){
                                case AIUIConstant.SYNC_DATA_SPEAKABLE:
                                    effectDynamicEntity();
                                    break;
                                case AIUIConstant.SYNC_DATA_STATUS:

                                    break;
                            }
                        }
                    }
                default:
                    break;
            }
            //AIUI状态分发给各客户端监听
            if (eventListener != null) {
                eventListener.onEvent(event);
            }
        }
    };

    private void ttsStartSpeaking(String ttsText, SynthesizerListener listener) {
        if (mTTs != null) {
            setTTSParam();
            mTTs.startSpeaking(ttsText, listener == null ? synthesizerListener : listener);
        }
    }

    private void tts(String ttsText) {
        if (TextUtils.isEmpty(ttsText))
            return;
        byte[] ttsData = new byte[0];  //转为二进制数据
        try {
            ttsData = ttsText.getBytes("utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        StringBuffer params = new StringBuffer();  //构建合成参数
        params.append("vcn=xiaoyan");  //合成发音人
        params.append(",speed=50");  //合成速度
        params.append(",pitch=50");  //合成音调
        params.append(",volume=50");  //合成音量
        //开始合成
        AIUIMessage startTts = new AIUIMessage(25, 1, 0, params.toString(), ttsData);
        mAIUIAgent.sendMessage(startTts);
    }

    private SynthesizerListener synthesizerListener = new SynthesizerListener() {
        @Override
        public void onSpeakBegin() {

        }

        @Override
        public void onBufferProgress(int i, int i1, int i2, String s) {

        }

        @Override
        public void onSpeakPaused() {

        }

        @Override
        public void onSpeakResumed() {

        }

        @Override
        public void onSpeakProgress(int i, int i1, int i2) {

        }

        @Override
        public void onCompleted(SpeechError speechError) {

        }

        @Override
        public void onEvent(int i, int i1, int i2, Bundle bundle) {

        }
    };

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
//    //同步所见即可说
//    public void syncSpeakableData(String data) {
//        try {
//            JSONObject syncSpeakableJson = new JSONObject();
//            // 识别用户数据
//            JSONObject iatUserDataJson = new JSONObject();
//            iatUserDataJson.put("recHotWords", "查看全部|下一页");
//            iatUserDataJson.put("sceneInfo", new JSONObject());
//            syncSpeakableJson.put("iat_user_data", iatUserDataJson);
//
//            // 语义理解用户数据
//            JSONObject nlpUserDataJson = new JSONObject();
//            JSONArray resArray = new JSONArray();
//            JSONObject resDataItem = new JSONObject();
//            resDataItem.put("res_name", "LINGXI2018.see_say_res");
//            //resDataItem.put("res_name", "IFLYTEK.viewCmd");
//            StringBuilder stringBuilder = new StringBuilder();
//            stringBuilder.append(data);
//            resDataItem.put("data", Base64.encodeToString(
//                    stringBuilder.toString().getBytes(), Base64.NO_WRAP));
//            resArray.put(resDataItem);
//
//            nlpUserDataJson.put("res", resArray);
//            nlpUserDataJson.put("skill_name", "LINGXI2018.see_say_res");
//
//            syncSpeakableJson.put("nlp_user_data", nlpUserDataJson);
//
//            // 传入的数据一定要为utf-8编码
//            byte[] syncData = syncSpeakableJson.toString().getBytes("utf-8");
//
//            AIUIMessage syncAthenaMessage = new AIUIMessage(AIUIConstant.CMD_SYNC,
//                    AIUIConstant.SYNC_DATA_SPEAKABLE, 0, "", syncData);
//            mAIUIAgent.sendMessage(syncAthenaMessage);
//        } catch (Exception e) {
//            e.printStackTrace();
//
//        }
//    }
    public void clearSpeakableData(){
        try {
            if (mAIUIAgent != null) {
                //确保AIUI处于唤醒状态
                if (mCurrentState != AIUIConstant.STATE_WORKING) {
                    mAIUIAgent.sendMessage(new AIUIMessage(AIUIConstant.CMD_WAKEUP, 0, 0, "", null));
                }
                String params = "{\"viewCmd::default\":{\"activeStatus\":\"fg\",\"data\":{\"hotInfo\":{}},\"sceneStatus\":\"\"}}";
                byte[] syncData = params.getBytes("utf-8");

                AIUIMessage syncAthenaMessage = new AIUIMessage(AIUIConstant.CMD_SYNC,
                        AIUIConstant.SYNC_DATA_STATUS, 0, params,syncData);
                mAIUIAgent.sendMessage(syncAthenaMessage);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //同步所见即可说
    public void syncSpeakableData(String hotInfo) {
        try {
            String params = "{\"viewCmd::default\":{\"activeStatus\":\"fg\",\"data\":{\"hotInfo\":{\"viewCmd\":\"%s\"}},\"sceneStatus\":\"default\"}}";
            params = String.format(params,hotInfo);
            byte[] syncData = params.getBytes("utf-8");

            AIUIMessage syncAthenaMessage = new AIUIMessage(AIUIConstant.CMD_SYNC,
                    AIUIConstant.SYNC_DATA_STATUS, 0, params, syncData);
            mAIUIAgent.sendMessage(syncAthenaMessage);
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
     * 设置TTS 参数
     */
    private void setTTSParam() {
        if (mTTs == null) return;
        // 清空参数
        mTTs.setParameter(SpeechConstant.PARAMS, null);
        //设置使用云端引擎
        mTTs.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
        //设置发音人
        mTTs.setParameter(SpeechConstant.VOICE_NAME, "xiaoyan");
        //设置语速
        mTTs.setParameter(SpeechConstant.SPEED, "50");
        //设置音调
        mTTs.setParameter(SpeechConstant.PITCH, "50");
        //设置音量
        mTTs.setParameter(SpeechConstant.VOLUME, "50");
        //设置播放器音频流类型
        mTTs.setParameter(SpeechConstant.STREAM_TYPE, "3");
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
                        PlayerManager.getInstance().changeToSpeaker();
                    } else if (intent.getIntExtra("state", 0) == 1) {
                        EventBus.getDefault().post(new MicBean(true));
                        //切换为耳机模式
                        PlayerManager.getInstance().changeToHeadset();
                    }
                }
            }
        }
    };


}
