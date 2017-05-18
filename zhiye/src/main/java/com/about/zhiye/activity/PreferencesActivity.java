package com.about.zhiye.activity;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;

import com.about.zhiye.fragment.ZhiyePreferenceFragment;

/**
 * Created by huangyuefeng on 2017/4/25.
 * Contact me : mcxinyu@foxmail.com
 */
public class PreferencesActivity extends SingleFragmentActivity {
    private static String TAG = "PreferencesActivity";

    public static Intent newIntent(Context context) {

        Intent intent = new Intent(context, PreferencesActivity.class);
        // intent.putExtra();
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        return ZhiyePreferenceFragment.newInstance();
    }

    @Override
    protected boolean setHasToolbar() {
        // 关闭 Toolbar 的滚动。
        AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) mToolbar.getLayoutParams();
        params.setScrollFlags(0);
        // mToolbar.setTitle(getString(R.string.title_pick_date));
        return true;
    }
}
