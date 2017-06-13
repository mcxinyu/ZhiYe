package com.about.zhiye.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;

import com.about.zhiye.R;

/**
 * Created by huangyuefeng on 2017/4/12.
 * Contact me : mcxinyu@foxmail.com
 */
public class DiscoverFragment extends SearchViewFragment {
    private Fragment mFragment;
    private boolean isVisibleToUser;
    private boolean isPreloadFailure;

    public static DiscoverFragment newInstance() {

        Bundle args = new Bundle();

        DiscoverFragment fragment = new DiscoverFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected Fragment createFragment() {
        return ZhihuThemeListFragment.newInstance();
    }

    @Override
    protected String setSearchViewTitle() {
        return getString(R.string.title_discover);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mFragment = getFragment();

        if (isVisibleToUser && isPreloadFailure) {
            mFragment.setUserVisibleHint(isVisibleToUser);
            isPreloadFailure = false;
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        this.isVisibleToUser = isVisibleToUser;
        if (isVisibleToUser) {
            if (mFragment == null) {
                // 加载的时候可能 mNewsListFragment 还没创建
                isPreloadFailure = true;
            } else if (mFragment instanceof ZhihuThemeListFragment) {
                // 每次 ReadLaterFragment 可见都去加载数据
                ((ZhihuThemeListFragment) mFragment).doRefresh();
            }
        }
    }

    @Override
    public boolean onActivityBackPress() {
        if (!isAppBarLayoutExpanded) {
            mAppBarLayout.setExpanded(true);
            ((ZhihuThemeListFragment) mFragment).scrollToTop();
            return true;
        } else if (mSearchView.isSearchBarFocused()) {
            mSearchView.setSearchFocused(false);
            return true;
        } else if (((ZhihuThemeListFragment) mFragment).getScrollY() != 0) {
            ((ZhihuThemeListFragment) mFragment).scrollToTop();
            return true;
        }
        return false;
    }
}
