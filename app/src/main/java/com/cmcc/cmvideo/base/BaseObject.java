package com.cmcc.cmvideo.base;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.cmcc.cmvideo.network.NetworkManager;
import com.cmcc.cmvideo.network.NetworkSession;
import com.cmcc.cmvideo.util.GlobalParam;
import com.cmcc.cmvideo.util.LogStatisticsUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Yyw on 2018/5/29.
 * Describe:
 */

public abstract class BaseObject implements NetworkManager.Callback {

    protected transient BaseObjectListener listener;

    protected transient JSONObject data;
    protected transient Map<String, List<String>> headers;

    protected transient NetworkManager networkManager;

    public BaseObject(NetworkManager manager) {
        networkManager = manager;
    }

    public BaseObject(){}

    public void post(String url, Map<String, Object> params, int what) {
        if (params == null) {
            params = new HashMap<>();
        }
        networkManager.post(url, params, what, this);
    }

    public void postSyn(String url, Map<String, Object> params, int what) {
        if (params == null) {
            params = new HashMap<>();
        }
        networkManager.postSyn(url, params, what, this);
    }

    public void postBodySyn(String url, String body, int what) {
        if (TextUtils.isEmpty(body)) {
            body = "";
        }
        networkManager.postBodySyn(url, body, what, this);
    }

    public void stream(String url, byte[] data, Map<String, String> params, int what) {
        if (params == null) {
            params = new HashMap<>();
        }
        networkManager.stream(url, data, params, what, this);
    }
    public void streamSyn(String url, byte[] data, Map<String, String> params, int what) {
        if (params == null) {
            params = new HashMap<>();
        }
        networkManager.streamSyn(url, data, params, what, this);
    }

    public void get(String url, Map<String, String> params, int what) {
        if (params == null) {
            params = new HashMap<>();
        }
        networkManager.get(url, params, what, this);
    }


    public void getSyn(String url, Map<String, String> params, int what) {
        if (params == null) {
            params = new HashMap<>();
        }
        networkManager.getSyn(url, params, what, this);
    }

    public void put(String url, Map<String, String> params, int what) {
        if (params == null) {
            params = new HashMap<>();
        }
        networkManager.put(url, params, what, this);
    }

    public void form(String url, Map<String, Object> params, int what) {
        if (params == null) {
            params = new HashMap<>();
        }
        networkManager.form(url, params, what, this);
    }

    public void del(String url, Map<String, String> params, int what) {
        if (params == null) {
            params = new HashMap<>();
        }
        networkManager.del(url, params, what, this);
    }

    public void setListener(BaseObjectListener listener) {
        this.listener = listener;
    }

    public void refresh() {
        loadData();
    }

    public abstract void loadData();

    public JSONObject parseResult(int what, JSONObject response) {
        return response;
    }

    public JSONArray getJSONArray(String key) {
        if (data != null) {
            return data.optJSONArray(key);
        } else {
            return null;
        }
    }

    public JSONObject getJSONObject(String key) {
        if (data != null) {
            return data.optJSONObject(key);
        } else {
            return null;
        }
    }

    public JSONObject getData() {
        return data;
    }

    public Map<String,List<String>> getHeaders()
    {
        return headers;
    }

    public String getString(String key) {
        if (data != null) {
            return data.optString(key);
        } else {
            return null;
        }
    }

    public void notifyDataObjectChanged(int what) {
        if (listener != null) {
            listener.dataObjectChanged(this, what);
        }
    }

    transient static Pattern r1 = Pattern.compile(",\\s*\\}");
    transient static Pattern r2 = Pattern.compile(",\\s*\\]");

    protected String regulateJSONString(String jsonStr) {

        Matcher m = r1.matcher(jsonStr);
        jsonStr = m.replaceAll("}");

        m = r2.matcher(jsonStr);
        jsonStr = m.replaceAll("]");

        return jsonStr;
    }


    private boolean checkDataFormat(JSONObject json) {
        return true;
    }

    @Override
    public void onSuccess(NetworkManager manager, NetworkSession session, int what, String response) {
        try {
            response = regulateJSONString(response);
            JSONObject json = new JSONObject(response);
            if (checkDataFormat(json)) {
                data = parseResult(what, json);
                if(session != null)
                {
                    headers = session.getResponseHeaders();
                }
                //                if(data.optInt("code") == 401){
                //                    LoginUtil loginUtil = new LoginUtil(ApplicationContext.application);
                //                    loginUtil.startSDKLogin(ApplicationContext.application);
                //                }
                notifyDataObjectChanged(what);
            } else {
                onFailure(manager, session, what, "Invalid data from server:" + json.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
            onFailure(manager, session, what, "Unable to convert to JSON:" + response);
        }
    }

    @Override
    public void onFailure(NetworkManager manager, NetworkSession session, int what, String error) {
        LogStatisticsUtils.addSendLogMessage(GlobalParam.LOG_LEVEL_TYPE_ERROR, "Server end error : Sesion=" + (session == null ? "null" : session.toString()) + ";Error=" + error, true);
        if (listener != null) {
            JSONObject msg = new JSONObject();
            try {
                msg.put("msg", error);
                listener.dataObjectFailed(this, what, msg);
            } catch (JSONException e) {
                e.printStackTrace();
                LogStatisticsUtils.addSendLogMessage(GlobalParam.LOG_LEVEL_TYPE_ERROR, "Client end error  : Sesion=" + (session == null ? "null" : session.toString()) + ";Error=" + error, true);
            }
        }
    }

    private static class NotifyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            BaseObject object = (BaseObject) msg.obj;
            object.notifyDataObjectChanged(msg.what);
        }
    }

    public void asyncNotifyDataObjectChanged(final int what) {
        final NotifyHandler handler = new NotifyHandler();

        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    Thread.sleep(10);
                    Message msg = handler.obtainMessage();
                    msg.obj = BaseObject.this;
                    msg.what = what;
                    handler.sendMessage(msg);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }

    private static class DataProcessHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            BaseObject object = (BaseObject) msg.obj;
            String data = msg.getData().getString("data");
            object.onSuccess(null, null, msg.what, data);
        }
    }

    public void asyncOnSuccess(final int what, final String response) {
        final DataProcessHandler handler = new DataProcessHandler();
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    Thread.sleep(10);
                    Message msg = handler.obtainMessage();
                    msg.obj = BaseObject.this;
                    Bundle bundle = new Bundle();
                    bundle.putString("data", response);
                    msg.setData(bundle);
                    msg.what = what;
                    handler.sendMessage(msg);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }
}
