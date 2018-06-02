package com.cmcc.cmvideo.search.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cmcc.cmvideo.R;
import com.cmcc.cmvideo.base.BaseRecyclerAdapter;
import com.cmcc.cmvideo.search.model.SearchByAIBean;

import static com.cmcc.cmvideo.utils.Constants.MESSAGE_FROM_AI;
import static com.cmcc.cmvideo.utils.Constants.MESSAGE_TYPE_APPOINTMENT;
import static com.cmcc.cmvideo.utils.Constants.MESSAGE_TYPE_CAN_ASK_AI;
import static com.cmcc.cmvideo.utils.Constants.MESSAGE_TYPE_EVERYONE_IS_WATCHING;
import static com.cmcc.cmvideo.utils.Constants.MESSAGE_TYPE_GUESS_WHAT_YOU_LIKE;
import static com.cmcc.cmvideo.utils.Constants.MESSAGE_TYPE_NORMAL;
import static com.cmcc.cmvideo.utils.Constants.MESSAGE_TYPE_THE_LATEST_VIDEO;

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
            } else if (MESSAGE_TYPE_GUESS_WHAT_YOU_LIKE == searchByAIBean.getMessageType()) {
                return MESSAGE_TYPE_GUESS_WHAT_YOU_LIKE;
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
            } else if (MESSAGE_TYPE_GUESS_WHAT_YOU_LIKE == viewType) {
                View view = layoutInflater.inflate(R.layout.item_search_by_ai_guess_what_you_like, null);
                return new ItemSearchByAIGuessWhatYouLikeViewHolder(view);
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
                    //                    if (0 == position) {
                    //                        itemSearchByAiNormalViewHolder.imHead.setVisibility(View.VISIBLE);
                    //                    } else {
                    //                        itemSearchByAiNormalViewHolder.imHead.setVisibility(View.INVISIBLE);
                    //                    }
                    itemSearchByAiNormalViewHolder.tvMessageFromAI.setVisibility(View.VISIBLE);
                    itemSearchByAiNormalViewHolder.tvMessageFromAI.setText(searchByAIBean.getMessage());
                    itemSearchByAiNormalViewHolder.tvMessageFromUser.setVisibility(View.GONE);
                } else {
                    itemSearchByAiNormalViewHolder.imHead.setVisibility(View.INVISIBLE);
                    itemSearchByAiNormalViewHolder.tvMessageFromAI.setVisibility(View.GONE);
                    itemSearchByAiNormalViewHolder.tvMessageFromUser.setVisibility(View.VISIBLE);
                    itemSearchByAiNormalViewHolder.tvMessageFromUser.setText(searchByAIBean.getMessage());
                }
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

        public ItemSearchByAIEveryoneISWatchingViewHolder(View itemView) {
            super(itemView);
        }
    }

    /**
     * 类型：MESSAGE_TYPE_GUESS_WHAT_YOU_LIKE
     */
    public class ItemSearchByAIGuessWhatYouLikeViewHolder extends BaseViewHolder {

        public ItemSearchByAIGuessWhatYouLikeViewHolder(View itemView) {
            super(itemView);
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
