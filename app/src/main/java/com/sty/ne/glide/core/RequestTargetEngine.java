package com.sty.ne.glide.core;

import android.util.Log;
import android.widget.ImageView;

import com.sty.ne.glide.fragment.LifecycleCallback;

/**
 * Author: ShiTianyi
 * Time: 2021/5/19 0019 21:17
 * Description: 加载图片资源
 */
public class RequestTargetEngine implements LifecycleCallback {
    private static final String TAG = RequestTargetEngine.class.getSimpleName();
    @Override
    public void glideInitAction() {
        Log.d(TAG, "glideInitAction: Glide生命周期之已经开始初始化了...");
    }

    @Override
    public void glideStopAction() {
        Log.d(TAG, "glideStopAction: Glide生命周期之已经停止了...");

    }

    @Override
    public void glideRecycleAction() {
        Log.d(TAG, "glideRecycleAction: Glide生命周期之 进行释放操作、缓存策略释放操作等...");

    }

    public void into(ImageView imageView) {

    }
}
