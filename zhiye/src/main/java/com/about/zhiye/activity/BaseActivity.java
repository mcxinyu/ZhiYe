package com.about.zhiye.activity;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.about.zhiye.R;

/**
 * Created by huangyuefeng on 2017/4/15.
 * Contact me : mcxinyu@foxmail.com
 * 具有一个简单的 FrameLayout 来存放 Fragment 的 Activity 抽象类
 */
public abstract class BaseActivity extends BaseAppCompatActivity {

    protected abstract Fragment createFragment();

    @LayoutRes
    protected int getContentViewId() {
        return R.layout.activity_frame_layout;
    }

    @IdRes
    protected int getFragmentContentId() {
        return R.id.fragment_container;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentViewId());

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(getFragmentContentId());
        if (fragment == null) {
            fragment = createFragment();
            fragmentManager.beginTransaction()
                    .add(getFragmentContentId(), fragment)
                    .commit();
        }
    }
}
