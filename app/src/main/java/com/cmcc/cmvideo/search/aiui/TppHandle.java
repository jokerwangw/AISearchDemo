package com.cmcc.cmvideo.search.aiui;

import android.text.TextUtils;

import com.cmcc.cmvideo.search.aiui.bean.NlpData;
import com.cmcc.cmvideo.search.aiui.bean.TppData;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

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
        }else if(nlpData.answer!=null&&!TextUtils.isEmpty(nlpData.answer.text)){
            aiuiService.tts(nlpData.answer.text,null);
        }
    }
}
