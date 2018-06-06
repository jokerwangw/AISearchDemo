package com.cmcc.cmvideo.search.aiui;

import com.iflytek.cloud.SynthesizerListener;

import java.util.Map;

public interface IAIUIService {
    void tts(String ttsText, SynthesizerListener synthesizerListener);
    void startRecordAudio();
    void stopRecordAudio();
    void setAIUIEventListener(AIUIService.AIUIEventListener resultDispatchListener);
    void setUserParam(Map<String,String> map);
    void syncSpeakableData();
}
