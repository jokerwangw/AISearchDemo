package com.cmcc.cmvideo.network;

import java.util.List;
import java.util.Map;

/**
 * Created by Yyw on 2018/5/29.
 * Describe:
 */

public abstract class NetworkSession {
    abstract public String getRequestURL();
    abstract public String getMethod();
    abstract public Map<String,Object> getQueryParameters();
    abstract public byte[] getBody();
    abstract public Map<String,String> getRequestHeaders();
    abstract public byte[] getResponse();
    abstract public Map<String, List<String>> getResponseHeaders();
}
