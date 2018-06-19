package com.cmcc.cmvideo.search.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cmcc.cmvideo.R;
import com.cmcc.cmvideo.base.BaseRecyclerAdapter;
import com.cmcc.cmvideo.foundation.fresco.MGSimpleDraweeView;
import com.cmcc.cmvideo.search.JsonViewActivity;
import com.cmcc.cmvideo.search.LookMoreActivity;
import com.cmcc.cmvideo.search.aiui.bean.TppData;
import com.cmcc.cmvideo.search.interactors.ItemSearchByAIClickListener;
import com.cmcc.cmvideo.search.model.SearchByAIBean;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.cmcc.cmvideo.util.Constants.IMG_BASE_URL;
import static com.cmcc.cmvideo.util.Constants.MESSAGE_FROM_AI;
import static com.cmcc.cmvideo.util.Constants.MESSAGE_TYPE_APPOINTMENT;
import static com.cmcc.cmvideo.util.Constants.MESSAGE_TYPE_CAN_ASK_AI;
import static com.cmcc.cmvideo.util.Constants.MESSAGE_TYPE_EVERYONE_IS_WATCHING;
import static com.cmcc.cmvideo.util.Constants.MESSAGE_TYPE_GUESS_WHAT_YOU_LIKE;
import static com.cmcc.cmvideo.util.Constants.MESSAGE_TYPE_GUESS_WHAT_YOU_LIKE_LIST_HORIZONTAL;
import static com.cmcc.cmvideo.util.Constants.MESSAGE_TYPE_GUESS_WHAT_YOU_LIKE_LIST_VERTICAL;
import static com.cmcc.cmvideo.util.Constants.MESSAGE_TYPE_I_WANT_TO_SEE;
import static com.cmcc.cmvideo.util.Constants.MESSAGE_TYPE_NORMAL;
import static com.cmcc.cmvideo.util.Constants.MESSAGE_TYPE_THE_LATEST_VIDEO;

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
            }
        }
        return null;
    }

    @Override
    public void onBindHoder(final RecyclerView.ViewHolder holder, final SearchByAIBean searchByAIBean, final int position) {
        if (null != holder && null != searchByAIBean) {
            if (holder instanceof ItemSearchByAINormalViewHolder) {
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
                    itemSearchByAiNormalViewHolder.tvMessageFromUser.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(mContext, JsonViewActivity.class);
                            intent.putExtra(JsonViewActivity.KEY_JSON_SOURCE,searchByAIBean.getSource());
                            mContext.startActivity(intent);
                        }
                    });
                    itemSearchByAiNormalViewHolder.tvMessageFromUser.setText(searchByAIBean.getMessage());
                }
            } else if (holder instanceof ItemSearchByAICanAskAIViewHolder) {
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
            } else if (holder instanceof ItemSearchByAIAppointmentViewHolder) {
            } else if (holder instanceof ItemSearchByAIEveryoneISWatchingViewHolder) {
                final List<TppData.DetailsListBean> videoList = searchByAIBean.getVideoList();
                if (null == videoList) {
                    return;
                }
                final ItemSearchByAIEveryoneISWatchingViewHolder itemSearchByAIEveryoneISWatchingViewHolder = (ItemSearchByAIEveryoneISWatchingViewHolder) holder;
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

                itemSearchByAIEveryoneISWatchingViewHolder.tvMovieList.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (null != ItemSearchByAIClickListener) {
                            ItemSearchByAIClickListener.clickItemSearchByAIEveryoneISWatching(searchByAIBean.getSpeechText(), itemSearchByAIEveryoneISWatchingViewHolder.title.getText().toString().trim());
                        }
                    }
                });
            } else if (holder instanceof ItemSearchByAIIWantTOSeeViewHolder) {
                final List<TppData.DetailsListBean> videoList = searchByAIBean.getVideoList();
                if (null != videoList) {

                }
            } else if (holder instanceof ItemSearchByAIGuessWhatYouLikeViewHolder) {
                final List<TppData.DetailsListBean> videoList = searchByAIBean.getVideoList();
                if (null != videoList && videoList.size() != 0) {
                    final TppData.DetailsListBean detailsListBean = videoList.get(0);
                    if (null == detailsListBean) {
                        return;
                    }
                    final ItemSearchByAIGuessWhatYouLikeViewHolder itemSearchByAIGuessWhatYouLikeViewHolder = (ItemSearchByAIGuessWhatYouLikeViewHolder) holder;

                    itemSearchByAIGuessWhatYouLikeViewHolder.itemVideoName.setText(TextUtils.isEmpty(detailsListBean.name) ? "" : detailsListBean.name);
                    itemSearchByAIGuessWhatYouLikeViewHolder.itemArea.setText(TextUtils.isEmpty(detailsListBean.area) ? "制片国家/地区:" : "制片国家/地区:" + detailsListBean.area);
                    itemSearchByAIGuessWhatYouLikeViewHolder.itemReleasetime.setText(TextUtils.isEmpty(detailsListBean.releasetime) ? "上映日期:" : "上映日期:" + detailsListBean.releasetime);
                    itemSearchByAIGuessWhatYouLikeViewHolder.itemVideoDeteil.setText(TextUtils.isEmpty(detailsListBean.detail) ? "" : "" + detailsListBean.detail);
                    itemSearchByAIGuessWhatYouLikeViewHolder.itemChange.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (null != ItemSearchByAIClickListener) {
                                ItemSearchByAIClickListener.clickItemSearchByAIGuessWhatYouLike(true, detailsListBean);
                            }
                        }
                    });
                    if (!TextUtils.isEmpty(detailsListBean.image)) {
                        String image = detailsListBean.image;
                        try {
                            JSONObject jsonObject = new JSONObject(image);
                            String imgUrl = jsonObject.optString("highResolutionV");
                            if (!TextUtils.isEmpty(imgUrl)) {
                                if (imgUrl.startsWith("http")) {
                                    itemSearchByAIGuessWhatYouLikeViewHolder.itemDetailImg.setImageURI(imgUrl);
                                } else {
                                    itemSearchByAIGuessWhatYouLikeViewHolder.itemDetailImg.setImageURI(IMG_BASE_URL + imgUrl);
                                }
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    if (null != detailsListBean.director) {
                        StringBuilder director = new StringBuilder("导演:");
                        for (String text : detailsListBean.director) {
                            director.append(text).append("/");
                        }
                        if (director.toString().contains("/")) {
                            director = new StringBuilder(director.substring(0, director.lastIndexOf("/")));
                        }
                        itemSearchByAIGuessWhatYouLikeViewHolder.itemDirector.setText(director.toString());
                    }

                    if (null != detailsListBean.actor) {
                        StringBuilder actor = new StringBuilder("主演:");
                        for (String text : detailsListBean.actor) {
                            actor.append(text).append("/");
                        }
                        if (actor.toString().contains("/")) {
                            actor = new StringBuilder(actor.substring(0, actor.lastIndexOf("/")));
                        }
                        itemSearchByAIGuessWhatYouLikeViewHolder.itemActor.setText(actor.toString());
                    }

                    if (null != detailsListBean.tag) {
                        StringBuilder tag = new StringBuilder("类型:");
                        for (String text : detailsListBean.tag) {
                            tag.append(text).append("/");
                        }
                        if (tag.toString().contains("/")) {
                            tag = new StringBuilder(tag.substring(0, tag.lastIndexOf("/")));
                        }
                        itemSearchByAIGuessWhatYouLikeViewHolder.itemCategory.setText(tag.toString());
                    }

                    if (null != detailsListBean.language) {
                        StringBuilder language = new StringBuilder("语言:");
                        for (String text : detailsListBean.language) {
                            language.append(text).append("/");
                        }
                        if (language.toString().contains("/")) {
                            language = new StringBuilder(language.substring(0, language.lastIndexOf("/")));
                        }
                        itemSearchByAIGuessWhatYouLikeViewHolder.itemLanguage.setText(language.toString());
                    }

                }
            } else if (holder instanceof ItemSearchByAIGuessWhatYouLikeListHorizontalViewHolder) {
                final List<TppData.DetailsListBean> videoList = searchByAIBean.getVideoList();
                if (null != videoList) {
                    final TppData.DetailsListBean detailsListBean = videoList.get(0);
                    if (null == detailsListBean) {
                        return;
                    }
                    final ItemSearchByAIGuessWhatYouLikeListHorizontalViewHolder itemSearchByAIGuessWhatYouLikeListHorizontalViewHolder = (ItemSearchByAIGuessWhatYouLikeListHorizontalViewHolder) holder;

                    itemSearchByAIGuessWhatYouLikeListHorizontalViewHolder.itemVideoName.setText(TextUtils.isEmpty(detailsListBean.name) ? "" : detailsListBean.name);
                    itemSearchByAIGuessWhatYouLikeListHorizontalViewHolder.itemArea.setText(TextUtils.isEmpty(detailsListBean.area) ? "制片国家/地区:" : "制片国家/地区:" + detailsListBean.area);
                    itemSearchByAIGuessWhatYouLikeListHorizontalViewHolder.itemReleasetime.setText(TextUtils.isEmpty(detailsListBean.releasetime) ? "上映日期:" : "上映日期:" + detailsListBean.releasetime);
                    itemSearchByAIGuessWhatYouLikeListHorizontalViewHolder.itemVideoDeteil.setText(TextUtils.isEmpty(detailsListBean.detail) ? "" : "" + detailsListBean.detail);
                    itemSearchByAIGuessWhatYouLikeListHorizontalViewHolder.itemChange.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (null != ItemSearchByAIClickListener) {
                                ItemSearchByAIClickListener.clickItemSearchByAIGuessWhatYouLikeListHorizontal(true, detailsListBean, -1);
                            }
                        }
                    });
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

                    if (null != detailsListBean.director) {
                        StringBuilder director = new StringBuilder("导演:");
                        for (String text : detailsListBean.director) {
                            director.append(text).append("/");
                        }
                        if (director.toString().contains("/")) {
                            director = new StringBuilder(director.substring(0, director.lastIndexOf("/")));
                        }
                        itemSearchByAIGuessWhatYouLikeListHorizontalViewHolder.itemDirector.setText(director.toString());
                    }

                    if (null != detailsListBean.actor) {
                        StringBuilder actor = new StringBuilder("主演:");
                        for (String text : detailsListBean.actor) {
                            actor.append(text).append("/");
                        }
                        if (actor.toString().contains("/")) {
                            actor = new StringBuilder(actor.substring(0, actor.lastIndexOf("/")));
                        }
                        itemSearchByAIGuessWhatYouLikeListHorizontalViewHolder.itemActor.setText(actor.toString());
                    }

                    if (null != detailsListBean.tag) {
                        StringBuilder tag = new StringBuilder("类型:");
                        for (String text : detailsListBean.tag) {
                            tag.append(text).append("/");
                        }
                        if (tag.toString().contains("/")) {
                            tag = new StringBuilder(tag.substring(0, tag.lastIndexOf("/")));
                        }
                        itemSearchByAIGuessWhatYouLikeListHorizontalViewHolder.itemCategory.setText(tag.toString());
                    }

                    if (null != detailsListBean.language) {
                        StringBuilder language = new StringBuilder("语言:");
                        for (String text : detailsListBean.language) {
                            language.append(text).append("/");
                        }
                        if (language.toString().contains("/")) {
                            language = new StringBuilder(language.substring(0, language.lastIndexOf("/")));
                        }
                        itemSearchByAIGuessWhatYouLikeListHorizontalViewHolder.itemLanguage.setText(language.toString());
                    }

                    if (null != detailsListBean.subserials) {
                        itemSearchByAIGuessWhatYouLikeListHorizontalViewHolder.llVideoList.removeAllViews();
                        if (detailsListBean.subserials.size() > 5) {
                            for (int i = 0; i < 3; i++) {
                                final int pos = i;
                                View root = LayoutInflater.from(holder.itemView.getContext()).inflate(R.layout.item_search_by_ai_horizontal_item, null, false);
                                if (2 == i) {
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
                                            ItemSearchByAIClickListener.clickItemSearchByAIGuessWhatYouLikeListHorizontal(true, detailsListBean, pos);
                                        }
                                    }
                                });
                            }

                            for (int i = detailsListBean.subserials.size() - 2; i < detailsListBean.subserials.size(); i++) {
                                int num = i + 1;
                                final int pos = i;
                                View root = LayoutInflater.from(holder.itemView.getContext()).inflate(R.layout.item_search_by_ai_horizontal_item, null, false);
                                ((TextView) root.findViewById(R.id.tv_release_num)).setText("" + num);
                                itemSearchByAIGuessWhatYouLikeListHorizontalViewHolder.llVideoList.addView(root);
                                root.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (null != ItemSearchByAIClickListener) {
                                            ItemSearchByAIClickListener.clickItemSearchByAIGuessWhatYouLikeListHorizontal(true, detailsListBean, pos);
                                        }
                                    }
                                });
                            }
                        } else {
                            for (int i = 0; i < detailsListBean.subserials.size(); i++) {
                                View root = LayoutInflater.from(holder.itemView.getContext()).inflate(R.layout.item_search_by_ai_horizontal_item, null, false);
                                int num = i + 1;
                                ((TextView) root.findViewById(R.id.tv_release_num)).setText("" + num);
                                itemSearchByAIGuessWhatYouLikeListHorizontalViewHolder.llVideoList.addView(root);
                            }
                        }

                    }
                }
            } else if (holder instanceof ItemSearchByAIGuessWhatYouLikeListVerticalViewHolder) {
                final List<TppData.DetailsListBean> videoList = searchByAIBean.getVideoList();
                if (null != videoList) {
                    final TppData.DetailsListBean detailsListBean = videoList.get(0);
                    if (null == detailsListBean) {
                        return;
                    }
                    final ItemSearchByAIGuessWhatYouLikeListVerticalViewHolder itemSearchByAIGuessWhatYouLikeListVerticalViewHolder = (ItemSearchByAIGuessWhatYouLikeListVerticalViewHolder) holder;

                    itemSearchByAIGuessWhatYouLikeListVerticalViewHolder.itemVideoName.setText(TextUtils.isEmpty(detailsListBean.name) ? "" : detailsListBean.name);
                    itemSearchByAIGuessWhatYouLikeListVerticalViewHolder.itemArea.setText(TextUtils.isEmpty(detailsListBean.area) ? "制片国家/地区:" : "制片国家/地区:" + detailsListBean.area);
                    itemSearchByAIGuessWhatYouLikeListVerticalViewHolder.itemReleasetime.setText(TextUtils.isEmpty(detailsListBean.releasetime) ? "上映日期:" : "上映日期:" + detailsListBean.releasetime);
                    itemSearchByAIGuessWhatYouLikeListVerticalViewHolder.itemChange.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (null != ItemSearchByAIClickListener) {
                                ItemSearchByAIClickListener.clickItemSearchByAIGuessWhatYouLikeListVertical(true, detailsListBean, -1);
                            }
                        }
                    });
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

                    if (null != detailsListBean.tag) {
                        StringBuilder tag = new StringBuilder("类型:");
                        for (String text : detailsListBean.tag) {
                            tag.append(text).append("/");
                        }
                        if (tag.toString().contains("/")) {
                            tag = new StringBuilder(tag.substring(0, tag.lastIndexOf("/")));
                        }
                        itemSearchByAIGuessWhatYouLikeListVerticalViewHolder.itemCategory.setText(tag.toString());
                    }

                    if (null != detailsListBean.language) {
                        StringBuilder language = new StringBuilder("语言:");
                        for (String text : detailsListBean.language) {
                            language.append(text).append("/");
                        }
                        if (language.toString().contains("/")) {
                            language = new StringBuilder(language.substring(0, language.lastIndexOf("/")));
                        }
                        itemSearchByAIGuessWhatYouLikeListVerticalViewHolder.itemLanguage.setText(language.toString());
                    }

                    if (null != detailsListBean.subserials) {
                        itemSearchByAIGuessWhatYouLikeListVerticalViewHolder.llVideoList.removeAllViews();
                        int count = detailsListBean.subserials.size();
                        if (count > 3) {
                            count = 3;
                        }
                        for (int i = 0; i < count; i++) {
                            final int pos = i;
                            View root = LayoutInflater.from(holder.itemView.getContext()).inflate(R.layout.item_search_by_ai_vertical_item, null, false);
                            ((TextView) root.findViewById(R.id.tv_release_num)).setText(detailsListBean.subserials.get(i).id);
                            ((TextView) root.findViewById(R.id.tv_release_name)).setText(detailsListBean.subserials.get(i).name);
                            itemSearchByAIGuessWhatYouLikeListVerticalViewHolder.llVideoList.addView(root);
                            root.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (null != ItemSearchByAIClickListener) {
                                        ItemSearchByAIClickListener.clickItemSearchByAIGuessWhatYouLikeListVertical(false, detailsListBean, pos);
                                    }
                                }
                            });
                        }
                    }
                }
            } else if (holder instanceof ItemSearchByAITheLatestVideoViewHolder) {
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
        }
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

        private ItemSearchByAIGuessWhatYouLikeViewHolder(View itemView) {
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
            itemChange = (TextView) itemView.findViewById(R.id.item_change);
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
            itemChange = (TextView) itemView.findViewById(R.id.tv_change_list);
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
            itemChange = (TextView) itemView.findViewById(R.id.tv_change_list);
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
}
