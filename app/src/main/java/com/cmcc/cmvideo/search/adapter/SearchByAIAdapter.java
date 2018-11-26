package com.cmcc.cmvideo.search.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cmcc.cmvideo.R;
import com.cmcc.cmvideo.base.BaseRecyclerAdapter;
import com.cmcc.cmvideo.foundation.fresco.MGSimpleDraweeView;
import com.cmcc.cmvideo.search.aiui.bean.TppData;
import com.cmcc.cmvideo.search.interactors.ItemSearchByAIClickListener;
import com.cmcc.cmvideo.search.model.SearchByAIBean;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import static com.cmcc.cmvideo.util.AiuiConstants.IMG_BASE_URL;
import static com.cmcc.cmvideo.util.AiuiConstants.MessageFrom.MESSAGE_FROM_AI;
import static com.cmcc.cmvideo.util.AiuiConstants.MessageType.MESSAGE_TYPE_APPOINTMENT;
import static com.cmcc.cmvideo.util.AiuiConstants.MessageType.MESSAGE_TYPE_CAN_ASK_AI;
import static com.cmcc.cmvideo.util.AiuiConstants.MessageType.MESSAGE_TYPE_EVERYONE_IS_WATCHING;
import static com.cmcc.cmvideo.util.AiuiConstants.MessageType.MESSAGE_TYPE_GUESS_WHAT_YOU_LIKE;
import static com.cmcc.cmvideo.util.AiuiConstants.MessageType.MESSAGE_TYPE_GUESS_WHAT_YOU_LIKE_LIST_HORIZONTAL;
import static com.cmcc.cmvideo.util.AiuiConstants.MessageType.MESSAGE_TYPE_GUESS_WHAT_YOU_LIKE_LIST_VERTICAL;
import static com.cmcc.cmvideo.util.AiuiConstants.MessageType.MESSAGE_TYPE_I_WANT_TO_SEE;
import static com.cmcc.cmvideo.util.AiuiConstants.MessageType.MESSAGE_TYPE_LIST_OF_SPORTS;
import static com.cmcc.cmvideo.util.AiuiConstants.MessageType.MESSAGE_TYPE_NORMAL;
import static com.cmcc.cmvideo.util.AiuiConstants.MessageType.MESSAGE_TYPE_THE_LATEST_VIDEO;
import static com.cmcc.cmvideo.util.AiuiConstants.MessageType.MESSAGE_TYPE_VIDEO_OF_SPORTS;

/**
 * Created by Yyw on 2018/5/21.
 * Describe:
 */

public class SearchByAIAdapter extends BaseRecyclerAdapter<SearchByAIBean> {
    private final ItemSearchByAIClickListener ItemSearchByAIClickListener;
    private final Context mContext;

    public SearchByAIAdapter(Context ctx, ItemSearchByAIClickListener itemSearchByAIClickListener) {
        super(ctx);
        this.mContext = ctx;
        this.ItemSearchByAIClickListener = itemSearchByAIClickListener;
    }

    @Override
    public int getItemViewType(int position) {
        SearchByAIBean searchByAIBean = getData().get(position);
        if (null != searchByAIBean) {
            if (MESSAGE_TYPE_NORMAL == searchByAIBean.getMessageType()) {
                return MESSAGE_TYPE_NORMAL;
            } else if (MESSAGE_TYPE_CAN_ASK_AI == searchByAIBean.getMessageType()) {
                return MESSAGE_TYPE_CAN_ASK_AI;
            } else if (MESSAGE_TYPE_APPOINTMENT == searchByAIBean.getMessageType()) {
                return MESSAGE_TYPE_APPOINTMENT;
            } else if (MESSAGE_TYPE_EVERYONE_IS_WATCHING == searchByAIBean.getMessageType()) {
                return MESSAGE_TYPE_EVERYONE_IS_WATCHING;
            } else if (MESSAGE_TYPE_I_WANT_TO_SEE == searchByAIBean.getMessageType()) {
                return MESSAGE_TYPE_I_WANT_TO_SEE;
            } else if (MESSAGE_TYPE_GUESS_WHAT_YOU_LIKE == searchByAIBean.getMessageType()) {
                return MESSAGE_TYPE_GUESS_WHAT_YOU_LIKE;
            } else if (MESSAGE_TYPE_GUESS_WHAT_YOU_LIKE_LIST_HORIZONTAL == searchByAIBean.getMessageType()) {
                return MESSAGE_TYPE_GUESS_WHAT_YOU_LIKE_LIST_HORIZONTAL;
            } else if (MESSAGE_TYPE_GUESS_WHAT_YOU_LIKE_LIST_VERTICAL == searchByAIBean.getMessageType()) {
                return MESSAGE_TYPE_GUESS_WHAT_YOU_LIKE_LIST_VERTICAL;
            } else if (MESSAGE_TYPE_THE_LATEST_VIDEO == searchByAIBean.getMessageType()) {
                return MESSAGE_TYPE_THE_LATEST_VIDEO;
            } else if (MESSAGE_TYPE_LIST_OF_SPORTS == searchByAIBean.getMessageType()) {
                return MESSAGE_TYPE_LIST_OF_SPORTS;
            } else if (MESSAGE_TYPE_VIDEO_OF_SPORTS == searchByAIBean.getMessageType()) {
                return MESSAGE_TYPE_VIDEO_OF_SPORTS;
            }
        }
        return -1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (-1 != viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            if (MESSAGE_TYPE_NORMAL == viewType) {
                View view = layoutInflater.inflate(R.layout.item_search_by_ai_normal, null);
                return new ItemSearchByAINormalViewHolder(view);
            } else if (MESSAGE_TYPE_CAN_ASK_AI == viewType) {
                View view = layoutInflater.inflate(R.layout.item_search_by_ai_can_ask_ai, null);
                return new ItemSearchByAICanAskAIViewHolder(view);
            } else if (MESSAGE_TYPE_APPOINTMENT == viewType) {
                View view = layoutInflater.inflate(R.layout.item_search_by_ai_appointment, null);
                return new ItemSearchByAIAppointmentViewHolder(view);
            } else if (MESSAGE_TYPE_EVERYONE_IS_WATCHING == viewType) {
                View view = layoutInflater.inflate(R.layout.item_search_by_ai_everyone_is_watching, null);
                return new ItemSearchByAIEveryoneISWatchingViewHolder(view);
            } else if (MESSAGE_TYPE_I_WANT_TO_SEE == viewType) {
                View view = layoutInflater.inflate(R.layout.item_search_by_ai_i_want_to_see, null);
                return new ItemSearchByAIIWantTOSeeViewHolder(view);
            } else if (MESSAGE_TYPE_GUESS_WHAT_YOU_LIKE == viewType) {
                View view = layoutInflater.inflate(R.layout.item_search_by_ai_guess_what_you_like, null);
                return new ItemSearchByAIGuessWhatYouLikeViewHolder(view);
            } else if (MESSAGE_TYPE_GUESS_WHAT_YOU_LIKE_LIST_HORIZONTAL == viewType) {
                View view = layoutInflater.inflate(R.layout.item_search_by_ai_guess_what_you_like_lise_horizontal, null);
                return new ItemSearchByAIGuessWhatYouLikeListHorizontalViewHolder(view);
            } else if (MESSAGE_TYPE_GUESS_WHAT_YOU_LIKE_LIST_VERTICAL == viewType) {
                View view = layoutInflater.inflate(R.layout.item_search_by_ai_guess_what_you_like_list_vertical, null);
                return new ItemSearchByAIGuessWhatYouLikeListVerticalViewHolder(view);
            } else if (MESSAGE_TYPE_THE_LATEST_VIDEO == viewType) {
                View view = layoutInflater.inflate(R.layout.item_search_by_ai_the_latest_video, null);
                return new ItemSearchByAITheLatestVideoViewHolder(view);
            } else if (MESSAGE_TYPE_LIST_OF_SPORTS == viewType) {
                View view = layoutInflater.inflate(R.layout.item_search_by_ai_list_of_sports, null);
                return new ItemSearchByAIListOfSportViewHolder(view);
            } else if (MESSAGE_TYPE_VIDEO_OF_SPORTS == viewType) {
                View view = layoutInflater.inflate(R.layout.item_search_by_ai_video_of_sports, null);
                return new ItemSearchByAIVideoOfSportsViewHolder(view);
            }
        }
        return null;
    }

