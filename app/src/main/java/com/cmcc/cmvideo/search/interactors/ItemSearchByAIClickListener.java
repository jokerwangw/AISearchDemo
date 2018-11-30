package com.cmcc.cmvideo.search.interactors;

import com.cmcc.cmvideo.search.aiui.bean.TppData;
import com.cmcc.cmvideo.search.model.SearchByAIBean;

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
    public void clickItemSearchByAIEveryoneISWatching(boolean isClickMore, int position, String speechText, String titleText);

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
     * @param isClickLookMore
     * @param detailsListBean
     * @param position
     */
    public void clickItemSearchByAIGuessWhatYouLikeListHorizontal(boolean isChangeBt, boolean isClickLookMore, TppData.DetailsListBean detailsListBean, int position);

    /**
     * 点击了类型为MESSAGE_TYPE_GUESS_WHAT_YOU_LIKE_LIST_VERTICAL的条目
     *
     * @param isChangeBt
     * @param isClickLookMore
     * @param detailsListBean
     * @param position
     */
    public void clickItemSearchByAIGuessWhatYouLikeListVertical(boolean isChangeBt, boolean isClickLookMore, TppData.DetailsListBean detailsListBean, int position);

    /**
     * 点击了类型为MESSAGE_TYPE_THE_LATEST_VIDEO的条目
     */
    public void clickItemSearchByAITheLatestVideo();

    /**
     * 点击了类型为MESSAGE_TYPE_LIST_OF_SPORTS的条目
     *
     * @param position
     * @param isClickTheDayBefore
     * @param searchByAIBean
     */
    public void clickItemSearchByAIListOfSports(int position, boolean isClickTheDayBefore, SearchByAIBean searchByAIBean);

    /**
     * 点击了类型为MESSAGE_TYPE_VIDEO_OF_SPORTS的条目
     */
    public void clickItemSearchByAIVideoOfSports();
}
