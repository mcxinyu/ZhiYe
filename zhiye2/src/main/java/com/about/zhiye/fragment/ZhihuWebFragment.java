package com.about.zhiye.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.about.zhiye.R;
import com.about.zhiye.api.ApiFactory;
import com.about.zhiye.model.News;
import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * A placeholder fragment containing a simple view.
 */
public class ZhihuWebFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, Observer<News> {
    private static final String ARGS_NEWS_ID = "news";

    @BindView(R.id.image_view)
    ImageView mImageView;
    @BindView(R.id.image_source)
    TextView mImageSource;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.collapsing_layout)
    CollapsingToolbarLayout mCollapsingLayout;
    @BindView(R.id.web_view)
    WebView mWebView;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.floating_action_button)
    FloatingActionButton mFloatingActionButton;
    @BindView(R.id.coordinator_layout)
    CoordinatorLayout mCoordinatorLayout;

    private boolean isRefreshed = false;
    private String mNewsId;
    private News mNews;

    public static ZhihuWebFragment newInstance(String newsId) {
        Bundle args = new Bundle();
        args.putString(ARGS_NEWS_ID, newsId);
        ZhihuWebFragment fragment = new ZhihuWebFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mNewsId = getArguments().getString(ARGS_NEWS_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_zhihu_web, container, false);
        ButterKnife.bind(this, view);

        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);

        loadDetailNews(mNewsId);

        initToolbar();
        return view;
    }

    private void initToolbar() {
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        // mCollapsingLayout.setTitle(getString(R.string.title_zhihu));
        // mCollapsingLayout.setExpandedTitleColor(Color.TRANSPARENT);

        ActionBar supportActionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (supportActionBar != null){
            supportActionBar.setDisplayHomeAsUpEnabled(true);
            supportActionBar.setHomeButtonEnabled(true);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mImageView != null) {
            Glide.clear(mImageView);
        }
    }

    /**
     * Fragment 的懒加载
     * @param isVisibleToUser
     */
    // @Override
    // public void setUserVisibleHint(boolean isVisibleToUser) {
    //     super.setUserVisibleHint(isVisibleToUser);
    //     loadDetailNews(mNewsId);
    // }

    public void loadDetailNews(String id) {
        ApiFactory.getZhihuApiSingleton().getDetailNews(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this);

        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setRefreshing(true);
        }
    }

    private void setWebView(News news) {

        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);

        String head = "<head>\n\t<link rel=\"stylesheet\" href=\""
                + news.getCss()[0] + "\"/>\n</head>";
        String image = "<div class=\"headline\">";
        String html = head + news.getBody().replace(image, " ");
        mWebView.loadDataWithBaseURL(null, html, "text/html", "utf-8", null);
        Glide.with(this).load(news.getImage()).centerCrop().into(mImageView);

        // mTitleTextView.setText(news.getTitle());
        mImageSource.setText(news.getImageSource());
    }

    @Override
    public void onRefresh() {
        // loadDetailNews(mNewsId);
    }

    @Override
    public void onCompleted() {
        isRefreshed = true;
        mSwipeRefreshLayout.setRefreshing(false);
        mCollapsingLayout.setTitle(mNews.getTitle());
        setWebView(mNews);
    }

    @Override
    public void onError(Throwable e) {
        e.printStackTrace();
        mSwipeRefreshLayout.setRefreshing(false);
        Snackbar.make(mCoordinatorLayout, getString(R.string.network_error), Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onNext(News news) {
        mNews = news;
    }
}
