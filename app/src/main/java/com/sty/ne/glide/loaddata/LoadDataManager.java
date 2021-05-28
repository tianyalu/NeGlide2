package com.sty.ne.glide.loaddata;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.net.UrlQuerySanitizer;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.sty.ne.glide.resource.Value;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Author: ShiTianyi
 * Time: 2021/5/27 0027 20:00
 * Description:
 */
public class LoadDataManager implements ILoadData, Runnable{
    private static final String TAG = LoadDataManager.class.getSimpleName();
    private String path;
    private ResponseListener responseListener;
    private Context context;
    private static volatile ThreadPoolExecutor threadPoolExecutor;

    public ThreadPoolExecutor getThreadPoolExecutor() {
        if(threadPoolExecutor == null) {
            synchronized (LoadDataManager.this) {
                if(threadPoolExecutor == null) {
                    threadPoolExecutor =  threadPoolExecutor = new ThreadPoolExecutor(0, Integer.MAX_VALUE,
                            60, TimeUnit.SECONDS,
                            new SynchronousQueue<>());
                }
            }
        }
        return threadPoolExecutor;
    }

    @Override
    public Value loadResource(String path, ResponseListener responseListener, Context context) {
        this.path = path;
        this.responseListener = responseListener;
        this.context = context;

        //加载 网络图片/本地SD卡中的图片
        Uri uri = Uri.parse(path);

        //网络图片
        if("HTTP".equalsIgnoreCase(uri.getScheme()) || "HTTPS".equalsIgnoreCase(uri.getScheme())) {
            getThreadPoolExecutor().execute(this);
        }

        //SD本地图片, 返回Value

        return null;
    }

    @Override
    public void run() {
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;

        try {
            URL url = new URL(path);
            URLConnection urlConnection = url.openConnection();
            httpURLConnection = (HttpURLConnection) urlConnection;
            httpURLConnection.setConnectTimeout(5000);
            int responseCode = httpURLConnection.getResponseCode();
            if(HttpURLConnection.HTTP_OK == responseCode) {
                inputStream = httpURLConnection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                //切换主线程
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Value value = Value.getInstance();
                        value.setmBitmap(bitmap);
                        //回调成功
                        responseListener.responseSuccess(value);
                    }
                });
            }else {
                //切换主线程
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        //回调失败
                        responseListener.responseException(new IllegalStateException("请求失败，请求码：" + responseCode));
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d(TAG, "run: 关闭inputStream.close() e: " + e.getMessage());
                }
            }
            if(httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }
    }
}
