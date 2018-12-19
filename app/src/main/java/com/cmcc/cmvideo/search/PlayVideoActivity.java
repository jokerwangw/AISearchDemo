package com.cmcc.cmvideo.search;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.cmcc.cmvideo.MainActivity;
import com.cmcc.cmvideo.R;
import com.cmcc.cmvideo.base.MainThreadImpl;
import com.cmcc.cmvideo.base.ThreadExecutor;
import com.cmcc.cmvideo.search.aiui.bean.ControlEventBean;
import com.cmcc.cmvideo.search.model.SearchByAIEventBean;
import com.cmcc.cmvideo.search.presenters.PlayVideoPresenter;
import com.cmcc.cmvideo.search.presenters.impl.PlayVideoPresenterImpl;
import com.cmcc.cmvideo.util.AiuiConstants;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Yyw on 2018/6/23.
 * Describe:模拟播放器界面
 */

public class PlayVideoActivity extends AppCompatActivity implements PlayVideoPresenter.View {
    @BindView(R.id.tv_response_control)
    TextView tvResponseControl;
    private PlayVideoActivity mContext;
    private PlayVideoPresenter playVideoPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_play_video);
        ButterKnife.bind(this);
        initData();
    }

    private void initData() {
        playVideoPresenter = new PlayVideoPresenterImpl(
                ThreadExecutor.getInstance(),
                MainThreadImpl.getInstance(),
                this,
                this
        );
    }

    /**
     * 根据语音进行相关操作
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ControlEventBean event) {
        if (null != event) {
            switch (event.getControlType()) {
                case AiuiConstants.VDO_PAUSE:
                    //暂停
                    tvResponseControl.setText(getResources().getString(R.string.vdo_pause));
                    break;
                case AiuiConstants.VDO_PLAY:
                    //播放
                    tvResponseControl.setText(getResources().getString(R.string.vdo_play));
                    break;
                case AiuiConstants.VDO_NEXT:
                    //下一集
                    tvResponseControl.setText(getResources().getString(R.string.vdo_next));
                    break;
                case AiuiConstants.VDO_PREVIOUS:
                    //上一集
                    tvResponseControl.setText(getResources().getString(R.string.vdo_previous));
                    break;
                case AiuiConstants.VDO_OPEN:
                    //打开语音助手
                    break;
                case AiuiConstants.VDO_CLOSE:
                    //关闭语音助手
                    break;
                case AiuiConstants.VDO_SCREEN:
                    //投屏播放
                    tvResponseControl.setText(getResources().getString(R.string.vdo_screen));
                    break;
                case AiuiConstants.VDO_FASTWORD:
                    //快进
                    tvResponseControl.setText(getResources().getString(R.string.vdo_fastword));
                    break;
                case AiuiConstants.VDO_BACKWORD:
                    //快退
                    tvResponseControl.setText(getResources().getString(R.string.vdo_backword));
                    break;
                case AiuiConstants.VDO_FASTWORD_TO:
                    //快进（快退）至
                    tvResponseControl.setText(getResources().getString(R.string.vdo_fastword_to) + event.getHour() + ":" + event.getMinute() + ":" + event.getSecond());
                    break;
                case AiuiConstants.VDO_CHANGE:
                    //换一集
                    tvResponseControl.setText(getResources().getString(R.string.vdo_change));
                    break;
                case AiuiConstants.VDO_WHICH_EPISODE:
                    //第几集
                    tvResponseControl.setText(getResources().getString(R.string.vdo_episode));
                    break;
                default:
                    break;
            }
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
}
