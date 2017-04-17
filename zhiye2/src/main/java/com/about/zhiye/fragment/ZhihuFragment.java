package com.about.zhiye.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
import com.about.zhiye.activity.PickDateActivity;
import com.about.zhiye.activity.ZhihuWebActivity;
import com.about.zhiye.api.ZhihuHelper;
import com.about.zhiye.model.News;
import com.jude.rollviewpager.OnItemClickListener;
import com.jude.rollviewpager.RollPagerView;
import com.jude.rollviewpager.hintview.ColorPointHintView;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.about.zhiye.util.DateUtil.SIMPLE_DATE_FORMAT;

/**
 * Created by huangyuefeng on 2017/3/17.
 * Contact me : mcxinyu@foxmail.com
 * 管理按日期排列的 ViewPager 里面存放最近一周各个日期的 NewsListFragment
 */
public class ZhihuFragment extends Fragment implements Observer<List<News>> {

    private static final int PAGER_COUNT = 7;

    @BindView(R.id.zhihu_tab_layout)
    TabLayout mTabLayout;
    @BindView(R.id.story_list_view_pager)
    ViewPager mViewPager;
    @BindView(R.id.floating_action_button)
    FloatingActionButton mFloatingActionButton;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.collapsing_layout)
    CollapsingToolbarLayout mCollapsingLayout;
    @BindView(R.id.roll_pager_view)
    RollPagerView mRollPagerView;
    private Unbinder unbinder;

    private TopNewsPagerAdapter mTopNewsPagerAdapter;
    private List<News> mTopNewses;
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

        mTopNewsPagerAdapter = new TopNewsPagerAdapter(getFragmentManager());
        mRollPagerView.setAdapter(mTopNewsPagerAdapter);
        mRollPagerView.setAnimationDurtion(500);
        mRollPagerView.setHintView(new ColorPointHintView(getContext(), Color.WHITE, Color.GRAY));
        mRollPagerView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                startActivity(ZhihuWebActivity.newIntent(getContext(),
                        mTopNewses.get(position).getId(),
                        mTopNewses.get(position).getType()));
            }
        });

        mViewPager.setOffscreenPageLimit(PAGER_COUNT);
        mNewsListPagerAdapter = new NewsListPagerAdapter(getFragmentManager());
        mViewPager.setAdapter(mNewsListPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);

        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(PickDateActivity.newIntent(getContext()));
            }
        });
        mFloatingActionButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Snackbar.make(mViewPager, getString(R.string.title_pick_date), Snackbar.LENGTH_SHORT).show();
                return false;
            }
        });

        initToolbar();
        return view;
    }

    private void initToolbar() {
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        mCollapsingLayout.setTitle(getString(R.string.title_zhihu));
        mCollapsingLayout.setExpandedTitleColor(Color.TRANSPARENT);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        doRefreshTopNewses();
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

    private class NewsListPagerAdapter extends FragmentStatePagerAdapter {
        List<NewsListFragment> mFragmentList = new ArrayList<>();

        NewsListPagerAdapter(FragmentManager fm) {
            super(fm);
            for (int i = 0; i < PAGER_COUNT; i++) {
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.DAY_OF_YEAR, 1 - i);
                String date = SIMPLE_DATE_FORMAT.format(calendar.getTime());
                mFragmentList.add(NewsListFragment.newInstance(date));
            }
        }

        @Override
        public NewsListFragment getItem(int position) {
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
    }

    // TopNews
    private void doRefreshTopNewses() {
        getTopNewses()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this);
    }

    private Observable<List<News>> getTopNewses() {
        return ZhihuHelper.getTopNews();
    }

    /**
     * RxJava
     */
    @Override
    public void onCompleted() {
        mTopNewsPagerAdapter.updateTopStories(mTopNewses);
    }

    /**
     * RxJava
     */
    @Override
    public void onError(Throwable e) {
        e.printStackTrace();
    }

    /**
     * RxJava
     */
    @Override
    public void onNext(List<News> topNewses) {
        mTopNewses = topNewses;
    }

    private class TopNewsPagerAdapter extends FragmentStatePagerAdapter {
        List<News> list = new ArrayList<>();

        TopNewsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return TopNewsFragment.newInstance(list.get(position));
        }

        @Override
        public int getCount() {
            return list.size();
        }

        private void updateTopStories(List<News> topNewses) {
            setTopNewses(topNewses);
            notifyDataSetChanged();
        }

        private void setTopNewses(List<News> topNews) {
            list = topNews;
        }
    }
}
