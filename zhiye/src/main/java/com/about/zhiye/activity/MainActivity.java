package com.about.zhiye.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.about.zhiye.R;
import com.about.zhiye.db.DBLab;
import com.about.zhiye.fragment.NewsListFragment;
import com.about.zhiye.fragment.ReadLaterFragment;
import com.about.zhiye.fragment.ThemeListFragment;
import com.about.zhiye.fragment.ZhihuFragment;
import com.about.zhiye.model.VersionInfoFir;
import com.about.zhiye.util.CheckUpdateHelper;
import com.about.zhiye.util.QueryPreferences;
import com.about.zhiye.util.StateUtils;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationAdapter;
import com.google.gson.Gson;
import com.qiangxi.checkupdatelibrary.dialog.ForceUpdateDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import im.fir.sdk.FIR;
import im.fir.sdk.VersionCheckCallback;

import static com.qiangxi.checkupdatelibrary.dialog.ForceUpdateDialog.FORCE_UPDATE_DIALOG_PERMISSION_REQUEST_CODE;

/**
 * Created by huangyuefeng on 2017/3/17.
 * Contact me : mcxinyu@foxmail.com
 * 管理 fragment
 */
public class MainActivity extends AppCompatActivity implements NewsListFragment.Callbacks {
    public static final String TAG = "MainActivity";
    private static final int CHECK_UPDATE_WHAT = 1024;

    @BindView(R.id.fragment_content)
    FrameLayout mFragmentContent;
    @BindView(R.id.bottom_navigation)
    AHBottomNavigation mBottomNavigation;
    @BindView(R.id.status_bar_view)
    View mStatusBarView;
    @BindView(R.id.nav_view)
    NavigationView mNavView;
    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    private Unbinder unbinder;
    private TextView mEmailTextView;
    private ActionBarDrawerToggle mDrawerToggle;

    private FragmentManager mFragmentManager;

