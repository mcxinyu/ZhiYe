package com.about.zhiye.activity;

import android.Manifest;
import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.about.zhiye.R;
import com.about.zhiye.data.SearchDataHelper;
import com.about.zhiye.data.SearchNewsSuggestion;
import com.about.zhiye.db.DBLab;
import com.about.zhiye.fragment.BaseFragment;
import com.about.zhiye.fragment.DiscoverFragment;
import com.about.zhiye.fragment.SingleZhihuNewsListFragment;
import com.about.zhiye.fragment.ZhihuFragment;
import com.about.zhiye.model.VersionInfoFir;
import com.about.zhiye.util.CheckUpdateHelper;
import com.about.zhiye.util.QueryPreferences;
import com.about.zhiye.util.StateUtils;
import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationAdapter;
import com.google.gson.Gson;
import com.qiangxi.checkupdatelibrary.dialog.ForceUpdateDialog;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import im.fir.sdk.FIR;
import im.fir.sdk.VersionCheckCallback;

import static com.about.zhiye.util.DateUtil.ZHIHU_DAILY_BIRTHDAY;
import static com.qiangxi.checkupdatelibrary.dialog.ForceUpdateDialog.FORCE_UPDATE_DIALOG_PERMISSION_REQUEST_CODE;

/**
 * Created by huangyuefeng on 2017/3/17.
 * Contact me : mcxinyu@foxmail.com
 * 管理 fragment
 */
