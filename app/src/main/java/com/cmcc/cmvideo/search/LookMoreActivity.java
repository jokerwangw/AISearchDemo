package com.cmcc.cmvideo.search;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;
import android.widget.Toast;

import com.cmcc.cmvideo.R;
import com.cmcc.cmvideo.base.MainThreadImpl;
import com.cmcc.cmvideo.base.ThreadExecutor;
import com.cmcc.cmvideo.search.adapter.LookMoreAdapter;
import com.cmcc.cmvideo.search.aiui.AIUIService;
import com.cmcc.cmvideo.search.aiui.IAIUIService;
import com.cmcc.cmvideo.search.aiui.Logger;
import com.cmcc.cmvideo.search.aiui.bean.TppData;
import com.cmcc.cmvideo.search.presenters.LookMorePresenter;
import com.cmcc.cmvideo.search.presenters.impl.LookMorePresenterImpl;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Yyw on 2018/6/11.
 * Describe:
 */


public class LookMoreActivity extends AppCompatActivity implements LookMorePresenter.View, LookMoreAdapter.OnLookMoreItemClick {
    public static final String KEY_MORE_DATE = "more_data";
    public static final String KEY_TITLE = "more_data_title";
    @BindView(R.id.look_more_recyclerView)
    RecyclerView mLookMoreRecyclerView;
    @BindView(R.id.tv_title)
    TextView titleTv;
    private Context mContext;
    private LookMorePresenterImpl lookMorePresenter;
    private LookMoreAdapter mLookMoreAdapter;
    private IAIUIService aiuiService;
    private List<TppData.DetailsListBean> detailsList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        detailsList = new ArrayList<>();
        setContentView(R.layout.activity_look_more);
        ButterKnife.bind(this);
        initData();
    }

    private void initData() {
        lookMorePresenter = new LookMorePresenterImpl(
                ThreadExecutor.getInstance(),
                MainThreadImpl.getInstance(),
                this,
                this);
        initCustomView();
        lookMorePresenter.setDetailsJson(getIntent().getStringExtra(KEY_MORE_DATE));
    }

    private void initCustomView() {
        mLookMoreAdapter = new LookMoreAdapter(mContext, this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, 3);
        mLookMoreRecyclerView.setHasFixedSize(true);
        mLookMoreRecyclerView.setLayoutManager(gridLayoutManager);
        mLookMoreRecyclerView.setAdapter(mLookMoreAdapter);
        titleTv.setText(getIntent().getStringExtra(KEY_TITLE));
    }

    @Override
    public void showProgress() {
    }

    @Override
    public void hideProgress() {
    }

    @Override
    public void showError(String message) {
    }

    @OnClick(R.id.bt_back)
    public void clickBackClose() {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        lookMorePresenter.destroy();
    }

    /**
     * 显示数据
     */
    @Override
    public void showInitList(List<TppData.DetailsListBean> detailsListBeanArrayList) {
        if (null != detailsListBeanArrayList && null != mLookMoreAdapter) {
            detailsList = detailsListBeanArrayList;
            mLookMoreAdapter.bindData(detailsListBeanArrayList, true);
        }
    }

    @Override
    public void onClickItemVideo(int position) {
        Toast.makeText(mContext, "查看更多==" + position + "===" + detailsList.get(position).name, Toast.LENGTH_SHORT).show();
    }
}
