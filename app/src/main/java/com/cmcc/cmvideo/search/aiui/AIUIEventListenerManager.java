package com.cmcc.cmvideo.search.aiui;

import com.iflytek.aiui.AIUIEvent;

import java.util.ArrayList;
import java.util.List;

public class AIUIEventListenerManager implements AIUIService.AIUIEventListener {
    private List<AIUIService.AIUIEventListener> listeners = new ArrayList<>();

    @Override
    public void onResult(String iatResult, String nlpReslult, String tppResult) {
        for (AIUIService.AIUIEventListener listener : listeners) {
            listener.onResult(iatResult, nlpReslult, tppResult);
        }
    }

    @Override
    public void onEvent(AIUIEvent event) {
        for (AIUIService.AIUIEventListener listener : listeners) {
            listener.onEvent(event);
        }
    }

    public void addAIUIEventListener(AIUIService.AIUIEventListener aiuiEventListener) {
        if (aiuiEventListener != null && !listeners.contains(aiuiEventListener)) {
            listeners.add(aiuiEventListener);
        }
    }

    public void removeAIUIEventListener(AIUIService.AIUIEventListener aiuiEventListener) {
        if (aiuiEventListener != null && listeners.contains(aiuiEventListener)) {
            listeners.remove(aiuiEventListener);
        }
    }
}
