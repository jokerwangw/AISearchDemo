package com.cmcc.cmvideo.search;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.TextView;

import com.cmcc.cmvideo.R;
import com.cmcc.cmvideo.base.MainThreadImpl;
import com.cmcc.cmvideo.base.ThreadExecutor;
import com.cmcc.cmvideo.search.aiui.AIUIService;
import com.cmcc.cmvideo.search.aiui.IAIUIService;
import com.cmcc.cmvideo.search.presenters.GetVoiceTextPresenter;
import com.cmcc.cmvideo.search.presenters.impl.GetVoiceTextPresenterImpl;
import com.cmcc.cmvideo.util.ToastUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Yyw on 2018/11/13.
 * Describe:
 */
public class GetVoiceTextActivity extends AppCompatActivity implements GetVoiceTextPresenter.View {
    @BindView(R.id.tv_show_voice_text)
    TextView tvShowVoiceText;

    private IAIUIService aiuiService = null;
    private GetVoiceTextPresenterImpl mGetVoiceTextPresenter = null;
    private boolean isBindService = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_voice_text);
        ButterKnife.bind(this);
        initView();
        initData();
    }

    private void initView() {
    }

    private void initData() {
        mGetVoiceTextPresenter = new GetVoiceTextPresenterImpl(
                ThreadExecutor.getInstance(),
                MainThreadImpl.getInstance(),
                this,
                this);
        bindService(new Intent(this, AIUIService.class), connection, Context.BIND_AUTO_CREATE | Context.BIND_IMPORTANT);
    }

    @OnClick(R.id.bt_start_recording)
    public void startRecording() {
        if (null != mGetVoiceTextPresenter) {
            mGetVoiceTextPresenter.startRecording();
        }
    }

    @OnClick(R.id.bt_stop_recording)
    public void stopRecording() {
        if (null != mGetVoiceTextPresenter) {
            mGetVoiceTextPresenter.stopRecording();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (null != mGetVoiceTextPresenter) {
            mGetVoiceTextPresenter.resume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (null != mGetVoiceTextPresenter) {
            mGetVoiceTextPresenter.pause();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (null != mGetVoiceTextPresenter) {
            mGetVoiceTextPresenter.stop();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isBindService && null != connection) {
            unbindService(connection);
        }
        isBindService = false;
        if (null != mGetVoiceTextPresenter) {
            mGetVoiceTextPresenter.destroy();
        }
    }

    /**
     * 初始化conn
     */
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            if (null == service) {
                return;
            }
            isBindService = true;
            aiuiService = (IAIUIService) service;
            aiuiService.setIsPlayAIVoice(false);
            if (null != mGetVoiceTextPresenter) {
                mGetVoiceTextPresenter.setAIUIService(aiuiService);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };


    @Override
    public void showVoiceText(boolean isError, String voiceText) {
        if (isError || TextUtils.isEmpty(voiceText)) {
            ToastUtil.showShort(this, voiceText);
        } else {
            tvShowVoiceText.setText(voiceText);
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
