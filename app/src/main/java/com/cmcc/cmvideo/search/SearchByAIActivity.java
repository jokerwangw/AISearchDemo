package com.cmcc.cmvideo.search;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cmcc.cmvideo.R;
import com.cmcc.cmvideo.base.MainThreadImpl;
import com.cmcc.cmvideo.base.ThreadExecutor;
import com.cmcc.cmvideo.search.adapter.SearchByAIAdapter;
import com.cmcc.cmvideo.search.aiui.AIUIService;
import com.cmcc.cmvideo.search.aiui.IAIUIService;
import com.cmcc.cmvideo.search.aiui.bean.TppData;
import com.cmcc.cmvideo.search.interactors.ItemSearchByAIClickListener;
import com.cmcc.cmvideo.search.model.SearchByAIBean;
import com.cmcc.cmvideo.search.model.SearchByAIEventBean;
import com.cmcc.cmvideo.search.model.SearchByAIRefreshUIEventBean;
import com.cmcc.cmvideo.search.presenters.SearchByAIPresenter;
import com.cmcc.cmvideo.search.presenters.impl.SearchByAIPresenterImpl;
import com.cmcc.cmvideo.util.AIUIUtils;
import com.cmcc.cmvideo.widget.VoiceLineView;
import com.cmcc.cmvideo.widget.WrapContentLinearLayoutManager;
import com.iflytek.aiui.AIUIConstant;
import com.iflytek.aiui.AIUIEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.cmcc.cmvideo.util.AiuiConstants.MessageType.MESSAGE_TYPE_EVERYONE_IS_WATCHING;
import static com.cmcc.cmvideo.util.AiuiConstants.MessageType.MESSAGE_TYPE_GUESS_WHAT_YOU_LIKE;
import static com.cmcc.cmvideo.util.AiuiConstants.MessageType.MESSAGE_TYPE_GUESS_WHAT_YOU_LIKE_LIST_HORIZONTAL;
import static com.cmcc.cmvideo.util.AiuiConstants.MessageType.MESSAGE_TYPE_GUESS_WHAT_YOU_LIKE_LIST_VERTICAL;


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
    @BindView(R.id.v_spekaker)
    View vSpekaker;
    private final String TAG = "SearchByAIActivity";
    private int MAX_CLICK_TIME = 500;
    private int MIN_MOVE_VALUE = 50;
    private int MAX_CLICK_MOVE_VALUE = 10;
    private SearchByAIPresenter mSearchByAIPresenter;
    private Context mContext;
    private SearchByAIAdapter mSearchByAIAdapter;
    private IAIUIService aiuiService;
    private long downTime, upTime = 0;
    private float downY = 0;
    private boolean isOpenSpeaker = false;
    private AudioManager audioManager = null;
    private int currVolume = 0;
    private int mViewCacheSize = 100;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_search_by_ai);
        ButterKnife.bind(this);
        initView();
        initData();
    }

    private void initView() {
        mSearchByAIAdapter = new SearchByAIAdapter(mContext, itemSearchByAIClickListener);
        WrapContentLinearLayoutManager wrapContentLinearLayoutManager = new WrapContentLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mSearchRecyclerView.setHasFixedSize(true);
        mSearchRecyclerView.setItemViewCacheSize(mViewCacheSize);
        mSearchRecyclerView.setLayoutManager(wrapContentLinearLayoutManager);
        mSearchRecyclerView.setAdapter(mSearchByAIAdapter);
        btSearchVoiceInput.setOnTouchListener(onTouchListener);
    }

    private void initData() {
        mSearchByAIPresenter = new SearchByAIPresenterImpl(
                ThreadExecutor.getInstance(),
                MainThreadImpl.getInstance(),
                this,
                this);

        bindService(new Intent(this, AIUIService.class), connection, Context.BIND_AUTO_CREATE | Context.BIND_IMPORTANT);
        mSearchByAIPresenter.initListSearchItem();
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        if (null != audioManager) {
            isOpenSpeaker = audioManager.isSpeakerphoneOn();
            vSpekaker.setVisibility(isOpenSpeaker ? View.VISIBLE : View.GONE);
        }
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
                default:
                    break;
            }
        }
    }

    /**
     * 点击不同条目对应的点击事件
     */
    private ItemSearchByAIClickListener itemSearchByAIClickListener = new ItemSearchByAIClickListener() {
        @Override
        public void clickItemSearchByAICanAskAI(String recommendText) {
            if (aiuiService != null) {
                aiuiService.textUnderstander(recommendText);
            }
        }

        @Override
        public void clickItemSearchByAIAppointment() {
        }

        @Override
        public void clickItemSearchByAIEveryoneISWatching(boolean isClickMore, int position, String deailsJson, String titleText) {
            if (isClickMore) {
                Intent intent = new Intent(mContext, LookMoreActivity.class);
                intent.putExtra(LookMoreActivity.KEY_MORE_DATE, deailsJson);
                intent.putExtra(LookMoreActivity.KEY_TITLE, titleText);
                startActivity(intent);
            } else {
                int i = position + 1;
                Toast.makeText(mContext, "查看==" + i, Toast.LENGTH_SHORT).show();
                mSearchByAIPresenter.turnToPlayVideo(MESSAGE_TYPE_EVERYONE_IS_WATCHING, false, null, deailsJson, position);
            }
        }

        @Override
        public void clickItemSearchByAIIWantTOSee() {
        }

        @Override
        public void clickItemSearchByAIGuessWhatYouLike(boolean isChangeBt, TppData.DetailsListBean detailsListBean) {
            if (null != aiuiService && null != detailsListBean) {
                if (isChangeBt) {
                    aiuiService.textUnderstander("换一个");
                } else {
                    Toast.makeText(mContext, "查看==" + detailsListBean.name, Toast.LENGTH_SHORT).show();
                    mSearchByAIPresenter.turnToPlayVideo(MESSAGE_TYPE_GUESS_WHAT_YOU_LIKE, false, detailsListBean, null, -1);
                }
            }
        }

        @Override
        public void clickItemSearchByAIGuessWhatYouLikeListHorizontal(boolean isChangeBt, boolean isClickLookMore, TppData.DetailsListBean detailsListBean, int position) {
            if (null != aiuiService) {
                if (isChangeBt) {
                    aiuiService.textUnderstander("换一个");
                } else if (isClickLookMore) {
                    Toast.makeText(mContext, "查看更多", Toast.LENGTH_SHORT).show();
                    mSearchByAIPresenter.turnToPlayVideo(MESSAGE_TYPE_GUESS_WHAT_YOU_LIKE_LIST_HORIZONTAL, true, detailsListBean, null, position);
                } else {
                    int pos = position + 1;
                    Toast.makeText(mContext, "查看第" + pos + "集", Toast.LENGTH_SHORT).show();
                    mSearchByAIPresenter.turnToPlayVideo(MESSAGE_TYPE_GUESS_WHAT_YOU_LIKE_LIST_HORIZONTAL, false, detailsListBean, null, position);
                }
            }
        }

        @Override
        public void clickItemSearchByAIGuessWhatYouLikeListVertical(boolean isChangeBt, boolean isClickLookMore, TppData.DetailsListBean detailsListBean, int position) {
            if (null != aiuiService) {
                if (isChangeBt) {
                    aiuiService.textUnderstander("换一个");
                } else if (isClickLookMore) {
                    Toast.makeText(mContext, "查看更多", Toast.LENGTH_SHORT).show();
                    mSearchByAIPresenter.turnToPlayVideo(MESSAGE_TYPE_GUESS_WHAT_YOU_LIKE_LIST_VERTICAL, true, detailsListBean, null, position);
                } else {
                    int pos = position + 1;
                    Toast.makeText(mContext, "查看第" + pos + "条", Toast.LENGTH_SHORT).show();
                    mSearchByAIPresenter.turnToPlayVideo(MESSAGE_TYPE_GUESS_WHAT_YOU_LIKE_LIST_VERTICAL, false, detailsListBean, null, position);
                }
            }
        }

        @Override
        public void clickItemSearchByAITheLatestVideo() {
        }
    };

    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @SuppressLint("ClickableViewAccessibility")
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

    /**
     * 标题栏关闭事件
     */
    @OnClick(R.id.bt_title_close)
    public void clickTitleClose() {
        finish();
    }

    /**
     * 扬声器
     */
    @OnClick(R.id.bt_title_voice)
    public void clickOpenSpeaker() {
        try {
            if (isOpenSpeaker) {
                //关闭扬声器
                if (audioManager != null) {
                    AIUIUtils.setAudioMode(SearchByAIActivity.this, AudioManager.MODE_IN_COMMUNICATION);
                }
            } else {
                //打开扬声器
                if (audioManager != null) {
                    AIUIUtils.setAudioMode(SearchByAIActivity.this, AudioManager.MODE_NORMAL);
                }
            }
            isOpenSpeaker = audioManager.isSpeakerphoneOn();
            vSpekaker.setVisibility(isOpenSpeaker ? View.VISIBLE : View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
     * 设置适配器
     *
     * @param isInit
     * @param searchByAIBeanList
     */
    private void setAdapterData(boolean isInit, List<SearchByAIBean> searchByAIBeanList) {
        if (null != searchByAIBeanList && null != mSearchByAIAdapter) {
            if (isInit) {
                mSearchByAIAdapter.bindData(searchByAIBeanList, false);
            } else {
                mSearchByAIAdapter.addItems(searchByAIBeanList);
            }
            int position = mSearchByAIAdapter.getItemCount() - 1;
            if (position > 1 && position < mSearchByAIAdapter.getItemCount()) {
                mSearchRecyclerView.scrollToPosition(position);
            }
        }
    }

    /**
     * 区分用户搜索的点击事件类型
     *
     * @param clickTime
     */
    private void checkClickType(long clickTime, float moveValue) {
        if (clickTime <= MAX_CLICK_TIME && moveValue > -MAX_CLICK_MOVE_VALUE && moveValue < MAX_CLICK_MOVE_VALUE) {
            //点击事件
            closeViewAnimation();
            tvCancelSearch.setVisibility(View.VISIBLE);
            rlSearchVoiceInputRing.setVisibility(View.GONE);
            tvSlideCancelSearch.setVisibility(View.GONE);
            rlSearchVoiceInput.setVisibility(View.GONE);
            setViewAnimation(true);
        } else {
            //长按事件
            if (moveValue >= MIN_MOVE_VALUE) {
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

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            aiuiService = (IAIUIService) service;
            mSearchByAIPresenter.setAIUIService(aiuiService);
            mSearchByAIPresenter.analysisDefaultData(getIntent().getStringExtra("TPP_DATA"));
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (null != mSearchByAIPresenter) {
            mSearchByAIPresenter.resume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (null != mSearchByAIPresenter) {
            mSearchByAIPresenter.pause();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (null != mSearchByAIPresenter) {
            mSearchByAIPresenter.stop();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != connection) {
            unbindService(connection);
        }
        if (null != mSearchByAIPresenter) {
            mSearchByAIPresenter.destroy();
        }
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
