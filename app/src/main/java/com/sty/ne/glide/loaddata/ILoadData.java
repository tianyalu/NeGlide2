package com.sty.ne.glide.loaddata;

import android.content.Context;

import com.sty.ne.glide.resource.Value;

/**
 * Author: ShiTianyi
 * Time: 2021/5/27 0027 19:56
 * Description: 加载外部资源 标准
 */
public interface ILoadData {
    //加载外部资源的行为
    public Value loadResource(String path, ResponseListener responseListener, Context context);
}
