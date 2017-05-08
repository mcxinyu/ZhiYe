package com.about.zhiye.util;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;

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
    public static ForceUpdateDialog buildForceUpdateDialog(Activity activity, VersionInfoFir versionInfo) {
        ForceUpdateDialog forceUpdateDialog = new ForceUpdateDialog(activity);
        forceUpdateDialog.setAppSize(versionInfo.getBinary().getFileSize())
                .setDownloadUrl(versionInfo.getInstallUrl())
                .setTitle(versionInfo.getName() + "有更新啦")
                .setReleaseTime(versionInfo.getUpdatedAt())
                .setVersionName(versionInfo.getVersionShort())
                .setUpdateDesc(versionInfo.getChangelog())
                .setFileName(versionInfo.getName() + " v" + versionInfo.getVersionShort() + ".apk")
                .setFilePath(Environment.getExternalStorageDirectory().getPath() + "/update")
                .show();
        return forceUpdateDialog;
    }

    /**
     * 非强制更新
     */
    public static UpdateDialog buildUpdateDialog(Activity activity, VersionInfoFir versionInfo) {
        UpdateDialog updateDialog = new UpdateDialog(activity);
        updateDialog.setAppSize(versionInfo.getBinary().getFileSize())
                .setDownloadUrl(versionInfo.getInstallUrl())
                .setTitle(versionInfo.getName() + "有更新啦")
                .setReleaseTime(versionInfo.getUpdatedAt())
                .setVersionName(versionInfo.getVersionShort())
                .setUpdateDesc(versionInfo.getChangelog())
                .setFileName(versionInfo.getName() + " v" + versionInfo.getVersionShort() + ".apk")
                .setFilePath(Environment.getExternalStorageDirectory().getPath() + "/Download")
                .setShowProgress(true)
                .setIconResId(R.mipmap.ic_launcher)
                .setAppName(versionInfo.getName())
                .show();
        return updateDialog;
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
