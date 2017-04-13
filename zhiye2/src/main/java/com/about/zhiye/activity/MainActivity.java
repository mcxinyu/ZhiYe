package com.about.zhiye.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.about.zhiye.R;
import com.about.zhiye.fragment.ReadLaterFragment;
import com.about.zhiye.fragment.ThemeFragment;
import com.about.zhiye.fragment.ZhihuFragment;
import com.about.zhiye.util.StateUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by huangyuefeng on 2017/3/17.
 * Contact me : mcxinyu@foxmail.com
 * 管理 fragment
 */
public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";

    @BindView(R.id.fragment_content)
    FrameLayout mFragmentContent;
    @BindView(R.id.bottom_navigation)
    BottomNavigationView mBottomNavigation;
    @BindView(R.id.status_bar_view)
    View mStatusBarView;
    Unbinder unbinder;

    private FragmentManager mFragmentManager;

    private ZhihuFragment mZhihuFragment;
    private ThemeFragment mThemeFragment;
    private ReadLaterFragment mReadLaterFragment;
    private Fragment currentFragment;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    if (mZhihuFragment == null) {
                        mZhihuFragment = ZhihuFragment.newInstance();
                    }
                    mStatusBarView.setVisibility(View.GONE);
                    switchFragment(mZhihuFragment);
                    return true;
                case R.id.navigation_themes:
                    if (mThemeFragment == null) {
                        mThemeFragment = ThemeFragment.newInstance();
                    }
                    mStatusBarView.setVisibility(View.VISIBLE);
                    switchFragment(mThemeFragment);
                    return true;
                case R.id.navigation_read_later:
                    if (mReadLaterFragment == null) {
                        mReadLaterFragment = ReadLaterFragment.newInstance();
                    }
                    mStatusBarView.setVisibility(View.VISIBLE);
                    switchFragment(mReadLaterFragment);
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        unbinder = ButterKnife.bind(this);
        setStatusBarView();

        initToolbar();

        mFragmentManager = getSupportFragmentManager();
        currentFragment = mFragmentManager.findFragmentById(R.id.fragment_content);

        if (currentFragment == null) {
            currentFragment = mZhihuFragment = ZhihuFragment.newInstance();
            mFragmentManager.beginTransaction()
                    .add(R.id.fragment_content, currentFragment)
                    .commit();
        }

        mBottomNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    private void switchFragment(Fragment fragment) {

        if (currentFragment != fragment) {
            FragmentTransaction transaction = mFragmentManager.beginTransaction();
            if (fragment.isAdded()) {
                transaction.hide(currentFragment)
                        .show(fragment)
                        .commit();
            } else {
                transaction.hide(currentFragment)
                        .add(R.id.fragment_content, fragment)
                        .commit();
            }
            currentFragment.setUserVisibleHint(false);
            fragment.setUserVisibleHint(true);
            currentFragment = fragment;
        }
    }

    private void initToolbar() {
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setHomeButtonEnabled(true);
        }
    }

    private void setStatusBarView(){
        // ViewGroup contentView = (ViewGroup) findViewById(android.R.id.content);
        // View statusBarView = new View(MainActivity.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                StateUtils.getStatusBarHeight(this));
        mStatusBarView.setLayoutParams(lp);
        mStatusBarView.setVisibility(View.GONE);
        // contentView.addView(statusBarView, lp);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
