package com.about.zhiye.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.about.zhiye.R;
import com.about.zhiye.fragment.PickDateFragment;

/**
 * Created by huangyuefeng on 2017/4/11.
 * Contact me : mcxinyu@foxmail.com
 */
public class PickDateActivity extends SingleFragmentActivity {

    public static Intent newIntent(Context context) {

        Intent intent = new Intent(context, PickDateActivity.class);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        return PickDateFragment.newInstance();
    }

    @Override
    protected boolean setHasToolbar() {
        mToolbar.setTitle(getString(R.string.title_pick_date));
        return true;
    }
}
