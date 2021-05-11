package com.sty.ne.glide.cache;

import android.util.Log;

import com.sty.ne.glide.resource.Value;

/**
 * Author: ShiTianyi
 * Time: 2021/5/11 0011 21:09
 * Description:
 */
public class Test {

    public void test() {
        MemoryCache memoryCache = new MemoryCache(5);
        memoryCache.put("abcdef", Value.getInstance());
        Value v = memoryCache.get("abcdef");
        memoryCache.manualRemove("abcdef");
        memoryCache.setMemoryCacheCallback(new MemoryCacheCallback() {
            @Override
            public void entryRemovedMemoryCache(String key, Value oldValue) {
                Log.d("sty", "entryRemovedMemoryCache: 内存缓存中的元素被移除了【被动移除】" +
                        " value: " + oldValue);
            }
        });
    }
}
