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
import static com.cmcc.cmvideo.utils.Constants.MESSAGE_TYPE_NORMAL;
import static com.cmcc.cmvideo.utils.Constants.MESSAGE_TYPE_OTHER;

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
            } else if (MESSAGE_TYPE_OTHER == searchByAIBean.getMessageType()) {
                return MESSAGE_TYPE_OTHER;
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
                return new ItemSearchByAiNormalViewHolder(view);
            } else if (MESSAGE_TYPE_OTHER == viewType) {
            }

        }
        return null;
    }

    @Override
    public void onBindHoder(RecyclerView.ViewHolder holder, SearchByAIBean searchByAIBean, int position) {
        if (null != holder && null != searchByAIBean) {
            if (holder instanceof ItemSearchByAiNormalViewHolder) {
                ItemSearchByAiNormalViewHolder itemSearchByAiNormalViewHolder = (ItemSearchByAiNormalViewHolder) holder;
                if (TextUtils.equals(searchByAIBean.getMessageFrom(), MESSAGE_FROM_AI)) {
                    if (0 == position) {
                        itemSearchByAiNormalViewHolder.imHead.setVisibility(View.VISIBLE);
                    } else {
                        itemSearchByAiNormalViewHolder.imHead.setVisibility(View.INVISIBLE);
                    }
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

    public class ItemSearchByAiNormalViewHolder extends BaseViewHolder {
        ImageView imHead;
        TextView tvMessageFromAI;
        TextView tvMessageFromUser;

        public ItemSearchByAiNormalViewHolder(View itemView) {
            super(itemView);
            imHead = (ImageView) itemView.findViewById(R.id.im_head);
            tvMessageFromAI = (TextView) itemView.findViewById(R.id.tv_message_from_ai);
            tvMessageFromUser = (TextView) itemView.findViewById(R.id.tv_message_from_user);
        }
    }
}
