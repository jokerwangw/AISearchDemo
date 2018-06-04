package com.cmcc.cmvideo.search.adapter;

import android.content.Context;
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
import com.cmcc.cmvideo.search.aiui.bean.TppData;
import com.cmcc.cmvideo.search.model.SearchByAIBean;

import java.util.List;

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
                return new ItemSearchByAIEveryoneIWantTOSeeViewHolder(view);
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
                List<TppData.DetailsListBean> videoList = searchByAIBean.getVideoList();
                if (null != videoList) {

                }
            } else if (holder instanceof ItemSearchByAIEveryoneIWantTOSeeViewHolder) {
                List<TppData.DetailsListBean> videoList = searchByAIBean.getVideoList();
                if (null != videoList) {

                }
            } else if (holder instanceof ItemSearchByAIGuessWhatYouLikeViewHolder) {
                List<TppData.DetailsListBean> videoList = searchByAIBean.getVideoList();
                if (null != videoList) {

                }
            } else if (holder instanceof ItemSearchByAIGuessWhatYouLikeListHorizontalViewHolder) {
                List<TppData.DetailsListBean> videoList = searchByAIBean.getVideoList();
                if (null != videoList) {

                }
            } else if (holder instanceof ItemSearchByAIGuessWhatYouLikeListVerticalViewHolder) {
                List<TppData.DetailsListBean> videoList = searchByAIBean.getVideoList();
                if (null != videoList) {

                }
            } else if (holder instanceof ItemSearchByAITheLatestVideoViewHolder) {
            }
        }
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
        }
    }

    /**
     * 类型：MESSAGE_TYPE_I_WANT_TO_SEE
     */
    public class ItemSearchByAIEveryoneIWantTOSeeViewHolder extends BaseViewHolder {
        public MGSimpleDraweeView itemImg1;
        public MGSimpleDraweeView itemImg2;
        public MGSimpleDraweeView itemImg3;
        public TextView itemWatchNum1;
        public TextView itemWatchNum2;
        public TextView itemWatchNum3;
        public TextView itemName1;
        public TextView itemName2;
        public TextView itemName3;

        public ItemSearchByAIEveryoneIWantTOSeeViewHolder(View itemView) {
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
        public TextView tvvideodeteil;
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
            tvvideodeteil = (TextView) itemView.findViewById(R.id.tv_video_deteil);
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
        public TextView tvvideodeteil;
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
            tvvideodeteil = (TextView) itemView.findViewById(R.id.tv_video_deteil);
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

        public ItemSearchByAITheLatestVideoViewHolder(View itemView) {
            super(itemView);
        }
    }
}
