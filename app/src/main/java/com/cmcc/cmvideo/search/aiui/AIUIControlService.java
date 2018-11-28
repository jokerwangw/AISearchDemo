package com.cmcc.cmvideo.search.aiui;

import android.app.Service;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.cmcc.cmvideo.search.aiui.bean.IatBean;
import com.google.gson.Gson;
import com.iflytek.aiui.AIUIAgent;
import com.iflytek.aiui.AIUIConstant;
import com.iflytek.aiui.AIUIEvent;
import com.iflytek.aiui.AIUIListener;
import com.iflytek.aiui.AIUIMessage;
import com.iflytek.cloud.SpeechUtility;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Yyw on 2018/11/18.
 * Describe:
 */
public class AIUIControlService extends Service {
    private AIUIAgent mAIUIAgent;
    private int mCurrentState = AIUIConstant.STATE_IDLE;
    private AIUIControlServiceImpl aiuiCtrolService = null;
    private AIUIEventListener mAIUIEventListener = null;

    public interface AIUIEventListener {
        void onResult(String iatResult, String nlpReslult, String tppResult);

        void onEvent(AIUIEvent event);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return aiuiCtrolService;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        aiuiCtrolService = new AIUIControlServiceImpl();
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
        super.onDestroy();
    }

    /**
     * SDK 初始化
     */
    private void init() {
        //AIUI初始化
        mAIUIAgent = AIUIAgent.createAgent(this, getAIUIParams(), aiuiListener);
        mAIUIAgent.sendMessage(new AIUIMessage(AIUIConstant.CMD_START, 0, 0, "", null));
        //MSC初始化（登陆）
        SpeechUtility.createUtility(this, "appid=5aceb703");

        setUserDataParam("", "", "", "1");
        Logger.debug(">>>>>>>>>>>onCreate>>>>>>>");
    }

    private class AIUIControlServiceImpl extends Binder implements IAIUIControlService {

        @Override
        public void startRecordAudio() {
            sendMessage(new AIUIMessage(AIUIConstant.CMD_START_RECORD, 0, 0, "data_type=audio,sample_rate=16000", null));
        }

        @Override
        public void stopRecordAudio() {
            sendMessage(new AIUIMessage(AIUIConstant.CMD_STOP_RECORD, 0, 0, "data_type=audio,sample_rate=16000", null));
        }

        @Override
        public void addAIUIEventListener(AIUIEventListener resultDispatchListener) {
            mAIUIEventListener = resultDispatchListener;
        }

        @Override
        public void removeAIUIEventListener(AIUIEventListener resultDispatchListener) {
            mAIUIEventListener = null;
        }

