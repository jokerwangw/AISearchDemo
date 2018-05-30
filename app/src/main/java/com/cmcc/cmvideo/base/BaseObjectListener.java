package com.cmcc.cmvideo.base;

import org.json.JSONObject;

/**
 * Created by Yyw on 2018/5/29.
 * Describe:
 */

public interface BaseObjectListener {

    void dataObjectChanged(BaseObject dataObject, int what);

    void dataObjectFailed(BaseObject dataObject, int what, JSONObject errorMsg);
}
