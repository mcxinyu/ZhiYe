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
import com.about.zhiye.fragment.ReadLaterFragment;
import com.about.zhiye.fragment.ZhihuFragment;

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
    Unbinder unbinder;

    private ActionBar mSupportActionBar;

    private FragmentManager mFragmentManager;

    private ZhihuFragment mZhihuFragment;
    private ZhihuFragment mZhihuFragment1;
    private ReadLaterFragment mReadLaterFragment;
    private Fragment currentFragment;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    if (mZhihuFragment == null){
                        mZhihuFragment = ZhihuFragment.newInstance();
                    }
                    switchFragment(mZhihuFragment);
                    return true;
                case R.id.navigation_dashboard:
                    switchFragment(mZhihuFragment1);
                    return true;
                case R.id.navigation_read_later:
                    if (mReadLaterFragment == null){
                        mReadLaterFragment = ReadLaterFragment.newInstance();
                    }
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
            // TODO: 2017/3/30 后续需要移除 放入 BottomNavigationView 点击事件里
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
        mSupportActionBar = getSupportActionBar();
        if (mSupportActionBar != null) {
            mSupportActionBar.setHomeButtonEnabled(true);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
