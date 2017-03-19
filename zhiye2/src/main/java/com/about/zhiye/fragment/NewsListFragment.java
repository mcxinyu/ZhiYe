package com.about.zhiye.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.about.zhiye.R;
import com.about.zhiye.adapter.NewsAdapter;
import com.about.zhiye.api.ZhihuObservable;
import com.about.zhiye.model.Story;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


/**
 * Created by huangyuefeng on 2017/3/18.
 * Contact me : mcxinyu@foxmail.com
 * 一天的 NewsList
 */
public class NewsListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, Observer<List<Story>> {
    private static final String ARGS_DATE = "mDate";

    @BindView(R.id.news_list_recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.container)
    FrameLayout mContainer;

    private List<Story> mStories = new ArrayList<>();

    private OnFragmentInteractionListener mListener;
    private NewsAdapter mNewsAdapter;
    private String mDate;
    private boolean isRefreshed = false;

    public static NewsListFragment newInstance(String date) {
        NewsListFragment fragment = new NewsListFragment();
        Bundle args = new Bundle();
        args.putString(ARGS_DATE, date);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mDate = getArguments().getString(ARGS_DATE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news_list, container, false);
        ButterKnife.bind(this, view);

        // mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        mNewsAdapter = new NewsAdapter(getContext(), mStories);
        mRecyclerView.setAdapter(mNewsAdapter);

        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);

        return view;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // if (context instanceof OnFragmentInteractionListener) {
        //     mListener = (OnFragmentInteractionListener) context;
        // } else {
        //     throw new RuntimeException(context.toString()
        //             + " must implement OnFragmentInteractionListener");
        // }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        refreshIf(shouldRefreshOnVisibilityChange(isVisibleToUser));
    }

    private boolean UserWantsToRefreshAutomatically() {
        // TODO: 2017/3/19 用户首选项
        return false;
    }

    private boolean shouldRefreshOnVisibilityChange(boolean isVisibleToUser) {
        return isVisibleToUser && UserWantsToRefreshAutomatically() && !isRefreshed;
    }

    private void refreshIf(boolean need) {
        if (need) {
            doRefresh();
        }
    }

    @Override
    public void onRefresh() {
        doRefresh();
    }

    private void doRefresh() {
        getStoryObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this);

        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setRefreshing(true);
        }
    }

    private Observable<List<Story>> getStoryObservable() {
        return ZhihuObservable.ofDate(mDate);
    }

    /**
     * RxJava
     */
    @Override
    public void onCompleted() {
        isRefreshed = true;
        mSwipeRefreshLayout.setRefreshing(false);
        mNewsAdapter.updateStories(mStories);
    }

    /**
     * RxJava
     */
    @Override
    public void onError(Throwable e) {
        mSwipeRefreshLayout.setRefreshing(false);
        Snackbar.make(mContainer, getString(R.string.network_error), Snackbar.LENGTH_SHORT).show();
    }

    /**
     * RxJava
     */
    @Override
    public void onNext(List<Story> stories) {
        mStories = stories;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
