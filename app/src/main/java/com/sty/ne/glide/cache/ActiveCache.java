package com.sty.ne.glide.cache;

import android.util.Log;

import com.sty.ne.glide.Tool;
import com.sty.ne.glide.resource.Value;
import com.sty.ne.glide.resource.ValueCallback;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Author: ShiTianyi
 * Time: 2021/5/8 0008 19:46
 * Description: 活动缓存-->真正被使用的资源
 */
public class ActiveCache {
    private static final String TAG = ActiveCache.class.getSimpleName();
    private Map<String, WeakReference<Value>> mapList = new HashMap<>();
    private ReferenceQueue<Value> queue;
    private boolean isCloseThread;
    private Thread thread;
    private boolean isManualRemoved;
    private ValueCallback valueCallback;

    public ActiveCache(ValueCallback valueCallback) {
        this.valueCallback = valueCallback;
    }

    /**
     * 添加活动缓存
     * @param key
     * @param value
     */
    public void put(String key, Value value) {
        Tool.checkNotEmpty(key);

        //绑定Value的监听 --> Value发起来的（Value没有被使用了就会发起这个监听，给外界业务去使用）
        value.setCallback(valueCallback);
        //存储 --> 容器
        mapList.put(key, new CustomWeakReference(value, getQueue(), key));
    }

    /**
     * 给外界获取value
     * @param key
     * @return
     */
    public Value get(String key) {
        WeakReference<Value> valueWeakReference = mapList.get(key);
        if(null != valueWeakReference) {
            return valueWeakReference.get(); //返回value
        }
        return null;
    }

    /**
     * 手动删除key对应的value
     * @param key
     * @return
     */
    public Value remove(String key) {
        isManualRemoved = true;
        WeakReference<Value> valueWeakReference = mapList.remove(key);
        isManualRemoved = false; //还原，目的是让GC自动移除，继续工作
        if(null != valueWeakReference) {
            return valueWeakReference.get();
        }
        return null;
    }

    /**
     * 释放 关闭线程
     */
    public void closeThread() {
        isCloseThread = true;
//        if(null != thread) {
//            thread.interrupt(); //中断线程
//            try {
//                thread.join(TimeUnit.SECONDS.toMillis(5)); //线程稳定、安全地停下来
//                if(thread.isAlive()) { //证明线程还是没有结束
//                    throw new IllegalStateException("活动缓存中关闭线程，线程最终未停止...");
//                }
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }

        mapList.clear();
        System.gc();
    }

    /**
     * 监听弱引用，成为弱引用的子类 --> 为了监听这个弱引用是否被回收掉了
     */
    public class CustomWeakReference extends WeakReference<Value> {
        private String key;

        //用ReferenceQueue 来监听弱引用是否被回收掉了
        public CustomWeakReference(Value referent, ReferenceQueue<? super Value> q, String key) {
            super(referent, q);
            this.key = key;
        }
    }

    /**
     * 监听弱引用被回收 <--被动移除
     * @return
     */
    private ReferenceQueue<Value> getQueue() {
        if(queue == null) {
            queue = new ReferenceQueue<>();
            //监听这个弱引用是否被回收了
            thread = new Thread() {
                @Override
                public void run() {
                    super.run();
                    while(!isCloseThread) {
                        if( !isManualRemoved) { //区分是否为手动移除的
                            try {
                                Reference<? extends Value> remove = queue.remove(); //如果被回收了就会执行到这个方法 <-- 阻塞方法
                                CustomWeakReference weakReference = (CustomWeakReference) remove;
                                //移除容器
                                if (mapList != null && !mapList.isEmpty()) {
                                    Log.e(TAG, "key: " + weakReference.key + " is recycled");
                                    mapList.remove(weakReference.key);
                                }
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            };
            thread.start();
        }
        return queue;
    }
}
