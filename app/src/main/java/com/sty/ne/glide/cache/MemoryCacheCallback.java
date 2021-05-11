package com.sty.ne.glide.cache;

import com.sty.ne.glide.resource.Value;

/**
 * Author: ShiTianyi
 * Time: 2021/5/11 0011 20:51
 * Description: 内存缓存中，元素被移除的接口回调
 */
public interface MemoryCacheCallback {

    /**
     * 内存缓存中移除的 key -- value
     * @param key
     * @param oldValue
     */
    public void entryRemovedMemoryCache(String key, Value oldValue);
}
