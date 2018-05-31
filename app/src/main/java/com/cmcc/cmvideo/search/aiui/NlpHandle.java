package com.cmcc.cmvideo.search.aiui;

import android.text.TextUtils;

import com.cmcc.cmvideo.search.aiui.bean.ChatBean;
import com.cmcc.cmvideo.search.aiui.bean.NlpData;
import com.cmcc.cmvideo.search.model.SearchByAIBean;
import com.cmcc.cmvideo.search.model.SearchByAIEventBean;
import com.cmcc.cmvideo.utils.AiuiConstants;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import static com.cmcc.cmvideo.utils.Constants.MESSAGE_FROM_AI;
import static com.cmcc.cmvideo.utils.Constants.MESSAGE_FROM_USER;
import static com.cmcc.cmvideo.utils.Constants.MESSAGE_TYPE_NORMAL;


public class NlpHandle implements IHandle {
    private IAIUIService aiuiService;
    private Gson mGson;
    private String intent;

    public NlpHandle(IAIUIService service) {
        aiuiService = service;
        if (null == mGson) {
            mGson = new Gson();
        }
    }

    public void handle(String nlpHandle) {
        if (TextUtils.isEmpty(nlpHandle)) return;
        NlpData nlpData = mGson.fromJson(nlpHandle, NlpData.class);
        Logger.debug("nlp " + nlpHandle + "应答码==="
                + nlpData.service + "-------------"
                + nlpData.answer.getText());
        if (4 == nlpData.rc) {
            //播报
            aiuiService.tts(AiuiConstants.ERROR_MESSAGE, null);
            return;
        }
        String service = nlpData.service;

        if (null != nlpData.semantic) {
            intent = nlpData.semantic.get(0).getIntent();
        }

        switch (service) {
            case AiuiConstants.QA_SERVICE:
                //闲聊
                intentQa(nlpHandle);
                break;
            case AiuiConstants.CHANNEL_SERVICE:
                //频道 如想看央视5台

                break;
            case AiuiConstants.QUERY_MIGU:
                //业务查询与办理
                intentQuery(intent);
                break;


            case AiuiConstants.CONTROL_MIGU:
                //指令控制  如：打开语音助手/投屏播放
                intentControl(intent);
                break;

        }


    }


    /**
     * 处理查询指令
     *
     * @param intent
     */
    private void intentQuery(String intent) {
        switch (intent) {
            case AiuiConstants.MEMBER_INTENT:
                //会员业务

                break;
            case AiuiConstants.INTERNET_INTENT:
                //流量业务

                break;
            case AiuiConstants.TICKET_INTENT:
                //购票业务

                break;
            case AiuiConstants.ACYIVITY_INTENT:
                //活动打折业务

                break;
            case AiuiConstants.GCUSTOMER_INTENT:
                //G客业务，如：上传视频

                break;
        }


    }


    /**
     * 处理控制指令
     *
     * @param intent
     */
    private void intentControl(String intent) {
        switch (intent) {
            case AiuiConstants.CONTROL_INTENT:
                // TODO: 2018/5/30 控制指令跳转
                Logger.debug("控制指令intent===" + intent);

                break;
            case AiuiConstants.SREEN_INTENT:
                // TODO: 2018/5/30 投屏跳转
                Logger.debug("投屏指令intent===" + intent);
                break;
        }

    }

    /**
     * 处理闲聊技能
     */
    private void intentQa(String nlpHandle) {
        NlpData nlpData = mGson.fromJson(nlpHandle, NlpData.class);
        aiuiService.tts(nlpData.getAnswer().getText(), null);
        List<SearchByAIBean> userRequestList = new ArrayList<SearchByAIBean>();
        userRequestList.add(new SearchByAIBean(nlpData.text, MESSAGE_TYPE_NORMAL, MESSAGE_FROM_USER));
        userRequestList.add(new SearchByAIBean(nlpData.getAnswer().getText(), MESSAGE_TYPE_NORMAL, MESSAGE_FROM_AI));
        EventBus.getDefault().post(new SearchByAIEventBean(userRequestList));
        Logger.debug("闲聊数据Q&A ==== " + nlpData.text + "-----" + nlpData.getAnswer().getText());
    }
}