public class MainActivity extends AppCompatActivity
        implements SingleZhihuNewsListFragment.Callbacks {
    private static final String TAG = "MainActivity";
    private static final int WHAT_CHECK_UPDATE = 1024;
    private static final int WHAT_DELETE_ALL_READ_LATER = 1023;

    private static final int ZHIHU_FRAGMENT = 0;
    private static final int DISCOVER_FRAGMENT = 1;
    private static final int READ_LATER_FRAGMENT = 2;

    private static final long FIND_SUGGESTION_SIMULATED_DELAY = 250;
    private static final int SUGGESTION_COUNT = 5;
    private static final long ANIM_DURATION = 350;

    @BindView(R.id.fragment_content)
    FrameLayout mFragmentContent;
    @BindView(R.id.bottom_navigation)
    AHBottomNavigation mBottomNavigation;
    @BindView(R.id.nav_view)
    NavigationView mNavView;
    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @BindView(R.id.app_bar_layout)
    AppBarLayout mAppBarLayout;
    @BindView(R.id.floating_search_view)
    FloatingSearchView mSearchView;
    @BindView(R.id.dim_background)
    FrameLayout mDimBackground;
    private Unbinder unbinder;

    private TextView mEmailTextView;
    private ActionBarDrawerToggle mDrawerToggle;

    private ColorDrawable mDimDrawable;

    private FragmentManager mFragmentManager;
    private ZhihuFragment mZhihuFragment;
    private DiscoverFragment mDiscoverFragment;
    private SingleZhihuNewsListFragment mReadLaterFragment;
    private Fragment currentFragment;

    private boolean isAppBarLayoutExpanded = false;

    private ForceUpdateDialog mForceUpdateDialog;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (!QueryPreferences.getDrawerOpenState(MainActivity.this)) {
                mDrawerLayout.openDrawer(Gravity.START);
                QueryPreferences.setDrawerOpenState(MainActivity.this, true);
            }

            switch (msg.what) {
                case WHAT_DELETE_ALL_READ_LATER:
                    int i = DBLab.get(MainActivity.this)
                            .deleteAllReadLaterNews();
                    Toast.makeText(MainActivity.this,
                            getResources().getQuantityString(R.plurals.clear_count, i, i),
                            Toast.LENGTH_SHORT)
                            .show();
                    mReadLaterFragment.doRefresh(true);
                    break;
                case WHAT_CHECK_UPDATE:
                    checkForUpdate();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_Zhiye_Light_NoActionbar);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer_layout);
        unbinder = ButterKnife.bind(this);
        // setStatusBarView();

        initDrawer();
        initBottomNavigation();
        initAppBar();
        setupFloatingSearchView();
        setupDrawer();

        mFragmentManager = getSupportFragmentManager();
        currentFragment = mFragmentManager.findFragmentById(R.id.fragment_content);

        if (currentFragment == null) {
            currentFragment = mZhihuFragment = ZhihuFragment.newInstance();
            mFragmentManager.beginTransaction()
                    .add(R.id.fragment_content, currentFragment)
                    .commit();
        }

        mHandler.sendEmptyMessageDelayed(WHAT_CHECK_UPDATE, 3000);
    }

    private void initBottomNavigation() {
        AHBottomNavigationAdapter bottomNavigationAdapter = new AHBottomNavigationAdapter(this, R.menu.navigation);
        bottomNavigationAdapter.setupWithBottomNavigation(mBottomNavigation, getResources().getIntArray(R.array.tab_colors));

        final boolean isColorful = QueryPreferences.getColorfulState(this);
        mBottomNavigation.setTranslucentNavigationEnabled(true);
        // mBottomNavigation.setBehaviorTranslationEnabled(true);
        // mBottomNavigation.setTitleState(AHBottomNavigation.TitleState.ALWAYS_HIDE);

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
                    case ZHIHU_FRAGMENT:
                        if (mZhihuFragment == null) {
                            mZhihuFragment = ZhihuFragment.newInstance();
                        }
                        if (isColorful) {
                            // mStatusBarView.setBackgroundColor(getResources().getIntArray(R.array.tab_colors)[0]);
                        }
                        mSearchView.setSearchBarTitle(getString(R.string.title_zhihu));
                        mSearchView.inflateOverflowMenu(R.menu.menu_zhihu_daily);
                        switchFragment(mZhihuFragment);
                        return true;
                    case DISCOVER_FRAGMENT:
                        if (mDiscoverFragment == null) {
                            mDiscoverFragment = DiscoverFragment.newInstance();
                        }
                        if (isColorful) {
                            // mStatusBarView.setBackgroundColor(getResources().getIntArray(R.array.tab_colors)[1]);
                        }
                        mSearchView.setSearchBarTitle(getString(R.string.title_discover));
                        mSearchView.inflateOverflowMenu(R.menu.menu_empty);
                        switchFragment(mDiscoverFragment);
                        return true;
                    case READ_LATER_FRAGMENT:
                        if (mReadLaterFragment == null) {
                            mReadLaterFragment = SingleZhihuNewsListFragment.newInstance(null, null);
                        }
                        if (isColorful) {
                            // mStatusBarView.setBackgroundColor(getResources().getIntArray(R.array.tab_colors)[2]);
                        }
                        mSearchView.setSearchBarTitle(getString(R.string.title_read_later));
                        mSearchView.inflateOverflowMenu(R.menu.menu_read_later);
                        switchFragment(mReadLaterFragment);
                        return true;
                }
                return false;
            }
        });
    }

    private void initAppBar() {
        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                mSearchView.setTranslationY(verticalOffset);
                // onListViewScroll(verticalOffset);
                mBottomNavigation.setTranslationY(-verticalOffset);
                // EXPANDED
                isAppBarLayoutExpanded = verticalOffset == 0;
            }
        });
    }

    private void setupFloatingSearchView() {
        mDimDrawable = new ColorDrawable(Color.BLACK);
        mDimDrawable.setAlpha(0);
        mDimBackground.setBackground(mDimDrawable);

        mSearchView.setSearchBarTitle(getString(R.string.title_zhihu));

        mSearchView.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {
            @Override
            public void onSearchTextChanged(String oldQuery, String newQuery) {
                if (!oldQuery.equals("") && newQuery.equals("")) {
                    mSearchView.swapSuggestions(
                            SearchDataHelper.getSearchSuggestion(MainActivity.this, SUGGESTION_COUNT));
                } else {
                    mSearchView.showProgress();
                    SearchDataHelper.findSuggestions(MainActivity.this, newQuery, SUGGESTION_COUNT,
                            FIND_SUGGESTION_SIMULATED_DELAY,
                            new SearchDataHelper.OnFindSuggestionsListener() {
                                @Override
                                public void onResults(List<SearchNewsSuggestion> results) {
                                    mSearchView.swapSuggestions(results);
                                    mSearchView.hideProgress();
                                }
                            });
                }
            }
        });

        mSearchView.setOnSearchListener(new FloatingSearchView.OnSearchListener() {
            // 此处处理点击搜索
            @Override
            public void onSuggestionClicked(final SearchSuggestion searchSuggestion) {
                SearchNewsSuggestion suggestion = (SearchNewsSuggestion) searchSuggestion;
                MainActivity.this.startActivity(
                        SingleNewsListActivity.newIntent(MainActivity.this, suggestion.getBody()));
                // mLastQuery = searchSuggestion.getBody();
            }

            @Override
            public void onSearchAction(final String query) {
                // mLastQuery = query;
                QueryPreferences.setSearchHistory(MainActivity.this, query);
                MainActivity.this.startActivity(SingleNewsListActivity.newIntent(MainActivity.this, query));
            }
        });

        mSearchView.setOnFocusChangeListener(new FloatingSearchView.OnFocusChangeListener() {
            @Override
            public void onFocus() {
                fadeDimBackground(0, 150, null);
                mSearchView.swapSuggestions(
                        SearchDataHelper.getSearchSuggestion(MainActivity.this, SUGGESTION_COUNT));
            }

            @Override
            public void onFocusCleared() {
                mSearchView.setSearchBarTitle(getString(R.string.title_zhihu));
                fadeDimBackground(150, 0, null);
            }
        });

        mSearchView.inflateOverflowMenu(R.menu.menu_zhihu_daily);

        mSearchView.setOnMenuItemClickListener(new FloatingSearchView.OnMenuItemClickListener() {
            @Override
            public void onActionMenuItemSelected(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_date:
                        selectDate();
                        break;
                    case R.id.action_clear:
                        clearAllReadLater();
                        break;
                }
            }
        });
    }

    private void selectDate() {
        // mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
        //     @Override
        //     public void onClick(View v) {
        //         startActivity(PickDateActivity.newIntent(getContext()));
        //     }
        // });
        // mFloatingActionButton.setOnLongClickListener(new View.OnLongClickListener() {
        //     @Override
        //     public boolean onLongClick(View v) {
        //         Snackbar.make(mViewPager, getString(R.string.title_pick_date), Snackbar.LENGTH_SHORT)
        //                 .setAction(getResources().getString(R.string.start), new View.OnClickListener() {
        //                     @Override
        //                     public void onClick(View v) {
        //                         startActivity(PickDateActivity.newIntent(getContext()));
        //                     }
        //                 })
        //                 .show();
        //         return false;
        //     }
        // });

        final Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("GMT+08"));

        Calendar zhihuDailyBirthday = Calendar.getInstance();
        zhihuDailyBirthday.set(2013, 5, 20);

        DatePickerDialog dialog = DatePickerDialog.newInstance(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                calendar.set(year, monthOfYear, dayOfMonth);
                Date date = calendar.getTime();
                startActivity(SingleNewsListActivity.newIntent(MainActivity.this, date));
            }
        });

        dialog.setMinDate(zhihuDailyBirthday);
        dialog.setMaxDate(calendar);
        dialog.dismissOnPause(true);
        dialog.autoDismiss(true);
        dialog.setNeutralButton(getString(R.string.random_data), View.VISIBLE, new DatePickerDialog.NeutralClickListener() {

            @Override
            public void onClickListener() {
                startActivity(SingleNewsListActivity.newIntent(MainActivity.this, getRandomDate()));
            }
        });

        dialog.show(getFragmentManager(), MainActivity.class.getSimpleName());
    }

    private Date getRandomDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(ZHIHU_DAILY_BIRTHDAY);
        calendar.add(Calendar.DAY_OF_YEAR, 1 - (int) (Math.random() * getDaysBetween(new Date(), ZHIHU_DAILY_BIRTHDAY)));
        return calendar.getTime();
    }

    public int getDaysBetween(Date date1, Date date2) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date1);
        long time1 = calendar.getTimeInMillis();
        calendar.setTime(date2);
        long time2 = calendar.getTimeInMillis();
        long between_days = (time2 - time1) / (1000 * 3600 * 24);

        return Integer.parseInt(String.valueOf(between_days));
    }

    private void clearAllReadLater() {
        if (DBLab.get(MainActivity.this).queryAllReadLater().size() > 0) {
            new AlertDialog.Builder(this, R.style.DialogStyle)
                    .setTitle(R.string.warning)
                    .setMessage(R.string.confirm_empty)
                    .setCancelable(true)
                    .setOnKeyListener(new DialogInterface.OnKeyListener() {
                        @Override
                        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                            if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
                                dialog.dismiss();
                            }
                            return false;
                        }
                    })
                    .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mHandler.sendEmptyMessage(1024);
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create()
                    .show();
        } else {
            Toast.makeText(MainActivity.this, R.string.no_more_records, Toast.LENGTH_SHORT).show();
        }
    }

    private void setupDrawer() {
        mSearchView.attachNavigationDrawerToMenuButton(mDrawerLayout);
    }

    private void fadeDimBackground(int from, int to, Animator.AnimatorListener listener) {
        ValueAnimator anim = ValueAnimator.ofInt(from, to);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {

                int value = (Integer) animation.getAnimatedValue();
                mDimDrawable.setAlpha(value);
            }
        });
        if (listener != null) {
            anim.addListener(listener);
        }
        anim.setDuration(ANIM_DURATION);
        anim.start();
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

    private void setStatusBarView() {
        // ViewGroup contentView = (ViewGroup) findViewById(android.R.id.content);
        // View statusBarView = new View(MainActivity.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                StateUtils.getStatusBarHeight(this));
        // mStatusBarView.setBackgroundColor(getResources().getIntArray(R.array.tab_colors)[0]);
        // mStatusBarView.setLayoutParams(lp);
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
        } else if (!isAppBarLayoutExpanded && QueryPreferences.getOpenBottomNavigateState(this)) {
            mAppBarLayout.setExpanded(true);
            if (QueryPreferences.getBackToTopState(this)) {
                scrollCurrentFragmentToTop();
            }
        } else if (mSearchView.isSearchBarFocused()) {
            mSearchView.setSearchFocused(false);
        } else if (getCurrentFragmentVertical() != 0 && QueryPreferences.getBackToTopState(this)) {
            scrollCurrentFragmentToTop();
        } else {
            super.onBackPressed();
        }
    }

    private int getCurrentFragmentVertical() {
        if (currentFragment != null) {
            return ((BaseFragment) currentFragment).getVerticalOffset();
        }
        return 0;
    }

    private void scrollCurrentFragmentToTop() {
        if (currentFragment != null) ((BaseFragment) currentFragment).scrollToTop();
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
