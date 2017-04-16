package com.about.zhiye.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.about.zhiye.R;
import com.about.zhiye.adapter.ThemesAdapter;
import com.about.zhiye.api.ZhihuHelper;
import com.about.zhiye.model.Themes;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by huangyuefeng on 2017/4/12.
 * Contact me : mcxinyu@foxmail.com
 */
public class ThemeListFragment extends Fragment implements Observer<List<Themes.OthersBean>>, SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    Unbinder unbinder;

    private List<Themes.OthersBean> mOthersBeanList;
    private ThemesAdapter mAdapter;
    private boolean isRefreshed;

    public static ThemeListFragment newInstance() {

        Bundle args = new Bundle();

        ThemeListFragment fragment = new ThemeListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_theme_list, container, false);
        unbinder = ButterKnife.bind(this, view);
        initToolbar();

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mAdapter = new ThemesAdapter(getContext(), mOthersBeanList);
        mRecyclerView.setAdapter(mAdapter);

        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);

        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && !isRefreshed) {
            getOthersBeanList();
        }
    }

    private void getOthersBeanList() {
        getThemesObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this);

        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setRefreshing(true);
        }
    }

    private Observable<List<Themes.OthersBean>> getThemesObservable() {
        return ZhihuHelper.getThemes();
    }

    private void initToolbar() {
        mToolbar.setTitle(getString(R.string.title_themes));
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

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
            Snackbar.make(mSwipeRefreshLayout, getString(R.string.network_error), Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onNext(List<Themes.OthersBean> list) {
        mOthersBeanList = list;
    }

    @Override
    public void onRefresh() {
        getOthersBeanList();
    }
}
