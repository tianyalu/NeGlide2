package com.sty.ne.glide.fragment;

import android.annotation.SuppressLint;
import android.app.Fragment;

/**
 * Author: ShiTianyi
 * Time: 2021/5/19 0019 20:42
 * Description: 生命周期管理
 */
public class ActivityFragmentManager extends Fragment {
    private LifecycleCallback lifecycleCallback;

    public ActivityFragmentManager() {}

    @SuppressLint("ValidFragment")
    public ActivityFragmentManager(LifecycleCallback lifecycleCallback) {
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
