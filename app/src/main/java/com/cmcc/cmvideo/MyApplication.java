package com.cmcc.cmvideo;

import android.app.Application;

import com.cmcc.cmvideo.base.ApplicationContext;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.backends.okhttp.OkHttpNetworkFetcher;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.producers.NetworkFetcher;
import com.iflytek.cloud.SpeechUtility;
import com.orhanobut.logger.Logger;
import com.squareup.okhttp.Dispatcher;
import com.squareup.okhttp.OkHttpClient;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by Yyw on 2018/5/29.
 * Describe:
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        ApplicationContext.init(this);
        // 初始化 Fresco
        initFresco();
    }

    private void initFresco() {

        try {
            BlockingDeque<Runnable> queue = new LIFOLinkedBlockingQueue<>();
            ThreadPoolExecutor executor = new ThreadPoolExecutor(3, 3, 30, TimeUnit.SECONDS, queue);
            Dispatcher dispatcher = new Dispatcher(executor);

            OkHttpClient okHttpClient = new OkHttpClient();
            okHttpClient.setDispatcher(dispatcher);
            NetworkFetcher fetcher = new OkHttpNetworkFetcher(okHttpClient);

            // Fresco 缓存限制
            // ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            // MGBitmapMemoryCacheParamsSupplier supplier = new MGBitmapMemoryCacheParamsSupplier(activityManager);

            ImagePipelineConfig.Builder builder = ImagePipelineConfig.newBuilder(this)
                    .setNetworkFetcher(fetcher)
                    .setDownsampleEnabled(true);
            // .setBitmapMemoryCacheParamsSupplier(supplier);
            Fresco.initialize(this, builder.build());
        } catch (Exception e) {
            Logger.e("Fresco inierr", e);
        }
    }

    private static class LIFOLinkedBlockingQueue<E> extends LinkedBlockingDeque<E> {
        @Override
        public E take() throws InterruptedException {
            return takeLast();
        }

        @Override
        public E poll(long timeout, TimeUnit unit) throws InterruptedException {
            return pollLast(timeout, unit);
        }

        @Override
        public E poll() {
            return pollLast();
        }
    }
}
