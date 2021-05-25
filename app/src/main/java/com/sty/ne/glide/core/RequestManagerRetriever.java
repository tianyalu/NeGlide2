package com.sty.ne.glide.core;

import android.app.Activity;
import android.content.Context;

import androidx.fragment.app.FragmentActivity;

/**
 * Author: ShiTianyi
 * Time: 2021/5/18 0018 20:47
 * Description: 管理RequestManager
 */
public class RequestManagerRetriever {
    public RequestManager get(FragmentActivity fragmentActivity) {
        return new RequestManager(fragmentActivity);
    }

    public RequestManager get(Activity activity) {
        return new RequestManager(activity);
    }

    public RequestManager get(Context context) {
        return new RequestManager(context);
    }
}
