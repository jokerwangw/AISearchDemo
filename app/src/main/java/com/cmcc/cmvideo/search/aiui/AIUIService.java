package com.cmcc.cmvideo.search.aiui;

import android.app.Service;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

public class AIUIService extends Service {
    private static final String TAG ="AIUIService";

    private AIUIServiceImpl aiuiService;
    private AIUIAgent mAIUIAgent;
    private int mCurrentState = AIUIConstant.STATE_IDLE;
    private IHandle mNlpHandle;
    private IHandle mTppHandle;
    private SpeechSynthesizer mTTs;

    @Override
    public void onCreate() {
        super.onCreate();
        aiuiService = new AIUIServiceImpl();
        mNlpHandle  = new NlpHandle(aiuiService);
        mTppHandle  = new TppHandle(aiuiService);
        init();
    }

    private void init() {
        //AIUI初始化
        mAIUIAgent = AIUIAgent.createAgent(this, getAIUIParams(), aiuiListener);
        //MSC初始化（登陆）
        SpeechUtility.createUtility(this, "appid=5aceb703");
        //TTS 初始化MSC中的TTS 功能
        mTTs = SpeechSynthesizer.createSynthesizer(this, new InitListener() {
            @Override
            public void onInit(int i) {
                if(i!=0) {
                    Logger.debug("合成初始化失败");
                }else {
                    Logger.debug("合成初始化成功");
                }
            }
        });
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return aiuiService;
    }

    private class AIUIServiceImpl extends Binder implements IAIUIService {
        @Override
        public void tts(String ttsText,SynthesizerListener synthesizerListener) {
            ttsStartSpeaking(ttsText,synthesizerListener);
        }

        @Override
        public void startRecordAudio() {
            sendMessage(new AIUIMessage(AIUIConstant.CMD_START_RECORD, 0, 0, "data_type=audio,sample_rate=16000", null));
        }

        @Override
        public void stopRecordAudio() {
            sendMessage(new AIUIMessage(AIUIConstant.CMD_STOP_RECORD, 0, 0, "data_type=audio,sample_rate=16000", null));
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
                            if ("nlp".equals(sub)||"tpp".equals(sub)) {
                                // 解析得到语义结果

                                if("nlp".equals(sub)){
                                    String resultStr = cntJson.optString("intent");
                                    if (resultStr.equals("{}") || resultStr.isEmpty())
                                        return;
                                    Logger.debug("NLP 【" + resultStr + "】");
                                    mNlpHandle.handle(resultStr);
                                }else {
                                    String resultStr = cntJson.optString("intent");
                                    if (resultStr.equals("{}"))
                                        return;
                                    Logger.debug("TPP 【" + cntJson.toString() + "】");
                                    mTppHandle.handle(cntJson.toString());
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
                    break;
                default:
                    break;
            }
        }
    };
    private void ttsStartSpeaking(String ttsText,SynthesizerListener listener){
        if(mTTs!=null){
            mTTs.startSpeaking(ttsText,listener==null?synthesizerListener:listener);
        }
    }

    private SynthesizerListener synthesizerListener  = new SynthesizerListener() {
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

    private void sendMessage(AIUIMessage message){
        if(mAIUIAgent != null) {
            //确保AIUI处于唤醒状态
            if(mCurrentState != AIUIConstant.STATE_WORKING){
                mAIUIAgent.sendMessage(new AIUIMessage(AIUIConstant.CMD_WAKEUP, 0, 0, "", null));
            }
            mAIUIAgent.sendMessage(message);
        }
    }
    /**
     * 设置TTS 参数
     */
    private void setTTSParam()
    {
        if(mTTs == null) return;
        // 清空参数
        mTTs.setParameter(SpeechConstant.PARAMS, null);
        //设置使用云端引擎
        mTTs.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
        //设置发音人
        mTTs.setParameter(SpeechConstant.VOICE_NAME,"xiaoyan");
        //设置语速
        mTTs.setParameter(SpeechConstant.SPEED,"50");
        //设置音调
        mTTs.setParameter(SpeechConstant.PITCH,"50");
        //设置音量
        mTTs.setParameter(SpeechConstant.VOLUME,"50");
        //设置播放器音频流类型
        mTTs.setParameter(SpeechConstant.STREAM_TYPE,"3");
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

}
