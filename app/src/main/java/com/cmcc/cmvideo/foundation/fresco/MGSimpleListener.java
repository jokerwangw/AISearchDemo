package com.cmcc.cmvideo.foundation.fresco;

import android.graphics.drawable.Animatable;

import com.facebook.imagepipeline.image.ImageInfo;

/**
 * @author zhanghongxing
 * @data 2018/1/24.
 */

public interface MGSimpleListener {

    /**
     * 图片成功显示回调
     *
     * @param id
     * @param imageInfo
     * @param animatable
     */
    public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable);

    /**
     * 图片加载失败回调
     *
     * @param id
     * @param throwable
     */
    public void onFailure(String id, Throwable throwable);

}
