package com.cmcc.cmvideo.search.aiui;

import com.iflytek.util.IflyRecorderListener;

public interface IRecorderListener extends IflyRecorderListener {
    void startSave(String path,String audioFormat);
    void stopSave(boolean canSave);
}