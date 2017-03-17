package com.about.zhiye.util;

import android.content.Context;

/**
 * Created by huangyuefeng on 2017/3/14.
 * Contact me : mcxinyu@foxmail.com
 */
public class ScreenUtil {
    private static ScreenUtil mScreenUtil;
    private Context mContext;

    private ScreenUtil(Context context) {
        mContext = context;
    }

    public static ScreenUtil instance(Context context) {
        if (mScreenUtil == null) {
            mScreenUtil = new ScreenUtil(context);
        }
        return mScreenUtil;
    }

    public int dip2px(int i) {
        return (int) (0.5D + (double) (getDensity(mContext) * (float) i));
    }

    private float getDensity(Context context) {
        return context.getResources().getDisplayMetrics().density;
    }

    public int getScreenWidth() {
        return mContext.getResources().getDisplayMetrics().widthPixels;
    }
}
