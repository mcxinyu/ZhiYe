package com.about.zhiye;

import android.app.Application;

import com.oubowu.slideback.ActivityHelper;
import com.pgyersdk.crash.PgyCrashManager;

/**
 * Created by huangyuefeng on 2017/3/17.
 * Contact me : mcxinyu@foxmail.com
 */
public class ZhiYeApp extends Application {
    private ActivityHelper mActivityHelper;
    private static ZhiYeApp zhiYeApp;

    public static ZhiYeApp getInstance() {
        return zhiYeApp;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        PgyCrashManager.register(this);

        mActivityHelper = new ActivityHelper();
        registerActivityLifecycleCallbacks(mActivityHelper);

        zhiYeApp = this;
    }

    public static ActivityHelper getActivityHelper() {
        return zhiYeApp.mActivityHelper;
    }
}
