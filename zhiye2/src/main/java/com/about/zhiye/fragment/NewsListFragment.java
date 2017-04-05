package com.about.zhiye.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.about.zhiye.activity.ZhihuWebActivity;
import com.about.zhiye.adapter.NewsListAdapter;
import com.about.zhiye.api.ZhihuHelper;
import com.about.zhiye.model.News;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static android.app.Activity.RESULT_OK;


/**
 * Created by huangyuefeng on 2017/3/18.
 * Contact me : mcxinyu@foxmail.com
 * 一天的 NewsList
 */
public class NewsListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener,
        Observer<List<News>>,
        NewsListAdapter.Callbacks {

    private static final String ARGS_DATE = "date";
    private static final int REQUEST_CODE = 1024;

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.container)
    FrameLayout mContainer;
    Unbinder unbinder;

    private List<News> mNewses;

    private OnFragmentInteractionListener mListener;
    private NewsListAdapter mNewsAdapter;
    private String mDate;
    private boolean isRefreshed = false;
    private boolean isPreloadFailure = false;
    private boolean isReadLaterFragment = false;

    public static NewsListFragment newInstance(@Nullable String date) {
        NewsListFragment fragment = new NewsListFragment();
        if (date == null){
            return fragment;
        }

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
        } else {
            // 如果没有参数传入，那么当作 ReadLaterFragment 用
            isReadLaterFragment = true;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news_list, container, false);
        unbinder = ButterKnife.bind(this, view);

        boolean reverseLayout = false;
        if (isReadLaterFragment){
            reverseLayout = true;
            mRecyclerView.setHasFixedSize(true);
        }
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, reverseLayout));
        mNewsAdapter = new NewsListAdapter(getContext(), mNewses, this);
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

    public int getCurrentItem() {
        return ((LinearLayoutManager) mRecyclerView.getLayoutManager())
                .findFirstCompletelyVisibleItemPosition();
    }

    public void setRecyclerScrollTo(int position) {
        if (mNewses.size() > position) {
            mRecyclerView.smoothScrollToPosition(position);
            // mRecyclerView.scrollToPosition(position);
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
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (isPreloadFailure) {
            refreshIf(shouldRefreshOnVisibilityChange(true));
        }
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
        return true;
    }

    private boolean shouldRefreshOnVisibilityChange(boolean isVisibleToUser) {
        return isVisibleToUser && UserWantsToRefreshAutomatically() && !isRefreshed;
    }

    private void refreshIf(boolean need) {
        if (need) {
            doRefresh(false);
        }
    }

    @Override
    public void onRefresh() {
        doRefresh(false);
    }

    /**
     * 获取数据后刷新
     * @param isRefreshReadLater 外部手动调用该方法刷新 ReadLaterNewses
     */
    public void doRefresh(boolean isRefreshReadLater) {
        if (isRefreshReadLater || isReadLaterFragment) {
            getReadLaterNewsesObservable()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this);
        } else {
            // date 有可能为空，因为 setUserVisibleHint 可能在 onCreate 之前调用。
            if (mDate == null) {
                isPreloadFailure = true;
                return;
            }
            getNewsesObservableOfDate()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this);
        }

        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setRefreshing(true);
        }
    }

    private Observable<List<News>> getNewsesObservableOfDate() {
        return ZhihuHelper.getNewsesOfDate(mDate);
    }

    private Observable<List<News>> getReadLaterNewsesObservable() {
        return ZhihuHelper.getNewsesOfIds(getContext());
    }

    /**
     * RxJava
     */
    @Override
    public void onCompleted() {
        isRefreshed = true;
        mSwipeRefreshLayout.setRefreshing(false);
        mNewsAdapter.updateStories(mNewses);
    }

    /**
     * RxJava
     */
    @Override
    public void onError(Throwable e) {
        e.printStackTrace();
        mSwipeRefreshLayout.setRefreshing(false);
        if (isAdded()) {
            Snackbar.make(mContainer, getString(R.string.network_error), Snackbar.LENGTH_SHORT).show();
        }
    }

    /**
     * RxJava
     */
    @Override
    public void onNext(List<News> newses) {
        mNewses = newses;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            switch (resultCode) {
                case RESULT_OK:
                    boolean change = data.getBooleanExtra("change", false);
                    if (change) {
                        // TODO: 2017/3/29 添加了稍后阅读需要修改刘表中相对应的 item 的显示，
                        // 阅读过的 item 也需要修改以表示已经阅读过了，或者直接在点击事件中修改
                    }
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void startZhihuWebActivity(String newsId) {
        startActivityForResult(ZhihuWebActivity.newIntent(getContext(), newsId), REQUEST_CODE);
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
