package com.cmcc.cmvideo.search.model;

import org.json.JSONObject;

/**
 * Created by Yyw on 2018/5/29.
 * Describe:
 */

public class EventBusMessage {
    public JSONObject body;
    public JSONObject data;
    public JSONObject data1;
    public JSONObject data2;


    public Object getJsonString(JSONObject jsData,String key){
        if( !"null".equals(jsData.opt(key).toString())){
            return  jsData.opt(key);
        }
        return "";
    }
}
