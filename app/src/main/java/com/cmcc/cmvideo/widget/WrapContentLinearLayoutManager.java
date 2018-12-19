package com.cmcc.cmvideo.widget;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * Created by Yyw on 2018/7/6.
 * Describe:解决崩溃
 * java.lang.IndexOutOfBoundsException: Inconsistency detected. Invalid item position 10(offset:10).state:11
 * at android.support.v7.widget.RecyclerView$Recycler.tryGetViewHolderForPositionByDeadline(RecyclerView.java:5504)
 * at android.support.v7.widget.RecyclerView$Recycler.getViewForPosition(RecyclerView.java:5440)
 * at android.support.v7.widget.RecyclerView$Recycler.getViewForPosition(RecyclerView.java:5436)
 * at android.support.v7.widget.LinearLayoutManager$LayoutState.next(LinearLayoutManager.java:2224)
 * at android.support.v7.widget.LinearLayoutManager.layoutChunk(LinearLayoutManager.java:1551)
 * at android.support.v7.widget.LinearLayoutManager.fill(LinearLayoutManager.java:1511)
 * at android.support.v7.widget.LinearLayoutManager.scrollBy(LinearLayoutManager.java:1325)
 * at android.support.v7.widget.LinearLayoutManager.scrollHorizontallyBy(LinearLayoutManager.java:1049)
 * at android.support.v7.widget.RecyclerView$ViewFlinger.run(RecyclerView.java:4722)
 * at android.os.Handler.handleCallback(Handler.java:733)
 * at android.os.Handler.dispatchMessage(Handler.java:95)
 * at android.os.Looper.loop(Looper.java:136)
 * at android.app.ActivityThread.main(ActivityThread.java:5336)
 * at java.lang.reflect.Method.invokeNative(Native Method)
 * at java.lang.reflect.Method.invoke(Method.java:515)
 * at com.android.internal.os.ZygoteInit$MethodAndArgsCaller.run(ZygoteInit.java:871)
 * at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:687)
 * at dalvik.system.NativeStart.main(Native Method)
 */

public class WrapContentLinearLayoutManager extends LinearLayoutManager {
    public WrapContentLinearLayoutManager(Context context) {
        super(context);
    }

    public WrapContentLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public WrapContentLinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        try {
            super.onLayoutChildren(recycler, state);
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
    }
}
