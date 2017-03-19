package com.about.zhiye;

import android.app.Application;

/**
 * Created by huangyuefeng on 2017/3/17.
 * Contact me : mcxinyu@foxmail.com
 */
public class ZhiYeApp extends Application {
    private static ZhiYeApp zhiYeApp;

    public static ZhiYeApp getInstance() {
        return zhiYeApp;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        zhiYeApp = this;
    }
}
