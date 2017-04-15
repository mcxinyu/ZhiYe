package com.about.zhiye.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.about.zhiye.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by huangyuefeng on 2017/4/13.
 * Contact me : mcxinyu@foxmail.com
 */
public abstract class SingleFragmentActivity extends AppCompatActivity {

    @BindView(R.id.app_bar_layout)
    AppBarLayout mAppBarLayout;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.fragment_container)
    FrameLayout mFragmentContainer;

    protected abstract Fragment createFragment();

    protected abstract boolean setHasToolbar();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_fragment);
        ButterKnife.bind(this);

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.fragment_container);
        if (fragment == null) {
            fragment = createFragment();
            fragmentManager.beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    .commit();
        }

        if (setHasToolbar()) {
            initToolbar();
        }
    }

    private void initToolbar() {
        setSupportActionBar(mToolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
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
