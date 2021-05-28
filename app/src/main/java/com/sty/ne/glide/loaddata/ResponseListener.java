package com.sty.ne.glide.loaddata;

import com.sty.ne.glide.resource.Value;

/**
 * Author: ShiTianyi
 * Time: 2021/5/27 0027 19:58
 * Description: 加载外部资源 成功或失败后的回调
 */
public interface ResponseListener {

    public void responseSuccess(Value value);

    public void responseException(Exception e);
}
