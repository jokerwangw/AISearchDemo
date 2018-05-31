package com.cmcc.cmvideo.search.aiui;

import android.text.TextUtils;

import com.cmcc.cmvideo.search.aiui.bean.NlpData;
import com.cmcc.cmvideo.search.aiui.bean.TppData;
import com.cmcc.cmvideo.search.model.SearchByAIBean;
import com.cmcc.cmvideo.search.model.SearchByAIEventBean;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.cmcc.cmvideo.utils.Constants.MESSAGE_FROM_AI;
import static com.cmcc.cmvideo.utils.Constants.MESSAGE_TYPE_NORMAL;

public class TppHandle implements IHandle{
    private IAIUIService aiuiService;
    private  Gson gson;
    public TppHandle(IAIUIService service){
        aiuiService = service;
        gson =new Gson();
    }

    public void handle(String tppHandle){
        if(TextUtils.isEmpty(tppHandle))
            return;
        NlpData nlpData =  gson.fromJson(tppHandle,NlpData.class);
        if(nlpData.rc==4
                ||!"video".equals(nlpData.service)
                ||nlpData.data ==null
                ||nlpData.data.lxresult == null)
            return;
        if(nlpData.data.lxresult.data.detailslist!=null&&nlpData.data.lxresult.data.detailslist.size()>0){
            aiuiService.tts("为你找到"+nlpData.data.lxresult.data.detailslist.size()+"个结果",null);
            //TODO 展示图片列表
        }else if(nlpData.answer!=null&&!TextUtils.isEmpty(nlpData.answer.text)){
            aiuiService.tts(nlpData.answer.text,null);
            final List<SearchByAIBean> responseList = new ArrayList<SearchByAIBean>();
            responseList.add(new SearchByAIBean(nlpData.answer.text, MESSAGE_TYPE_NORMAL, MESSAGE_FROM_AI));
            EventBus.getDefault().post(new SearchByAIEventBean(responseList));
        }
    }
}
