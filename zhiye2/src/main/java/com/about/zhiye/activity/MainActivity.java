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
import android.widget.FrameLayout;

import com.about.zhiye.R;
import com.about.zhiye.fragment.ZhihuFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by huangyuefeng on 2017/3/17.
 * Contact me : mcxinyu@foxmail.com
 * 管理 fragment
 */
public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";

    // @BindView(R.id.toolbar)
    // Toolbar mToolbar;
    @BindView(R.id.fragment_content)
    FrameLayout mFragmentContent;
    @BindView(R.id.bottom_navigation)
    BottomNavigationView mBottomNavigation;

    private FragmentManager mFragmentManager;

    private ZhihuFragment mZhihuFragment;
    private ZhihuFragment mZhihuFragment1;
    private ZhihuFragment mZhihuFragment2;
    private Fragment currentFragment;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    // mToolbar.setTitle(getString(R.string.title_zhihu));
                    switchFragment(mZhihuFragment);
                    return true;
                case R.id.navigation_dashboard:
                    // mToolbar.setTitle(getString(R.string.title_dashboard));
                    switchFragment(mZhihuFragment1);
                    return true;
                case R.id.navigation_notifications:
                    // mToolbar.setTitle(getString(R.string.title_notifications));
                    switchFragment(mZhihuFragment2);
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        initToolbar();

        mFragmentManager = getSupportFragmentManager();
        currentFragment = mFragmentManager.findFragmentById(R.id.fragment_content);

        if (currentFragment == null){
            currentFragment = mZhihuFragment = ZhihuFragment.newInstance();
            mFragmentManager.beginTransaction()
                    .add(R.id.fragment_content, currentFragment)
                    .commit();
        }

        mBottomNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    private void switchFragment(Fragment fragment) {
        if (null == fragment){
            fragment = ZhihuFragment.newInstance();
        }

        if (currentFragment != fragment){
            FragmentTransaction transaction = mFragmentManager.beginTransaction();
            if (fragment.isAdded()){
                transaction.hide(currentFragment)
                        .show(fragment)
                        .commit();
            } else {
                transaction.hide(currentFragment)
                        .add(R.id.fragment_content, fragment)
                        .commit();
            }
            currentFragment = fragment;
        }
    }

    private void initToolbar() {
        // setSupportActionBar(mToolbar);
        // mToolbar.setTitle(getString(R.string.title_zhihu));
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
        }
    }

}
