package com.about.zhiye.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.about.zhiye.R;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.about.zhiye.util.DateUtil.SIMPLE_DATE_FORMAT;

/**
 * Created by huangyuefeng on 2017/3/17.
 * Contact me : mcxinyu@foxmail.com
 * 管理按日期排列的 ViewPager 里面存放最近一周各个日期的 SingleZhihuNewsListFragment
 */
public class ZhihuFragment extends BaseFragment {
    private static final int PAGER_COUNT = 5;

    @BindView(R.id.zhihu_tab_layout)
    TabLayout mTabLayout;
    @BindView(R.id.story_list_view_pager)
    ViewPager mViewPager;
    private Unbinder unbinder;

    private NewsListPagerAdapter mNewsListPagerAdapter;

    public static ZhihuFragment newInstance() {

        Bundle args = new Bundle();

        ZhihuFragment fragment = new ZhihuFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_zhihu, container, false);
        unbinder = ButterKnife.bind(this, view);

        mViewPager.setOffscreenPageLimit(PAGER_COUNT);
        mNewsListPagerAdapter = new NewsListPagerAdapter(getChildFragmentManager());
        mViewPager.setAdapter(mNewsListPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);

        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // if (isAppBarLayoutExpanded)
                //     mAppBarLayout.setExpanded(false);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // if (mNewsListPagerAdapter.isFirstItemOnTop()) {
                //     if (isAppBarLayoutExpanded)
                //         mAppBarLayout.setExpanded(false);
                //     else
                //         mAppBarLayout.setExpanded(true);
                // } else {
                mNewsListPagerAdapter.scrollCurrentItemToTop();
                // }
            }
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (mViewPager != null) {
            int currentItem = mViewPager.getCurrentItem();
            mNewsListPagerAdapter.getItem(currentItem).notifyDataSetChanged();
        }
    }

    @Override
    public void scrollToTop() {
        mNewsListPagerAdapter.scrollCurrentItemToTop();
    }

    @Override
    public int getVerticalOffset() {
        return mNewsListPagerAdapter.getCurrentItemScrollY();
    }

    /**
     * 各日期的新闻列表适配器
     */
    private class NewsListPagerAdapter extends FragmentStatePagerAdapter {
        List<SingleZhihuNewsListFragment> mFragmentList = new ArrayList<>();

        NewsListPagerAdapter(FragmentManager fm) {
            super(fm);
            for (int i = 0; i < PAGER_COUNT; i++) {
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.DAY_OF_YEAR, 1 - i);
                String date = SIMPLE_DATE_FORMAT.format(calendar.getTime());
                mFragmentList.add(SingleZhihuNewsListFragment.newInstance(date, null));
            }
        }

        @Override
        public SingleZhihuNewsListFragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return PAGER_COUNT;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_YEAR, -position);

            return (position == 0 ?
                    getString(R.string.zhihu_daily_today) + " " :
                    DateFormat.getDateInstance().format(calendar.getTime()));
        }

        int getCurrentItemScrollY() {
            // 获取 RecyclerView 的滚动距离
            return mFragmentList.get(mViewPager.getCurrentItem()).getVerticalOffset();
        }

        void scrollCurrentItemToTop() {
            scrollItemToTop(mViewPager.getCurrentItem());
        }

        void scrollItemToTop(int position) {
            mFragmentList.get(position).scrollToTop();
        }

        boolean isFirstItemOnTop() {
            return mFragmentList.get(mViewPager.getCurrentItem()).isFirstItemOnTop();
        }
    }
}
