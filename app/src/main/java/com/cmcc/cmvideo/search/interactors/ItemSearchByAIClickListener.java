package com.cmcc.cmvideo.search.interactors;

/**
 * Created by Yyw on 2018/6/15.
 * Describe:
 */

public interface ItemSearchByAIClickListener {
    public void clickItemSearchByAICanAskAI(String recommendText);

    public void clickItemSearchByAIAppointment();

    public void clickItemSearchByAIEveryoneISWatching(String speechText, String titleText);

    public void clickChangeItem();

    public void clickItemSearchByAIIWantTOSee();

    public void clickItemSearchByAIGuessWhatYouLike();

    public void clickItemSearchByAIGuessWhatYouLikeListHorizontal();

    public void clickItemSearchByAIGuessWhatYouLikeListVertical();

    public void clickItemSearchByAITheLatestVideo();
}
