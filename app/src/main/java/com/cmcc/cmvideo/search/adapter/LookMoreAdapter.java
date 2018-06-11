package com.cmcc.cmvideo.search.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cmcc.cmvideo.R;
import com.cmcc.cmvideo.base.BaseRecyclerAdapter;
import com.cmcc.cmvideo.foundation.fresco.MGSimpleDraweeView;
import com.cmcc.cmvideo.search.model.SearchByAIBean;

/**
 * Created by Yyw on 2018/6/11.
 * Describe:
 */

public class LookMoreAdapter extends BaseRecyclerAdapter<Object> {
    private Context mContext;

    public LookMoreAdapter(Context ctx) {
        super(ctx);
        this.mContext = ctx;
    }

    @Override
    public void onBindHoder(RecyclerView.ViewHolder holder, Object o, int position) {
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_look_more, null);
        return new ItemViewHolder(view);
    }

    public class ItemViewHolder extends BaseViewHolder {
        MGSimpleDraweeView itemImg;
        TextView itemWatchNum;
        TextView itemName;

        public ItemViewHolder(View itemView) {
            super(itemView);
            itemImg = (MGSimpleDraweeView) itemView.findViewById(R.id.item_img);
            itemWatchNum = (TextView) itemView.findViewById(R.id.item_watch_num);
            itemName = (TextView) itemView.findViewById(R.id.item_name);
        }
    }
}