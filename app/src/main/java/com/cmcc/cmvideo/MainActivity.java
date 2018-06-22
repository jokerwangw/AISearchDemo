package com.cmcc.cmvideo;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.cmcc.cmvideo.search.SearchByAIActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.turn_to_ai_search)
    Button btTurnToAISearch;
    private int downX, downY, btLeft, btRight, btBottom, btTop;
    private long downTime = 0;
    private int MAX_CLICK_TIME = 500;
    private boolean hasMove = false;
    private int widthPixels, heightPixels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        initView();
        initData();
    }

    private void initView() {
        btTurnToAISearch.setOnTouchListener(btTouchListener);
    }

    private void initData() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        widthPixels = dm.widthPixels;
        heightPixels = dm.heightPixels;
    }

    //    @OnClick(R.id.turn_to_ai_search)
    //    public void turnToSearch(){
    //        Intent intent = new Intent(MainActivity.this, SearchByAIActivity.class);
    //        startActivity(intent);
    //    }

    private View.OnTouchListener btTouchListener = new View.OnTouchListener() {
        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View view, MotionEvent event) {
            if (R.id.turn_to_ai_search == view.getId()) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        downTime = System.currentTimeMillis();
                        btLeft = view.getLeft();
                        btRight = view.getRight();
                        btTop = view.getTop();
                        btBottom = view.getBottom();
                        downX = (int) event.getRawX();
                        downY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        long moveTime = System.currentTimeMillis() - downTime;
                        int moveX = (int) event.getRawX();
                        int moveY = (int) event.getRawY();
                        showMoveDrag(moveTime > MAX_CLICK_TIME, moveX - downX, moveY - downY);
                        break;
                    case MotionEvent.ACTION_UP:
                        long upTime = System.currentTimeMillis() - downTime;
                        int upX = (int) event.getRawX();
                        int upY = (int) event.getRawY();
                        dragSearchBt(upTime <= MAX_CLICK_TIME, upX - downX, upY - downY);
                        break;
                    default:
                        break;
                }
                return true;
            }
            return false;

        }
    };

    private void dragSearchBt(boolean isClick, int dx, int dy) {
        if (isClick) {
            Intent intent = new Intent(MainActivity.this, SearchByAIActivity.class);
            startActivity(intent);
        } else {
            showUpDrag(dx, dy);
        }
    }

    private void showMoveDrag(boolean beginMove, int dx, int dy) {
        if (beginMove) {
            int l = btLeft + dx;
            int t = btTop + dy;
            int r = btRight + dx;
            int b = btBottom + dy;
            // 判断超出屏幕
            if (l < 0) {
                l = 0;
                r = l + btTurnToAISearch.getWidth();
            }
            if (t < 0) {
                t = 0;
                b = t + btTurnToAISearch.getHeight();
            }
            if (r > widthPixels) {
                r = widthPixels;
                l = r - btTurnToAISearch.getWidth();
            }
            if (b > heightPixels) {
                b = heightPixels;
                t = b - btTurnToAISearch.getHeight();
            }

            if (null != btTurnToAISearch) {
                btTurnToAISearch.layout(l, t, r, b);
                btTurnToAISearch.postInvalidate();
            }
        }
    }

    private void showUpDrag(int dx, int dy) {
        int left = btTurnToAISearch.getLeft();

        int l = btLeft + dx;
        int t = btTop + dy;
        int r = btRight + dx;
        int b = btBottom + dy;
        if (t < 0) {
            t = 0;
            b = t + btTurnToAISearch.getHeight();
        }
        if (b > heightPixels) {
            b = heightPixels;
            t = b - btTurnToAISearch.getHeight();
        }
        if (left * 2 < widthPixels) {
            l = 0;
            r = l + btTurnToAISearch.getWidth();
        } else {
            r = widthPixels;
            l = r - btTurnToAISearch.getWidth();
        }

        if (null != btTurnToAISearch) {
            btTurnToAISearch.layout(l, t, r, b);
            btTurnToAISearch.postInvalidate();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
