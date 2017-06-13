package com.about.zhiye.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.about.zhiye.R;
import com.about.zhiye.activity.ZhihuWebActivity;
import com.about.zhiye.adapter.ThemesAdapter;
import com.about.zhiye.api.ZhihuHelper;
import com.about.zhiye.model.News;
import com.about.zhiye.model.Themes;
import com.jude.rollviewpager.OnItemClickListener;
import com.jude.rollviewpager.RollPagerView;
import com.jude.rollviewpager.hintview.ColorPointHintView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by huangyuefeng on 2017/6/4.
 * Contact me : mcxinyu@foxmail.com
 */
public class ZhihuThemeListFragment extends Fragment
        implements SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.roll_pager_view)
    RollPagerView mRollPagerView;
    @BindView(R.id.nested_scroll_view)
    NestedScrollView mNestedScrollView;
    private Unbinder unbinder;

    private TopNewsPagerAdapter mTopNewsPagerAdapter;
    private List<News> mTopNewses;

    private List<Themes.OthersBean> mOthersBeanList;
    private ThemesAdapter mAdapter;
    private boolean isRefreshed;
    private Subscription mTopNewsSubscribe;
    private Subscription mThemeSubscribe;

    private int recyclerScrollY = 0;

    public static ZhihuThemeListFragment newInstance() {
        ZhihuThemeListFragment fragment = new ZhihuThemeListFragment();
        Bundle args = new Bundle();
        // args.putString(ARG_PARAM1, param1);
        // args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_zhihu_theme_list, container, false);
        unbinder = ButterKnife.bind(this, view);

        mTopNewsPagerAdapter = new TopNewsPagerAdapter(getChildFragmentManager());
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

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mAdapter = new ThemesAdapter(getContext(), mOthersBeanList);
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                recyclerScrollY += dy;
            }
        });

        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        return view;
    }

    public void scrollToTop() {
        mNestedScrollView.smoothScrollTo(0, 0);
    }

    public int getScrollY() {
        return mNestedScrollView.getScrollY();
    }

    // TopNews
    private void doRefreshTopNewses() {
        if (null == mTopNewses || mTopNewses.size() == 0) {
            mTopNewsSubscribe = getTopNewses()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<List<News>>() {
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
                    });
        }
    }

    private Observable<List<News>> getTopNewses() {
        return ZhihuHelper.getTopNews();
    }

    /**
     * 顶部"top"推荐新闻适配器
     */
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

    /**
     * 设置自动加载的方法
     *
     * @return
     */
    // private boolean UserWantsToRefreshAutomatically() {
    //     return QueryPreferences.getAutoRefreshState(ZhiYeApp.getInstance());
    // }

    // @Override
    // public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    //     super.onActivityCreated(savedInstanceState);
    //     if (UserWantsToRefreshAutomatically()) {
    //         doRefreshTopNewses();
    //     }
    // }
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && !isRefreshed) {
            doRefreshTopNewses();
            getOthersBeanList();
        }
    }

    public void doRefresh() {
        if (mTopNewsSubscribe == null && mTopNewsSubscribe.isUnsubscribed()) {
            doRefreshTopNewses();
        }
        if (mThemeSubscribe == null && mThemeSubscribe.isUnsubscribed()) {
            getOthersBeanList();
        }
    }

    @Override
    public void onRefresh() {
        doRefreshTopNewses();
        getOthersBeanList();
    }

    private void getOthersBeanList() {
        mThemeSubscribe = getThemesObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Themes.OthersBean>>() {
                    @Override
                    public void onCompleted() {
                        mSwipeRefreshLayout.setRefreshing(false);
                        mSwipeRefreshLayout.setEnabled(false);
                        isRefreshed = true;
                        mAdapter.updateStories(mOthersBeanList);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        mSwipeRefreshLayout.setRefreshing(false);
                        mSwipeRefreshLayout.setEnabled(true);
                        if (isAdded()) {
                            Snackbar.make(mSwipeRefreshLayout, getString(R.string.load_failure), Snackbar.LENGTH_SHORT)
                                    .setAction(getResources().getString(R.string.retry), new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            getOthersBeanList();
                                        }
                                    })
                                    .show();
                        }
                    }

                    @Override
                    public void onNext(List<Themes.OthersBean> list) {
                        mOthersBeanList = list;
                    }
                });

        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setRefreshing(true);
        }
    }

    private Observable<List<Themes.OthersBean>> getThemesObservable() {
        return ZhihuHelper.getThemes();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        if (mTopNewsSubscribe != null && !mTopNewsSubscribe.isUnsubscribed()) {
            mTopNewsSubscribe.unsubscribe();
        }
        if (mThemeSubscribe != null && !mThemeSubscribe.isUnsubscribed()) {
            mThemeSubscribe.unsubscribe();
        }
    }
}
