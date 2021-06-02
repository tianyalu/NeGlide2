package com.sty.ne.glide.core;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;

import androidx.loader.app.LoaderManager;

import com.sty.ne.glide.Tool;
import com.sty.ne.glide.cache.ActiveCache;
import com.sty.ne.glide.cache.MemoryCache;
import com.sty.ne.glide.cache.MemoryCacheCallback;
import com.sty.ne.glide.cache.disk.DiskLruCache;
import com.sty.ne.glide.cache.disk.DiskLruCacheImpl;
import com.sty.ne.glide.fragment.LifecycleCallback;
import com.sty.ne.glide.loaddata.LoadDataManager;
import com.sty.ne.glide.loaddata.ResponseListener;
import com.sty.ne.glide.resource.Key;
import com.sty.ne.glide.resource.Value;
import com.sty.ne.glide.resource.ValueCallback;

/**
 * Author: ShiTianyi
 * Time: 2021/5/19 0019 21:17
 * Description: 加载图片资源
 */
public class RequestTargetEngine implements LifecycleCallback, ValueCallback, MemoryCacheCallback, ResponseListener {
    private static final String TAG = RequestTargetEngine.class.getSimpleName();
    private ActiveCache activeCache; //活动缓存
    private MemoryCache memoryCache; //内存缓存
    private DiskLruCacheImpl diskLruCache; //磁盘缓存

    private final int MEMORY_MAX_SIZE = 1024 * 1024 * 60;
    //TODO 复用池

    private String path;
    private Context glideContext;
    private String key; // 900d489a4c4c31235dc856b6a982c4e0ae1f53f14dbf04733107b51553562c0a
    private ImageView imageView; //显示的目标

    public RequestTargetEngine() {
        if(activeCache == null) {
            activeCache = new ActiveCache(this); //回调告诉外界，Value资源不再使用了
            //Log.e(TAG, "activeCache initialized");
        }
        if(memoryCache == null) {
            memoryCache = new MemoryCache(MEMORY_MAX_SIZE);
            memoryCache.setMemoryCacheCallback(this); //LRU最少使用的元素会被移除,设置监听
        }
        //初始化磁盘缓存
        diskLruCache = new DiskLruCacheImpl();
    }

    /**
     * RequestManager传递的值
     */
    public void loadValueInitAction(String path, Context glideContext) {
        this.path = path;
        this.glideContext = glideContext;
        key = new Key(path).getKey();
    }

    @Override
    public void glideInitAction() {
        Log.d(TAG, "glideInitAction: Glide生命周期之已经开始初始化了...");
    }

    @Override
    public void glideStopAction() {
        Log.d(TAG, "glideStopAction: Glide生命周期之已经停止了...");

    }

    @Override
    public void glideRecycleAction() {
        Log.d(TAG, "glideRecycleAction: Glide生命周期之 进行释放操作、缓存策略释放操作等...");
        if(activeCache != null) {
            activeCache.closeThread(); //把活动缓存给释放掉
        }
    }

    public void into(ImageView imageView) {
        this.imageView = imageView;
        Tool.checkNotEmpty(imageView);
        Tool.assertMainThread();

        // 加载资源 --> 缓存 --> 网络/`SD`卡 加载资源 --> 成功后保存到缓存中
        Value value = cacheAction();
        if(value != null) {
            value.nonUseAction(); //使用完成了 减一
            imageView.setImageBitmap(value.getmBitmap());
        }
    }

    // 加载资源 --> 缓存 --> 网络/`SD`卡 加载资源 --> 成功后保存到缓存中
    private Value cacheAction() {
        //1.判断活动缓存是否有资源，如果有资源就返回，否则继续往下找
        Value value = activeCache.get(key);
        if(null != value) {
            Log.d(TAG, "cacheAction: 本次加载是在（活动缓存）中获取的资源>>>");
            //返回代表使用了一次Value
            value.useAction(); //使用了一次 加一
            return value;
        }

        //2.从内存缓存中去找，如果找到了，将内存缓存中的元素“移动”到活动缓存，然后再返回
        value = memoryCache.get(key);
        if(null != value) {
            //移动操作
            memoryCache.manualRemove(key); //移除内存缓存
            activeCache.put(key, value); //把内存缓存中的元素加入到活动缓存中
            Log.d(TAG, "cacheAction: 本次加载是在（内存缓存）中获取的资源>>>");
            //返回代表使用了一次Value
            value.useAction(); //使用了一次 加一
            return value;
        }

        //3.去磁盘缓存中去找，如果找到了，把磁盘缓存中的元素加入到活动缓存中
        value = diskLruCache.get(key);
        if(null != value) {
            //把磁盘缓存中的元素加入到活动缓存中
            activeCache.put(key, value);
            //把磁盘缓存中的元素加入到内存缓存中
            //memoryCache.put(key, value);

            Log.d(TAG, "cacheAction: 本次加载是在（磁盘缓存）中获取的资源>>>");
            //返回代表使用了一次Value
            value.useAction(); //使用了一次 加一
            return value;
        }

        //4.真正去加载外部资源了，去网络/本地SD卡上加载
        value = new LoadDataManager().loadResource(path, this, glideContext);
        if(value != null) {
            return value;
        }

        return null;
    }

    /**
     * 活动缓存间接地调用Value所发出的
     *  回调告诉外界，Value资源不再使用了
     * @param key
     * @param value
     */
    @Override
    public void valueNonUseListener(String key, Value value) {
        //把活动缓存操作的Value资源加入到内存缓存
        if(key != null && value != null) {
            //Log.e(TAG, "加入到内存缓存key： " + key);
            memoryCache.put(key, value);
        }
    }

    /**
     * 内存缓存发出的
     * LRU最少使用的元素会被移除
     * @param key
     * @param oldValue
     */
    @Override
    public void entryRemovedMemoryCache(String key, Value oldValue) {
        //添加到复用池...
    }

    //加载外部资源成功
    @Override
    public void responseSuccess(Value value) {
        if(value != null) {
            saveCache(key, value);
            imageView.setImageBitmap(value.getmBitmap());
        }
    }

    // 加载外部资源失败
    @Override
    public void responseException(Exception e) {
        Log.d(TAG, "responseException: 加载外部资源失败 e: " + e.getMessage());
    }

    public void saveCache(String key, Value value) {
        Log.d(TAG, "saveCache: >>>>> 加载外部资源成功后保存到缓存中 key: " + key + " value: " + value);
        value.setKey(key);

        if(diskLruCache != null) {
            diskLruCache.put(key, value); //保存到磁盘缓存中
        }
    }
}
