package com.sty.ne.glide.core;

/**
 * Author: ShiTianyi
 * Time: 2021/5/18 0018 20:53
 * Description:
 */
public class GlideBuilder {

    /**
     * 创建Glide
     * @return
     */
    public Glide build() {
        RequestManagerRetriever requestManagerRetriever = new RequestManagerRetriever();
        Glide glide = new Glide(requestManagerRetriever);
        return glide;
    }
}
