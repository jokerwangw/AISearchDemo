package com.cmcc.cmvideo.search.aiui;

import com.iflytek.cloud.SynthesizerListener;

public interface IAIUIService {
    void tts(String ttsText, SynthesizerListener synthesizerListener);
    void startRecordAudio();
    void stopRecordAudio();
    void setResultDispatchListener(AIUIService.ResultDispatchListener resultDispatchListener);
}
