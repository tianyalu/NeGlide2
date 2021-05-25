package com.sty.ne.glide.fragment;

/**
 * Author: ShiTianyi
 * Time: 2021/5/19 0019 21:11
 * Description:
 */
public interface LifecycleCallback {
    //生命周期初始化了
    public void glideInitAction();

    //生命周期停止了
    public void glideStopAction();

    //生命周期释放操作了
    public void glideRecycleAction();
}
