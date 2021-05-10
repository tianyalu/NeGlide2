package com.sty.ne.glide.resource;

/**
 * Author: ShiTianyi
 * Time: 2021/4/27 0027 20:57
 * Description: 专门给Value回调：不再使用时的接口回调
 */
public interface ValueCallback {
    /**
     * 监听的方法（Value不再使用了）
     * @param key
     * @param value
     */
    public void valueMonUseListener(String key, Value value);
}
