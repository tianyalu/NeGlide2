package com.sty.ne.glide.cache.pool;

import android.graphics.Bitmap;

/**
 * Author: ShiTianyi
 * Time: 2021/6/3 0003 21:13
 * Description: 复用池的标准
 */
public interface BitmapPool {
    /**
     * 存入到复用池
     * @param bitmap 表面上是存入bitmap，实际上我们不需要Bitmap的图片，只需要Bitmap的内存
     */
    void put(Bitmap bitmap);

    /**
     * 采取匹配可以复用的Bitmap
     * @param width
     * @param height
     * @param config
     * @return Bitmap的内存，图片其实不需要
     */
    Bitmap get(int width, int height, Bitmap.Config config);
}
