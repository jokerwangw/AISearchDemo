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
import android.support.v7.widget.LinearLayoutManager;
import android.widget.TextView;
import android.widget.Toast;

import com.cmcc.cmvideo.R;
import com.cmcc.cmvideo.base.MainThreadImpl;
import com.cmcc.cmvideo.base.ThreadExecutor;
import com.cmcc.cmvideo.search.adapter.LookMoreAdapter;
import com.cmcc.cmvideo.search.aiui.AIUIService;
import com.cmcc.cmvideo.search.aiui.IAIUIService;
import com.cmcc.cmvideo.search.aiui.bean.TppData;
import com.cmcc.cmvideo.search.presenters.LookMorePresenter;
import com.cmcc.cmvideo.search.presenters.impl.LookMorePresenterImpl;
import com.cmcc.cmvideo.util.ToastUtil;
import com.google.gson.Gson;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

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
    @BindView(R.id.look_more_recyclerView)
    XRecyclerView mLookMoreRecyclerView;
    @BindView(R.id.tv_title)
    TextView titleTv;
    public static final String KEY_MORE_DATE = "more_data";
    public static final String KEY_TITLE = "more_data_title";
    private Context mContext;
    private LookMorePresenterImpl lookMorePresenter;
    private LookMoreAdapter mLookMoreAdapter;
    private List<TppData.DetailsListBean> detailsList;
    private IAIUIService aiuiService;
    private Gson gson;
    private boolean isBind = false;
    private boolean isLoadMore = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        isLoadMore = true;
        setContentView(R.layout.activity_look_more);
        ButterKnife.bind(this);

        initCustomView();
        initData();
    }

    private void initCustomView() {
        titleTv.setText(getIntent().getStringExtra(KEY_TITLE));

        mLookMoreAdapter = new LookMoreAdapter(mContext, this);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mLookMoreRecyclerView.setLayoutManager(layoutManager);
        mLookMoreRecyclerView.setHasFixedSize(true);
        mLookMoreRecyclerView.setPullRefreshEnabled(false);
        mLookMoreRecyclerView.setLoadingMoreProgressStyle(ProgressStyle.Pacman);
        mLookMoreRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                //refresh data here
                lookMorePresenter.refresh();
            }

            @Override
            public void onLoadMore() {
                // load more data here
                if (isLoadMore) {
                    lookMorePresenter.loadMore();
                } else {
                    noMoreDataWithTip();
                }
            }
        });

        mLookMoreRecyclerView.setAdapter(mLookMoreAdapter);
    }

    private void initData() {
        gson = new Gson();
        bindService(new Intent(this, AIUIService.class), connection, Context.BIND_AUTO_CREATE | Context.BIND_IMPORTANT);
        detailsList = new ArrayList<>();
        lookMorePresenter = new LookMorePresenterImpl(
                ThreadExecutor.getInstance(),
                MainThreadImpl.getInstance(),
                this,
                this);
    }

    @Override
    public void loadComplate() {
        mLookMoreRecyclerView.loadMoreComplete();
    }

    @Override
    public void noMoreData() {
        mLookMoreRecyclerView.loadMoreComplete();
        mLookMoreRecyclerView.setFootViewText("", "");
        isLoadMore = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        ToastUtil.cancelToast();
        super.onStop();
    }

    @Override
    public void noMoreDataWithTip() {
        noMoreData();
        ToastUtil.show(this, "没有更多数据了哦", 1000);
    }

    /**
     * 显示数据
     */
    @Override
    public void showInitList(List<TppData.DetailsListBean> detailsListBeanArrayList) {
        if (null != detailsListBeanArrayList && null != mLookMoreAdapter) {
            detailsList = detailsListBeanArrayList;
            mLookMoreAdapter.bindData(detailsListBeanArrayList, true);
            mLookMoreAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onClickItemVideo(int position) {
        Toast.makeText(mContext, "查看更多==" + position + "===" + detailsList.get(position).name, Toast.LENGTH_SHORT).show();
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            if (null == service) {
                return;
            }
            aiuiService = (IAIUIService) service;
            aiuiService.setIsPlayAIVoice(true);
            String lockMoreData = getIntent().getStringExtra(KEY_MORE_DATE);
            lookMorePresenter.setAIUIService(aiuiService);
            lookMorePresenter.loadData(lockMoreData);
            isBind = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

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
        this.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ToastUtil.cancelToast();
        lookMorePresenter.destroy();
        if (null != aiuiService) {
        }
        if (isBind) {
            unbindService(connection);
        }
        isBind = false;
    }

    @Override
    public void exit() {
        finish();
    }
}
