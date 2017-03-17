package com.about.zhiye.ui.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.about.zhiye.R;
import com.about.zhiye.ui.adapter.ViewPagerFragmentAdapter;
import com.about.zhiye.ui.base.BaseActivity;
import com.about.zhiye.ui.base.BaseFragment;
import com.about.zhiye.ui.base.BasePresenter;
import com.about.zhiye.ui.fragment.ZhihuFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by 跃峰 on 2016/9/18.
 * Contact Me : mcxinyu@foxmail.com
 * 主界面，包含了三个 Fragment
 */
public class MainActivity extends BaseActivity {

    @BindView(R.id.tab_layout)
    TabLayout mTabLayout;
    @BindView(R.id.content_view_pager)
    ViewPager mViewPager;

    private List<BaseFragment> mFragmentList;

    @Override
    protected int setContentViewRes() {
        return R.layout.activity_main;
    }

    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        initTabLayout();
    }

    private void initTabLayout() {
        mFragmentList = new ArrayList<>();
        mFragmentList.add(new ZhihuFragment());
        // mFragmentList.add(new GankFragment());
        // mFragmentList.add(new DailyFragment());
        // mViewPager.setOffscreenPageLimit(3);
        mViewPager.setAdapter(new ViewPagerFragmentAdapter(getSupportFragmentManager(),
                mFragmentList,
                "main_view_pager"));
        mTabLayout.setupWithViewPager(mViewPager);
    }

    // private void initDrawerLayout() {
    //     mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
    //     ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
    //             this, mDrawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
    //     toggle.syncState();
    //     mDrawer.addDrawerListener(toggle);
    //
    //     NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
    //     navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
    //         @Override
    //         public boolean onNavigationItemSelected(@NonNull MenuItem item) {
    //             switch (item.getItemId()) {
    //                 case R.id.nav_camera:
    //                     break;
    //                 case R.id.nav_gallery:
    //                     break;
    //                 case R.id.nav_slideshow:
    //                     break;
    //                 case R.id.nav_manage:
    //                     break;
    //                 case R.id.nav_share:
    //                     break;
    //                 case R.id.nav_send:
    //                     break;
    //                 default:
    //                     break;
    //             }
    //
    //             mDrawer.closeDrawer(GravityCompat.START);
    //             return true;
    //         }
    //     });
    // }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Toast.makeText(this, "请期待", Toast.LENGTH_SHORT).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
