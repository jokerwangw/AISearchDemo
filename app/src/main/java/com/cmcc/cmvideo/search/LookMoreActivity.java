package com.cmcc.cmvideo.search;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.cmcc.cmvideo.R;
import com.cmcc.cmvideo.base.MainThreadImpl;
import com.cmcc.cmvideo.base.ThreadExecutor;
import com.cmcc.cmvideo.search.adapter.LookMoreAdapter;
import com.cmcc.cmvideo.search.adapter.SearchByAIAdapter;
import com.cmcc.cmvideo.search.presenters.LookMorePresenter;
import com.cmcc.cmvideo.search.presenters.impl.LookMorePresenterImpl;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Yyw on 2018/6/11.
 * Describe:
 */

public class LookMoreActivity extends AppCompatActivity implements LookMorePresenter.View {
    @BindView(R.id.look_more_recyclerView)
    RecyclerView mLookMoreRecyclerView;
    private Context mContext;
    private LookMorePresenterImpl lookMorePresenter;
    private LookMoreAdapter mLookMoreAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
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
        lookMorePresenter.initListItem();
    }

    private void initCustomView() {
        mLookMoreAdapter = new LookMoreAdapter(mContext);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, 3,GridLayoutManager.HORIZONTAL,true);
        mLookMoreRecyclerView.setHasFixedSize(true);
        mLookMoreRecyclerView.setLayoutManager(gridLayoutManager);
        mLookMoreRecyclerView.setAdapter(mLookMoreAdapter);
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

    /**
     * 显示数据
     */
    @Override
    public void showInitList() {
        setAdapterData();
    }

    private void setAdapterData() {

    }
}
