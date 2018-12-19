package com.cmcc.cmvideo.search.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cmcc.cmvideo.R;
import com.cmcc.cmvideo.base.BaseRecyclerAdapter;
import com.cmcc.cmvideo.foundation.fresco.MGSimpleDraweeView;
import com.cmcc.cmvideo.search.aiui.bean.TppData;

import org.json.JSONObject;

import java.util.List;

import static com.cmcc.cmvideo.util.AiuiConstants.IMG_BASE_URL;


/**
 * Created by Yyw on 2018/6/11.
 * Describe:
 */

public class LookMoreAdapter extends BaseRecyclerAdapter<TppData.DetailsListBean> {
    private Context mContext;
    public OnLookMoreItemClick onLookMoreItemClick;

    public LookMoreAdapter(Context ctx, OnLookMoreItemClick onItemClick) {
        super(ctx);
        this.mContext = ctx;
        this.onLookMoreItemClick = onItemClick;
    }

    @Override
    public void onBindHoder(RecyclerView.ViewHolder holder, TppData.DetailsListBean detailsListBean, final int position) {
        if (null != holder && null != detailsListBean) {
            ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
            itemViewHolder.itemWatchNum.setVisibility(View.GONE);
            itemViewHolder.itemName.setText(detailsListBean.name);
            itemViewHolder.itemImg.setImageURI(getImageUrl(detailsListBean.image));
            itemViewHolder.itemContain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onLookMoreItemClick.onClickItemVideo(position);
                }
            });
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_look_more, null);
        return new ItemViewHolder(view);
    }

    public class ItemViewHolder extends BaseViewHolder {
        LinearLayout itemContain;
        MGSimpleDraweeView itemImg;
        TextView itemWatchNum;
        TextView itemName;

        public ItemViewHolder(View itemView) {
            super(itemView);
            itemContain = (LinearLayout) itemView.findViewById(R.id.item_contian);
            itemImg = (MGSimpleDraweeView) itemView.findViewById(R.id.item_img);
            itemWatchNum = (TextView) itemView.findViewById(R.id.item_watch_num);
            itemName = (TextView) itemView.findViewById(R.id.item_name);
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

    public interface OnLookMoreItemClick {
        void onClickItemVideo(int position);
    }
}
