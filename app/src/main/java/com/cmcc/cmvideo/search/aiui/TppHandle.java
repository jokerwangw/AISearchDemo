package com.cmcc.cmvideo.search.aiui;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.google.zxing.common.StringUtils;

import org.json.JSONArray;
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
        try{
            JSONObject jsonObject = new JSONObject(tppHandle);
            int rc = jsonObject.getInt("rc");
            if(rc==4)
                return;
            String service = jsonObject.getString("service");
            if(!"video".equals(service))
                return;
            JSONObject jsonDataObj = jsonObject.getJSONObject("data");

            if(jsonDataObj ==null||jsonDataObj.isNull("lxresult"))
                return;
            TppData data =  gson.fromJson(jsonDataObj.toString(),TppData.class);
        }catch (JSONException je){

        }
    }
}
