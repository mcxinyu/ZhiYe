package com.about.zhiye.fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.about.zhiye.R;
import com.about.zhiye.model.VersionInfoFir;
import com.about.zhiye.util.CheckUpdateHelper;
import com.about.zhiye.util.QueryPreferences;
import com.google.gson.Gson;
import com.mikepenz.aboutlibraries.Libs;
import com.mikepenz.aboutlibraries.LibsBuilder;
import com.qiangxi.checkupdatelibrary.dialog.UpdateDialog;

import java.io.File;
import java.text.DecimalFormat;

import im.fir.sdk.FIR;
import im.fir.sdk.VersionCheckCallback;

import static com.qiangxi.checkupdatelibrary.dialog.UpdateDialog.UPDATE_DIALOG_PERMISSION_REQUEST_CODE;

/**
 * Created by huangyuefeng on 2017/4/25.
 * Contact me : mcxinyu@foxmail.com
 */
public class PreferencesFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener {
    public static final String TAG = "PreferencesFragment";

    private PreferenceScreen mCheckUpdatePreference;
    private PreferenceScreen mCleanCachePreference;
    private UpdateDialog mUpdateDialog;

    public static PreferencesFragment newInstance() {

        Bundle args = new Bundle();

        PreferencesFragment fragment = new PreferencesFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        initPreferences();
    }

    private void initPreferences() {
        findPreference(QueryPreferences.SETTING_AUTO_REFRESH).setOnPreferenceClickListener(this);
        findPreference(QueryPreferences.SETTING_COLORFUL).setOnPreferenceClickListener(this);
        findPreference(QueryPreferences.SETTING_NOTIFICATION).setOnPreferenceClickListener(this);
        findPreference(QueryPreferences.SETTING_ABOUT).setOnPreferenceClickListener(this);
        findPreference(QueryPreferences.SETTING_FEEDBACK).setOnPreferenceClickListener(this);
        mCheckUpdatePreference = (PreferenceScreen) findPreference(QueryPreferences.SETTING_CHECK_UPDATE);
        mCheckUpdatePreference.setOnPreferenceClickListener(this);

        mCleanCachePreference = (PreferenceScreen) findPreference(QueryPreferences.SETTING_CLEAN_CACHE);
        mCleanCachePreference.setOnPreferenceClickListener(this);

        initSummary();
    }

    private void initSummary() {
        mCheckUpdatePreference.setSummary("当前版本：" + CheckUpdateHelper.getCurrentVersionName(getActivity()));
        mCleanCachePreference.setSummary("缓存占用：" + getCacheSizeDesc());
    }

    private String getCacheSizeDesc() {
        return formatFileSize(getCacheSize());
    }

