package com.cmcc.cmvideo.search;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cmcc.cmvideo.R;
import com.cmcc.cmvideo.base.MainThreadImpl;
import com.cmcc.cmvideo.base.ThreadExecutor;
import com.cmcc.cmvideo.search.adapter.SearchByAIAdapter;
import com.cmcc.cmvideo.search.aiui.AIUIService;
import com.cmcc.cmvideo.search.aiui.FuncAdapter;
import com.cmcc.cmvideo.search.aiui.IAIUIService;
import com.cmcc.cmvideo.search.aiui.Logger;
import com.cmcc.cmvideo.search.model.SearchByAIBean;
import com.cmcc.cmvideo.search.model.SearchByAIEventBean;
import com.cmcc.cmvideo.search.model.SearchByAIRefreshUIEventBean;
import com.cmcc.cmvideo.search.presenters.SearchByAIPresenter;
import com.cmcc.cmvideo.search.presenters.impl.SearchByAIPresenterImpl;
import com.cmcc.cmvideo.weight.VoiceLineView;
import com.iflytek.aiui.AIUIConstant;
import com.iflytek.aiui.AIUIEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Yyw on 2018/5/21.
 * Describe:
 */

public class SearchByAIActivity extends AppCompatActivity implements SearchByAIPresenter.View {