    @Override
    public void onBindHoder(RecyclerView.ViewHolder holder, SearchByAIBean searchByAIBean, int position) {
        if (null != holder && null != searchByAIBean) {
            if (holder instanceof ItemSearchByAINormalViewHolder) {
                bindItemSearchByAINormalView(holder, searchByAIBean);
            } else if (holder instanceof ItemSearchByAICanAskAIViewHolder) {
                bindItemSearchByAICanAskAIView(holder, searchByAIBean);
            } else if (holder instanceof ItemSearchByAIAppointmentViewHolder) {
                bindItemSearchByAIAppointmentView(holder, searchByAIBean);
            } else if (holder instanceof ItemSearchByAIEveryoneISWatchingViewHolder) {
                bindItemSearchByAIEveryoneISWatchingView(holder, searchByAIBean);
            } else if (holder instanceof ItemSearchByAIIWantTOSeeViewHolder) {
                bindItemSearchByAIIWantTOSeeView(holder, searchByAIBean);
            } else if (holder instanceof ItemSearchByAIGuessWhatYouLikeViewHolder) {
                bindItemSearchByAIGuessWhatYouLikeView(holder, searchByAIBean);
            } else if (holder instanceof ItemSearchByAIGuessWhatYouLikeListHorizontalViewHolder) {
                bindItemSearchByAIGuessWhatYouLikeListHorizontalView(holder, searchByAIBean);
            } else if (holder instanceof ItemSearchByAIGuessWhatYouLikeListVerticalViewHolder) {
                bindItemSearchByAIGuessWhatYouLikeListVerticalView(holder, searchByAIBean);
            } else if (holder instanceof ItemSearchByAITheLatestVideoViewHolder) {
                bindItemSearchByAITheLatestVideoView(holder, searchByAIBean);
            } else if (holder instanceof ItemSearchByAIListOfSportViewHolder) {
                bindItemSearchByAIListOfSportView(holder, searchByAIBean, position);
            } else if (holder instanceof ItemSearchByAIVideoOfSportsViewHolder) {
                bindItemSearchByAIVideoOfSportsView(holder, searchByAIBean);
            }
        }
    }

    /**
     * 聊天类消息
     *
     * @param holder
     * @param searchByAIBean
     */
    private void bindItemSearchByAINormalView(RecyclerView.ViewHolder holder, SearchByAIBean searchByAIBean) {
        final ItemSearchByAINormalViewHolder itemSearchByAiNormalViewHolder = (ItemSearchByAINormalViewHolder) holder;
        if (TextUtils.equals(searchByAIBean.getMessageFrom(), MESSAGE_FROM_AI)) {
            itemSearchByAiNormalViewHolder.imHead.setVisibility(View.VISIBLE);
            itemSearchByAiNormalViewHolder.tvMessageFromAI.setVisibility(View.VISIBLE);
            itemSearchByAiNormalViewHolder.tvMessageFromUser.setVisibility(View.GONE);
            itemSearchByAiNormalViewHolder.tvMessageFromAI.setText(searchByAIBean.getMessage());
        } else {
            itemSearchByAiNormalViewHolder.imHead.setVisibility(View.INVISIBLE);
            itemSearchByAiNormalViewHolder.tvMessageFromAI.setVisibility(View.GONE);
            itemSearchByAiNormalViewHolder.tvMessageFromUser.setVisibility(View.VISIBLE);
            itemSearchByAiNormalViewHolder.tvMessageFromUser.setText(searchByAIBean.getMessage());
        }
    }

