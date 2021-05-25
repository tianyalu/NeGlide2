package com.sty.ne.glide.core;

import android.app.Activity;
import android.content.Context;

import androidx.fragment.app.FragmentActivity;

/**
 * Author: ShiTianyi
 * Time: 2021/5/18 0018 20:43
 * Description:
 */
public class Glide {
    private RequestManagerRetriever retriever;

    public Glide(RequestManagerRetriever retriever) {
        this.retriever = retriever;
    }

    public static RequestManager with(FragmentActivity fragmentActivity) {
        return getRetriever(fragmentActivity).get(fragmentActivity);
    }

    public static RequestManager with(Activity activity) {
        return getRetriever(activity).get(activity);
    }

    public static RequestManager with(Context context) {
        return getRetriever(context).get(context);
    }

    /**
     * RequestManager由我们的 RequestManagerRetriever 创建的
     * @param context
     * @return
     */
    public static RequestManagerRetriever getRetriever(Context context) {
        return Glide.get(context).getRetriever();
    }

    /**
     * Glide是new出来的
     * @param context
     * @return
     */
    public static Glide get(Context context) {
        return new GlideBuilder().build();
    }

    public RequestManagerRetriever getRetriever() {
        return retriever;
    }
}
