package com.sty.ne.glide.cache.pool;

import android.graphics.Bitmap;
import android.os.Build;
import android.util.Log;
import android.util.LruCache;

import java.util.TreeMap;

/**
 * Author: ShiTianyi
 * Time: 2021/6/3 0003 21:17
 * Description:
 */
public class BitmapPoolImpl extends LruCache<Integer, Bitmap> implements BitmapPool{
    private static final String TAG = BitmapPoolImpl.class.getSimpleName();
    //为了筛选出合适的Bitmap容器
    private TreeMap<Integer, String> treeMap = new TreeMap<>();

    /**
     * @param maxSize for caches that do not override {@link #sizeOf}, this is
     *                the maximum number of entries in the cache. For all other caches,
     *                this is the maximum sum of the sizes of the entries in this cache.
     */
    public BitmapPoolImpl(int maxSize) {
        super(maxSize);
    }

    //存入到复用池
    @Override
    public void put(Bitmap bitmap) {
        // 条件一：bitmap.isMutable() == true
        if(!bitmap.isMutable()) {
            Log.d(TAG, "put: 条件一：bitmap.isMutable() == true 不满足， 不能存入复用池");
            return;
        }

        //条件二：计算Bitmap的大小
        int bitmapSize = getBitmapSize(bitmap);
        if(bitmapSize >= maxSize()) {
            Log.d(TAG, "put: 条件二 大于了maxSize不满足，不能存入复用池");
            return;
        }

        //容器一: 就是为了存储
        put(bitmapSize, bitmap);
        //容器二：就是为了筛选key
        treeMap.put(bitmapSize, null);
        Log.d(TAG, "put: 添加到复用池");
    }

    /**
     * 计算Bitmap的大小
     * @param bitmap
     * @return
     */
    private int getBitmapSize(Bitmap bitmap) {
        // 最早期的时候 getRowBytes() + getHeight();

        // Android 3.0 12 API bitmap.getByteCount()
        // bitmap.getByteCount()

        // Android 4.4 19 API 以后的版本
        // bitmap.getAllocationByteCount();

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return bitmap.getAllocationByteCount();
        }
        return bitmap.getByteCount();
    }

    //获取可用复用的Bitmap
    @Override
    public Bitmap get(int width, int height, Bitmap.Config config) {
        /**
         * ALPHA_8 只有透明度 8位 1个字节
         * RGB_565 R红色5  G绿色6  B蓝色5  16位 2个字节 没有透明度
         * ARGB_4444 A4位  R4位  G4位  B4位  2个字节 有透明度
         * ARGB_8888 A8位  R8位  G8位  B8位  4个字节 有透明度
         */
        int getSize = width * height * (config == Bitmap.Config.ARGB_8888 ? 4 : 2);
        //容器二 就是为了筛选key
        Integer key = treeMap.ceilingKey(getSize); //通过size筛选key
        //如果treeMap 还没有put, 那么一定是null
        if(key == null) {
            return null; //没有找到和数的可以复用的key
        }

        Bitmap bitmap = remove(key);
        Log.d(TAG, "get: 从复用池中获取了Bitmap...");
        return bitmap;
    }
}