        @Override
        public void setEnableVadEos(boolean isVadEos) {
            if (isVadEos) {
                //正常视频搜索时候设置
                String setParams = "{\"vad\":{\"vad_eos\":\"1500\"}}";
                AIUIMessage setMsg = new AIUIMessage(AIUIConstant.CMD_SET_PARAMS, 0, 0, setParams, null);
                mAIUIAgent.sendMessage(setMsg);
            } else {
                //大屏遥控器设置
                String setParams = "{\"vad\":{\"vad_eos\":\"10000\"}}";
                AIUIMessage setMsg = new AIUIMessage(AIUIConstant.CMD_SET_PARAMS, 0, 0, setParams, null);
                mAIUIAgent.sendMessage(setMsg);
            }
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
                            String sub = params.optString("sub");

                            if ("iat".equals(sub) || "nlp".equals(sub) || "tpp".equals(sub)) {
                                // 解析得到语义结果
                                String json = new String(event.data.getByteArray(cnt_id), "utf-8");
                                JSONObject cntJson = null;
                                if (!TextUtils.isEmpty(json)) {
                                    cntJson = new JSONObject(json);
                                }
                                if (cntJson == null) {
                                    return;
                                }

                                if (!TextUtils.isEmpty(event.data.getString("tag"))) {
                                    String tag = event.data.getString("tag");
                                    Logger.debug("文本请求的标签===" + tag);
                                }

                                if ("iat".equals(sub)) {
                                    String iat = cntJson.optString("text");
                                    if (iat.equals("{}") || iat.isEmpty()) {
                                        return;
                                    }

                                    String iatTxt = getIatTxt(iat);
                                    if (TextUtils.isEmpty(iatTxt)) {
                                        return;
                                    }
                                    if (null != mAIUIEventListener) {
                                        mAIUIEventListener.onResult(iatTxt, null, null);
                                    }

                                } else if ("nlp".equals(sub)) {
                                    String resultStr = cntJson.optString("intent");
                                    if (resultStr.equals("{}") || resultStr.isEmpty()) {
                                        return;
                                    }
                                    Logger.debug("NLP 【" + resultStr + "】");
                                    if (null != mAIUIEventListener) {
                                        mAIUIEventListener.onResult(null, resultStr, null);
                                    }

                                } else {
                                    String resultStr = cntJson.optString("intent");
                                    if (resultStr.equals("{}")) {
                                        return;
                                    }
                                    String jsonResultStr = cntJson.toString();
                                    //LogUtil.e("TPP===", resultStr);
                                    Logger.debug("TPP 【" + jsonResultStr + "】");
                                    if (null != mAIUIEventListener) {
                                        mAIUIEventListener.onResult(null, null, jsonResultStr);
                                    }
                                }
                            }
                        }
                    } catch (Throwable e) {
                        Logger.error(e);
                    }
                }
                break;
                case AIUIConstant.CMD_START_RECORD:
                    break;
                case AIUIConstant.CMD_STOP_RECORD:
                    break;
                case AIUIConstant.EVENT_ERROR:
                    break;
                case AIUIConstant.EVENT_WAKEUP:
                    break;
                case AIUIConstant.EVENT_SLEEP:
                    break;
                case AIUIConstant.EVENT_VAD:
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
                        if (0 == event.arg2) {
                            // 同步成功
                            Logger.debug("sync_dtype is " + dtype);
                            switch (dtype) {
                                case AIUIConstant.SYNC_DATA_SPEAKABLE:
                                    Logger.debug("SYNC_DATA_SPEAKABLE 生效");
                                    effectDynamicEntity();
                                    break;
                                default:
                                    break;
                            }
                        } else {
                            Logger.debug(event.info);
                        }
                    }
                case AIUIConstant.EVENT_TTS:
                    break;
                default:
                    break;
            }
            //AIUI状态分发给各客户端监听
            if (null != mAIUIEventListener) {
                mAIUIEventListener.onEvent(event);
            }
        }
    };

    /**
     * 生效动态实体
     */
    public void effectDynamicEntity() {
        try {
            JSONObject params = new JSONObject();
            JSONObject audioParams = new JSONObject();
            audioParams.put("pers_param", "{\"uid\":\"\"}");
            params.put("audioparams", audioParams);
            AIUIMessage setMsg = new AIUIMessage(AIUIConstant.CMD_SET_PARAMS, 0, 0, params.toString(), null);
            mAIUIAgent.sendMessage(setMsg);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String getIatTxt(String iat) {
        IatBean iatBean = new Gson().fromJson(iat, IatBean.class);
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
     * 获取AIUI参数
     *
     * @return
     */
    private String getAIUIParams() {
        String params = "";
        AssetManager assetManager = getResources().getAssets();
        try {
            InputStream ins = assetManager.open("cfg/aiui_tv.cfg");
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

    /**
     * 设置用户参数
     *
     * @param pagesize
     * @param pageindex
     * @param screen_type
     * @param req_more_num 1表示 正常视频搜索页面请求  2表示查看更多的请求   3表示查看更多页面上拉加载请求
     */
    private void setUserDataParam(final String pagesize, final String pageindex, final String screen_type, final String req_more_num) {
        setUserDataParam(pagesize, pageindex, screen_type, req_more_num, "");
    }

    private void setUserDataParam(final String pagesize, final String pageindex, final String screen_type, final String req_more_num, String lastVideoNlp) {
        try {

            Map<String, String> map = new HashMap<String, String>() {{
                put("client_id", "897ddadc222ec9c20651da355daee9cc");
                put("user_id", "553782460");
                put("msisdn", "13764279837");
                put("pagesize", pagesize);
                put("pageindex", pageindex);
                put("screen_type", screen_type);
                put("req_more_num", req_more_num);
            }};

            JSONObject objectJson = new JSONObject();
            JSONObject paramJson = new JSONObject();
            JSONObject lastNlpIntent = new JSONObject();
            if (TextUtils.isEmpty(lastVideoNlp)) {
            } else {
                lastNlpIntent.put("intent", new JSONObject(lastVideoNlp));
            }

            //用户数据添加的初始化参数中
            Iterator<Map.Entry<String, String>> iterator = map.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> item = iterator.next();
                paramJson.put(item.getKey(), item.getValue());
            }
            paramJson.put("content", lastNlpIntent);
            objectJson.put("userparams", paramJson);
            String userParams = objectJson.toString();
            Logger.debug("lastUserParams is 【" + userParams + "】");
            sendMessage(new AIUIMessage(AIUIConstant.CMD_SET_PARAMS, 0, 0, userParams, null));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送AIUI消息
     *
     * @param message
     */
    private void sendMessage(final AIUIMessage message) {
        if (mAIUIAgent != null) {
            //确保AIUI处于唤醒状态
            if (mCurrentState != AIUIConstant.STATE_WORKING) {
                mAIUIAgent.sendMessage(new AIUIMessage(AIUIConstant.CMD_WAKEUP, 0, 0, "", null));
            }
            mAIUIAgent.sendMessage(message);
        }
    }
}
