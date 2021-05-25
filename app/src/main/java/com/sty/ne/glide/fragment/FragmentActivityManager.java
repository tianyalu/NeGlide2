package com.sty.ne.glide.fragment;

import android.annotation.SuppressLint;

import androidx.fragment.app.Fragment;

/**
 * Author: ShiTianyi
 * Time: 2021/5/19 0019 20:41
 * Description: 生命周期管理
 */
public class FragmentActivityManager extends Fragment {
    private LifecycleCallback lifecycleCallback;

    public FragmentActivityManager() {}

    @SuppressLint("ValidFragment")
    public FragmentActivityManager(LifecycleCallback lifecycleCallback) {
        this.lifecycleCallback = lifecycleCallback;
    }

    @Override
    public void onStart() {
        super.onStart();
        if(lifecycleCallback != null) {
            lifecycleCallback.glideInitAction();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if(lifecycleCallback != null) {
            lifecycleCallback.glideStopAction();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(lifecycleCallback != null) {
            lifecycleCallback.glideRecycleAction();
        }
    }
}
