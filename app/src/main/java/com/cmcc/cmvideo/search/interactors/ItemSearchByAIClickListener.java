package com.cmcc.cmvideo.search.interactors;

import com.cmcc.cmvideo.search.aiui.bean.TppData;

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
     *
     * @param isChangeBt
     * @param detailsListBean
     */
    public void clickItemSearchByAIGuessWhatYouLike(boolean isChangeBt, TppData.DetailsListBean detailsListBean);

    /**
     * 点击了类型为MESSAGE_TYPE_GUESS_WHAT_YOU_LIKE_LIST_HORIZONTAL的条目
     *
     * @param isChangeBt
     * @param detailsListBean
     * @param position
     */
    public void clickItemSearchByAIGuessWhatYouLikeListHorizontal(boolean isChangeBt, TppData.DetailsListBean detailsListBean, int position);

    /**
     * 点击了类型为MESSAGE_TYPE_GUESS_WHAT_YOU_LIKE_LIST_VERTICAL的条目
     *
     * @param isChangeBt
     * @param detailsListBean
     * @param position
     */
    public void clickItemSearchByAIGuessWhatYouLikeListVertical(boolean isChangeBt, TppData.DetailsListBean detailsListBean, int position);

    /**
     * 点击了类型为MESSAGE_TYPE_THE_LATEST_VIDEO的条目
     */
    public void clickItemSearchByAITheLatestVideo();
}
