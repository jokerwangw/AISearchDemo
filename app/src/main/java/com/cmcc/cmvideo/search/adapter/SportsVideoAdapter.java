package com.cmcc.cmvideo.search.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cmcc.cmvideo.R;
import com.cmcc.cmvideo.base.BaseRecyclerAdapter;
import com.cmcc.cmvideo.search.interactors.ItemSportsVideoClickListener;
import com.cmcc.cmvideo.search.model.SearchByAIBean;

/**
 * Created by Yyw on 2018/11/26.
 * Describe:
 */
public class SportsVideoAdapter extends BaseRecyclerAdapter<SearchByAIBean> {
    private Context mContext;
    private ItemSportsVideoClickListener mItemSportsVideoClickListener;

    public SportsVideoAdapter(Context ctx, ItemSportsVideoClickListener itemSportsVideoClickListener) {
        super(ctx);
        this.mContext = ctx;
        this.mItemSportsVideoClickListener = itemSportsVideoClickListener;
    }

    @Override
    public int getItemCount() {
        return 10;
    }

    @Override
    public void onBindHoder(RecyclerView.ViewHolder holder, SearchByAIBean searchByAIBean, int position) {
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View view = layoutInflater.inflate(R.layout.item_sports_video, null);
        return new ItemSportsVideoViewHolder(view);
    }

    private class ItemSportsVideoViewHolder extends BaseViewHolder {
        private ItemSportsVideoViewHolder(View itemView) {
            super(itemView);
        }
    }
}