    /**
     * 可以这样问AI
     *
     * @param holder
     * @param searchByAIBean
     */
    private void bindItemSearchByAICanAskAIView(RecyclerView.ViewHolder holder, SearchByAIBean searchByAIBean) {
        final ItemSearchByAICanAskAIViewHolder itemSearchByAICanAskAIViewHolder = (ItemSearchByAICanAskAIViewHolder) holder;
        itemSearchByAICanAskAIViewHolder.rlItem1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != ItemSearchByAIClickListener) {
                    ItemSearchByAIClickListener.clickItemSearchByAICanAskAI(mContext.getResources().getString(R.string.ai_ask_recommend1));
                }
            }
        });
        itemSearchByAICanAskAIViewHolder.rlItem2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != ItemSearchByAIClickListener) {
                    ItemSearchByAIClickListener.clickItemSearchByAICanAskAI(mContext.getResources().getString(R.string.ai_ask_recommend2));
                }
            }
        });
        itemSearchByAICanAskAIViewHolder.rlItem3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != ItemSearchByAIClickListener) {
                    ItemSearchByAIClickListener.clickItemSearchByAICanAskAI(mContext.getResources().getString(R.string.ai_ask_recommend3));
                }
            }
        });
        itemSearchByAICanAskAIViewHolder.rlItem4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != ItemSearchByAIClickListener) {
                    ItemSearchByAIClickListener.clickItemSearchByAICanAskAI(mContext.getResources().getString(R.string.ai_ask_recommend4));
                }
            }
        });
        itemSearchByAICanAskAIViewHolder.rlItem5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != ItemSearchByAIClickListener) {
                    ItemSearchByAIClickListener.clickItemSearchByAICanAskAI(mContext.getResources().getString(R.string.ai_ask_recommend5));
                }
            }
        });
    }

    /**
     * AI预约电影成功的类型
     *
     * @param holder
     * @param searchByAIBean
     */
    private void bindItemSearchByAIAppointmentView(RecyclerView.ViewHolder holder, SearchByAIBean searchByAIBean) {
    }

    /**
     * 大家都在看
     *
     * @param holder
     * @param searchByAIBean
     */
    private void bindItemSearchByAIEveryoneISWatchingView(RecyclerView.ViewHolder holder, final SearchByAIBean searchByAIBean) {
        if (null == searchByAIBean) {
            return;
        }
        final List<TppData.DetailsListBean> videoList = searchByAIBean.getVideoList();
        String deailsJson = searchByAIBean.getDeailsJson();
        if (null == videoList || TextUtils.isEmpty(deailsJson)) {
            return;
        }

        boolean satisfy = false;
        final ItemSearchByAIEveryoneISWatchingViewHolder itemSearchByAIEveryoneISWatchingViewHolder = (ItemSearchByAIEveryoneISWatchingViewHolder) holder;

        try {
            JSONObject jsonObject = new JSONObject(deailsJson);
            satisfy = jsonObject.optJSONObject("data").optJSONObject("lxresult").optBoolean("satisfy");
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!satisfy || videoList.size() <= 2) {
            itemSearchByAIEveryoneISWatchingViewHolder.tvMovieList.setTextColor(Color.parseColor("#999999"));
            itemSearchByAIEveryoneISWatchingViewHolder.tvMovieList.setEnabled(false);
        } else {
            itemSearchByAIEveryoneISWatchingViewHolder.tvMovieList.setTextColor(Color.parseColor("#FF4F16"));
            itemSearchByAIEveryoneISWatchingViewHolder.tvMovieList.setEnabled(true);
        }

        if (!TextUtils.isEmpty(searchByAIBean.getMessage())) {
            itemSearchByAIEveryoneISWatchingViewHolder.title.setText(searchByAIBean.getMessage());
        }
        itemSearchByAIEveryoneISWatchingViewHolder.itemImg1.setVisibility(View.VISIBLE);
        itemSearchByAIEveryoneISWatchingViewHolder.itemImg2.setVisibility(View.VISIBLE);
        itemSearchByAIEveryoneISWatchingViewHolder.itemImg3.setVisibility(View.VISIBLE);
        itemSearchByAIEveryoneISWatchingViewHolder.itemName1.setVisibility(View.VISIBLE);
        itemSearchByAIEveryoneISWatchingViewHolder.itemName2.setVisibility(View.VISIBLE);
        itemSearchByAIEveryoneISWatchingViewHolder.itemName3.setVisibility(View.VISIBLE);
        itemSearchByAIEveryoneISWatchingViewHolder.itemWatchNum1.setVisibility(View.GONE);
        itemSearchByAIEveryoneISWatchingViewHolder.itemWatchNum2.setVisibility(View.GONE);
        itemSearchByAIEveryoneISWatchingViewHolder.itemWatchNum3.setVisibility(View.GONE);
        if (videoList.size() >= 3) {
            itemSearchByAIEveryoneISWatchingViewHolder.itemImg1.setImageURI(getImageUrl(videoList.get(0).image));
            itemSearchByAIEveryoneISWatchingViewHolder.itemImg2.setImageURI(getImageUrl(videoList.get(1).image));
            itemSearchByAIEveryoneISWatchingViewHolder.itemImg3.setImageURI(getImageUrl(videoList.get(2).image));
            itemSearchByAIEveryoneISWatchingViewHolder.itemName1.setText(videoList.get(0).name);
            itemSearchByAIEveryoneISWatchingViewHolder.itemName2.setText(videoList.get(1).name);
            itemSearchByAIEveryoneISWatchingViewHolder.itemName3.setText(videoList.get(2).name);
        } else if (videoList.size() == 2) {
            itemSearchByAIEveryoneISWatchingViewHolder.itemImg1.setImageURI(getImageUrl(videoList.get(0).image));
            itemSearchByAIEveryoneISWatchingViewHolder.itemImg2.setImageURI(getImageUrl(videoList.get(1).image));
            itemSearchByAIEveryoneISWatchingViewHolder.itemImg3.setVisibility(View.INVISIBLE);
            itemSearchByAIEveryoneISWatchingViewHolder.itemName1.setText(videoList.get(0).name);
            itemSearchByAIEveryoneISWatchingViewHolder.itemName2.setText(videoList.get(1).name);
            itemSearchByAIEveryoneISWatchingViewHolder.itemName3.setVisibility(View.INVISIBLE);
        } else if (videoList.size() == 1) {
            itemSearchByAIEveryoneISWatchingViewHolder.itemImg1.setImageURI(getImageUrl(videoList.get(0).image));
            itemSearchByAIEveryoneISWatchingViewHolder.itemImg2.setVisibility(View.INVISIBLE);
            itemSearchByAIEveryoneISWatchingViewHolder.itemImg3.setVisibility(View.INVISIBLE);
            itemSearchByAIEveryoneISWatchingViewHolder.itemName1.setText(videoList.get(0).name);
            itemSearchByAIEveryoneISWatchingViewHolder.itemName2.setVisibility(View.INVISIBLE);
            itemSearchByAIEveryoneISWatchingViewHolder.itemName3.setVisibility(View.INVISIBLE);
        }

        itemSearchByAIEveryoneISWatchingViewHolder.itemVideoOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != ItemSearchByAIClickListener) {
                    ItemSearchByAIClickListener.clickItemSearchByAIEveryoneISWatching(false, 0, searchByAIBean.getDeailsJson(), itemSearchByAIEveryoneISWatchingViewHolder.title.getText().toString().trim());
                }
            }
        });

        itemSearchByAIEveryoneISWatchingViewHolder.itemVideoTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != ItemSearchByAIClickListener) {
                    ItemSearchByAIClickListener.clickItemSearchByAIEveryoneISWatching(false, 1, searchByAIBean.getDeailsJson(), itemSearchByAIEveryoneISWatchingViewHolder.title.getText().toString().trim());
                }
            }
        });

        itemSearchByAIEveryoneISWatchingViewHolder.itemVideoThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != ItemSearchByAIClickListener) {
                    ItemSearchByAIClickListener.clickItemSearchByAIEveryoneISWatching(false, 2, searchByAIBean.getDeailsJson(), itemSearchByAIEveryoneISWatchingViewHolder.title.getText().toString().trim());
                }
            }
        });

        itemSearchByAIEveryoneISWatchingViewHolder.tvMovieList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != ItemSearchByAIClickListener) {
                    ItemSearchByAIClickListener.clickItemSearchByAIEveryoneISWatching(true, -1, searchByAIBean.getDeailsJson(), itemSearchByAIEveryoneISWatchingViewHolder.title.getText().toString().trim());
                }
            }
        });
    }

    /**
     * 我想看XXXX
     *
     * @param holder
     * @param searchByAIBean
     */
    private void bindItemSearchByAIIWantTOSeeView(RecyclerView.ViewHolder holder, SearchByAIBean searchByAIBean) {
        final List<TppData.DetailsListBean> videoList = searchByAIBean.getVideoList();
        if (null != videoList) {
        }
    }

    /**
     * 猜你喜欢
     *
     * @param holder
     * @param searchByAIBean
     */
    private void bindItemSearchByAIGuessWhatYouLikeView(RecyclerView.ViewHolder holder, SearchByAIBean searchByAIBean) {
        final List<TppData.DetailsListBean> videoList = searchByAIBean.getVideoList();
        if (null != videoList && videoList.size() != 0) {
            final TppData.DetailsListBean detailsListBean = videoList.get(0);
            if (null == detailsListBean) {
                return;
            }
            final ItemSearchByAIGuessWhatYouLikeViewHolder itemSearchByAIGuessWhatYouLikeViewHolder = (ItemSearchByAIGuessWhatYouLikeViewHolder) holder;

            if (!TextUtils.isEmpty(detailsListBean.image)) {
                String image = detailsListBean.image;
                try {
                    JSONObject jsonObject = new JSONObject(image);
                    String imgUrl = jsonObject.optString("highResolutionV");
                    if (!TextUtils.isEmpty(imgUrl)) {
                        if (imgUrl.startsWith("http")) {

                            itemSearchByAIGuessWhatYouLikeViewHolder.itemDetailImg.setImageURI(imgUrl);
                        } else {
                            imgUrl = IMG_BASE_URL + imgUrl;
                            itemSearchByAIGuessWhatYouLikeViewHolder.itemDetailImg.setImageURI(imgUrl);
                        }

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (TextUtils.isEmpty(detailsListBean.name)) {
                itemSearchByAIGuessWhatYouLikeViewHolder.itemVideoName.setVisibility(View.GONE);
            } else {
                itemSearchByAIGuessWhatYouLikeViewHolder.itemVideoName.setVisibility(View.VISIBLE);
                itemSearchByAIGuessWhatYouLikeViewHolder.itemVideoName.setText(detailsListBean.name);
            }

            if (TextUtils.isEmpty(detailsListBean.area)) {
                itemSearchByAIGuessWhatYouLikeViewHolder.itemArea.setVisibility(View.GONE);
            } else {
                itemSearchByAIGuessWhatYouLikeViewHolder.itemArea.setVisibility(View.VISIBLE);
                itemSearchByAIGuessWhatYouLikeViewHolder.itemArea.setText("制片国家/地区:" + detailsListBean.area);
            }

            if (TextUtils.isEmpty(detailsListBean.releasetime)) {
                itemSearchByAIGuessWhatYouLikeViewHolder.itemReleasetime.setVisibility(View.GONE);
            } else {
                itemSearchByAIGuessWhatYouLikeViewHolder.itemReleasetime.setVisibility(View.VISIBLE);
                itemSearchByAIGuessWhatYouLikeViewHolder.itemReleasetime.setText("上映日期:" + detailsListBean.releasetime);
            }

            if (TextUtils.isEmpty(detailsListBean.detail)) {
                itemSearchByAIGuessWhatYouLikeViewHolder.itemPlotHeader.setVisibility(View.GONE);
                itemSearchByAIGuessWhatYouLikeViewHolder.itemVideoDeteil.setVisibility(View.GONE);
            } else {
                itemSearchByAIGuessWhatYouLikeViewHolder.itemPlotHeader.setVisibility(View.VISIBLE);
                itemSearchByAIGuessWhatYouLikeViewHolder.itemVideoDeteil.setVisibility(View.VISIBLE);
                itemSearchByAIGuessWhatYouLikeViewHolder.itemVideoDeteil.setText(detailsListBean.detail);
            }

            if (null != detailsListBean.director && detailsListBean.director.size() != 0) {
                StringBuilder director = new StringBuilder("导演:");
                for (String text : detailsListBean.director) {
                    director.append(text).append("/");
                }
                if (director.toString().contains("/")) {
                    director = new StringBuilder(director.substring(0, director.lastIndexOf("/")));
                }
                itemSearchByAIGuessWhatYouLikeViewHolder.itemDirector.setVisibility(View.VISIBLE);
                itemSearchByAIGuessWhatYouLikeViewHolder.itemDirector.setText(director.toString());
            } else {
                itemSearchByAIGuessWhatYouLikeViewHolder.itemDirector.setVisibility(View.GONE);
            }

            if (null != detailsListBean.actor && detailsListBean.actor.size() != 0) {
                StringBuilder actor = new StringBuilder("主演:");
                for (String text : detailsListBean.actor) {
                    actor.append(text).append("/");
                }
                if (actor.toString().contains("/")) {
                    actor = new StringBuilder(actor.substring(0, actor.lastIndexOf("/")));
                }
                itemSearchByAIGuessWhatYouLikeViewHolder.itemActor.setVisibility(View.VISIBLE);
                itemSearchByAIGuessWhatYouLikeViewHolder.itemActor.setText(actor.toString());
            } else {
                itemSearchByAIGuessWhatYouLikeViewHolder.itemActor.setVisibility(View.GONE);
            }

            if (null != detailsListBean.tag && detailsListBean.tag.size() != 0) {
                StringBuilder tag = new StringBuilder("类型:");
                for (String text : detailsListBean.tag) {
                    tag.append(text).append("/");
                }
                if (tag.toString().contains("/")) {
                    tag = new StringBuilder(tag.substring(0, tag.lastIndexOf("/")));
                }
                itemSearchByAIGuessWhatYouLikeViewHolder.itemCategory.setVisibility(View.VISIBLE);
                itemSearchByAIGuessWhatYouLikeViewHolder.itemCategory.setText(tag.toString());
            } else {
                itemSearchByAIGuessWhatYouLikeViewHolder.itemCategory.setVisibility(View.GONE);
            }

            if (null != detailsListBean.language && detailsListBean.language.size() != 0) {
                StringBuilder language = new StringBuilder("语言:");
                for (String text : detailsListBean.language) {
                    language.append(text).append("/");
                }
                if (language.toString().contains("/")) {
                    language = new StringBuilder(language.substring(0, language.lastIndexOf("/")));
                }
                itemSearchByAIGuessWhatYouLikeViewHolder.itemLanguage.setVisibility(View.VISIBLE);
                itemSearchByAIGuessWhatYouLikeViewHolder.itemLanguage.setText(language.toString());
            } else {
                itemSearchByAIGuessWhatYouLikeViewHolder.itemLanguage.setVisibility(View.GONE);
            }

            itemSearchByAIGuessWhatYouLikeViewHolder.itemContain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != ItemSearchByAIClickListener) {
                        ItemSearchByAIClickListener.clickItemSearchByAIGuessWhatYouLike(false, detailsListBean);
                    }
                }
            });

            itemSearchByAIGuessWhatYouLikeViewHolder.itemChange.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != ItemSearchByAIClickListener) {
                        ItemSearchByAIClickListener.clickItemSearchByAIGuessWhatYouLike(true, detailsListBean);
                    }
                }
            });
        }
    }

    /**
     * 猜你喜欢_列表横向展示
     *
     * @param holder
     * @param searchByAIBean
     */
    private void bindItemSearchByAIGuessWhatYouLikeListHorizontalView(RecyclerView.ViewHolder holder, SearchByAIBean searchByAIBean) {
        final List<TppData.DetailsListBean> videoList = searchByAIBean.getVideoList();
        if (null != videoList) {
            final TppData.DetailsListBean detailsListBean = videoList.get(0);
            if (null == detailsListBean) {
                return;
            }
            final ItemSearchByAIGuessWhatYouLikeListHorizontalViewHolder itemSearchByAIGuessWhatYouLikeListHorizontalViewHolder = (ItemSearchByAIGuessWhatYouLikeListHorizontalViewHolder) holder;

            if (!TextUtils.isEmpty(detailsListBean.image)) {
                String image = detailsListBean.image;
                try {
                    JSONObject jsonObject = new JSONObject(image);
                    String imgUrl = jsonObject.optString("highResolutionV");
                    if (!TextUtils.isEmpty(imgUrl)) {
                        if (imgUrl.startsWith("http")) {
                            itemSearchByAIGuessWhatYouLikeListHorizontalViewHolder.itemDetailImg.setImageURI(imgUrl);
                        } else {
                            itemSearchByAIGuessWhatYouLikeListHorizontalViewHolder.itemDetailImg.setImageURI(IMG_BASE_URL + imgUrl);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (TextUtils.isEmpty(detailsListBean.name)) {
                itemSearchByAIGuessWhatYouLikeListHorizontalViewHolder.itemVideoName.setVisibility(View.GONE);
            } else {
                itemSearchByAIGuessWhatYouLikeListHorizontalViewHolder.itemVideoName.setVisibility(View.VISIBLE);
                itemSearchByAIGuessWhatYouLikeListHorizontalViewHolder.itemVideoName.setText(detailsListBean.name);
            }

            if (TextUtils.isEmpty(detailsListBean.area)) {
                itemSearchByAIGuessWhatYouLikeListHorizontalViewHolder.itemArea.setVisibility(View.GONE);
            } else {
                itemSearchByAIGuessWhatYouLikeListHorizontalViewHolder.itemArea.setVisibility(View.VISIBLE);
                itemSearchByAIGuessWhatYouLikeListHorizontalViewHolder.itemArea.setText("制片国家/地区:" + detailsListBean.area);
            }

            if (TextUtils.isEmpty(detailsListBean.releasetime)) {
                itemSearchByAIGuessWhatYouLikeListHorizontalViewHolder.itemReleasetime.setVisibility(View.GONE);
            } else {
                itemSearchByAIGuessWhatYouLikeListHorizontalViewHolder.itemReleasetime.setVisibility(View.VISIBLE);
                itemSearchByAIGuessWhatYouLikeListHorizontalViewHolder.itemReleasetime.setText("上映日期:" + detailsListBean.releasetime);
            }

            if (TextUtils.isEmpty(detailsListBean.detail)) {
                itemSearchByAIGuessWhatYouLikeListHorizontalViewHolder.itemPlotHeader.setVisibility(View.GONE);
                itemSearchByAIGuessWhatYouLikeListHorizontalViewHolder.itemVideoDeteil.setVisibility(View.GONE);
            } else {
                itemSearchByAIGuessWhatYouLikeListHorizontalViewHolder.itemPlotHeader.setVisibility(View.VISIBLE);
                itemSearchByAIGuessWhatYouLikeListHorizontalViewHolder.itemVideoDeteil.setVisibility(View.VISIBLE);
                itemSearchByAIGuessWhatYouLikeListHorizontalViewHolder.itemVideoDeteil.setText(detailsListBean.detail);
            }

            if (null != detailsListBean.director && detailsListBean.director.size() != 0) {
                StringBuilder director = new StringBuilder("导演:");
                for (String text : detailsListBean.director) {
                    director.append(text).append("/");
                }
                if (director.toString().contains("/")) {
                    director = new StringBuilder(director.substring(0, director.lastIndexOf("/")));
                }
                itemSearchByAIGuessWhatYouLikeListHorizontalViewHolder.itemDirector.setVisibility(View.VISIBLE);
                itemSearchByAIGuessWhatYouLikeListHorizontalViewHolder.itemDirector.setText(director.toString());
            } else {
                itemSearchByAIGuessWhatYouLikeListHorizontalViewHolder.itemDirector.setVisibility(View.GONE);
            }

            if (null != detailsListBean.actor && detailsListBean.actor.size() != 0) {
                StringBuilder actor = new StringBuilder("主演:");
                for (String text : detailsListBean.actor) {
                    actor.append(text).append("/");
                }
                if (actor.toString().contains("/")) {
                    actor = new StringBuilder(actor.substring(0, actor.lastIndexOf("/")));
                }
                itemSearchByAIGuessWhatYouLikeListHorizontalViewHolder.itemActor.setVisibility(View.VISIBLE);
                itemSearchByAIGuessWhatYouLikeListHorizontalViewHolder.itemActor.setText(actor.toString());
            } else {
                itemSearchByAIGuessWhatYouLikeListHorizontalViewHolder.itemActor.setVisibility(View.GONE);
            }

            if (null != detailsListBean.tag && detailsListBean.tag.size() != 0) {
                StringBuilder tag = new StringBuilder("类型:");
                for (String text : detailsListBean.tag) {
                    tag.append(text).append("/");
                }
                if (tag.toString().contains("/")) {
                    tag = new StringBuilder(tag.substring(0, tag.lastIndexOf("/")));
                }
                itemSearchByAIGuessWhatYouLikeListHorizontalViewHolder.itemCategory.setVisibility(View.VISIBLE);
                itemSearchByAIGuessWhatYouLikeListHorizontalViewHolder.itemCategory.setText(tag.toString());
            } else {
                itemSearchByAIGuessWhatYouLikeListHorizontalViewHolder.itemCategory.setVisibility(View.GONE);
            }

            if (null != detailsListBean.language && detailsListBean.language.size() != 0) {
                StringBuilder language = new StringBuilder("语言:");
                for (String text : detailsListBean.language) {
                    language.append(text).append("/");
                }
                if (language.toString().contains("/")) {
                    language = new StringBuilder(language.substring(0, language.lastIndexOf("/")));
                }
                itemSearchByAIGuessWhatYouLikeListHorizontalViewHolder.itemLanguage.setVisibility(View.VISIBLE);
                itemSearchByAIGuessWhatYouLikeListHorizontalViewHolder.itemLanguage.setText(language.toString());
            } else {
                itemSearchByAIGuessWhatYouLikeListHorizontalViewHolder.itemLanguage.setVisibility(View.GONE);
            }

            if (null != detailsListBean.subserials && detailsListBean.subserials.size() != 0) {
                itemSearchByAIGuessWhatYouLikeListHorizontalViewHolder.llVideoList.setVisibility(View.VISIBLE);
                itemSearchByAIGuessWhatYouLikeListHorizontalViewHolder.llVideoList.removeAllViews();
                if (detailsListBean.subserials.size() > 5) {
                    for (int i = detailsListBean.subserials.size() - 1; i > detailsListBean.subserials.size() - 4; i--) {
                        final int pos = i;
                        View root = LayoutInflater.from(holder.itemView.getContext()).inflate(R.layout.item_search_by_ai_horizontal_item, null, false);
                        if (detailsListBean.subserials.size() - 3 == i) {
                            ((TextView) root.findViewById(R.id.tv_release_num)).setText("...");
                        } else {
                            int num = i + 1;
                            ((TextView) root.findViewById(R.id.tv_release_num)).setText("" + num);
                        }
                        itemSearchByAIGuessWhatYouLikeListHorizontalViewHolder.llVideoList.addView(root);
                        root.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (null != ItemSearchByAIClickListener) {
                                    ItemSearchByAIClickListener.clickItemSearchByAIGuessWhatYouLikeListHorizontal(false, pos == detailsListBean.subserials.size() - 3, detailsListBean, pos);
                                }
                            }
                        });
                    }

                    for (int i = 1; i >= 0; i--) {
                        int num = i + 1;
                        final int pos = i;
                        View root = LayoutInflater.from(holder.itemView.getContext()).inflate(R.layout.item_search_by_ai_horizontal_item, null, false);
                        ((TextView) root.findViewById(R.id.tv_release_num)).setText("" + num);
                        itemSearchByAIGuessWhatYouLikeListHorizontalViewHolder.llVideoList.addView(root);
                        root.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (null != ItemSearchByAIClickListener) {
                                    ItemSearchByAIClickListener.clickItemSearchByAIGuessWhatYouLikeListHorizontal(false, false, detailsListBean, pos);
                                }
                            }
                        });
                    }
                } else {
                    for (int i = 4; i >= 0; i--) {
                        final int pos = i;
                        View root = LayoutInflater.from(holder.itemView.getContext()).inflate(R.layout.item_search_by_ai_horizontal_item, null, false);
                        int num = i + 1;
                        ((TextView) root.findViewById(R.id.tv_release_num)).setText("" + num);
                        itemSearchByAIGuessWhatYouLikeListHorizontalViewHolder.llVideoList.addView(root);
                        root.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (null != ItemSearchByAIClickListener) {
                                    ItemSearchByAIClickListener.clickItemSearchByAIGuessWhatYouLikeListHorizontal(false, false, detailsListBean, pos);
                                }
                            }
                        });
                    }
                }

            } else {
                itemSearchByAIGuessWhatYouLikeListHorizontalViewHolder.llVideoList.setVisibility(View.GONE);
            }

            itemSearchByAIGuessWhatYouLikeListHorizontalViewHolder.itemChange.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != ItemSearchByAIClickListener) {
                        ItemSearchByAIClickListener.clickItemSearchByAIGuessWhatYouLikeListHorizontal(true, false, detailsListBean, -1);
                    }
                }
            });
        }
    }

    /**
     * 猜你喜欢_列表垂直展示
     *
     * @param holder
     * @param searchByAIBean
     */
    private void bindItemSearchByAIGuessWhatYouLikeListVerticalView(RecyclerView.ViewHolder holder, SearchByAIBean searchByAIBean) {
        final List<TppData.DetailsListBean> videoList = searchByAIBean.getVideoList();
        if (null != videoList) {
            final TppData.DetailsListBean detailsListBean = videoList.get(0);
            if (null == detailsListBean) {
                return;
            }
            final ItemSearchByAIGuessWhatYouLikeListVerticalViewHolder itemSearchByAIGuessWhatYouLikeListVerticalViewHolder = (ItemSearchByAIGuessWhatYouLikeListVerticalViewHolder) holder;

            if (!TextUtils.isEmpty(detailsListBean.image)) {
                String image = detailsListBean.image;
                try {
                    JSONObject jsonObject = new JSONObject(image);
                    String imgUrl = jsonObject.optString("highResolutionV");
                    if (!TextUtils.isEmpty(imgUrl)) {
                        if (imgUrl.startsWith("http")) {
                            itemSearchByAIGuessWhatYouLikeListVerticalViewHolder.itemDetailImg.setImageURI(imgUrl);
                        } else {
                            itemSearchByAIGuessWhatYouLikeListVerticalViewHolder.itemDetailImg.setImageURI(IMG_BASE_URL + imgUrl);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (TextUtils.isEmpty(detailsListBean.name)) {
                itemSearchByAIGuessWhatYouLikeListVerticalViewHolder.itemVideoName.setVisibility(View.GONE);
            } else {
                itemSearchByAIGuessWhatYouLikeListVerticalViewHolder.itemVideoName.setVisibility(View.VISIBLE);
                itemSearchByAIGuessWhatYouLikeListVerticalViewHolder.itemVideoName.setText(detailsListBean.name);
            }

            if (TextUtils.isEmpty(detailsListBean.area)) {
                itemSearchByAIGuessWhatYouLikeListVerticalViewHolder.itemArea.setVisibility(View.GONE);
            } else {
                itemSearchByAIGuessWhatYouLikeListVerticalViewHolder.itemArea.setVisibility(View.VISIBLE);
                itemSearchByAIGuessWhatYouLikeListVerticalViewHolder.itemArea.setText("制片国家/地区:" + detailsListBean.area);
            }

            if (TextUtils.isEmpty(detailsListBean.releasetime)) {
                itemSearchByAIGuessWhatYouLikeListVerticalViewHolder.itemReleasetime.setVisibility(View.GONE);
            } else {
                itemSearchByAIGuessWhatYouLikeListVerticalViewHolder.itemReleasetime.setVisibility(View.VISIBLE);
                itemSearchByAIGuessWhatYouLikeListVerticalViewHolder.itemReleasetime.setText("上映日期:" + detailsListBean.releasetime);
            }

            if (null != detailsListBean.tag && detailsListBean.tag.size() != 0) {
                StringBuilder tag = new StringBuilder("类型:");
                for (String text : detailsListBean.tag) {
                    tag.append(text).append("/");
                }
                if (tag.toString().contains("/")) {
                    tag = new StringBuilder(tag.substring(0, tag.lastIndexOf("/")));
                }
                itemSearchByAIGuessWhatYouLikeListVerticalViewHolder.itemCategory.setText(tag.toString());
                itemSearchByAIGuessWhatYouLikeListVerticalViewHolder.itemCategory.setVisibility(View.VISIBLE);
            } else {
                itemSearchByAIGuessWhatYouLikeListVerticalViewHolder.itemCategory.setVisibility(View.GONE);
            }

            if (null != detailsListBean.language && detailsListBean.language.size() != 0) {
                StringBuilder language = new StringBuilder("语言:");
                for (String text : detailsListBean.language) {
                    language.append(text).append("/");
                }
                if (language.toString().contains("/")) {
                    language = new StringBuilder(language.substring(0, language.lastIndexOf("/")));
                }
                itemSearchByAIGuessWhatYouLikeListVerticalViewHolder.itemLanguage.setVisibility(View.VISIBLE);
                itemSearchByAIGuessWhatYouLikeListVerticalViewHolder.itemLanguage.setText(language.toString());
            } else {
                itemSearchByAIGuessWhatYouLikeListVerticalViewHolder.itemLanguage.setVisibility(View.GONE);
            }

            if (null != detailsListBean.subserials && detailsListBean.subserials.size() != 0) {
                itemSearchByAIGuessWhatYouLikeListVerticalViewHolder.llVideoList.setVisibility(View.VISIBLE);
                itemSearchByAIGuessWhatYouLikeListVerticalViewHolder.llVideoList.removeAllViews();
                int count = detailsListBean.subserials.size();
                if (count > 3) {
                    count = 3;
                }
                for (int i = 0; i < count; i++) {
                    final int pos = i;
                    View root = LayoutInflater.from(holder.itemView.getContext()).inflate(R.layout.item_search_by_ai_vertical_item, null, false);
                    ((TextView) root.findViewById(R.id.tv_release_num)).setVisibility(View.GONE);
                    ((TextView) root.findViewById(R.id.tv_release_name)).setVisibility(View.VISIBLE);
                    ((TextView) root.findViewById(R.id.tv_look_more)).setVisibility(View.GONE);
                    ((TextView) root.findViewById(R.id.tv_release_num)).setText(detailsListBean.subserials.get(i).id);
                    ((TextView) root.findViewById(R.id.tv_release_name)).setText(detailsListBean.subserials.get(i).name);
                    itemSearchByAIGuessWhatYouLikeListVerticalViewHolder.llVideoList.addView(root);
                    root.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (null != ItemSearchByAIClickListener) {
                                ItemSearchByAIClickListener.clickItemSearchByAIGuessWhatYouLikeListVertical(false, false, detailsListBean, pos);
                            }
                        }
                    });
                }

                View rootMore = LayoutInflater.from(holder.itemView.getContext()).inflate(R.layout.item_search_by_ai_vertical_item, null, false);
                ((TextView) rootMore.findViewById(R.id.tv_release_num)).setVisibility(View.GONE);
                ((TextView) rootMore.findViewById(R.id.tv_release_name)).setVisibility(View.GONE);
                ((TextView) rootMore.findViewById(R.id.tv_look_more)).setVisibility(View.VISIBLE);
                ((TextView) rootMore.findViewById(R.id.tv_release_name)).setText(mContext.getResources().getString(R.string.look_more_resource));
                itemSearchByAIGuessWhatYouLikeListVerticalViewHolder.llVideoList.addView(rootMore);
                rootMore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (null != ItemSearchByAIClickListener) {
                            ItemSearchByAIClickListener.clickItemSearchByAIGuessWhatYouLikeListVertical(false, true, detailsListBean, 4);
                        }
                    }
                });
            } else {
                itemSearchByAIGuessWhatYouLikeListVerticalViewHolder.llVideoList.setVisibility(View.GONE);
            }

            itemSearchByAIGuessWhatYouLikeListVerticalViewHolder.itemChange.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != ItemSearchByAIClickListener) {
                        ItemSearchByAIClickListener.clickItemSearchByAIGuessWhatYouLikeListVertical(true, false, detailsListBean, -1);
                    }
                }
            });
        }
    }

    /**
     * 最新影讯
     *
     * @param holder
     * @param searchByAIBean
     */
    private void bindItemSearchByAITheLatestVideoView(RecyclerView.ViewHolder holder, SearchByAIBean searchByAIBean) {
        final ItemSearchByAITheLatestVideoViewHolder itemSearchByAITheLatestVideoViewHolder = (ItemSearchByAITheLatestVideoViewHolder) holder;
        final List<TppData.DetailsListBean> videoList = searchByAIBean.getVideoList();
        itemSearchByAITheLatestVideoViewHolder.itemWatchNum1.setVisibility(View.GONE);
        itemSearchByAITheLatestVideoViewHolder.itemWatchNum2.setVisibility(View.GONE);
        itemSearchByAITheLatestVideoViewHolder.itemWatchNum3.setVisibility(View.GONE);
        if (null != videoList && videoList.size() >= 3) {
            itemSearchByAITheLatestVideoViewHolder.itemImg1.setImageURI(getImageUrl(videoList.get(0).image));
            itemSearchByAITheLatestVideoViewHolder.itemImg2.setImageURI(getImageUrl(videoList.get(1).image));
            itemSearchByAITheLatestVideoViewHolder.itemImg3.setImageURI(getImageUrl(videoList.get(2).image));
            itemSearchByAITheLatestVideoViewHolder.itemName1.setText(videoList.get(0).name);
            itemSearchByAITheLatestVideoViewHolder.itemName2.setText(videoList.get(1).name);
            itemSearchByAITheLatestVideoViewHolder.itemName3.setText(videoList.get(2).name);
        } else if (null != videoList && videoList.size() == 2) {
            itemSearchByAITheLatestVideoViewHolder.itemImg1.setImageURI(getImageUrl(videoList.get(0).image));
            itemSearchByAITheLatestVideoViewHolder.itemImg2.setImageURI(getImageUrl(videoList.get(1).image));
            itemSearchByAITheLatestVideoViewHolder.itemImg3.setVisibility(View.INVISIBLE);
            itemSearchByAITheLatestVideoViewHolder.itemName1.setText(videoList.get(0).name);
            itemSearchByAITheLatestVideoViewHolder.itemName2.setText(videoList.get(1).name);
            itemSearchByAITheLatestVideoViewHolder.itemName3.setVisibility(View.INVISIBLE);
        } else if (null != videoList && videoList.size() == 1) {
            itemSearchByAITheLatestVideoViewHolder.itemImg1.setImageURI(getImageUrl(videoList.get(0).image));
            itemSearchByAITheLatestVideoViewHolder.itemImg2.setVisibility(View.INVISIBLE);
            itemSearchByAITheLatestVideoViewHolder.itemImg3.setVisibility(View.INVISIBLE);
            itemSearchByAITheLatestVideoViewHolder.itemName1.setText(videoList.get(0).name);
            itemSearchByAITheLatestVideoViewHolder.itemName2.setVisibility(View.INVISIBLE);
            itemSearchByAITheLatestVideoViewHolder.itemName3.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * 体育赛事列表
     *
     * @param holder
     * @param searchByAIBean
     */
    private void bindItemSearchByAIListOfSportView(RecyclerView.ViewHolder holder, SearchByAIBean searchByAIBean, final int position) {
        final ItemSearchByAIListOfSportViewHolder itemSearchByAIListOfSportViewHolder = (ItemSearchByAIListOfSportViewHolder) holder;
        itemSearchByAIListOfSportViewHolder.tvTheDayBefore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != ItemSearchByAIClickListener) {
                    ItemSearchByAIClickListener.clickItemSearchByAIListOfSports(position, true);
                }
            }
        });
        itemSearchByAIListOfSportViewHolder.tvTheNextDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != ItemSearchByAIClickListener) {
                    ItemSearchByAIClickListener.clickItemSearchByAIListOfSports(position, false);
                }
            }
        });
    }

    /**
     * 体育赛事视频
     *
     * @param holder
     * @param searchByAIBean
     */
    private void bindItemSearchByAIVideoOfSportsView(RecyclerView.ViewHolder holder, SearchByAIBean searchByAIBean) {

    }

    private String getImageUrl(String imageJsonObj) {
        try {
            JSONObject jsonObject = new JSONObject(imageJsonObj);
            String imgUrl = jsonObject.optString("highResolutionV");
            if (!TextUtils.isEmpty(imgUrl)) {
                if (imgUrl.startsWith("http")) {
                    return imgUrl;
                } else {
                    return IMG_BASE_URL + imgUrl;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 类型：MESSAGE_TYPE_NORMAL
     */
    private class ItemSearchByAINormalViewHolder extends BaseViewHolder {
        private ImageView imHead;
        private TextView tvMessageFromAI;
        private TextView tvMessageFromUser;

        private ItemSearchByAINormalViewHolder(View itemView) {
            super(itemView);
            imHead = (ImageView) itemView.findViewById(R.id.im_head);
            tvMessageFromAI = (TextView) itemView.findViewById(R.id.tv_message_from_ai);
            tvMessageFromUser = (TextView) itemView.findViewById(R.id.tv_message_from_user);
        }
    }

    /**
     * 类型：MESSAGE_TYPE_CAN_ASK_AI
     */
    private class ItemSearchByAICanAskAIViewHolder extends BaseViewHolder {
        private RelativeLayout rlItem1;
        private RelativeLayout rlItem2;
        private RelativeLayout rlItem3;
        private RelativeLayout rlItem4;
        private RelativeLayout rlItem5;

        private ItemSearchByAICanAskAIViewHolder(View itemView) {
            super(itemView);
            rlItem1 = (RelativeLayout) itemView.findViewById(R.id.rl_item1);
            rlItem2 = (RelativeLayout) itemView.findViewById(R.id.rl_item2);
            rlItem3 = (RelativeLayout) itemView.findViewById(R.id.rl_item3);
            rlItem4 = (RelativeLayout) itemView.findViewById(R.id.rl_item4);
            rlItem5 = (RelativeLayout) itemView.findViewById(R.id.rl_item5);
        }
    }

    /**
     * 类型：MESSAGE_TYPE_APPOINTMENT
     */
    private class ItemSearchByAIAppointmentViewHolder extends BaseViewHolder {

        private ItemSearchByAIAppointmentViewHolder(View itemView) {
            super(itemView);
        }
    }

    /**
     * 类型：MESSAGE_TYPE_EVERYONE_IS_WATCHING
     */
    private class ItemSearchByAIEveryoneISWatchingViewHolder extends BaseViewHolder {
        private LinearLayout itemVideoOne;
        private LinearLayout itemVideoTwo;
        private LinearLayout itemVideoThree;
        private MGSimpleDraweeView itemImg1;
        private MGSimpleDraweeView itemImg2;
        private MGSimpleDraweeView itemImg3;
        private TextView itemWatchNum1;
        private TextView itemWatchNum2;
        private TextView itemWatchNum3;
        private TextView itemName1;
        private TextView itemName2;
        private TextView itemName3;
        private TextView tvMovieList;
        private TextView title;

        private ItemSearchByAIEveryoneISWatchingViewHolder(View itemView) {
            super(itemView);
            itemVideoOne = (LinearLayout) itemView.findViewById(R.id.ll_item_video_one);
            itemVideoTwo = (LinearLayout) itemView.findViewById(R.id.ll_item_video_two);
            itemVideoThree = (LinearLayout) itemView.findViewById(R.id.ll_item_video_three);
            itemImg1 = (MGSimpleDraweeView) itemView.findViewById(R.id.item_img1);
            itemImg2 = (MGSimpleDraweeView) itemView.findViewById(R.id.item_img2);
            itemImg3 = (MGSimpleDraweeView) itemView.findViewById(R.id.item_img3);
            itemWatchNum1 = (TextView) itemView.findViewById(R.id.item_watch_num1);
            itemWatchNum2 = (TextView) itemView.findViewById(R.id.item_watch_num2);
            itemWatchNum3 = (TextView) itemView.findViewById(R.id.item_watch_num3);
            itemName1 = (TextView) itemView.findViewById(R.id.item_name1);
            itemName2 = (TextView) itemView.findViewById(R.id.item_name2);
            itemName3 = (TextView) itemView.findViewById(R.id.item_name3);
            tvMovieList = (TextView) itemView.findViewById(R.id.tv_movie_list);
            title = (TextView) itemView.findViewById(R.id.title);
        }
    }

    /**
     * 类型：MESSAGE_TYPE_I_WANT_TO_SEE
     */
    private class ItemSearchByAIIWantTOSeeViewHolder extends BaseViewHolder {
        private MGSimpleDraweeView itemImg1;
        private MGSimpleDraweeView itemImg2;
        private MGSimpleDraweeView itemImg3;
        private TextView itemWatchNum1;
        private TextView itemWatchNum2;
        private TextView itemWatchNum3;
        private TextView itemName1;
        private TextView itemName2;
        private TextView itemName3;

        private ItemSearchByAIIWantTOSeeViewHolder(View itemView) {
            super(itemView);
            itemImg1 = (MGSimpleDraweeView) itemView.findViewById(R.id.item_img1);
            itemImg2 = (MGSimpleDraweeView) itemView.findViewById(R.id.item_img2);
            itemImg3 = (MGSimpleDraweeView) itemView.findViewById(R.id.item_img3);
            itemWatchNum1 = (TextView) itemView.findViewById(R.id.item_watch_num1);
            itemWatchNum2 = (TextView) itemView.findViewById(R.id.item_watch_num2);
            itemWatchNum3 = (TextView) itemView.findViewById(R.id.item_watch_num3);
            itemName1 = (TextView) itemView.findViewById(R.id.item_name1);
            itemName2 = (TextView) itemView.findViewById(R.id.item_name2);
            itemName3 = (TextView) itemView.findViewById(R.id.item_name3);
        }
    }

    /**
     * 类型：MESSAGE_TYPE_GUESS_WHAT_YOU_LIKE
     */
    private class ItemSearchByAIGuessWhatYouLikeViewHolder extends BaseViewHolder {

        private RelativeLayout itemContain;
        private MGSimpleDraweeView itemDetailImg;
        private TextView itemWatchNum;
        private TextView itemVideoName;
        private TextView itemDirector;
        private TextView itemActor;
        private TextView itemCategory;
        private TextView itemArea;
        private TextView itemLanguage;
        private TextView itemReleasetime;
        private TextView itemVideoDeteil;
        private TextView itemChange;
        private TextView itemPlotHeader;

        private ItemSearchByAIGuessWhatYouLikeViewHolder(View itemView) {
            super(itemView);
            itemContain = (RelativeLayout) itemView.findViewById(R.id.rl_contain);
            itemDetailImg = (MGSimpleDraweeView) itemView.findViewById(R.id.item_detail_img);
            itemWatchNum = (TextView) itemView.findViewById(R.id.item_watch_num);
            itemVideoName = (TextView) itemView.findViewById(R.id.item_video_name);
            itemDirector = (TextView) itemView.findViewById(R.id.item_director);
            itemActor = (TextView) itemView.findViewById(R.id.item_actor);
            itemCategory = (TextView) itemView.findViewById(R.id.item_category);
            itemArea = (TextView) itemView.findViewById(R.id.item_area);
            itemLanguage = (TextView) itemView.findViewById(R.id.item_language);
            itemReleasetime = (TextView) itemView.findViewById(R.id.item_releasetime);
            itemVideoDeteil = (TextView) itemView.findViewById(R.id.tv_video_deteil);
            itemChange = (TextView) itemView.findViewById(R.id.tv_item_change);
            itemPlotHeader = (TextView) itemView.findViewById(R.id.tv_plot_header);
        }
    }

    /**
     * 类型：MESSAGE_TYPE_GUESS_WHAT_YOU_LIKE_LIST_HORIZONTAL
     */
    private class ItemSearchByAIGuessWhatYouLikeListHorizontalViewHolder extends BaseViewHolder {
        private MGSimpleDraweeView itemDetailImg;
        private TextView itemWatchNum;
        private TextView itemVideoName;
        private TextView itemDirector;
        private TextView itemActor;
        private TextView itemCategory;
        private TextView itemArea;
        private TextView itemLanguage;
        private TextView itemReleasetime;
        private TextView itemVideoDeteil;
        private LinearLayout llVideoList;
        private TextView itemChange;
        private TextView itemPlotHeader;

        private ItemSearchByAIGuessWhatYouLikeListHorizontalViewHolder(View itemView) {
            super(itemView);
            itemDetailImg = (MGSimpleDraweeView) itemView.findViewById(R.id.item_detail_img);
            itemWatchNum = (TextView) itemView.findViewById(R.id.item_watch_num);
            itemVideoName = (TextView) itemView.findViewById(R.id.item_video_name);
            itemDirector = (TextView) itemView.findViewById(R.id.item_director);
            itemActor = (TextView) itemView.findViewById(R.id.item_actor);
            itemCategory = (TextView) itemView.findViewById(R.id.item_category);
            itemArea = (TextView) itemView.findViewById(R.id.item_area);
            itemLanguage = (TextView) itemView.findViewById(R.id.item_language);
            itemReleasetime = (TextView) itemView.findViewById(R.id.item_releasetime);
            itemVideoDeteil = (TextView) itemView.findViewById(R.id.tv_video_deteil);
            llVideoList = (LinearLayout) itemView.findViewById(R.id.ll_video_list);
            itemChange = (TextView) itemView.findViewById(R.id.tv_item_change);
            itemPlotHeader = (TextView) itemView.findViewById(R.id.tv_plot_header);
        }
    }

    /**
     * 类型：MESSAGE_TYPE_GUESS_WHAT_YOU_LIKE_LIST_VERTICAL
     */
    private class ItemSearchByAIGuessWhatYouLikeListVerticalViewHolder extends BaseViewHolder {
        private MGSimpleDraweeView itemDetailImg;
        private TextView itemWatchNum;
        private TextView itemVideoName;
        private TextView itemCategory;
        private TextView itemArea;
        private TextView itemLanguage;
        private TextView itemReleasetime;
        private LinearLayout llVideoList;
        private TextView itemChange;

        private ItemSearchByAIGuessWhatYouLikeListVerticalViewHolder(View itemView) {
            super(itemView);
            itemDetailImg = (MGSimpleDraweeView) itemView.findViewById(R.id.item_detail_img);
            itemWatchNum = (TextView) itemView.findViewById(R.id.item_watch_num);
            itemVideoName = (TextView) itemView.findViewById(R.id.item_video_name);
            itemCategory = (TextView) itemView.findViewById(R.id.item_category);
            itemArea = (TextView) itemView.findViewById(R.id.item_area);
            itemLanguage = (TextView) itemView.findViewById(R.id.item_language);
            itemReleasetime = (TextView) itemView.findViewById(R.id.item_releasetime);
            llVideoList = (LinearLayout) itemView.findViewById(R.id.ll_video_list);
            itemChange = (TextView) itemView.findViewById(R.id.tv_item_change);
        }
    }

    /**
     * 类型：MESSAGE_TYPE_THE_LATEST_VIDEO
     */
    private class ItemSearchByAITheLatestVideoViewHolder extends BaseViewHolder {
        private TextView itemTime1;
        private TextView itemTime2;
        private TextView itemTime3;
        private MGSimpleDraweeView itemImg1;
        private MGSimpleDraweeView itemImg2;
        private MGSimpleDraweeView itemImg3;
        private TextView itemWatchNum1;
        private TextView itemWatchNum2;
        private TextView itemWatchNum3;
        private TextView itemName1;
        private TextView itemName2;
        private TextView itemName3;

        private ItemSearchByAITheLatestVideoViewHolder(View itemView) {
            super(itemView);
            itemTime1 = (TextView) itemView.findViewById(R.id.itemTime1);
            itemTime2 = (TextView) itemView.findViewById(R.id.itemTime2);
            itemTime3 = (TextView) itemView.findViewById(R.id.itemTime3);
            itemImg1 = (MGSimpleDraweeView) itemView.findViewById(R.id.item_img1);
            itemImg2 = (MGSimpleDraweeView) itemView.findViewById(R.id.item_img2);
            itemImg3 = (MGSimpleDraweeView) itemView.findViewById(R.id.item_img3);
            itemWatchNum1 = (TextView) itemView.findViewById(R.id.item_watch_num1);
            itemWatchNum2 = (TextView) itemView.findViewById(R.id.item_watch_num2);
            itemWatchNum3 = (TextView) itemView.findViewById(R.id.item_watch_num3);
            itemName1 = (TextView) itemView.findViewById(R.id.item_name1);
            itemName2 = (TextView) itemView.findViewById(R.id.item_name2);
            itemName3 = (TextView) itemView.findViewById(R.id.item_name3);
        }
    }

    /**
     * 类型：MESSAGE_TYPE_LIST_OF_SPORTS
     */
    private class ItemSearchByAIListOfSportViewHolder extends BaseViewHolder {
        private RecyclerView sportsListRecyclerView;
        private TextView tvTheDayBefore;
        private TextView tvCurDate;
        private TextView tvTheNextDay;

        private ItemSearchByAIListOfSportViewHolder(View itemView) {
            super(itemView);
            sportsListRecyclerView = (RecyclerView) itemView.findViewById(R.id.sports_list_recyclerview);
            tvTheDayBefore = (TextView) itemView.findViewById(R.id.tv_the_day_before);
            tvCurDate = (TextView) itemView.findViewById(R.id.tv_cur_date);
            tvTheNextDay = (TextView) itemView.findViewById(R.id.tv_the_next_day);
        }
    }

    /**
     * 类型：MESSAGE_TYPE_VIDEO_OF_SPORTS
     */
    private class ItemSearchByAIVideoOfSportsViewHolder extends BaseViewHolder {
        private ItemSearchByAIVideoOfSportsViewHolder(View itemView) {
            super(itemView);
        }
    }
}
