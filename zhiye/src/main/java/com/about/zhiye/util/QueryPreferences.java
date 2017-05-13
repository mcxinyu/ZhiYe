package com.about.zhiye.util;

import android.content.Context;
import android.preference.PreferenceManager;

/**
 * Created by huangyuefeng on 2017/4/25.
 * Contact me : mcxinyu@foxmail.com
 */
public class QueryPreferences {
    public static final String SETTING_AUTO_REFRESH = "setting_auto_refresh";
    public static final String SETTING_COLORFUL = "setting_colorful";
    public static final String SETTING_NOTIFICATION = "setting_notification";

    public static final String SETTING_CLEAN_CACHE = "setting_clean_cache";
    public static final String SETTING_CHECK_UPDATE = "setting_check_update";
    public static final String SETTING_ABOUT = "setting_about";
    public static final String SETTING_FEEDBACK = "setting_feedback";

    public static boolean getAutoRefreshState(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(SETTING_AUTO_REFRESH, true);
    }

    public static void setAutoRefreshState(Context context, boolean enable) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(SETTING_AUTO_REFRESH, enable)
                .apply();
    }

    public static boolean getColorfulState(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(SETTING_COLORFUL, false);
    }

    public static void setColorfulState(Context context, boolean colorful) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(SETTING_COLORFUL, colorful)
                .apply();
    }

    public static boolean getNotificationState(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(SETTING_NOTIFICATION, false);
    }

    public static void setNotificationState(Context context, boolean enable) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(SETTING_NOTIFICATION, enable)
                .apply();
    }
}
