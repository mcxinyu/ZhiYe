package com.about.zhiye.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.about.zhiye.R;
import com.about.zhiye.ZhiYeApp;
import com.about.zhiye.activity.ZhihuWebActivity;
import com.about.zhiye.adapter.NewsListAdapter;
import com.about.zhiye.adapter.QuestionListAdapter;
import com.about.zhiye.api.ZhihuHelper;
import com.about.zhiye.db.DBLab;
import com.about.zhiye.model.News;
import com.about.zhiye.model.Question;
import com.about.zhiye.util.QueryPreferences;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static android.app.Activity.RESULT_OK;


/**
 * Created by huangyuefeng on 2017/3/18.
 * Contact me : mcxinyu@foxmail.com
 * 一天的 NewsList
 */
public class SingleZhihuNewsListFragment extends BaseFragment
        implements SwipeRefreshLayout.OnRefreshListener,
        Observer<List<News>>,
        NewsListAdapter.Callbacks {

    private static final String ARGS_DATE = "date";
    private static final String ARGS_KEY_WORD = "key_word";

    private static final int REQUEST_CODE = 1024;

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.container)
    FrameLayout mContainer;
    @BindView(R.id.read_later_empty_layout)
    RelativeLayout mReadLaterEmptyLayout;
    @BindView(R.id.empty_image_view)
    ImageView mEmptyImageView;
    @BindView(R.id.empty_text_view)
    TextView mEmptyTextView;
    private Unbinder unbinder;

    private List<News> mNewses;
    private List<Question> mQuestions;

    private Callbacks mCallback;
    private NewsListAdapter mNewsAdapter;
    private QuestionListAdapter mQuestionListAdapter;
    private String mDate;
    private String mKeyWord;
    private boolean isRefreshed = false;
    private boolean isPreloadFailure = false;

    private int totalScrollY = 0;

    private boolean isReadLaterFragment = false;
    private boolean isSearchResultFragment = false;
    private boolean isDailyNewsFragment = false;

    private Subscription mSubscribe;
    private int mUnReadLaterCount;

    public static SingleZhihuNewsListFragment newInstance(@Nullable String date, @Nullable String keyWord) {
        SingleZhihuNewsListFragment fragment = new SingleZhihuNewsListFragment();

        Bundle args = new Bundle();
        if (date != null) {
            args.putString(ARGS_DATE, date);
            fragment.setArguments(args);
        }
        if (keyWord != null) {
            args.putString(ARGS_KEY_WORD, keyWord);
            fragment.setArguments(args);
        }

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            if ((mDate = getArguments().getString(ARGS_DATE)) != null) {
                isDailyNewsFragment = true;
            } else if ((mKeyWord = getArguments().getString(ARGS_KEY_WORD)) != null) {
                isSearchResultFragment = true;
            }
        } else {
            isReadLaterFragment = true;
        }
        mUnReadLaterCount = DBLab.get(getContext()).queryAllUnHaveReadCountForReadLater();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_single_zhihu_news_list, container, false);
        unbinder = ButterKnife.bind(this, view);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        if (isSearchResultFragment) {
            mQuestionListAdapter = new QuestionListAdapter(getContext(), mQuestions);
            mRecyclerView.setAdapter(mQuestionListAdapter);
        } else {
            mNewsAdapter = new NewsListAdapter(getContext(), mNewses, this);
            mRecyclerView.setAdapter(mNewsAdapter);
        }

        // 为了让下拉刷新的时候不先打开 mCoordinatorLayout
        mRecyclerView.setVisibility(View.INVISIBLE);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                totalScrollY -= dy;
            }
        });

        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Callbacks) {
            mCallback = (Callbacks) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement SearchViewCallbacks");
        }
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
        mCallback = null;
        if (mSubscribe != null && !mSubscribe.isUnsubscribed()) {
            mSubscribe.unsubscribe();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        refreshIf(shouldRefreshOnVisibilityChange(isVisibleToUser));
    }

    private void setEmptyNewsView(int visible) {
        if (isReadLaterFragment) {
            mEmptyImageView.setImageResource(R.drawable.ic_empty_bookmark);
            mEmptyTextView.setText(getString(R.string.read_later_empty));
        }
        mReadLaterEmptyLayout.setVisibility(visible);
        if (isSearchResultFragment) {
            if (mQuestions.size() > 0) mRecyclerView.setVisibility(View.VISIBLE);
        } else {
            if (mNewses.size() > 0) mRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void setErrorView() {
        mEmptyImageView.setImageResource(R.drawable.ic_empty_error);
        mEmptyTextView.setText(getString(R.string.load_failure));
        mReadLaterEmptyLayout.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.INVISIBLE);
    }

    private boolean UserWantsToRefreshAutomatically() {
        return QueryPreferences.getAutoRefreshState(ZhiYeApp.getInstance());
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

    @Override
    public void scrollToTop() {
        mRecyclerView.smoothScrollToPosition(0);
    }

    @Override
    public int getVerticalOffset() {
        return totalScrollY;
    }

    public void notifyDataSetChanged() {
        mNewsAdapter.notifyDataSetChanged();
    }

    public boolean isFirstItemOnTop() {
        return mRecyclerView.getVisibility() == View.INVISIBLE ||
                mRecyclerView == null ||
                ((LinearLayoutManager) mRecyclerView.getLayoutManager()).findFirstVisibleItemPosition() == 0;
    }

    /**
     * 获取数据后刷新
     *
     * @param isRefreshReadLater 外部手动调用该方法刷新 ReadLaterNewses
     */
    public void doRefresh(boolean isRefreshReadLater) {
        if (isReadLaterFragment) {
            mSubscribe = getReadLaterNewsesObservable()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this);
        } else if (isSearchResultFragment) {
            if (mKeyWord == null) {
                isPreloadFailure = true;
                return;
            }
            mSubscribe = getSearchResultObservable()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<List<Question>>() {
                        @Override
                        public void onCompleted() {
                            isRefreshed = true;
                            mSwipeRefreshLayout.setRefreshing(false);
                            mQuestionListAdapter.updateStories(mQuestions);
                            setEmptyNewsView(mQuestions.size() > 0 ? View.INVISIBLE : View.VISIBLE);
                        }

                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                            mSwipeRefreshLayout.setRefreshing(false);
                            setErrorView();
                            if (isAdded()) {
                                Snackbar.make(mContainer, getString(R.string.load_failure), Snackbar.LENGTH_SHORT)
                                        .setAction(getResources().getString(R.string.retry), new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                doRefresh(true);
                                            }
                                        })
                                        .show();
                            }
                        }

                        @Override
                        public void onNext(List<Question> questions) {
                            mQuestions = questions;
                        }
                    });
        } else {
            // date 有可能为空，因为 setUserVisibleHint 可能在 onCreate 之前调用。
            if (mDate == null) {
                isPreloadFailure = true;
                return;
            }
            mSubscribe = getNewsesObservableOfDate()
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

    private Observable<List<Question>> getSearchResultObservable() {
        return ZhihuHelper.getQuestionsWithKeyword(mKeyWord);
    }

    /**
     * RxJava
     */
    @Override
    public void onCompleted() {
        if (isReadLaterFragment && mNewses.size() < mUnReadLaterCount) {
            //  如果存在记录被服务器删除了，那么需要再刷新一次
            doRefresh(true);
            updateBottomNavigationNotification();
        } else {
            isRefreshed = true;
            mSwipeRefreshLayout.setRefreshing(false);
            mNewsAdapter.updateStories(mNewses);
            setEmptyNewsView(mNewses.size() > 0 ? View.INVISIBLE : View.VISIBLE);
            if (isReadLaterFragment) {
                updateBottomNavigationNotification();
            }
        }
    }

    /**
     * RxJava
     */
    @Override
    public void onError(Throwable e) {
        e.printStackTrace();
        mSwipeRefreshLayout.setRefreshing(false);
        setErrorView();
        if (isAdded()) {
            Snackbar.make(mContainer, getString(R.string.load_failure), Snackbar.LENGTH_SHORT)
                    .setAction(getResources().getString(R.string.retry), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            doRefresh(true);
                        }
                    })
                    .show();
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
                    notifyItemChangeOfNewsId(data.getStringExtra("newsId"));
                    break;
                default:
                    break;
            }
        }
    }

    private void notifyItemChangeOfNewsId(String newsId) {
        for (int i = 0; i < mNewses.size(); i++) {
            if (mNewses.get(i).getId().equals(newsId)) {
                mNewsAdapter.notifyItemChanged(i, "newsId");
                updateBottomNavigationNotification();
            }
        }
    }

    @Override
    public void startZhihuWebActivity(String newsId) {
        startActivityForResult(ZhihuWebActivity.newIntent(getContext(), newsId, "0"), REQUEST_CODE);
    }

    @Override
    public void hasRead(String newsId) {
        if (isReadLaterFragment) {
            DBLab.get(getContext()).insertHaveReadNewsForReadLater(newsId);
            updateBottomNavigationNotification();
        } else {
            DBLab.get(getContext()).insertHaveReadNews(newsId);
        }
    }

    @Override
    public boolean isReadLaterFragment() {
        return isReadLaterFragment;
    }

    @Override
    public void addReadLater(final String newsId, boolean added) {
        if (added) {
            Snackbar.make(mContainer, getString(R.string.added_read_later), Snackbar.LENGTH_SHORT)
                    .setAction(getString(R.string.undo), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            DBLab.get(getContext()).deleteReadLaterNews(newsId);
                            notifyItemChangeOfNewsId(newsId);
                        }
                    })
                    .show();
        } else {
            Snackbar.make(mContainer, getString(R.string.removed_read_later), Snackbar.LENGTH_SHORT)
                    .setAction(getString(R.string.undo), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            DBLab.get(getContext()).insertReadLaterNews(newsId);
                            notifyItemChangeOfNewsId(newsId);
                        }
                    })
                    .show();
        }
        updateBottomNavigationNotification();
    }

    public void updateBottomNavigationNotification() {
        mUnReadLaterCount = DBLab.get(getContext()).queryAllUnHaveReadCountForReadLater();
        if (mUnReadLaterCount > 0) {
            mCallback.setBottomNavigationNotification("" + mUnReadLaterCount, 2);
        } else {
            mCallback.setBottomNavigationNotification(null, 2);
        }
    }

    public interface Callbacks {
        void setBottomNavigationNotification(String title, int position);
    }
}
