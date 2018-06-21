package com.cmcc.cmvideo;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.cmcc.cmvideo.base.MainThread;
import com.cmcc.cmvideo.base.MainThreadImpl;
import com.cmcc.cmvideo.base.ThreadExecutor;
import com.cmcc.cmvideo.search.SearchByAIActivity;
import com.cmcc.cmvideo.search.aiui.AIUIService;
import com.cmcc.cmvideo.search.aiui.IAIUIService;
import com.cmcc.cmvideo.search.aiui.Logger;
import com.cmcc.cmvideo.search.aiui.bean.ControlEventBean;
import com.cmcc.cmvideo.search.aiui.bean.MicBean;
import com.cmcc.cmvideo.search.model.SearchByAIBean;
import com.cmcc.cmvideo.search.presenters.SearchByAIPresenter;
import com.cmcc.cmvideo.search.presenters.impl.SearchByAIPresenterImpl;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

    }


    @OnClick(R.id.turn_to_ai_search)
    public void turnToAISearch() {
        Intent intent = new Intent(MainActivity.this, SearchByAIActivity.class);
        startActivity(intent);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

}