    private String formatFileSize(long fileS) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString;
        if (fileS == 0) {
            fileSizeString = "0MB";
        } else if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "KB";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "MB";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "GB";
        }
        return fileSizeString;
    }

    private long getCacheSize() {
        try {
            return getFolderSize(getActivity().getCacheDir()) +
                    getFolderSize(getActivity().getExternalCacheDir());
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    private long getFolderSize(File file) throws Exception {
        long size = 0;
        try {
            File[] fileList = file.listFiles();
            for (File aFileList : fileList) {
                if (aFileList.isDirectory()) {
                    size = size + getFolderSize(aFileList);
                } else {
                    size = size + aFileList.length();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        String key = preference.getKey();
        switch (key) {
            case QueryPreferences.SETTING_AUTO_REFRESH:
                break;
            case QueryPreferences.SETTING_COLORFUL:
                break;
            case QueryPreferences.SETTING_NOTIFICATION:
                break;
            case QueryPreferences.SETTING_ABOUT:
                startAboutActivity();
                return true;
            case QueryPreferences.SETTING_CHECK_UPDATE:
                checkForUpdate();
                return true;
            case QueryPreferences.SETTING_CLEAN_CACHE:
                clearAppCache();
                return true;
            case QueryPreferences.SETTING_FEEDBACK:
                sendEmailFeedback();
                return true;
        }
        return false;
    }

    private void clearAppCache() {
        final Handler handler = new Handler() {
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    mCleanCachePreference.setSummary("缓存占用：" + getCacheSizeDesc());
                    Toast.makeText(getActivity(), getString(R.string.clean_cache_up), Toast.LENGTH_SHORT).show();
                } else {
                    mCleanCachePreference.setSummary("缓存占用：" + getCacheSizeDesc());
                    Toast.makeText(getActivity(), getString(R.string.clean_cache_fail), Toast.LENGTH_SHORT).show();
                }
            }
        };
        new Thread() {
            public void run() {
                Message msg = new Message();
                try {
                    clearCache();
                    msg.what = 1;
                } catch (Exception e) {
                    e.printStackTrace();
                    msg.what = -1;
                }
                handler.sendMessage(msg);
            }
        }.start();
    }

    private void clearCache() {
        //清除数据缓存
        cleanCacheFolder(getActivity().getCacheDir(), System.currentTimeMillis());
        cleanCacheFolder(getActivity().getExternalCacheDir(), System.currentTimeMillis());
    }

    private int cleanCacheFolder(File dir, long curTime) {
        int deletedFiles = 0;
        if (dir != null && dir.isDirectory()) {
            try {
                for (File child : dir.listFiles()) {
                    if (child.isDirectory()) {
                        deletedFiles += cleanCacheFolder(child, curTime);
                    }
                    if (child.lastModified() < curTime) {
                        if (child.delete()) {
                            deletedFiles++;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return deletedFiles;
    }

    private void checkForUpdate() {
        try {
            ApplicationInfo appInfo = getActivity().getPackageManager()
                    .getApplicationInfo(getActivity().getPackageName(), PackageManager.GET_META_DATA);
            String firToken = appInfo.metaData.getString("fir_token");

            FIR.checkForUpdateInFIR(firToken, new VersionCheckCallback() {
                @Override
                public void onSuccess(String versionJson) {
                    Log.i(TAG, "check from fir.im success! " + "\n" + versionJson);
                    final VersionInfoFir versionInfoFir = new Gson().fromJson(versionJson, VersionInfoFir.class);

                    if (versionInfoFir.getVersion() > CheckUpdateHelper.getCurrentVersionCode(getActivity())) {
                        mUpdateDialog = CheckUpdateHelper.buildUpdateDialog(getActivity(), PreferencesFragment.this, versionInfoFir);
                    } else {
                        mCheckUpdatePreference.setSummary("当前为最新版本：" + CheckUpdateHelper.getCurrentVersionName(getActivity()));
                    }
                }

                @Override
                public void onFail(Exception exception) {
                    Log.i(TAG, "check fir.im fail! " + "\n" + exception.getMessage());
                    Toast.makeText(getActivity(), "更新失败", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onStart() {
                    Toast.makeText(getActivity(), "正在获取更新", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFinish() {
                    // Toast.makeText(getActivity(), "获取完成", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void startAboutActivity() {
        new LibsBuilder()
                .withAboutIconShown(true)
                .withAboutVersionShown(true)
                .withAboutDescription(getString(R.string.aboutLibraries_description_text))
                .withAboutAppName(getString(R.string.app_name))
                .withActivityTitle(getString(R.string.title_settings_others_about))
                .withActivityStyle(Libs.ActivityStyle.LIGHT_DARK_TOOLBAR)
                .start(getActivity());
    }

    private void sendEmailFeedback() {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        String uriText = "mailto:mcxinyu@gmail.com?subject=Feedback&body=";
        uriText = uriText.replace(" ", "%20");
        Uri uri = Uri.parse(uriText);
        intent.setData(uri);
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        //如果用户同意所请求的权限
        if (permissions[0].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //所以在进行判断时,必须要结合这两个常量进行判断.
            //非强制更新对话框
            if (requestCode == UPDATE_DIALOG_PERMISSION_REQUEST_CODE) {
                //进行下载操作
                mUpdateDialog.download();
            }
        } else {
            //用户不同意,提示用户,如下载失败,因为您拒绝了相关权限
            Toast.makeText(getActivity(), "程序将无法正常运行", Toast.LENGTH_SHORT).show();
            if (!ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                Log.e(TAG, "false.请开启读写sd卡权限,不然无法正常工作");
            } else {
                Log.e(TAG, "true.请开启读写sd卡权限,不然无法正常工作");
            }
        }
    }
}
