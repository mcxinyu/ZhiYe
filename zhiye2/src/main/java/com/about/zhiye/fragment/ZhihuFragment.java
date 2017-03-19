package com.about.zhiye.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.about.zhiye.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by huangyuefeng on 2017/3/17.
 * Contact me : mcxinyu@foxmail.com
 * 管理按日期排列的 ViewPager 里面存放最近一周各个日期的 NewsListFragment
 */
public class ZhihuFragment extends Fragment {
    @SuppressWarnings("deprecation")
    public static final Date ZHIHU_DAILY_BIRTHDAY = new Date(113, 4, 19); // May 19th, 2013
    public static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyyMMdd", Locale.US);

    private static final int PAGER_COUNT = 7;

    @BindView(R.id.zhihu_tab_layout)
    TabLayout mTabLayout;
    @BindView(R.id.zhihu_view_pager)
    ViewPager mViewPager;
    @BindView(R.id.floating_action_button)
    FloatingActionButton mFloatingActionButton;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;

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
        ButterKnife.bind(this, view);

        mViewPager.setOffscreenPageLimit(PAGER_COUNT);
        mViewPager.setAdapter(new ZhihuPagerAdapter(getFragmentManager()));
        mTabLayout.setupWithViewPager(mViewPager);

        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 2017/3/19 选择日期
            }
        });

        initToolbar();
        return view;
    }

    private void initToolbar() {
        ((AppCompatActivity)getActivity()).setSupportActionBar(mToolbar);
    }

    private class ZhihuPagerAdapter extends FragmentStatePagerAdapter {

        ZhihuPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_YEAR, 1 - position);
            String date = SIMPLE_DATE_FORMAT.format(calendar.getTime());

            return NewsListFragment.newInstance(date);
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
    }
}
