package com.about.zhiye.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by 跃峰 on 2016/9/18.
 * Contact Me : mcxinyu@foxmail.com
 * 手机状态工具类
 */
public class StateUtils {

    public static boolean isNetworkAvailable(Context context){
        if (context != null){
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = connectivityManager.getActiveNetworkInfo();
            if (info != null) {
                return info.isAvailable();
            }
        }
        return false;
    }


}