    @BindView(R.id.search_by_ai_recyclerView)
    RecyclerView mSearchRecyclerView;
    @BindView(R.id.rl_search_voice_input_ring)
    RelativeLayout rlSearchVoiceInputRing;
    @BindView(R.id.rl_search_voice_input)
    RelativeLayout rlSearchVoiceInput;
    @BindView(R.id.im_search_voice_input_ring)
    ImageView imSearchVoiceInputRing;
    @BindView(R.id.bt_search_voice_input)
    Button btSearchVoiceInput;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_cancel_search)
    TextView tvCancelSearch;
    @BindView(R.id.voicLine)
    VoiceLineView mVoiceLineView;
    @BindView(R.id.im_release_finger)
    ImageView imReleaseFinger;
    @BindView(R.id.tv_slide_cancel_search)
    TextView tvSlideCancelSearch;
    @BindView(R.id.tv_release_finger_cancel_search)
    TextView tvReleaseFingerCancelSearch;
    private final String TAG = "SearchByAIActivity";
    private SearchByAIPresenterImpl mSearchByAIPresenter;
    private Context mContext;
    private SearchByAIAdapter mSearchByAIAdapter;
    private IAIUIService aiuiService;
    private long downTime = 0;
    private long upTime = 0;
    private float downY = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_search_by_ai);
        ButterKnife.bind(this);
        initData();
    }

    private void initData() {
        mSearchByAIPresenter = new SearchByAIPresenterImpl(
                ThreadExecutor.getInstance(),
                MainThreadImpl.getInstance(),
                this,
                this);
        bindService(new Intent(this, AIUIService.class), connection, Context.BIND_AUTO_CREATE | Context.BIND_IMPORTANT);
        initCustomView();
        mSearchByAIPresenter.initListSearchItem();

    }

    private void initCustomView() {
        mSearchByAIAdapter = new SearchByAIAdapter(mContext);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mSearchRecyclerView.setHasFixedSize(true);
        mSearchRecyclerView.setLayoutManager(layoutManager);
        mSearchRecyclerView.setAdapter(mSearchByAIAdapter);
        btSearchVoiceInput.setOnTouchListener(onTouchListener);
    }

    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    downY = event.getY();
                    downTime = System.currentTimeMillis();
                    startSearch();
                    break;
                case MotionEvent.ACTION_MOVE:
                    fingerStartMove(downY, event.getY());
                    break;
                case MotionEvent.ACTION_UP:
                    upTime = System.currentTimeMillis();
                    long clickTime = upTime - downTime;
                    checkClickType(clickTime, downY - event.getY());
                    break;
                default:
                    break;
            }
            return true;
        }
    };

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            aiuiService = (IAIUIService) service;
            mSearchByAIPresenter.setAIUIService(aiuiService);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    /**
     * 获取音频播放焦点
     */
    @Override
    public void requestAudioFocus() {
        FuncAdapter.Lock(this, null);
    }

    /**
     * 释放音频播放焦点
     */
    @Override
    public void abandonAudioFocus() {
        FuncAdapter.UnLock(this, null);
    }

    /**
     * 区分用户搜索的点击事件类型
     *
     * @param clickTime
     */
    private void checkClickType(long clickTime, float moveValue) {
        if (clickTime <= 1000) {
            //点击事件
            closeViewAnimation();
            tvCancelSearch.setVisibility(View.VISIBLE);
            rlSearchVoiceInputRing.setVisibility(View.GONE);
            tvSlideCancelSearch.setVisibility(View.GONE);
            rlSearchVoiceInput.setVisibility(View.GONE);
            setViewAnimation(true);
        } else {
            //长按事件
            if (moveValue >= 50) {
                mSearchByAIPresenter.cancelRecordAudio();
            }
            //长按事件
            closeSearch();

        }
    }

    /**
     * 长按状态下手指滑动得操作
     *
     * @param downY
     * @param moveY
     */
    private void fingerStartMove(float downY, float moveY) {
        float moveValue = downY - moveY;
        if (moveValue >= 50) {
            tvSlideCancelSearch.setVisibility(View.GONE);
            imReleaseFinger.setVisibility(View.VISIBLE);
            tvReleaseFingerCancelSearch.setVisibility(View.VISIBLE);
        } else {
            tvSlideCancelSearch.setVisibility(View.VISIBLE);
            imReleaseFinger.setVisibility(View.GONE);
            tvReleaseFingerCancelSearch.setVisibility(View.GONE);
        }
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
     * 取消搜索
     */
    @OnClick(R.id.tv_cancel_search)
    public void clickCancelSearch() {
        closeSearch();
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
     * 根据相关事件更新UI
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageRefreshUIEvent(SearchByAIRefreshUIEventBean event) {
        if (null != event) {
            AIUIEvent aiuiEvent = event.getAiuiEvent();
            switch (aiuiEvent.eventType) {
                case AIUIConstant.EVENT_WAKEUP:
                    //TODO AIUI 被唤醒
                    break;
                case AIUIConstant.EVENT_SLEEP:
                    //TODO AIUI 进入休眠 ，可以更新UI
                    closeSearch();
                    break;
                case AIUIConstant.EVENT_ERROR:
                    if (aiuiEvent.arg1 == 10120) {
                        // TODO 网络有点问题 ，超时
                        closeSearch();
                    }
                    break;
                case AIUIConstant.EVENT_START_RECORD:
                    break;
                case AIUIConstant.EVENT_STOP_RECORD:
                    break;
                case AIUIConstant.EVENT_VAD:
                    if (aiuiEvent.arg1 == 1) {
                        //VAD事件当检测到输入音频的前端点后，会抛出该事件，用arg1标识前后端点或者音量信息:0(前端点)、1(音量)、2(后端点)、3（前端点超时）。
                        //当arg1取值为1时，arg2为音量大小。
                        updateVoiceAnimation(aiuiEvent.arg2);
                    }
                    break;
            }
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
     * 开始搜索之后处理相关操作
     */
    private void startSearch() {
        if (aiuiService != null) {
            aiuiService.startRecordAudio();
            tvTitle.setText(getResources().getString(R.string.listening));
            rlSearchVoiceInputRing.setVisibility(View.VISIBLE);
            tvSlideCancelSearch.setVisibility(View.VISIBLE);
            setViewAnimation(false);
        }
    }

    /**
     * 关闭搜索之后处理相关操作
     */
    private void closeSearch() {
        if (aiuiService != null) {
            aiuiService.stopRecordAudio();
        }
        tvCancelSearch.setVisibility(View.GONE);
        rlSearchVoiceInputRing.setVisibility(View.GONE);
        tvSlideCancelSearch.setVisibility(View.GONE);
        imReleaseFinger.setVisibility(View.GONE);
        tvReleaseFingerCancelSearch.setVisibility(View.GONE);
        rlSearchVoiceInput.setVisibility(View.VISIBLE);
        tvTitle.setText(getResources().getString(R.string.cmvideo));
        closeViewAnimation();
    }

    /**
     * 给控件设置动画
     */
    private void setViewAnimation(boolean isClick) {
        closeViewAnimation();
        Animation animationRotate = AnimationUtils.loadAnimation(this, R.anim.search_by_ai_ring_rotate);
        Animation animationAlpha = AnimationUtils.loadAnimation(this, R.anim.search_by_ai_voice_bt_alpha);
        if (isClick) {
            btSearchVoiceInput.startAnimation(animationAlpha);
        } else {
            imSearchVoiceInputRing.startAnimation(animationRotate);
            btSearchVoiceInput.startAnimation(animationAlpha);
        }
    }

    /**
     * 关闭动画
     */
    private void closeViewAnimation() {
        if (null != imSearchVoiceInputRing) {
            imSearchVoiceInputRing.clearAnimation();
            imSearchVoiceInputRing.invalidate();
        }
        if (null != btSearchVoiceInput) {
            btSearchVoiceInput.clearAnimation();
            btSearchVoiceInput.invalidate();
        }
    }

    /**
     * 更新VoiceLineView
     *
     * @param arg2
     */
    private void updateVoiceAnimation(int arg2) {
        mVoiceLineView.setVolume(arg2 + 120);
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
        unbindService(connection);
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
