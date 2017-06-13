package com.about.zhiye.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.MenuItem;

import com.about.zhiye.fragment.SingleZhihuThemeFragment;

/**
 * Created by huangyuefeng on 2017/4/16.
 * Contact me : mcxinyu@foxmail.com
 */
public class ThemeActivity extends BaseActivity {

    private static final String EXTRA_THEME_ID = "theme_id";
    private static final String EXTRA_THEME_NAME = "theme_name";

    private int mThemeId;
    private String mThemeName;

    public static Intent newIntent(Context context, String themeName, int themeId) {

        Intent intent = new Intent(context, ThemeActivity.class);
        intent.putExtra(EXTRA_THEME_NAME, themeName);
        intent.putExtra(EXTRA_THEME_ID, themeId);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        return SingleZhihuThemeFragment.newInstance(mThemeName, mThemeId);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        mThemeId = getIntent().getIntExtra(EXTRA_THEME_ID, -1);
        mThemeName = getIntent().getStringExtra(EXTRA_THEME_NAME);

        super.onCreate(savedInstanceState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
