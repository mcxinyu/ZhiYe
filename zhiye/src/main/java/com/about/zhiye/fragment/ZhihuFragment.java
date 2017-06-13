package com.about.zhiye.fragment;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.about.zhiye.R;
import com.about.zhiye.activity.PickDateActivity;
import com.about.zhiye.activity.SingleNewsListActivity;
import com.about.zhiye.data.SearchDataHelper;
import com.about.zhiye.data.SearchNewsSuggestion;
import com.about.zhiye.util.QueryPreferences;
import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;

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
public class ZhihuFragment extends BaseSearchViewFragment {
    private static final long FIND_SUGGESTION_SIMULATED_DELAY = 250;
    private static final int SUGGESTION_COUNT = 5;
    private static final long ANIM_DURATION = 350;
    private static final int PAGER_COUNT = 5;

    @BindView(R.id.zhihu_tab_layout)
    TabLayout mTabLayout;
    @BindView(R.id.story_list_view_pager)
    ViewPager mViewPager;
    @BindView(R.id.floating_action_button)
    FloatingActionButton mFloatingActionButton;
    @BindView(R.id.app_bar_layout)
    AppBarLayout mAppBarLayout;
    @BindView(R.id.floating_search_view)
    FloatingSearchView mSearchView;
    @BindView(R.id.dim_background)
    FrameLayout mDimBackground;
    private Unbinder unbinder;

    private NewsListPagerAdapter mNewsListPagerAdapter;
    private boolean isAppBarLayoutExpanded = false;

    private ColorDrawable mDimDrawable;

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

        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(PickDateActivity.newIntent(getContext()));
            }
        });
        mFloatingActionButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Snackbar.make(mViewPager, getString(R.string.title_pick_date), Snackbar.LENGTH_SHORT)
                        .setAction(getResources().getString(R.string.start), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivity(PickDateActivity.newIntent(getContext()));
                            }
                        })
                        .show();
                return false;
            }
        });

        initAppBar();
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupFloatingSearchView();
        setupDrawer();

        mDimDrawable = new ColorDrawable(Color.BLACK);
        mDimDrawable.setAlpha(0);
        mDimBackground.setBackground(mDimDrawable);
    }

    private void setupFloatingSearchView() {
        mSearchView.setSearchBarTitle(getString(R.string.title_zhihu));

        mSearchView.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {
            @Override
            public void onSearchTextChanged(String oldQuery, String newQuery) {
                // 此处 show 搜索历史 或者什么都不做
                if (!oldQuery.equals("") && newQuery.equals("")) {
                    mSearchView.swapSuggestions(
                            SearchDataHelper.getSearchSuggestion(getActivity(), SUGGESTION_COUNT));
                } else {
                    mSearchView.showProgress();
                    SearchDataHelper.findSuggestions(getActivity(), newQuery, SUGGESTION_COUNT,
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
                getActivity().startActivity(
                        SingleNewsListActivity.newIntent(getActivity(), suggestion.getBody()));
                // mLastQuery = searchSuggestion.getBody();
            }

            @Override
            public void onSearchAction(final String query) {
                // mLastQuery = query;
                QueryPreferences.setSearchHistory(getActivity(), query);
                getActivity().startActivity(SingleNewsListActivity.newIntent(getActivity(), query));
            }
        });

        mSearchView.setOnFocusChangeListener(new FloatingSearchView.OnFocusChangeListener() {
            @Override
            public void onFocus() {
                fadeDimBackground(0, 150, null);
                mSearchView.swapSuggestions(
                        SearchDataHelper.getSearchSuggestion(getActivity(), SUGGESTION_COUNT));
            }

            @Override
            public void onFocusCleared() {
                mSearchView.setSearchBarTitle(getString(R.string.title_zhihu));
                fadeDimBackground(150, 0, null);
            }
        });
    }

    private void setupDrawer() {
        attachSearchViewToActivityDrawer(mSearchView);
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

    private void initAppBar() {
        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                mSearchView.setTranslationY(verticalOffset);
                onNestViewScroll(verticalOffset);
                // EXPANDED
                isAppBarLayoutExpanded = verticalOffset == 0;
            }
        });

        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (isAppBarLayoutExpanded)
                    mAppBarLayout.setExpanded(false);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                if (mNewsListPagerAdapter.isFirstItemOnTop()) {
                    if (isAppBarLayoutExpanded)
                        mAppBarLayout.setExpanded(false);
                    else
                        mAppBarLayout.setExpanded(true);
                } else {
                    mNewsListPagerAdapter.scrollCurrentItemToTop();
                }
            }
        });
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
    public boolean onActivityBackPress() {
        if (!isAppBarLayoutExpanded) {
            mAppBarLayout.setExpanded(true);
            mNewsListPagerAdapter.scrollCurrentItemToTop();
            return true;
        } else if (mSearchView.isSearchBarFocused()) {
            mSearchView.setSearchFocused(false);
            return true;
        } else if (mNewsListPagerAdapter.getCurrentItemScrollY() != 0) {
            mNewsListPagerAdapter.scrollCurrentItemToTop();
            return true;
        }
        return false;
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
            return mFragmentList.get(mViewPager.getCurrentItem()).getRecyclerScrollY();
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
