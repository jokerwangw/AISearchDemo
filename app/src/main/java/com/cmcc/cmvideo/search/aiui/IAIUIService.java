package com.cmcc.cmvideo.search.aiui;

import com.iflytek.aiui.AIUIMessage;
import com.iflytek.cloud.SynthesizerListener;

import java.util.Map;

public interface IAIUIService {

    /**
     * 设置交互模式
     * @param isOnShot
     */
    void setInteractMode(boolean isOnShot);

    /**
     * 语音合成
     * @param ttsText 要合成的文本
     */
    void tts(String ttsText);

    /**
     * 停止语音合成
     */
    void cancelTts();

    /**
     * 开始录音
     */
    void startRecordAudio();

    /**
     * 停止录音
     */
    void stopRecordAudio();

    /**
     * 取消录音（实际音频是上传的，只是返回结果被忽略）
     */
    void cancelRecordAudio();

    /**
     * 添加AIUIService中事件监听
     * @param resultDispatchListener 监听对象
     */
    void addAIUIEventListener(AIUIService.AIUIEventListener resultDispatchListener);

    /**
     * 移除AIUIService中事件监听
     * @param resultDispatchListener 监听对象
     */
    void removeAIUIEventListener(AIUIService.AIUIEventListener resultDispatchListener);

    /**
     * 设置用户参数
     * @param map key-value 形式，key表示参数名称value表示参数具体值
     */
    void setUserParam(Map<String, String> map);

    /**
     * 同步状态及所见即可说数据
     * @param stateKey 状态key值 例（fg::video::default::default）（fg-当前服务端语义状态是前景，video-当前服务端技能，default-当前技能的场景，default-当前场景的状态）
     * @param hotInfo 所见即可说的热词，服务端优先匹配上传的热词
     */
    void syncSpeakableData(String stateKey,String hotInfo);

    /**
     * 同步状态及所见即可说数据
     * @param stateKey 状态key值 例（fg::video::default::default）（fg-当前服务端语义状态是前景，video-当前服务端技能，default-当前技能的场景，default-当前场景的状态）
     * @param hotInfo 所见即可说的热词，服务端优先匹配上传的热词
     */
    void syncSpeakableData(String stateKey,Map<String,String> hotInfo);

    /**
     * 清除所见即可说状态
     */
    void clearSpeakableData();

    /**
     * 获取查看更多页面数据
     * @param lookMoreText 上一条记录的 text 文本 例如（我要看西游记）
     * @param pageIndex 分页index
     * @param pageSize 分页大小
     */
    void getLookMorePage(String lookMoreText,int pageIndex,int pageSize);

    /**
     * 文本语义理解
     * @param text 传入文本，（AIUI可输入多种数据类型，音频字节或文本，音频字节输入已封装在SDK中。startRecordAudio，stopRecordAudio即是音频字节输入。剩下就是文本即当前接口）
     */
    void textUnderstander(String text);

    /**
     * 判断当前获得的数据是否是查看更多的数据（如果是查看更多数据，聊天UI中不显示对话UI）
     * @return true-false
     */
    boolean isLookMorePageData();

    /**
     * 设置语音助手是否在自己的UI页面
     * @param isAttached
     */
    void setAttached(boolean isAttached);

    /**
     * 显示语音助手是否在自己的UI页面
     * @return
     */
    void showAiUi(String data);

    /**
     * 获取最后一个语义的状态
     * @return
     */
    String getLastNlpState();

    /**
     * 获取页面跳转的导航对象
     * @return 导航对象
     */
    INavigation getNavigation();

    /**
     * activity onResume
     * @param flag
     */
    void onResume(boolean flag);
}
