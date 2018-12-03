package com.cmcc.cmvideo.search.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cmcc.cmvideo.R;
import com.cmcc.cmvideo.base.BaseRecyclerAdapter;
import com.cmcc.cmvideo.foundation.fresco.MGSimpleDraweeView;
import com.cmcc.cmvideo.search.aiui.bean.TppData;
import com.cmcc.cmvideo.search.interactors.ItemSportsVideoClickListener;
import com.cmcc.cmvideo.search.model.SearchByAIBean;

import java.io.File;
import java.util.Date;

/**
 * Created by Yyw on 2018/11/26.
 * Describe:
 */
public class SportsVideoAdapter extends BaseRecyclerAdapter<TppData.MatchBean.MatchListBean.MatchEventInfoBean> {
    private Context mContext;
    private ItemSportsVideoClickListener mItemSportsVideoClickListener;

    public SportsVideoAdapter(Context ctx, ItemSportsVideoClickListener itemSportsVideoClickListener) {
        super(ctx);
        this.mContext = ctx;
        this.mItemSportsVideoClickListener = itemSportsVideoClickListener;
    }

    @Override
    public void onBindHoder(RecyclerView.ViewHolder holder, TppData.MatchBean.MatchListBean.MatchEventInfoBean matchEventInfoBean, int position) {
        if (null != holder && null != matchEventInfoBean) {
            ItemSportsVideoViewHolder itemSportsVideoViewHolder = (ItemSportsVideoViewHolder) holder;

            itemSportsVideoViewHolder.tvMatchStartTime.setText(matchEventInfoBean.matchStartTime);
            itemSportsVideoViewHolder.tvStageRoundName.setText(matchEventInfoBean.stageRoundName);

            itemSportsVideoViewHolder.imTeamBadgeOne.setImageURI(matchEventInfoBean.confrontTeamOneimage);
            itemSportsVideoViewHolder.tvTeamNameOne.setText(matchEventInfoBean.confrontTeamOnename);

            itemSportsVideoViewHolder.imTeamBadgeTwo.setImageURI(matchEventInfoBean.confrontTeamTwoimage);
            itemSportsVideoViewHolder.tvTeamNameTwo.setText(matchEventInfoBean.confrontTeamTwoname);

            if (!TextUtils.isEmpty(matchEventInfoBean.startTime) && !TextUtils.isEmpty(matchEventInfoBean.endTime)) {
                if (new Date().getTime() < Long.valueOf(matchEventInfoBean.startTime)) {
                    //预约
                    itemSportsVideoViewHolder.tvTeamScoreOne.setVisibility(View.GONE);
                    itemSportsVideoViewHolder.imTeamScoreOne.setVisibility(View.VISIBLE);
                    itemSportsVideoViewHolder.tvTeamScoreTwo.setVisibility(View.GONE);
                    itemSportsVideoViewHolder.imTeamScoreTwo.setVisibility(View.VISIBLE);
                    itemSportsVideoViewHolder.imTeamScoreOne.setImageURI(Uri.parse("res://" + mContext.getPackageName() + File.separator + R.mipmap.icon_team_score));
                    itemSportsVideoViewHolder.imTeamScoreTwo.setImageURI(Uri.parse("res://" + mContext.getPackageName() + File.separator + R.mipmap.icon_team_score));
                    itemSportsVideoViewHolder.imMatchState.setImageURI(Uri.parse("res://" + mContext.getPackageName() + File.separator + R.mipmap.icon_bespeak));
                } else if (new Date().getTime() >= Long.valueOf(matchEventInfoBean.endTime) && new Date().getTime() < Long.valueOf(matchEventInfoBean.endTime)) {
                    //直播中
                    itemSportsVideoViewHolder.tvTeamScoreOne.setVisibility(View.VISIBLE);
                    itemSportsVideoViewHolder.imTeamScoreOne.setVisibility(View.GONE);
                    itemSportsVideoViewHolder.tvTeamScoreTwo.setVisibility(View.VISIBLE);
                    itemSportsVideoViewHolder.imTeamScoreTwo.setVisibility(View.GONE);
                    itemSportsVideoViewHolder.tvTeamScoreOne.setText(matchEventInfoBean.confrontTeamOnescore);
                    itemSportsVideoViewHolder.tvTeamScoreTwo.setText(matchEventInfoBean.confrontTeamTwoscore);
                    itemSportsVideoViewHolder.imMatchState.setImageURI(Uri.parse("res://" + mContext.getPackageName() + File.separator + R.mipmap.icon_living));
                } else {
                    //回看
                    itemSportsVideoViewHolder.tvTeamScoreOne.setVisibility(View.VISIBLE);
                    itemSportsVideoViewHolder.imTeamScoreOne.setVisibility(View.GONE);
                    itemSportsVideoViewHolder.tvTeamScoreTwo.setVisibility(View.VISIBLE);
                    itemSportsVideoViewHolder.imTeamScoreTwo.setVisibility(View.GONE);
                    itemSportsVideoViewHolder.tvTeamScoreOne.setText(matchEventInfoBean.confrontTeamOnescore);
                    itemSportsVideoViewHolder.tvTeamScoreTwo.setText(matchEventInfoBean.confrontTeamTwoscore);
                    itemSportsVideoViewHolder.imMatchState.setImageURI(Uri.parse("res://" + mContext.getPackageName() + File.separator + R.mipmap.icon_look_back));
                }
            }
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View view = layoutInflater.inflate(R.layout.item_sports_video, null);
        return new ItemSportsVideoViewHolder(view);
    }

    private class ItemSportsVideoViewHolder extends BaseViewHolder {

        private TextView tvMatchStartTime;
        private TextView tvStageRoundName;
        private MGSimpleDraweeView imTeamBadgeOne;
        private TextView tvTeamNameOne;
        private TextView tvTeamScoreOne;
        private MGSimpleDraweeView imTeamScoreOne;
        private MGSimpleDraweeView imTeamBadgeTwo;
        private TextView tvTeamNameTwo;
        private TextView tvTeamScoreTwo;
        private MGSimpleDraweeView imTeamScoreTwo;
        private MGSimpleDraweeView imMatchState;

        private ItemSportsVideoViewHolder(View itemView) {
            super(itemView);
            tvMatchStartTime = (TextView) itemView.findViewById(R.id.tv_match_start_time);
            tvStageRoundName = (TextView) itemView.findViewById(R.id.tv_stage_round_name);
            imTeamBadgeOne = (MGSimpleDraweeView) itemView.findViewById(R.id.im_team_badge_one);
            tvTeamNameOne = (TextView) itemView.findViewById(R.id.tv_team_name_one);
            tvTeamScoreOne = (TextView) itemView.findViewById(R.id.tv_team_score_one);
            imTeamScoreOne = (MGSimpleDraweeView) itemView.findViewById(R.id.im_team_score_one);
            imTeamBadgeTwo = (MGSimpleDraweeView) itemView.findViewById(R.id.im_team_badge_two);
            tvTeamNameTwo = (TextView) itemView.findViewById(R.id.tv_team_name_two);
            tvTeamScoreTwo = (TextView) itemView.findViewById(R.id.tv_team_score_two);
            imTeamScoreTwo = (MGSimpleDraweeView) itemView.findViewById(R.id.im_team_score_two);
            imMatchState = (MGSimpleDraweeView) itemView.findViewById(R.id.im_match_state);
        }
    }
}
