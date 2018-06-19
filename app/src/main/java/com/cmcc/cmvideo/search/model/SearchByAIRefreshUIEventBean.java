package com.cmcc.cmvideo.search.model;

import com.iflytek.aiui.AIUIEvent;

import java.util.List;

/**
 * Created by Yyw on 2018/5/22.
 * Describe:
 */

public class SearchByAIRefreshUIEventBean extends EventBusMessage {
    private AIUIEvent aiuiEvent;

    public SearchByAIRefreshUIEventBean(AIUIEvent aiuiEvent) {
        this.aiuiEvent = aiuiEvent;
    }

    public AIUIEvent getAiuiEvent() {
        return aiuiEvent;
    }

    public void setAiuiEvent(AIUIEvent aiuiEvent) {
        this.aiuiEvent = aiuiEvent;
    }
}
