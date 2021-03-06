package com.cmcc.cmvideo.search.interactors;

/**
 * Created by Yyw on 2018/6/15.
 * Describe:
 */

public interface ItemSearchByAIClickListener {
    /**
     * 点击了类型为MESSAGE_TYPE_CAN_ASK_AI的条目
     *
     * @param recommendText
     */
    public void clickItemSearchByAICanAskAI(String recommendText);

    /**
     * 点击了类型为MESSAGE_TYPE_APPOINTMENT的条目
     */
    public void clickItemSearchByAIAppointment();

    /**
     * 点击了类型为MESSAGE_TYPE_EVERYONE_IS_WATCHING的条目
     */
    public void clickItemSearchByAIEveryoneISWatching(String speechText, String titleText);

    /**
     * 点击了类型为MESSAGE_TYPE_I_WANT_TO_SEE的条目
     */
    public void clickItemSearchByAIIWantTOSee();

    /**
     * 点击了类型为MESSAGE_TYPE_GUESS_WHAT_YOU_LIKE的条目
     */
    public void clickItemSearchByAIGuessWhatYouLike();

    /**
     * 点击了类型为MESSAGE_TYPE_GUESS_WHAT_YOU_LIKE_LIST_HORIZONTAL的条目
     */
    public void clickItemSearchByAIGuessWhatYouLikeListHorizontal();

    /**
     * 点击了类型为MESSAGE_TYPE_GUESS_WHAT_YOU_LIKE_LIST_VERTICAL的条目
     */
    public void clickItemSearchByAIGuessWhatYouLikeListVertical();

    /**
     * 点击了类型为MESSAGE_TYPE_THE_LATEST_VIDEO的条目
     */
    public void clickItemSearchByAITheLatestVideo();
}
