package com.about.zhiye.util;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.about.zhiye.R;
import com.about.zhiye.model.VersionInfoFir;
import com.qiangxi.checkupdatelibrary.dialog.ForceUpdateDialog;
import com.qiangxi.checkupdatelibrary.dialog.UpdateDialog;

/**
 * Created by huangyuefeng on 2017/5/8.
 * Contact me : mcxinyu@foxmail.com
 */
public class CheckUpdateHelper {

    /**
     * 强制更新
     */
    private static ForceUpdateDialog buildForceUpdateDialog(Activity activity,
                                                            @Nullable android.app.Fragment fragment,
                                                            @Nullable Fragment compatFragment,
                                                            VersionInfoFir versionInfo) {
        ForceUpdateDialog forceUpdateDialog;
        if (fragment != null) {
            forceUpdateDialog = new ForceUpdateDialog(activity, fragment);
        } else if (compatFragment != null) {
            forceUpdateDialog = new ForceUpdateDialog(activity, compatFragment);
        } else {
            forceUpdateDialog = new ForceUpdateDialog(activity);
        }
        forceUpdateDialog.setAppSize(versionInfo.getBinary().getFileSize())
                .setDownloadUrl(versionInfo.getInstallUrl())
                .setTitle(versionInfo.getName() + "有更新啦")
                .setReleaseTime(versionInfo.getUpdatedAt())
                .setVersionName(versionInfo.getVersionShort())
                .setUpdateDesc(versionInfo.getChangelog())
                .setFileName(versionInfo.getName() + " v" + versionInfo.getVersionShort() + ".apk")
                .setFilePath(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath())
                .show();
        return forceUpdateDialog;
    }

    public static ForceUpdateDialog buildForceUpdateDialog(Activity activity, android.app.Fragment fragment, VersionInfoFir versionInfo) {
        return buildForceUpdateDialog(activity, fragment, null, versionInfo);
    }

    public static ForceUpdateDialog buildForceUpdateDialog(Activity activity, Fragment compatFragment, VersionInfoFir versionInfo) {
        return buildForceUpdateDialog(activity, null, compatFragment, versionInfo);
    }

    public static ForceUpdateDialog buildForceUpdateDialog(Activity activity, VersionInfoFir versionInfo) {
        return buildForceUpdateDialog(activity, null, null, versionInfo);
    }

    /**
     * 非强制更新
     */
    private static UpdateDialog buildUpdateDialog(Activity activity,
                                                  @Nullable android.app.Fragment fragment,
                                                  @Nullable Fragment compatFragment,
                                                  VersionInfoFir versionInfo) {
        UpdateDialog updateDialog;
        if (fragment != null) {
            updateDialog = new UpdateDialog(activity, fragment);
        } else if (compatFragment != null) {
            updateDialog = new UpdateDialog(activity, compatFragment);
        } else {
            updateDialog = new UpdateDialog(activity);
        }
        updateDialog.setAppSize(versionInfo.getBinary().getFileSize())
                .setDownloadUrl(versionInfo.getInstallUrl())
                .setTitle(versionInfo.getName() + "有更新啦")
                .setReleaseTime(versionInfo.getUpdatedAt())
                .setVersionName(versionInfo.getVersionShort())
                .setUpdateDesc(versionInfo.getChangelog())
                .setFileName(versionInfo.getName() + " v" + versionInfo.getVersionShort() + ".apk")
                .setFilePath(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath())
                .setShowProgress(true)
                .setIconResId(R.mipmap.ic_launcher)
                .setAppName(versionInfo.getName())
                .show();
        return updateDialog;
    }

    public static UpdateDialog buildUpdateDialog(Activity activity, android.app.Fragment fragment, VersionInfoFir versionInfo) {
        return buildUpdateDialog(activity, fragment, null, versionInfo);
    }

    public static UpdateDialog buildUpdateDialog(Activity activity, Fragment compatFragment, VersionInfoFir versionInfo) {
        return buildUpdateDialog(activity, null, compatFragment, versionInfo);
    }

    public static UpdateDialog buildUpdateDialog(Activity activity, VersionInfoFir versionInfo) {
        return buildUpdateDialog(activity, null, null, versionInfo);
    }

    /**
     * 获取当前应用版本号
     */
    public static int getCurrentVersionCode(Context context) {
        try {
            return getPackageInfo(context).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * 获取当前应用版本号
     */
    public static String getCurrentVersionName(Context context) {
        try {
            return getPackageInfo(context).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
    }

    private static PackageInfo getPackageInfo(Context context) throws PackageManager.NameNotFoundException {
        PackageManager packageManager = context.getPackageManager();
        return packageManager.getPackageInfo(context.getPackageName(), 0);
    }
}
