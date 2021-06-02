package com.sty.ne.glide.core;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.sty.ne.glide.fragment.ActivityFragmentManager;
import com.sty.ne.glide.fragment.FragmentActivityManager;

/**
 * Author: ShiTianyi
 * Time: 2021/5/18 0018 20:46
 * Description:
 */
public class RequestManager {
    private static final String TAG = RequestManager.class.getSimpleName();
    private static final int NEXT_HANDLER_MSG = 995465;
    private final String FRAGMENT_ACTIVITY_NAME = "Fragment_Activity_Name";
    private final String ACTIVITY_NAME = "Activity_Name";
    private Context requestManagerContext;
    private static RequestTargetEngine requestTargetEngine;  //static important
    private FragmentActivity fragmentActivity;

    //构造代码块，不用在所有的构造方法中去实例化了，统一去写
    {
        if(requestTargetEngine == null) {
            requestTargetEngine = new RequestTargetEngine();
        }
    }

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            Fragment fragment = fragmentActivity.getSupportFragmentManager().findFragmentByTag(FRAGMENT_ACTIVITY_NAME);
            Log.e(TAG, "Handler: fragment " + fragment); //有值了
            return false;
        }
    });
    
    /**
     * 可以管理生命周期
     * @param fragmentActivity --> fragmentActivity是有生命周期方法的(Fragment)
     */
    public RequestManager(FragmentActivity fragmentActivity) {
        this.requestManagerContext = fragmentActivity;
        this.fragmentActivity = fragmentActivity;

        //拿到Fragment
        FragmentManager supportFragmentManager = fragmentActivity.getSupportFragmentManager();
        Fragment fragment = supportFragmentManager.findFragmentByTag(FRAGMENT_ACTIVITY_NAME);
        if(null == fragment) { //如果等于null就要去创建Fragment
            fragment = new FragmentActivityManager(requestTargetEngine); //Fragment的生命周期与requestTargetEngine关联起来了
            //添加到supportFragmentManager
            supportFragmentManager.beginTransaction().add(fragment, FRAGMENT_ACTIVITY_NAME).commitAllowingStateLoss();
        }

        //发送一次Handler
        mHandler.sendEmptyMessage(NEXT_HANDLER_MSG);

        Fragment fragment1 = supportFragmentManager.findFragmentByTag(FRAGMENT_ACTIVITY_NAME); //null:还在队列中，没有消费
        Log.e(TAG, "RequestManager fragment1: " + fragment1);
    }

    /**
     * 可以管理生命周期 --> Activity是有生命周期方法的(Fragment)
     * @param activity
     */
    public RequestManager(Activity activity) {
        this.requestManagerContext = activity;

        //拿到Fragment
        android.app.FragmentManager fragmentManager = activity.getFragmentManager();
        android.app.Fragment fragment = fragmentManager.findFragmentByTag(ACTIVITY_NAME);
        if(null == fragment) {
            fragment = new ActivityFragmentManager(requestTargetEngine);  //Fragment的生命周期与requestTargetEngine关联起来了
            //添加到管理器
            fragmentManager.beginTransaction().add(fragment, ACTIVITY_NAME).commitAllowingStateLoss(); //提交
        }

        //发送一次Handler
        mHandler.sendEmptyMessage(NEXT_HANDLER_MSG);

        android.app.Fragment fragment2 = fragmentManager.findFragmentByTag(ACTIVITY_NAME); //null:还在队列中，没有消费
        Log.e(TAG, "RequestManager fragment2: " + fragment2);
    }

    /**
     * 代表无法管理生命周期 --> 因为Application无法管理
     * @param context
     */
    public RequestManager(Context context) {
        this.requestManagerContext = context;
    }

    /**
     * 拿到要显示的图片路径
     * @param path
     * @return
     */
    public RequestTargetEngine load(String path) {
        //移除Handler
        mHandler.removeMessages(NEXT_HANDLER_MSG);
        //把值传递给资源加载引擎
        requestTargetEngine.loadValueInitAction(path, requestManagerContext);
        return requestTargetEngine;
    }
}
