package com.about.zhiye.util;

import android.content.Context;
import android.preference.PreferenceManager;

/**
 * Created by huangyuefeng on 2017/4/25.
 * Contact me : mcxinyu@foxmail.com
 */
public class QueryPreferences {
    public static final String SETTING_AUTO_REFRESH = "setting_auto_refresh";
    public static final String SETTING_ABOUT = "setting_about";
    public static final String SETTING_FEEDBACK = "setting_feedback";

    public static boolean getAutoRefreshState(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(SETTING_AUTO_REFRESH, true);
    }

    public static void setAutoRefreshState(Context context, boolean enable){
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(SETTING_AUTO_REFRESH, enable)
                .apply();
    }
}
