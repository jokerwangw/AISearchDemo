package com.cmcc.cmvideo.network;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Headers;

/**
 * Created by Yyw on 2018/5/29.
 * Describe:
 */

public abstract class NetworkManager {

    private Map<String, String> commonHeaders = new HashMap<>();

    public interface Callback {
        //        void onProgress(NetworkManager manager, int tag, int percent);
        void onSuccess(NetworkManager manager, NetworkSession session, int tag, String response);

        void onFailure(NetworkManager manager, NetworkSession session, int tag, String error);
    }

    public interface HeaderIntercepter {
        void processHead(Headers headers);
    }

    public abstract NetworkSession get(String url, Map<String, String> params, int tag, Callback callback);

    public abstract NetworkSession del(String url, Map<String, String> params, int tag, Callback callback);

    public abstract NetworkSession put(String url, Map<String, String> params, int tag, Callback callback);

    public abstract NetworkSession post(String url, Map<String, Object> params, int tag, Callback callback);

    public abstract NetworkSession form(String url, Map<String, Object> params, int tag, Callback callback);

    public abstract NetworkSession stream(String url, byte[] data, Map<String, String> params, int tag, Callback callback);

    public abstract NetworkSession streamSyn(String url, byte[] data, Map<String, String> params, int tag, Callback callback);

    public abstract NetworkSession postBody(String url, String body, int tag, Callback callback);

    public void addCommonHeader(String name, String value) {
        commonHeaders.put(name, value);
    }

    public void removeCommonHeader(String name) {
        if(commonHeaders != null){
            commonHeaders.remove(name);
        }
    }

    public void clearCommonHeader() {
        commonHeaders.clear();
    }

    public Map<String, String> getCommonHeaders() {
        return commonHeaders;
    }

    public abstract void cancelAll();

    public abstract NetworkSession getSyn(String url, Map<String, String> params, final int tag, Callback callback);

    public abstract NetworkSession postBodySyn(String url, String body, int tag, Callback callback);

    public abstract NetworkSession postSyn(String url, Map<String, Object> params, int tag, NetworkManager.Callback callback);
}
