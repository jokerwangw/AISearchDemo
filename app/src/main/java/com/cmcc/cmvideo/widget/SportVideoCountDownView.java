package com.cmcc.cmvideo.widget;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cmcc.cmvideo.R;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Yyw on 2018/12/4.
 * Describe:
 */
public class SportVideoCountDownView extends LinearLayout {
    private final Context mContext;
    private TextView countdownSec;
    private TextView countdownMin;
    private TextView countdownHour;
    private TextView countdownDay;
    // 计时器
    private Timer timer;

    private Handler handler = new Handler() {

        public void handleMessage(Message msg) {
            countDown();
        }
    };


    public SportVideoCountDownView(Context context) {
        this(context, null);
    }

    public SportVideoCountDownView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SportVideoCountDownView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        initView(bindView());
        initData();
    }

    private View bindView() {
        LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return layoutInflater.inflate(R.layout.sport_video_count_down_view, this);
    }

    private void initView(View view) {
        countdownDay = (TextView) view.findViewById(R.id.countdown_day);
        countdownHour = (TextView) view.findViewById(R.id.countdown_hour);
        countdownMin = (TextView) view.findViewById(R.id.countdown_min);
        countdownSec = (TextView) view.findViewById(R.id.countdown_sec);
    }

    private void initData() {
    }

    /**
     * 设置比赛开始时间
     *
     * @param matchBeginTime
     */
    public void setMatchBeginTime(long matchBeginTime) {
        long curTime = new Date().getTime();
        if (matchBeginTime > curTime) {
            long countDownTime = matchBeginTime - curTime;
        }
    }

    /**
     * 开始倒计时
     */
    public void startCountDown() {
        if (timer == null) {
            timer = new Timer();
            timer.schedule(new TimerTask() {

                @Override
                public void run() {
                    handler.sendEmptyMessage(0);
                }
            }, 0, 1000);
        }
    }

    /**
     * 停止倒计时
     */
    public void stopCountDown() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    private void countDown() {

    }
}
