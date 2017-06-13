package com.about.zhiye.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.MenuItem;
import android.view.View;

import com.about.zhiye.R;
import com.arlib.floatingsearchview.FloatingSearchView;

/**
 * Created by huangyuefeng on 2017/3/29.
 * Contact me : mcxinyu@foxmail.com
 */
public class ReadLaterFragment extends SearchViewFragment {

    private Fragment mFragment;
    private boolean isVisibleToUser;
    private boolean isPreloadFailure;

    public static ReadLaterFragment newInstance() {

        Bundle args = new Bundle();

        ReadLaterFragment fragment = new ReadLaterFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected Fragment createFragment() {
        return SingleZhihuNewsListFragment.newInstance(null, null);
    }

    @Override
    protected String setSearchViewTitle() {
        return getString(R.string.title_read_later);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mSearchView.inflateOverflowMenu(R.menu.menu_read_later);

        mSearchView.setOnMenuItemClickListener(new FloatingSearchView.OnMenuItemClickListener() {
            @Override
            public void onActionMenuItemSelected(MenuItem item) {
                if (item.getItemId() == R.id.action_clear) {
                    // TODO: 2017/6/7 clear read later
                }
            }
        });

        mFragment = getFragment();

        if (isVisibleToUser && isPreloadFailure) {
            mFragment.setUserVisibleHint(isVisibleToUser);
            isPreloadFailure = false;
        }
    }

    @Override
    public boolean onActivityBackPress() {
        if (!isAppBarLayoutExpanded) {
            mAppBarLayout.setExpanded(true);
            ((SingleZhihuNewsListFragment) mFragment).scrollToTop();
            return true;
        } else if (mSearchView.isSearchBarFocused()) {
            mSearchView.setSearchFocused(false);
            return true;
        } else if (((SingleZhihuNewsListFragment) mFragment).getRecyclerScrollY() != 0) {
            ((SingleZhihuNewsListFragment) mFragment).scrollToTop();
            return true;
        }
        return false;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        this.isVisibleToUser = isVisibleToUser;
        if (isVisibleToUser) {
            if (mFragment == null) {
                // 加载的时候可能 mFragment 还没创建
                isPreloadFailure = true;
            } else if (mFragment instanceof SingleZhihuNewsListFragment) {
                // 每次 ReadLaterFragment 可见都去加载数据
                ((SingleZhihuNewsListFragment) mFragment).doRefresh(true);
            }
        }
    }
}
