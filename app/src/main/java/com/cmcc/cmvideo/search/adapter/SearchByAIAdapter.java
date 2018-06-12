package com.cmcc.cmvideo.search.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cmcc.cmvideo.R;
import com.cmcc.cmvideo.base.BaseRecyclerAdapter;
import com.cmcc.cmvideo.foundation.fresco.MGSimpleDraweeView;
import com.cmcc.cmvideo.search.LookMoreActivity;
import com.cmcc.cmvideo.search.aiui.bean.TppData;
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
    private Context mContext;

    public SearchByAIAdapter(Context ctx) {
        super(ctx);
        this.mContext = ctx;
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
    public void onBindHoder(RecyclerView.ViewHolder holder, SearchByAIBean searchByAIBean, int position) {
        if (null != holder && null != searchByAIBean) {
            if (holder instanceof ItemSearchByAINormalViewHolder) {
                ItemSearchByAINormalViewHolder itemSearchByAiNormalViewHolder = (ItemSearchByAINormalViewHolder) holder;
                if (TextUtils.equals(searchByAIBean.getMessageFrom(), MESSAGE_FROM_AI)) {
                    itemSearchByAiNormalViewHolder.tvMessageFromAI.setVisibility(View.VISIBLE);
                    itemSearchByAiNormalViewHolder.tvMessageFromAI.setText(searchByAIBean.getMessage());
                    itemSearchByAiNormalViewHolder.tvMessageFromUser.setVisibility(View.GONE);
                } else {
                    itemSearchByAiNormalViewHolder.imHead.setVisibility(View.INVISIBLE);
                    itemSearchByAiNormalViewHolder.tvMessageFromAI.setVisibility(View.GONE);
                    itemSearchByAiNormalViewHolder.tvMessageFromUser.setVisibility(View.VISIBLE);
                    itemSearchByAiNormalViewHolder.tvMessageFromUser.setText(searchByAIBean.getMessage());
                }
            } else if (holder instanceof ItemSearchByAICanAskAIViewHolder) {
            } else if (holder instanceof ItemSearchByAIAppointmentViewHolder) {
            } else if (holder instanceof ItemSearchByAIEveryoneISWatchingViewHolder) {
                final List<TppData.DetailsListBean> videoList = searchByAIBean.getVideoList();
                ItemSearchByAIEveryoneISWatchingViewHolder itemSearchByAIEveryoneISWatchingViewHolder = (ItemSearchByAIEveryoneISWatchingViewHolder) holder;
                itemSearchByAIEveryoneISWatchingViewHolder.itemImg1.setVisibility(View.VISIBLE);
                itemSearchByAIEveryoneISWatchingViewHolder.itemImg2.setVisibility(View.VISIBLE);
                itemSearchByAIEveryoneISWatchingViewHolder.itemImg3.setVisibility(View.VISIBLE);
                itemSearchByAIEveryoneISWatchingViewHolder.itemName1.setVisibility(View.VISIBLE);
                itemSearchByAIEveryoneISWatchingViewHolder.itemName2.setVisibility(View.VISIBLE);
                itemSearchByAIEveryoneISWatchingViewHolder.itemName3.setVisibility(View.VISIBLE);
                itemSearchByAIEveryoneISWatchingViewHolder.itemWatchNum1.setVisibility(View.GONE);
                itemSearchByAIEveryoneISWatchingViewHolder.itemWatchNum2.setVisibility(View.GONE);
                itemSearchByAIEveryoneISWatchingViewHolder.itemWatchNum3.setVisibility(View.GONE);
                if (null != videoList && videoList.size() >= 3) {
                    itemSearchByAIEveryoneISWatchingViewHolder.itemImg1.setImageURI(getImageUrl(videoList.get(0).image));
                    itemSearchByAIEveryoneISWatchingViewHolder.itemImg2.setImageURI(getImageUrl(videoList.get(1).image));
                    itemSearchByAIEveryoneISWatchingViewHolder.itemImg3.setImageURI(getImageUrl(videoList.get(2).image));
                    itemSearchByAIEveryoneISWatchingViewHolder.itemName1.setText(videoList.get(0).name);
                    itemSearchByAIEveryoneISWatchingViewHolder.itemName2.setText(videoList.get(1).name);
                    itemSearchByAIEveryoneISWatchingViewHolder.itemName3.setText(videoList.get(2).name);
                    //                    itemSearchByAIEveryoneISWatchingViewHolder.itemWatchNum1.setText("6666");
                    //                    itemSearchByAIEveryoneISWatchingViewHolder.itemWatchNum2.setText("7777");
                    //                    itemSearchByAIEveryoneISWatchingViewHolder.itemWatchNum3.setText("8888");

                } else if (null != videoList && videoList.size() == 2) {
                    itemSearchByAIEveryoneISWatchingViewHolder.itemImg1.setImageURI(getImageUrl(videoList.get(0).image));
                    itemSearchByAIEveryoneISWatchingViewHolder.itemImg2.setImageURI(getImageUrl(videoList.get(1).image));
                    itemSearchByAIEveryoneISWatchingViewHolder.itemImg3.setVisibility(View.INVISIBLE);
                    itemSearchByAIEveryoneISWatchingViewHolder.itemName1.setText(videoList.get(0).name);
                    itemSearchByAIEveryoneISWatchingViewHolder.itemName2.setText(videoList.get(1).name);
                    itemSearchByAIEveryoneISWatchingViewHolder.itemName3.setVisibility(View.INVISIBLE);
                } else if (null != videoList && videoList.size() == 1) {
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
                        if(videoList !=null) {
                            Intent intent = new Intent(mContext, LookMoreActivity.class);
                            Bundle bundle = new Bundle();
                            ArrayList<TppData.DetailsListBean> videoList = new ArrayList<>();
                            videoList.addAll(videoList);
                            bundle.putSerializable(LookMoreActivity.KEY_MORE_DATE, videoList);
                            intent.putExtra(LookMoreActivity.KEY_MORE_DATE_BUNDLE,bundle);
                            mContext.startActivity(intent);
                        }
                    }
                });
            } else if (holder instanceof ItemSearchByAIIWantTOSeeViewHolder) {
                List<TppData.DetailsListBean> videoList = searchByAIBean.getVideoList();
                if (null != videoList) {

                }
            } else if (holder instanceof ItemSearchByAIGuessWhatYouLikeViewHolder) {
                List<TppData.DetailsListBean> videoList = searchByAIBean.getVideoList();
                if (null != videoList && videoList.size() != 0) {
                    TppData.DetailsListBean detailsListBean = videoList.get(0);
                    if (null == detailsListBean) {
                        return;
                    }
                    ItemSearchByAIGuessWhatYouLikeViewHolder itemSearchByAIGuessWhatYouLikeViewHolder = (ItemSearchByAIGuessWhatYouLikeViewHolder) holder;

                    itemSearchByAIGuessWhatYouLikeViewHolder.itemVideoName.setText(TextUtils.isEmpty(detailsListBean.name) ? "" : detailsListBean.name);
                    itemSearchByAIGuessWhatYouLikeViewHolder.itemArea.setText(TextUtils.isEmpty(detailsListBean.area) ? "制片国家/地区:" : "制片国家/地区:" + detailsListBean.area);
                    itemSearchByAIGuessWhatYouLikeViewHolder.itemReleasetime.setText(TextUtils.isEmpty(detailsListBean.releasetime) ? "上映日期:" : "上映日期:" + detailsListBean.releasetime);
                    itemSearchByAIGuessWhatYouLikeViewHolder.itemVideoDeteil.setText(TextUtils.isEmpty(detailsListBean.detail) ? "" : "" + detailsListBean.detail);

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
                        String director = "导演:";
                        for (String text : detailsListBean.director) {
                            director = director + text + "/";
                        }
                        if (director.contains("/")) {
                            director = director.substring(0, director.lastIndexOf("/"));
                        }
                        itemSearchByAIGuessWhatYouLikeViewHolder.itemDirector.setText(director);
                    }

                    if (null != detailsListBean.actor) {
                        String actor = "主演:";
                        for (String text : detailsListBean.actor) {
                            actor = actor + text + "/";
                        }
                        if (actor.contains("/")) {
                            actor = actor.substring(0, actor.lastIndexOf("/"));
                        }
                        itemSearchByAIGuessWhatYouLikeViewHolder.itemActor.setText(actor);
                    }

                    if (null != detailsListBean.tag) {
                        String tag = "类型:";
                        for (String text : detailsListBean.tag) {
                            tag = tag + text + "/";
                        }
                        if (tag.contains("/")) {
                            tag = tag.substring(0, tag.lastIndexOf("/"));
                        }
                        itemSearchByAIGuessWhatYouLikeViewHolder.itemCategory.setText(tag);
                    }

                    if (null != detailsListBean.language) {
                        String language = "语言:";
                        for (String text : detailsListBean.language) {
                            language = language + text + "/";
                        }
                        if (language.contains("/")) {
                            language = language.substring(0, language.lastIndexOf("/"));
                        }
                        itemSearchByAIGuessWhatYouLikeViewHolder.itemLanguage.setText(language);
                    }

                }
            } else if (holder instanceof ItemSearchByAIGuessWhatYouLikeListHorizontalViewHolder) {
                List<TppData.DetailsListBean> videoList = searchByAIBean.getVideoList();
                if (null != videoList) {
                    TppData.DetailsListBean detailsListBean = videoList.get(0);
                    if (null == detailsListBean) {
                        return;
                    }
                    ItemSearchByAIGuessWhatYouLikeListHorizontalViewHolder itemSearchByAIGuessWhatYouLikeListHorizontalViewHolder = (ItemSearchByAIGuessWhatYouLikeListHorizontalViewHolder) holder;

                    itemSearchByAIGuessWhatYouLikeListHorizontalViewHolder.itemVideoName.setText(TextUtils.isEmpty(detailsListBean.name) ? "" : detailsListBean.name);
                    itemSearchByAIGuessWhatYouLikeListHorizontalViewHolder.itemArea.setText(TextUtils.isEmpty(detailsListBean.area) ? "制片国家/地区:" : "制片国家/地区:" + detailsListBean.area);
                    itemSearchByAIGuessWhatYouLikeListHorizontalViewHolder.itemReleasetime.setText(TextUtils.isEmpty(detailsListBean.releasetime) ? "上映日期:" : "上映日期:" + detailsListBean.releasetime);
                    itemSearchByAIGuessWhatYouLikeListHorizontalViewHolder.itemVideoDeteil.setText(TextUtils.isEmpty(detailsListBean.detail) ? "" : "" + detailsListBean.detail);

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
                        String director = "导演:";
                        for (String text : detailsListBean.director) {
                            director = director + text + "/";
                        }
                        if (director.contains("/")) {
                            director = director.substring(0, director.lastIndexOf("/"));
                        }
                        itemSearchByAIGuessWhatYouLikeListHorizontalViewHolder.itemDirector.setText(director);
                    }

                    if (null != detailsListBean.actor) {
                        String actor = "主演:";
                        for (String text : detailsListBean.actor) {
                            actor = actor + text + "/";
                        }
                        if (actor.contains("/")) {
                            actor = actor.substring(0, actor.lastIndexOf("/"));
                        }
                        itemSearchByAIGuessWhatYouLikeListHorizontalViewHolder.itemActor.setText(actor);
                    }

                    if (null != detailsListBean.tag) {
                        String tag = "类型:";
                        for (String text : detailsListBean.tag) {
                            tag = tag + text + "/";
                        }
                        if (tag.contains("/")) {
                            tag = tag.substring(0, tag.lastIndexOf("/"));
                        }
                        itemSearchByAIGuessWhatYouLikeListHorizontalViewHolder.itemCategory.setText(tag);
                    }

                    if (null != detailsListBean.language) {
                        String language = "语言:";
                        for (String text : detailsListBean.language) {
                            language = language + text + "/";
                        }
                        if (language.contains("/")) {
                            language = language.substring(0, language.lastIndexOf("/"));
                        }
                        itemSearchByAIGuessWhatYouLikeListHorizontalViewHolder.itemLanguage.setText(language);
                    }

                    if (null != detailsListBean.subserials) {
                        itemSearchByAIGuessWhatYouLikeListHorizontalViewHolder.llVideoList.removeAllViews();
                        if (detailsListBean.subserials.size() > 5) {
                            for (int i = 0; i < 3; i++) {
                                View root = LayoutInflater.from(holder.itemView.getContext()).inflate(R.layout.item_search_by_ai_horizontal_item, null, false);
                                if (2 == i) {
                                    ((TextView) root.findViewById(R.id.tv_release_num)).setText("...");
                                } else {
                                    int num = i + 1;
                                    ((TextView) root.findViewById(R.id.tv_release_num)).setText("" + num);
                                }
                                itemSearchByAIGuessWhatYouLikeListHorizontalViewHolder.llVideoList.addView(root);
                            }

                            for (int i = detailsListBean.subserials.size() - 2; i < detailsListBean.subserials.size(); i++) {
                                View root = LayoutInflater.from(holder.itemView.getContext()).inflate(R.layout.item_search_by_ai_horizontal_item, null, false);
                                int num = i + 1;
                                ((TextView) root.findViewById(R.id.tv_release_num)).setText("" + num);
                                itemSearchByAIGuessWhatYouLikeListHorizontalViewHolder.llVideoList.addView(root);
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
                List<TppData.DetailsListBean> videoList = searchByAIBean.getVideoList();
                if (null != videoList) {
                    TppData.DetailsListBean detailsListBean = videoList.get(0);
                    if (null == detailsListBean) {
                        return;
                    }
                    ItemSearchByAIGuessWhatYouLikeListVerticalViewHolder itemSearchByAIGuessWhatYouLikeListVerticalViewHolder = (ItemSearchByAIGuessWhatYouLikeListVerticalViewHolder) holder;

                    itemSearchByAIGuessWhatYouLikeListVerticalViewHolder.itemVideoName.setText(TextUtils.isEmpty(detailsListBean.name) ? "" : detailsListBean.name);
                    itemSearchByAIGuessWhatYouLikeListVerticalViewHolder.itemArea.setText(TextUtils.isEmpty(detailsListBean.area) ? "制片国家/地区:" : "制片国家/地区:" + detailsListBean.area);
                    itemSearchByAIGuessWhatYouLikeListVerticalViewHolder.itemReleasetime.setText(TextUtils.isEmpty(detailsListBean.releasetime) ? "上映日期:" : "上映日期:" + detailsListBean.releasetime);

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
                        String tag = "类型:";
                        for (String text : detailsListBean.tag) {
                            tag = tag + text + "/";
                        }
                        if (tag.contains("/")) {
                            tag = tag.substring(0, tag.lastIndexOf("/"));
                        }
                        itemSearchByAIGuessWhatYouLikeListVerticalViewHolder.itemCategory.setText(tag);
                    }

                    if (null != detailsListBean.language) {
                        String language = "语言:";
                        for (String text : detailsListBean.language) {
                            language = language + text + "/";
                        }
                        if (language.contains("/")) {
                            language = language.substring(0, language.lastIndexOf("/"));
                        }
                        itemSearchByAIGuessWhatYouLikeListVerticalViewHolder.itemLanguage.setText(language);
                    }

                    if (null != detailsListBean.subserials) {
                        itemSearchByAIGuessWhatYouLikeListVerticalViewHolder.llVideoList.removeAllViews();
                        int count = detailsListBean.subserials.size();
                        if (count > 3) {
                            count = 3;
                        }
                        for (int i = 0; i < count; i++) {
                            View root = LayoutInflater.from(holder.itemView.getContext()).inflate(R.layout.item_search_by_ai_vertical_item, null, false);
                            ((TextView) root.findViewById(R.id.tv_release_num)).setText(detailsListBean.subserials.get(i).id);
                            ((TextView) root.findViewById(R.id.tv_release_name)).setText(detailsListBean.subserials.get(i).name);
                            itemSearchByAIGuessWhatYouLikeListVerticalViewHolder.llVideoList.addView(root);
                        }
                    }
                }
            } else if (holder instanceof ItemSearchByAITheLatestVideoViewHolder) {
                ItemSearchByAITheLatestVideoViewHolder itemSearchByAITheLatestVideoViewHolder = (ItemSearchByAITheLatestVideoViewHolder) holder;
                List<TppData.DetailsListBean> videoList = searchByAIBean.getVideoList();
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
                    //                    itemSearchByAIEveryoneISWatchingViewHolder.itemWatchNum1.setText("6666");
                    //                    itemSearchByAIEveryoneISWatchingViewHolder.itemWatchNum2.setText("7777");
                    //                    itemSearchByAIEveryoneISWatchingViewHolder.itemWatchNum3.setText("8888");

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
    public class ItemSearchByAINormalViewHolder extends BaseViewHolder {
        ImageView imHead;
        TextView tvMessageFromAI;
        TextView tvMessageFromUser;

        public ItemSearchByAINormalViewHolder(View itemView) {
            super(itemView);
            imHead = (ImageView) itemView.findViewById(R.id.im_head);
            tvMessageFromAI = (TextView) itemView.findViewById(R.id.tv_message_from_ai);
            tvMessageFromUser = (TextView) itemView.findViewById(R.id.tv_message_from_user);
        }
    }

    /**
     * 类型：MESSAGE_TYPE_CAN_ASK_AI
     */
    public class ItemSearchByAICanAskAIViewHolder extends BaseViewHolder {

        public ItemSearchByAICanAskAIViewHolder(View itemView) {
            super(itemView);
        }
    }

    /**
     * 类型：MESSAGE_TYPE_APPOINTMENT
     */
    public class ItemSearchByAIAppointmentViewHolder extends BaseViewHolder {

        public ItemSearchByAIAppointmentViewHolder(View itemView) {
            super(itemView);
        }
    }

    /**
     * 类型：MESSAGE_TYPE_EVERYONE_IS_WATCHING
     */
    public class ItemSearchByAIEveryoneISWatchingViewHolder extends BaseViewHolder {

        public MGSimpleDraweeView itemImg1;
        public MGSimpleDraweeView itemImg2;
        public MGSimpleDraweeView itemImg3;
        public TextView itemWatchNum1;
        public TextView itemWatchNum2;
        public TextView itemWatchNum3;
        public TextView itemName1;
        public TextView itemName2;
        public TextView itemName3;
        public TextView tvMovieList;

        public ItemSearchByAIEveryoneISWatchingViewHolder(View itemView) {
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
        }
    }

    /**
     * 类型：MESSAGE_TYPE_I_WANT_TO_SEE
     */
    public class ItemSearchByAIIWantTOSeeViewHolder extends BaseViewHolder {
        public MGSimpleDraweeView itemImg1;
        public MGSimpleDraweeView itemImg2;
        public MGSimpleDraweeView itemImg3;
        public TextView itemWatchNum1;
        public TextView itemWatchNum2;
        public TextView itemWatchNum3;
        public TextView itemName1;
        public TextView itemName2;
        public TextView itemName3;

        public ItemSearchByAIIWantTOSeeViewHolder(View itemView) {
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
    public class ItemSearchByAIGuessWhatYouLikeViewHolder extends BaseViewHolder {

        public MGSimpleDraweeView itemDetailImg;
        public TextView itemWatchNum;
        public TextView itemVideoName;
        public TextView itemDirector;
        public TextView itemActor;
        public TextView itemCategory;
        public TextView itemArea;
        public TextView itemLanguage;
        public TextView itemReleasetime;
        public TextView itemVideoDeteil;
        public TextView itemChange;

        public ItemSearchByAIGuessWhatYouLikeViewHolder(View itemView) {
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
    public class ItemSearchByAIGuessWhatYouLikeListHorizontalViewHolder extends BaseViewHolder {
        public MGSimpleDraweeView itemDetailImg;
        public TextView itemWatchNum;
        public TextView itemVideoName;
        public TextView itemDirector;
        public TextView itemActor;
        public TextView itemCategory;
        public TextView itemArea;
        public TextView itemLanguage;
        public TextView itemReleasetime;
        public TextView itemVideoDeteil;
        public LinearLayout llVideoList;
        public TextView itemChange;

        public ItemSearchByAIGuessWhatYouLikeListHorizontalViewHolder(View itemView) {
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
    public class ItemSearchByAIGuessWhatYouLikeListVerticalViewHolder extends BaseViewHolder {
        public MGSimpleDraweeView itemDetailImg;
        public TextView itemWatchNum;
        public TextView itemVideoName;
        public TextView itemCategory;
        public TextView itemArea;
        public TextView itemLanguage;
        public TextView itemReleasetime;
        public LinearLayout llVideoList;
        public TextView itemChange;

        public ItemSearchByAIGuessWhatYouLikeListVerticalViewHolder(View itemView) {
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
    public class ItemSearchByAITheLatestVideoViewHolder extends BaseViewHolder {
        public TextView itemTime1;
        public TextView itemTime2;
        public TextView itemTime3;
        public MGSimpleDraweeView itemImg1;
        public MGSimpleDraweeView itemImg2;
        public MGSimpleDraweeView itemImg3;
        public TextView itemWatchNum1;
        public TextView itemWatchNum2;
        public TextView itemWatchNum3;
        public TextView itemName1;
        public TextView itemName2;
        public TextView itemName3;

        public ItemSearchByAITheLatestVideoViewHolder(View itemView) {
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
