package com.sty.ne.glide.resource;

import android.graphics.Bitmap;
import android.util.Log;

import com.sty.ne.glide.Tool;

/**
 * Author: ShiTianyi
 * Time: 2021/4/27 0027 20:51
 * Description: 对Bitmap的封装
 */
public class Value {
    private static final String TAG = Value.class.getSimpleName();
    //单例模式
    private static volatile Value value;

    private Bitmap mBitmap;

    //使用计数
    private int count;

    //监听
    private ValueCallback callback;

    //定义key
    private String key;

    private Value() {

    }

    public static Value getInstance() {
        if(value == null) {
            synchronized (Value.class) {
                if(value == null) {
                    value = new Value();
                }
            }
        }
        return value;
    }

    /**
     * 使用一次就加一
     */
    public void useAction() {
        Tool.checkNotEmpty(mBitmap);
        if(mBitmap.isRecycled()) { //已经被回收了
            Log.d(TAG, "useAction: 加一 count: " + count);
            return;
        }
        count++;
    }

    /**
     * 使用完成（不使用）就减一
     */
    public void nonUseAction() {
        count--;
        if(count <= 0 && callback != null) {
            //回调告诉外界，不再使用了
            callback.valueNonUseListener(key, this);
        }
    }

    /**
     * 释放
     */
    public void recycleBitmap() {
        if(count > 0) {
            Log.d(TAG, "recycleBitmap: 引用计数大于0，证明还在使用中，不能去释放...");
            return;
        }
        if(mBitmap.isRecycled()) { //被回收了
            Log.d(TAG, "recycleBitmap: mBitmap.isRecycled() 已经被释放了...");
            return;
        }
        mBitmap.recycle();
        value = null;
        System.gc();
    }

    public Bitmap getmBitmap() {
        return mBitmap;
    }

    public void setmBitmap(Bitmap mBitmap) {
        this.mBitmap = mBitmap;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public ValueCallback getCallback() {
        return callback;
    }

    public void setCallback(ValueCallback callback) {
        this.callback = callback;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
