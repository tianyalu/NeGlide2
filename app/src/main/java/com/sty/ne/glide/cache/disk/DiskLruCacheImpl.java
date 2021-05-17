package com.sty.ne.glide.cache.disk;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import com.sty.ne.glide.Tool;
import com.sty.ne.glide.resource.Value;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Author: ShiTianyi
 * Time: 2021/5/17 0017 20:38
 * Description: 磁盘缓存的封装
 */
public class DiskLruCacheImpl {
    private static final String TAG = DiskLruCacheImpl.class.getSimpleName();

    // SD/disk_lru_cache_dir/900d489a4c4c31235dc856b6a982c4e0ae1f53f14dbf04733107b51553562c0a
    private static final String DISK_LRU_CACHE_DIR = "disk_lru_cache_dir"; //磁盘缓存的目录
    private final int APP_VERSION = 1; //我们的版本号，一旦修改这个版本号，之前的版本号失效
    private final int VALUE_COUNT = 1; //通常情况下都是1
    private final long MAX_SIZE = 1024 * 1024 * 10; //可以修改为使用者可设置的
    private DiskLruCache diskLruCache;

    public DiskLruCacheImpl(Context context) {
        File file = new File(context.getExternalFilesDir(null) + File.separator
                + DISK_LRU_CACHE_DIR);
        try {
            diskLruCache = DiskLruCache.open(file, APP_VERSION, VALUE_COUNT, MAX_SIZE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void put(String key, Value value) {
        DiskLruCache.Editor editor = null;
        OutputStream outputStream = null;
        try {
            editor = diskLruCache.edit(key);
            outputStream = editor.newOutputStream(0);//index 不能大于VALUE_COUNT
            Bitmap bitmap = value.getmBitmap();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
            //失败
            try {
                editor.abort();
            } catch (IOException ioException) {
                ioException.printStackTrace();
                Log.e(TAG, "put: editor.abort() e: " + e.getMessage());
            }
        }finally {
            try {
                editor.commit(); //类似sp，记得一定要提交
                diskLruCache.flush();
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "put: editor.commit() e: " + e.getMessage());
            }
            if(outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(TAG, "put: outputStream.close() e: " + e.getMessage());
                }
            }
        }
    }

    public Value get(String key) {
        Tool.checkNotEmpty(key);

        InputStream inputStream = null;
        try {
            DiskLruCache.Snapshot snapshot = diskLruCache.get(key);
            //判断快照不为null的情况下，再去读操作
            if(null != snapshot) {
                Value value = Value.getInstance();
                inputStream = snapshot.getInputStream(0); //index不能大于VALUE_COUNT
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                value.setmBitmap(bitmap);
                //保存key唯一标识
                value.setKey(key);
                return value;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(null != inputStream) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(TAG, "get: inputStream.close() e: " + e.getMessage());
                }
            }
        }
        return null; //好判断
    }
}
