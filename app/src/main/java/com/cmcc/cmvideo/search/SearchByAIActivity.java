package com.cmcc.cmvideo.search;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

import com.cmcc.cmvideo.R;
import com.cmcc.cmvideo.base.MainThreadImpl;
import com.cmcc.cmvideo.base.ThreadExecutor;
import com.cmcc.cmvideo.search.adapter.SearchByAIAdapter;
import com.cmcc.cmvideo.search.model.SearchByAIBean;
import com.cmcc.cmvideo.search.model.SearchByAIEventBean;
import com.cmcc.cmvideo.search.presenters.SearchByAIPresenter;
import com.cmcc.cmvideo.search.presenters.impl.SearchByAIPresenterImpl;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;

/**
 * Created by Yyw on 2018/5/21.
 * Describe:
 */

public class SearchByAIActivity extends AppCompatActivity implements SearchByAIPresenter.View {

    @BindView(R.id.search_by_ai_recyclerView)
    RecyclerView mSearchRecyclerView;
    @BindView(R.id.im_search_voice_input_ring)
    ImageView imSearchVoiceInputRing;
    @BindView(R.id.bt_search_voice_input)
    Button btSearchVoiceInput;
    private SearchByAIPresenterImpl mSearchByAIPresenter;
    private Context mContext;
    private SearchByAIAdapter mSearchByAIAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_search_by_ai);
        ButterKnife.bind(this);
        initData();
    }

    protected void initData() {
        mSearchByAIPresenter = new SearchByAIPresenterImpl(
                ThreadExecutor.getInstance(),
                MainThreadImpl.getInstance(),
                this,
                this);

        initCustomView();
        setViewAnimation(true);
        mSearchByAIPresenter.initListSearchItem();
    }

    private void initCustomView() {
        mSearchByAIAdapter = new SearchByAIAdapter(mContext);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mSearchRecyclerView.setHasFixedSize(true);
        mSearchRecyclerView.setLayoutManager(layoutManager);
        mSearchRecyclerView.setAdapter(mSearchByAIAdapter);
    }

    /**
     * 标题栏关闭事件
     */
    @OnClick(R.id.bt_title_close)
    public void clickTitleClose() {
        finish();
    }

    /**
     * 标题栏右侧按钮
     */
    @OnClick(R.id.bt_title_voice)
    public void clickTitleVoice() {
    }

    /**
     * 点击事件
     */
    @OnClick(R.id.bt_search_voice_input)
    public void clickSearchInputVoice() {
        setViewAnimation(true);
        mSearchByAIPresenter.updateUserRequestListItem();
    }

    /**
     * 长按事件
     */
    @OnLongClick(R.id.bt_search_voice_input)
    public boolean longClickSearchInputVoice() {
        setViewAnimation(false);
        mSearchByAIPresenter.updateUserRequestListItem();
        return true;
    }

    /**
     * 显示AI初始化数据
     *
     * @param searchByAIBeanList
     */
    @Override
    public void showInitList(List<SearchByAIBean> searchByAIBeanList) {
        setAdapterData(true, searchByAIBeanList);
    }

    /**
     * 更新list数据
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(SearchByAIEventBean event) {
        if (null != event) {
            setAdapterData(false, event.getSearchByAIBeanList());
        }
    }

    /**
     * 设置适配器
     *
     * @param isInit
     * @param searchByAIBeanList
     */
    private void setAdapterData(boolean isInit, List<SearchByAIBean> searchByAIBeanList) {
        if (null != searchByAIBeanList && null != mSearchByAIAdapter) {
            if (isInit) {
                mSearchByAIAdapter.bindData(searchByAIBeanList, true);
            } else {
                mSearchByAIAdapter.addItems(searchByAIBeanList);
            }
            mSearchRecyclerView.scrollToPosition(mSearchByAIAdapter.getItemCount() - 1);
        }
    }

    /**
     * 给空间设置动画
     */
    private void setViewAnimation(boolean isClick) {
        closeViewAnimation();
        Animation animationRotate = AnimationUtils.loadAnimation(this, R.anim.search_by_ai_ring_rotate);
        Animation animationAlpha = AnimationUtils.loadAnimation(this, R.anim.search_by_ai_voice_bt_alpha);
        if (isClick) {
            imSearchVoiceInputRing.setVisibility(View.GONE);
            btSearchVoiceInput.startAnimation(animationAlpha);
        } else {
            imSearchVoiceInputRing.setVisibility(View.VISIBLE);
            imSearchVoiceInputRing.startAnimation(animationRotate);
            btSearchVoiceInput.startAnimation(animationAlpha);
        }
    }

    /**
     * 关闭动画
     */
    private void closeViewAnimation() {
        imSearchVoiceInputRing.clearAnimation();
        btSearchVoiceInput.clearAnimation();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
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
}
