package com.about.zhiye.fragment;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.support.annotation.Nullable;
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

import im.fir.sdk.FIR;
import im.fir.sdk.VersionCheckCallback;

/**
 * Created by huangyuefeng on 2017/4/25.
 * Contact me : mcxinyu@foxmail.com
 */
public class PreferencesFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener {
    public static final String TAG = "PreferencesFragment";

    private PreferenceScreen mCheckUpdatePreference;
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
        mCheckUpdatePreference.setSummary("当前版本：" + CheckUpdateHelper.getCurrentVersionName(getActivity()));
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
            case QueryPreferences.SETTING_FEEDBACK:
                sendEmailFeedback();
                return true;
        }
        return false;
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
                        mUpdateDialog = CheckUpdateHelper.buildUpdateDialog(getActivity(), versionInfoFir);
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

    public UpdateDialog getUpdateDialog() {
        return mUpdateDialog;
    }
}
