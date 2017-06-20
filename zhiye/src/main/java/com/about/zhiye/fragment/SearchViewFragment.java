package com.about.zhiye.fragment;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.about.zhiye.R;
import com.about.zhiye.activity.SingleNewsListActivity;
import com.about.zhiye.data.SearchDataHelper;
import com.about.zhiye.data.SearchNewsSuggestion;
import com.about.zhiye.util.QueryPreferences;
import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by huangyuefeng on 2017/6/3.
 * Contact me : mcxinyu@foxmail.com
 */
public abstract class SearchViewFragment extends BaseSearchViewFragment {
    private static final long FIND_SUGGESTION_SIMULATED_DELAY = 250;
    private static final int SUGGESTION_COUNT = 5;
    private static final long ANIM_DURATION = 350;

    @BindView(R.id.fragment_container)
    FrameLayout mFragmentContainer;
    @BindView(R.id.floating_search_view)
    FloatingSearchView mSearchView;
    @BindView(R.id.app_bar_layout)
    AppBarLayout mAppBarLayout;
    @BindView(R.id.dim_background)
    FrameLayout mDimBackground;
    private Unbinder unbinder;

    protected boolean isAppBarLayoutExpanded = false;

    private ColorDrawable mDimDrawable;

    private Fragment mFragment;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_base_search_view, container, false);
        unbinder = ButterKnife.bind(this, view);

        FragmentManager fragmentManager = getChildFragmentManager();
        mFragment = fragmentManager.findFragmentById(R.id.fragment_container);

        if (mFragment == null) {
            mFragment = createFragment();
            fragmentManager.beginTransaction()
                    .add(R.id.fragment_container, mFragment)
                    .commit();
        }

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupFloatingSearchView();
        setupDrawer();

        initAppBar();

        mDimDrawable = new ColorDrawable(Color.BLACK);
        mDimDrawable.setAlpha(0);
        mDimBackground.setBackground(mDimDrawable);
    }

    private void initAppBar() {
        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                mSearchView.setTranslationY(verticalOffset);
                onNestViewScroll(verticalOffset);
                isAppBarLayoutExpanded = verticalOffset == 0;
            }
        });
    }

    private void setupFloatingSearchView() {
        mSearchView.setSearchBarTitle(setSearchViewTitle());

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
                getActivity().startActivity(
                        SingleNewsListActivity.newIntent(getActivity(), query));
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
                mSearchView.setSearchBarTitle(setSearchViewTitle());
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public boolean onActivityBackPress() {
        if (!isAppBarLayoutExpanded) {
            mAppBarLayout.setExpanded(true);
            return true;
        } else if (mSearchView.isSearchBarFocused()) {
            mSearchView.setSearchFocused(false);
            return true;
        }
        return false;
    }

    public Fragment getFragment() {
        return mFragment;
    }

    protected abstract Fragment createFragment();

    protected abstract String setSearchViewTitle();
}
