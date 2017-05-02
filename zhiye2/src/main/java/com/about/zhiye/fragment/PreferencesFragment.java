package com.about.zhiye.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;

import com.about.zhiye.R;
import com.about.zhiye.util.QueryPreferences;
import com.mikepenz.aboutlibraries.Libs;
import com.mikepenz.aboutlibraries.LibsBuilder;

/**
 * Created by huangyuefeng on 2017/4/25.
 * Contact me : mcxinyu@foxmail.com
 */
public class PreferencesFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener {

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
        findPreference(QueryPreferences.SETTING_ABOUT).setOnPreferenceClickListener(this);
        findPreference(QueryPreferences.SETTING_FEEDBACK).setOnPreferenceClickListener(this);
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        String key = preference.getKey();
        switch (key) {
            case QueryPreferences.SETTING_AUTO_REFRESH:
                break;
            case QueryPreferences.SETTING_COLORFUL:
                break;
            case QueryPreferences.SETTING_ABOUT:
                startAboutActivity();
                return true;
            case QueryPreferences.SETTING_FEEDBACK:
                sendEmailFeedback();
                return true;
        }
        return false;
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
}
