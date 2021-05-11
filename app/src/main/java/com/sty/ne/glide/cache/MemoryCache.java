package com.sty.ne.glide.cache;

import android.graphics.Bitmap;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.collection.LruCache;

import com.sty.ne.glide.resource.Value;

/**
 * Author: ShiTianyi
 * Time: 2021/5/10 0010 20:34
 * Description: 内存缓存 -- LRU算法
 */
public class MemoryCache extends LruCache<String, Value> {
    private boolean isManualRemove;
    private MemoryCacheCallback memoryCacheCallback;

    public void setMemoryCacheCallback(MemoryCacheCallback memoryCacheCallback) {
        this.memoryCacheCallback = memoryCacheCallback;
    }

    /**
     * 手动移除
     * @param key
     */
    public Value manualRemove(String key) {
        isManualRemove = true;
        Value value = remove(key);
        isManualRemove = false;
        return value;
    }

    /**
     * @param maxSize for caches that do not override {@link #sizeOf}, this is
     *                the maximum number of entries in the cache. For all other caches,
     *                this is the maximum sum of the sizes of the entries in this cache.
     */
    public MemoryCache(int maxSize) {
        super(maxSize);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected int sizeOf(@NonNull String key, @NonNull Value value) {
        //return super.sizeOf(key, value);
        Bitmap bitmap = value.getmBitmap();
        //最开始的时候
        //int result = bitmap.getRowBytes() * bitmap.getHeight();
        //API 12 3.0
        //int result = bitmap.getByteCount(); //bitmap内存复用有区别（所属的）
        //API 19 4.4
        //int result = bitmap.getAllocationByteCount(); //bitmap内存复用有区别（整个的）
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return bitmap.getAllocationByteCount();
        }
        return bitmap.getByteCount();
    }

    /**
     * 1. 重复的key会被移除
     * 2. 最少使用的元素会被移除
     * @param evicted
     * @param key
     * @param oldValue
     * @param newValue
     */
    @Override
    protected void entryRemoved(boolean evicted, @NonNull String key, @NonNull Value oldValue, @Nullable Value newValue) {
        super.entryRemoved(evicted, key, oldValue, newValue);
        if(!isManualRemove && memoryCacheCallback != null) { //非手动移除 --> 被动移除
            memoryCacheCallback.entryRemovedMemoryCache(key, oldValue);
        }
    }
}
