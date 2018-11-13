package com.cmcc.cmvideo;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.cmcc.cmvideo.search.GetVoiceTextActivity;
import com.cmcc.cmvideo.search.PlayVideoActivity;
import com.cmcc.cmvideo.search.SearchByAIActivity;
import com.cmcc.cmvideo.search.aiui.AIUIService;
import com.cmcc.cmvideo.search.aiui.IAIUIService;
import com.cmcc.cmvideo.util.AppUtil;
import com.cmcc.cmvideo.util.ServiceUtils;
import com.cmcc.cmvideo.util.SharedPreferencesHelper;
import com.iflytek.aiui.AIUIConstant;
import com.iflytek.aiui.AIUIMessage;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.tv_current_version)
    TextView tvCurrentVersion;
    @BindView(R.id.turn_to_ai_search)
    Button btTurnToAISearch;
    @BindView(R.id.open_aiui_helper)
    ToggleButton btOpenAIHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        initView();
        initData();
    }

    private void initView() {
    }

    private void initData() {
        String versionName = AppUtil.getVersionName(this);
        if (!TextUtils.isEmpty(versionName)) {
            tvCurrentVersion.setVisibility(View.VISIBLE);
            tvCurrentVersion.setText("当前版本号:" + versionName);
        } else {
            tvCurrentVersion.setVisibility(View.GONE);
        }

        //开启智能语音耳机服务
        //Intent aiService = new Intent(this, AIUIService.class);
        //startService(aiService);
    }

    @OnClick(R.id.turn_to_ai_search)
    public void turnSearchByAI() {
        Intent intent = new Intent(MainActivity.this, SearchByAIActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.turn_to_player)
    public void turnToPlayer() {
        Intent intent = new Intent(MainActivity.this, PlayVideoActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.turn_to_get_voice_text)
    public void turnToGetVoiceText() {
        Intent intent = new Intent(MainActivity.this, GetVoiceTextActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
