package com.cmcc.cmvideo.foundation.fresco;

import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;

import com.cmcc.cmvideo.util.NetworkUtil;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.generic.GenericDraweeHierarchyInflater;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

/**
 * Created by zhanghongxing on 2017/9/29.
 */

public class WithoutHolderMGSimpleDraweeView extends SimpleDraweeView {

    private MGSimpleListener mSimpleListener;
    private String mNodeId;

    public WithoutHolderMGSimpleDraweeView(Context context, GenericDraweeHierarchy hierarchy) {
        super(context, hierarchy);

    }


    public WithoutHolderMGSimpleDraweeView(Context context) {
        super(context);
    }

    public WithoutHolderMGSimpleDraweeView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WithoutHolderMGSimpleDraweeView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void inflateHierarchy(Context context, AttributeSet attrs) {
        GenericDraweeHierarchyBuilder builder =
                GenericDraweeHierarchyInflater.inflateBuilder(context, attrs);
        builder.setPlaceholderImageScaleType(ScalingUtils.ScaleType.CENTER_INSIDE);
        setAspectRatio(builder.getDesiredAspectRatio());
        setHierarchy(builder.build());
    }

    public WithoutHolderMGSimpleDraweeView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void setImageURI(String uriString) {
        setImageURI(uriString, 0, 0);
    }

    @Override
    public void setImageURI(Uri uri) {
        setImageURI(uri, 0, 0);
    }

    @Override
    public void setImageURI(String uriString, Object callerContext) {
        setImageURI(uriString, 0, 0, callerContext);
    }

    @Override
    public void setImageURI(Uri uri, Object callerContext) {
        setImageURI(uri, 0, 0, callerContext);
    }

    public void setImageURI(String uriString, int width, int height) {
        Uri uri = (uriString != null) ? Uri.parse(uriString) : null;
        setImageURI(uri, width, height);
    }

    public void setImageURI(Uri uri, int width, int height) {
        setImageURI(uri, width, height, null);
    }

    public void setImageURI(String uriString, int width, int height, Object callerContext) {
        Uri uri = (uriString != null) ? Uri.parse(uriString) : null;
        setImageURI(uri, width, height, callerContext);
    }

    public void setImageURI(Uri uri, int width, int height, Object callerContext) {
        if (uri == null) {
            return;
        }

        ImageRequest request;
        if (width > 0 && height > 0) {
            ResizeOptions options = new ResizeOptions(width, height);
            request = ImageRequestBuilder.newBuilderWithSource(uri)
                    .setResizeOptions(options)
                    .setProgressiveRenderingEnabled(true)
                    .build();
        } else {
            request = ImageRequestBuilder.newBuilderWithSource(uri)
                    .setProgressiveRenderingEnabled(true)
                    .build();
        }
        mNodeId = getContext().getClass().getSimpleName();

        //NetworkUtil.proccessInetAddress(uri.getHost());

        MGControllerListener controllerListener = new MGControllerListener(mSimpleListener,
                mNodeId, uri.toString(), uri.getHost());

        PipelineDraweeController controller;
        PipelineDraweeControllerBuilder builder = Fresco.newDraweeControllerBuilder();
        if (callerContext != null) {
            builder.setCallerContext(callerContext);
        }
        builder.setOldController(this.getController())
                .setImageRequest(request)
                .setControllerListener(controllerListener)
                .setAutoPlayAnimations(true)
                .build();
        controller = (PipelineDraweeController) builder.build();
        this.setController(controller);
    }

    public void setSimpleListener(MGSimpleListener listener) {
        mSimpleListener = listener;
    }

    public void setAnalysisData(String nodeId) {
        mNodeId = nodeId;
    }
}