    private ZhihuFragment mZhihuFragment;
    private ThemeListFragment mThemeFragment;
    private ReadLaterFragment mReadLaterFragment;
    private Fragment currentFragment;
    private ForceUpdateDialog mForceUpdateDialog;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == CHECK_UPDATE_WHAT) {
                checkForUpdate();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_Zhiye_Light_NoActionbar_Translucent);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer_layout);
        unbinder = ButterKnife.bind(this);
        setStatusBarView();

        initToolbar();
        initDrawer();

        mFragmentManager = getSupportFragmentManager();
        currentFragment = mFragmentManager.findFragmentById(R.id.fragment_content);

        if (currentFragment == null) {
            currentFragment = mZhihuFragment = ZhihuFragment.newInstance();
            mFragmentManager.beginTransaction()
                    .add(R.id.fragment_content, currentFragment)
                    .commit();
        }

        initBottomNavigation();

        mHandler.sendEmptyMessageDelayed(CHECK_UPDATE_WHAT, 3000);
    }

    private void initBottomNavigation() {
        AHBottomNavigationAdapter bottomNavigationAdapter = new AHBottomNavigationAdapter(this, R.menu.navigation);
        bottomNavigationAdapter.setupWithBottomNavigation(mBottomNavigation, getResources().getIntArray(R.array.tab_colors));

        final boolean isColorful = QueryPreferences.getColorfulState(this);
        mBottomNavigation.setTranslucentNavigationEnabled(true);
        if (isColorful) {
            mBottomNavigation.setColored(true);
        } else {
            mBottomNavigation.setAccentColor(getResources().getColor(R.color.colorPrimary));
        }
        mBottomNavigation.setSelectedBackgroundVisible(true);

        int count = DBLab.get(this).queryAllUnHaveReadCountForReadLater();
        if (count > 0) {
            setBottomNavigationNotification("" + count, 2);
        }

        mBottomNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {
                switch (position) {
                    case 0:
                        if (mZhihuFragment == null) {
                            mZhihuFragment = ZhihuFragment.newInstance();
                        }
                        mStatusBarView.setVisibility(View.GONE);
                        switchFragment(mZhihuFragment);
                        return true;
                    case 1:
                        if (mThemeFragment == null) {
                            mThemeFragment = ThemeListFragment.newInstance();
                        }
                        if (isColorful) {
                            mStatusBarView.setBackgroundColor(getResources().getIntArray(R.array.tab_colors)[1]);
                        }
                        mStatusBarView.setVisibility(View.VISIBLE);
                        switchFragment(mThemeFragment);
                        return true;
                    case 2:
                        if (mReadLaterFragment == null) {
                            mReadLaterFragment = ReadLaterFragment.newInstance();
                        }
                        if (isColorful) {
                            mStatusBarView.setBackgroundColor(getResources().getIntArray(R.array.tab_colors)[2]);
                        }
                        mStatusBarView.setVisibility(View.VISIBLE);
                        switchFragment(mReadLaterFragment);
                        return true;
                }
                return false;
            }
        });
    }

    @Override
    public void setBottomNavigationNotification(String title, int position) {
        if (QueryPreferences.getNotificationState(this)) {
            mBottomNavigation.setNotification(title, position);
        }
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

    private void initDrawer() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open_drawer, R.string.close_drawer);
        mDrawerToggle.syncState();
        mDrawerLayout.addDrawerListener(mDrawerToggle);

        mNavView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_settings:
                        startActivity(PreferencesActivity.newIntent(MainActivity.this));
                        break;
                    case R.id.menu_feedback:
                        sendEmailFeedback();
                        break;
                }
                mDrawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        // mEmailTextView = (TextView) findViewById(R.id.emailTextView);
        // mEmailTextView.setOnClickListener(new View.OnClickListener() {
        //     @Override
        //     public void onClick(View v) {
        //         sendEmailFeedback();
        //     }
        // });
    }

    private void sendEmailFeedback() {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        String uriText = "mailto:mcxinyu@gmail.com?subject=Feedback&body=";
        uriText = uriText.replace(" ", "%20");
        Uri uri = Uri.parse(uriText);
        intent.setData(uri);
        startActivity(intent);
    }

    private void initToolbar() {
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
            supportActionBar.setHomeButtonEnabled(true);
        }
    }

    private void setStatusBarView() {
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

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void checkForUpdate() {
        try {
            ApplicationInfo appInfo = MainActivity.this.getPackageManager()
                    .getApplicationInfo(MainActivity.this.getPackageName(), PackageManager.GET_META_DATA);
            String firToken = appInfo.metaData.getString("fir_token");

            FIR.checkForUpdateInFIR(firToken, new VersionCheckCallback() {
                @Override
                public void onSuccess(String versionJson) {
                    Log.i(TAG, "check from fir.im success! " + "\n" + versionJson);
                    final VersionInfoFir versionInfoFir = new Gson().fromJson(versionJson, VersionInfoFir.class);

                    if (versionInfoFir.getVersion() > CheckUpdateHelper.getCurrentVersionCode(MainActivity.this)) {
                        if (versionInfoFir.getVersionShort().contains("force")) {
                            mForceUpdateDialog = CheckUpdateHelper.buildForceUpdateDialog(MainActivity.this, versionInfoFir);
                        }
                    }
                }

                @Override
                public void onFail(Exception exception) {
                    Log.i(TAG, "check fir.im fail! " + "\n" + exception.getMessage());
                }

                @Override
                public void onStart() {
                    Log.i(TAG, "check update start.");
                }

                @Override
                public void onFinish() {
                    Log.i(TAG, "check update finish.");
                }
            });
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        //如果用户同意所请求的权限
        if (permissions[0].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //所以在进行判断时,必须要结合这两个常量进行判断.
            if (requestCode == FORCE_UPDATE_DIALOG_PERMISSION_REQUEST_CODE) {
                //进行下载操作
                mForceUpdateDialog.download();
            }
        } else {
            //用户不同意,提示用户,如下载失败,因为您拒绝了相关权限
            Toast.makeText(this, "程序将无法正常运行", Toast.LENGTH_SHORT).show();
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                Log.e(TAG, "false.请开启读写sd卡权限,不然无法正常工作");
            } else {
                Log.e(TAG, "true.请开启读写sd卡权限,不然无法正常工作");
            }
        }
    }
}
