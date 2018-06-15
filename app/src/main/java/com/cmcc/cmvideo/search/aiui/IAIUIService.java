package com.cmcc.cmvideo.search.aiui;

import com.iflytek.cloud.SynthesizerListener;

import java.util.Map;

public interface IAIUIService {
    void tts(String ttsText, SynthesizerListener synthesizerListener);
    void startRecordAudio();
    void stopRecordAudio();
    void addAIUIEventListener(AIUIService.AIUIEventListener resultDispatchListener);
    void removeAIUIEventListener(AIUIService.AIUIEventListener resultDispatchListener);
    void setUserParam(Map<String, String> map);
    void clearSpeakableData();
    void cancelRecordAudio();
    void syncSpeakableData(String stateKey,String hotInfo);
    void getLookMorePage(String lookMoreText,int pageIndex,int pageSize);
    void textUnderstander(String text);
    boolean isLookMorePageData();
}
