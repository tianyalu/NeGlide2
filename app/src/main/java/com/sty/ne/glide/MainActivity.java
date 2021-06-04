package com.sty.ne.glide;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.sty.ne.glide.cache.pool.BitmapPool;
import com.sty.ne.glide.cache.pool.BitmapPoolImpl;
import com.sty.ne.glide.core.Glide;
import com.sty.ne.glide.utils.PermissionUtils;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class MainActivity extends AppCompatActivity implements Runnable{
    private String[] needPermissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private static final String IMAGE_URL = "https://cn.bing.com/sa/simg/hpb/LaDigue_EN-CA1115245085_1920x1080.jpg";
    private BitmapPool bitmapPool = new BitmapPoolImpl(1024 * 1024 * 6);

    private ImageView ivImage1;
    private ImageView ivImage2;
    private ImageView ivImage3;
    private ImageView ivImage4;
    private Button btn1;
    private Button btn2;
    private Button btn3;
    private Button btnPool;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initListeners();
        requestPermission();
    }

    private void initView() {
        ivImage1 = findViewById(R.id.iv_image1);
        ivImage2 = findViewById(R.id.iv_image2);
        ivImage3 = findViewById(R.id.iv_image3);
        ivImage4 = findViewById(R.id.iv_image4);
        btn1 = findViewById(R.id.btn1);
        btn2 = findViewById(R.id.btn2);
        btn3 = findViewById(R.id.btn3);
        btnPool = findViewById(R.id.btn_pool);
    }

    private void initListeners() {
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBtn1Clicked();
            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBtn2Clicked();
            }
        });
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBtn3Clicked();
            }
        });
        btnPool.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBtnPoolClicked();
            }
        });
    }


    private void requestPermission() {
        if (!PermissionUtils.checkPermissions(this, needPermissions)) {
            PermissionUtils.requestPermissions(this, needPermissions);
        }
    }

    // 加载次图片：https://cn.bing.com/sa/simg/hpb/LaDigue_EN-CA1115245085_1920x1080.jpg
    private void onBtn1Clicked() {
        Glide.with(this).load(IMAGE_URL).into(ivImage1);
    }

    private void onBtn2Clicked() {
        Glide.with(this).load(IMAGE_URL).into(ivImage2);

    }

    private void onBtn3Clicked() {
        Glide.with(this).load(IMAGE_URL).into(ivImage3);
    }

    private void onBtnPoolClicked() {
        new Thread(this).start();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == PermissionUtils.REQUEST_PERMISSIONS_CODE) {
            if (!PermissionUtils.verifyPermissions(grantResults)) {
                PermissionUtils.showMissingPermissionDialog(this);
            } else {

            }
        }
    }

    @Override
    public void run() {
        try {
            URL url = new URL(IMAGE_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(5000);
            int responseCode = connection.getResponseCode();
            if(HttpURLConnection.HTTP_OK == responseCode) {
                InputStream inputStream = connection.getInputStream();
                /*BitmapFactory.Options options = new BitmapFactory.Options(); // 拿到图片宽和高
                options.inJustDecodeBounds = true; // 只拿到周围信息，outXXX， outW，outH
                // options = null; 执行下面代码
                BitmapFactory.decodeStream(inputStream, null, options);
                int w = options.outWidth;
                int h = options.outHeight;*/

                int w = 1920;
                int h = 1080;

                //复用内存
                BitmapFactory.Options options = new BitmapFactory.Options();
                // 拿到复用池  条件： bitmap.isMutable() == true;
                Bitmap bitmapPoolResult = bitmapPool.get(w, h, Bitmap.Config.RGB_565);

                // 如果设置为null，内部就不会去申请新的内存空间，无非复用，依然会照成：内存抖动，内存碎片
                options.inBitmap = bitmapPoolResult; // 把复用池的Bitmap 给 inBitmap
                options.inPreferredConfig = Bitmap.Config.RGB_565; // 2个字节
                options.inJustDecodeBounds = false;
                options.inMutable = true; // 符合 复用机制
                final Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null, options); // 复用内存

                // 添加到复用池
                bitmapPool.put(bitmap);

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        ivImage4.setImageBitmap(bitmap);
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}