package com.cmcc.cmvideo.search;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
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
import com.cmcc.cmvideo.search.aiui.bean.NlpData;
import com.cmcc.cmvideo.search.aiui.bean.TppData;
import com.cmcc.cmvideo.search.model.LookMoreEventDataBean;
import com.cmcc.cmvideo.search.presenters.LookMorePresenter;
import com.cmcc.cmvideo.search.presenters.impl.LookMorePresenterImpl;
import com.cmcc.cmvideo.util.SharedPreferencesHelper;
import com.google.gson.Gson;
import com.iflytek.aiui.AIUIConstant;
import com.iflytek.aiui.AIUIMessage;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public static final String KEY_LAST_TEXT = "last_text";
    private Context mContext;
    private LookMorePresenterImpl lookMorePresenter;
    private LookMoreAdapter mLookMoreAdapter;
    private List<TppData.DetailsListBean> detailsList;
    private int pageNum = 1;
    private int pageSize = 15;
    private IAIUIService aiuiService;
    private String lastTextData;
    private Gson gson;
    private boolean isBind = false;
    private String spLastData;
    //创建Handler
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1002) {
                aiuiService.getLookMorePage(lastTextData, pageNum, pageSize);
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;

        setContentView(R.layout.activity_look_more);
        ButterKnife.bind(this);

        initCustomView();
        initData();
    }

    private void initData() {
        gson = new Gson();
        EventBus.getDefault().register(this);
        bindService(new Intent(this, AIUIService.class), connection, Context.BIND_AUTO_CREATE | Context.BIND_IMPORTANT);
        detailsList = new ArrayList<>();
        lookMorePresenter = new LookMorePresenterImpl(
                ThreadExecutor.getInstance(),
                MainThreadImpl.getInstance(),
                this,
                this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveLookMoreData(LookMoreEventDataBean moreData) {
        NlpData nlpData = gson.fromJson(moreData.getMoreData(), NlpData.class);
        String lastText = nlpData.text;
        Logger.debug("文本数据===++++++" + lastText);
        if (!spLastData.equals(lastText) && !lastText.equals("查看更多")) {
            if (null != aiuiService) {
                aiuiService.onResume(false);
                this.finish();
            }
            return;
        }

        lookMorePresenter.setDetailsJson(moreData.getMoreData());
        mLookMoreAdapter.notifyDataSetChanged();
        if (nlpData.data == null
                || nlpData.data.lxresult == null
                || nlpData.data.lxresult.data.detailslist.size() == 0) {

            mLookMoreRecyclerView.setFootViewText(null, null);
            mLookMoreRecyclerView.loadMoreComplete();
            return;
        }
        mLookMoreRecyclerView.loadMoreComplete();
    }

    private void initCustomView() {
        SharedPreferencesHelper.getInstance(this).setValue(KEY_LAST_TEXT, getIntent().getStringExtra(KEY_LAST_TEXT));
        spLastData = SharedPreferencesHelper.getInstance(this).getValue(KEY_LAST_TEXT);
        lastTextData = getIntent().getStringExtra(KEY_LAST_TEXT);
        Logger.debug("上次请求文本====>>>>>>>>>>>" + lastTextData);
        mLookMoreAdapter = new LookMoreAdapter(mContext, this);
        titleTv.setText(getIntent().getStringExtra(KEY_TITLE));

        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mLookMoreRecyclerView.setLayoutManager(layoutManager);
        mLookMoreRecyclerView.setPullRefreshEnabled(false);
        mLookMoreRecyclerView.setLoadingMoreProgressStyle(ProgressStyle.Pacman);
        mLookMoreRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                //refresh data here
                pageNum = 1;
            }

            @Override
            public void onLoadMore() {
                // load more data here
                pageNum++;
                Message message = Message.obtain();
                message.what = 1002;
                handler.sendMessage(message);
            }
        });

        mLookMoreRecyclerView.setAdapter(mLookMoreAdapter);

    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            aiuiService = (IAIUIService) service;
            isBind = true;

            lookMorePresenter.setDetailsJson(getIntent().getStringExtra(KEY_MORE_DATE));
            //aiuiService.getLookMorePage(lastTextData, pageNum, pageSize);

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };


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
        EventBus.getDefault().unregister(this);
        super.onDestroy();
        lookMorePresenter.destroy();
        if (isBind) {
            unbindService(connection);
        }
        isBind = false;
        lastTextData = "";
        spLastData = "";
    }
}
