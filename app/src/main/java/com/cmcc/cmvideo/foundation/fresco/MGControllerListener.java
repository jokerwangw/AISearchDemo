package com.cmcc.cmvideo.foundation.fresco;

import android.graphics.drawable.Animatable;
import android.text.TextUtils;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.imagepipeline.image.CloseableStaticBitmap;
import com.facebook.imagepipeline.image.ImageInfo;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Random;

/**
 * Created by caominyan on 16/8/2.
 */
public class MGControllerListener extends BaseControllerListener<ImageInfo> {

    private String mNodeId;

    private String mUrl;

    private String mHost;

    private long mStartTime;

    private long mEndTime;

    private MGSimpleListener mSimpleListener;

    private volatile static int failPicMaxNum = 20;

    private volatile static Random random = new Random();

    public MGControllerListener(MGSimpleListener listener, String nodeId, String url, String host) {
        super();
        mNodeId = nodeId;
        mSimpleListener = listener;
        mUrl = url;
        mHost = host;
    }

    @Override
    public void onSubmit(String id, Object callerContext) {
        super.onSubmit(id, callerContext);
        mStartTime = Calendar.getInstance().getTimeInMillis();
    }

    @Override
    public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {
        super.onFinalImageSet(id, imageInfo, animatable);

        if (mSimpleListener != null) {
            mSimpleListener.onFinalImageSet(id, imageInfo, animatable);
        }

        mEndTime = Calendar.getInstance().getTimeInMillis();

        if (imageInfo instanceof CloseableStaticBitmap) {
            int size = ((CloseableStaticBitmap) imageInfo).getSizeInBytes();
        }
    }

    @Override
    public void onIntermediateImageSet(String id, ImageInfo imageInfo) {
        super.onIntermediateImageSet(id, imageInfo);
    }

    @Override
    public void onIntermediateImageFailed(String id, Throwable throwable) {
        super.onIntermediateImageFailed(id, throwable);
    }

    @Override
    public void onFailure(String id, Throwable throwable) {
        super.onFailure(id, throwable);

        if (mSimpleListener != null) {
            mSimpleListener.onFailure(id, throwable);
        }

        mEndTime = Calendar.getInstance().getTimeInMillis();
    }

    @Override
    public void onRelease(String id) {
        super.onRelease(id);
    }
}
