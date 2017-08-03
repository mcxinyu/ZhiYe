package com.about.zhiye.util;

import android.content.Context;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by huangyuefeng on 2017/4/25.
 * Contact me : mcxinyu@foxmail.com
 */
public class QueryPreferences {
    public static final String IS_DRAWER_OPENED = "is_drawer_opened";

    public static final String SETTING_AUTO_REFRESH = "setting_auto_refresh";
    public static final String SETTING_COLORFUL = "setting_colorful";
    public static final String SETTING_NOTIFICATION = "setting_notification";
    public static final String SETTING_OPEN_CLIENT = "setting_open_client";
    public static final String SETTING_BACK_TO_TOP = "setting_back_to_top";
    public static final String SETTING_OPEN_BOTTOM_NAVIGATE = "setting_open_bottom_navigate";

    public static final String SETTING_CLEAN_CACHE = "setting_clean_cache";
    public static final String SETTING_CHECK_UPDATE = "setting_check_update";
    public static final String SETTING_ABOUT = "setting_about";
    public static final String SETTING_SHAKE_FEEDBACK = "setting_shake_feedback";
    public static final String SETTING_FEEDBACK = "setting_feedback";

    public static final String SEARCH_HISTORY = "search_history";

    public static boolean getDrawerOpenState(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(IS_DRAWER_OPENED, false);
    }

    public static void setDrawerOpenState(Context context, boolean enable) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(IS_DRAWER_OPENED, enable)
                .apply();
    }

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
                .getBoolean(SETTING_NOTIFICATION, true);
    }

    public static void setNotificationState(Context context, boolean enable) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(SETTING_NOTIFICATION, enable)
                .apply();
    }

    public static boolean getOpenClientState(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(SETTING_OPEN_CLIENT, false);
    }

    public static void setOpenClientState(Context context, boolean enable) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(SETTING_OPEN_CLIENT, enable)
                .apply();
    }

    public static boolean getBackToTopState(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(SETTING_BACK_TO_TOP, true);
    }

    public static void setBackToTopState(Context context, boolean enable) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(SETTING_BACK_TO_TOP, enable)
                .apply();
    }

    public static boolean getOpenBottomNavigateState(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(SETTING_OPEN_BOTTOM_NAVIGATE, true);
    }

    public static void setOpenBottomNavigateState(Context context, boolean enable) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(SETTING_OPEN_BOTTOM_NAVIGATE, enable)
                .apply();
    }

    public static boolean getSettingShakeFeedbackState(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(SETTING_SHAKE_FEEDBACK, true);
    }

    public static void setSettingShakeFeedbackState(Context context, boolean enable) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(SETTING_SHAKE_FEEDBACK, enable)
                .apply();
    }

    public static List<String> getSearchHistory(Context context) {
        String history = context.getSharedPreferences(SEARCH_HISTORY, 0)
                .getString(SEARCH_HISTORY, "");

        List<String> list = new ArrayList<>();
        if (!TextUtils.isEmpty(history)) {
            Collections.addAll(list, history.split(","));
            return list;
        }

        return list;
    }

    public static void setSearchHistory(Context context, String keyWord) {
        String oldHistory = context.getSharedPreferences(SEARCH_HISTORY, 0)
                .getString(SEARCH_HISTORY, "");

        String replaceKeyWord = keyWord.replaceAll("/[^'’[^\\p{P}]]/", "");

        if (!TextUtils.isEmpty(replaceKeyWord) && !oldHistory.contains(replaceKeyWord + ",")) {
            context.getSharedPreferences(SEARCH_HISTORY, 0)
                    .edit()
                    .putString(SEARCH_HISTORY, replaceKeyWord + "," + oldHistory)
                    .apply();
        }
    }
}
