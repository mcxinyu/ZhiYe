package com.about.zhiye.ui.base;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.MenuItem;

import com.about.zhiye.R;

import butterknife.ButterKnife;

/**
 * Created by huangyuefeng on 2017/3/13.
 * Contact me : mcxinyu@foxmail.com
 */
public abstract class BaseActivity<V, T extends BasePresenter<V>> extends AppCompatActivity {
    private T mPresenter;
    private AppBarLayout mAppBar;
    private Toolbar mToolbar;
    private SwipeRefreshLayout mRefreshLayout;
    private boolean mIsRequestDataRefresh;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (createPresenter() != null) {
            // 可以不实现 Presenter
            mPresenter = createPresenter();
            mPresenter.attachView((V) this);
        }
        setContentView(setContentViewRes());
        ButterKnife.bind(this);

        mAppBar = (AppBarLayout) findViewById(R.id.appbar_layout);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        if (mAppBar != null && mToolbar != null){
            setSupportActionBar(mToolbar);
            if (setAllowBack()){
                ActionBar actionBar = getSupportActionBar();
                if (actionBar != null) {
                    actionBar.setDisplayHomeAsUpEnabled(true);
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    mAppBar.setElevation(10.6f);
                }
            }
        }

        if (isSetRefresh()){
            setupSwipeRefresh();
        }
    }

    private void setupSwipeRefresh() {
        mRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        if (mRefreshLayout != null){
            mRefreshLayout.setProgressViewOffset(true, 0, (int) TypedValue
                            .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics()));
            mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    requestDataRefresh();
                }
            });
        }
    }

    public void requestDataRefresh() {
        mIsRequestDataRefresh = true;
    }

    public void setRefresh(boolean requestDataRefresh){
        if (mRefreshLayout == null){
            return;
        }

        if (!requestDataRefresh){
            mIsRequestDataRefresh = false;
            mRefreshLayout.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (mRefreshLayout != null){
                        mRefreshLayout.setRefreshing(false);
                    }
                }
            }, 1000);
        } else {
            mRefreshLayout.setRefreshing(true);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null){
            mPresenter.detachView();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * 设置刷新布局
     * @return
     */
    private boolean isSetRefresh() {
        return false;
    }

    /**
     * 设置可返回
     * @return
     */
    private boolean setAllowBack() {
        return false;
    }

    /**
     * 设置内容布局
     * @return
     */
    @LayoutRes
    protected abstract int setContentViewRes();

    /**
     * 初始化 Presenter
     * @return
     */
    protected abstract T createPresenter();
}
