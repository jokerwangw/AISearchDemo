package com.cmcc.cmvideo.widget;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

/**
 * Created by Yyw on 2018/11/28.
 * Describe:
 */
public class NestRecyclerView extends RecyclerView {

    private static final String TAG = "nestrecyclerview";
    private int lastVisibleItemPosition;
    private int firstVisibleItemPosition;
    // 记录上次Y位置
    private float mLastY = 0;
    private boolean isTopToBottom = false;
    private boolean isBottomToTop = false;

    public NestRecyclerView(Context context) {
        this(context, null);
    }

    public NestRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NestRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        setFocusable(true);
        setNestedScrollingEnabled(true);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mLastY = event.getY();
                //不允许父View拦截事件
                getParent().requestDisallowInterceptTouchEvent(true);
                break;
            case MotionEvent.ACTION_MOVE:
                float nowY = event.getY();
                isIntercept(nowY);
                if (isBottomToTop || isTopToBottom) {
                    getParent().requestDisallowInterceptTouchEvent(false);
                    return false;
                } else {
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                mLastY = nowY;
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                getParent().requestDisallowInterceptTouchEvent(false);
                break;
        }
        return super.onInterceptTouchEvent(event);
    }

    private void isIntercept(float nowY) {

        isTopToBottom = false;
        isBottomToTop = false;

        RecyclerView.LayoutManager layoutManager = getLayoutManager();
        if (layoutManager instanceof LinearLayoutManager) {
            //得到当前界面，最后一个子视图对应的position
            lastVisibleItemPosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
            //得到当前界面，第一个子视图的position
            firstVisibleItemPosition = ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();
        }
        //得到当前界面可见数据的大小
        int visibleItemCount = layoutManager.getChildCount();
        //得到RecyclerView对应所有数据的大小
        int totalItemCount = layoutManager.getItemCount();
        Log.i(TAG, "onScrollStateChanged");
        if (visibleItemCount > 0) {
            if (lastVisibleItemPosition == totalItemCount - 1) {
                //最后视图对应的position等于总数-1时，说明上一次滑动结束时，触底了
                Log.i(TAG, "触底了");
                if (NestRecyclerView.this.canScrollVertically(-1) && nowY < mLastY) {
                    // 不能向上滑动
                    Log.i(TAG, "不能向上滑动");
                    isBottomToTop = true;
                } else {
                    Log.i(TAG, "向下滑动");
                }
            } else if (firstVisibleItemPosition == 0) {
                //第一个视图的position等于0，说明上一次滑动结束时，触顶了
                Log.i(TAG, "触顶了");
                if (NestRecyclerView.this.canScrollVertically(1) && nowY > mLastY) {
                    // 不能向下滑动
                    Log.i(TAG, "不能向下滑动");
                    isTopToBottom = true;
                } else {
                    Log.i(TAG, "向上滑动");
                }
            }
        }
    }
}
