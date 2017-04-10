package com.about.zhiye.fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.about.zhiye.R;
import com.about.zhiye.api.ApiFactory;
import com.about.zhiye.api.ZhihuHelper;
import com.about.zhiye.db.DBLab;
import com.about.zhiye.model.News;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * A placeholder fragment containing a simple view.
 */
public class ZhihuWebFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, Observer<News> {
    private static final String ARGS_NEWS_ID = "news_id";
    private static final String KEY_NEWS = "news_save";

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
    @BindView(R.id.coordinator_layout)
    CoordinatorLayout mCoordinatorLayout;
    @BindView(R.id.title_text_view)
    TextView mTitleTextView;
    @BindView(R.id.scroll_view)
    NestedScrollView mScrollView;
    private Unbinder unbinder;

    private String mNewsId;
    private News mNews;
    private OnFragmentInteractionListener mListener;

    public static ZhihuWebFragment newInstance(String newsId) {
        Bundle args = new Bundle();
        args.putString(ARGS_NEWS_ID, newsId);
        ZhihuWebFragment fragment = new ZhihuWebFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(KEY_NEWS, mNews);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mNews = (News) savedInstanceState.getSerializable(KEY_NEWS);
            mNewsId = mNews.getId();
        } else {
            if (getArguments() != null) {
                mNewsId = getArguments().getString(ARGS_NEWS_ID);
            }
        }
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_zhihu_web, container, false);
        unbinder = ButterKnife.bind(this, view);

        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);

        loadDetailNews(mNewsId);

        initToolbar();
        return view;
    }

    private void initToolbar() {

        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        mCollapsingLayout.setExpandedTitleColor(Color.TRANSPARENT);

        ActionBar supportActionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
            supportActionBar.setHomeButtonEnabled(true);
        }

        mToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mScrollView.fullScroll(View.FOCUS_UP);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mImageView != null) {
            Glide.clear(mImageView);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_zhihu_web, menu);

        MenuItem readLaterItem = menu.findItem(R.id.action_read_later);
        DBLab dbLab = DBLab.get(getContext());
        if (dbLab.queryReadLaterExist(mNewsId)) {
            readLaterItem.setIcon(R.drawable.ic_action_read_later_selected);
        } else {
            readLaterItem.setIcon(R.drawable.ic_action_read_later_unselected);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        DBLab dbLab = DBLab.get(getContext());
        switch (item.getItemId()) {
            case R.id.action_share:
                ZhihuHelper.shareNews(getContext(), mNews.getTitle(), mNews.getShareUrl());
                return false;
            case R.id.action_read_later:
                if (dbLab.queryReadLaterExist(mNews.getId())) {
                    dbLab.deleteReadLaterNews(mNews.getId());
                    item.setIcon(R.drawable.ic_action_read_later_unselected);
                    mListener.readLaterStatusChange(false);
                } else {
                    dbLab.insertReadLaterNews(mNews.getId());
                    item.setIcon(R.drawable.ic_action_read_later_selected);
                    mListener.readLaterStatusChange(true);
                }
                return false;
            case R.id.action_browser:
                ZhihuHelper.shareToBrowser(getContext(), mNews.getShareUrl());
                return false;
            case R.id.mark_as_unread:
                dbLab.deleteHaveReadNews(mNews.getId());
                return false;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    private void loadDetailNews(String id) {
        ApiFactory.getZhihuApiSingleton().getDetailNews(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this);

        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setRefreshing(true);
        }
    }

    private void setWebView(final News news) {
        mTitleTextView.setText(news.getTitle());
        Glide.with(this)
                .load(news.getImage()).centerCrop()
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(new GlideDrawableImageViewTarget(mImageView) {
                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> animation) {
                        super.onResourceReady(resource, animation);
                        mImageSource.setText(news.getImageSource());
                    }
                });

        WebSettings settings = mWebView.getSettings();
        // settings.setJavaScriptEnabled(true);
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);

        String head = "<head>\n\t<link rel=\"stylesheet\" href=\""
                + news.getCss()[0] + "\"/>\n</head>";
        String image = "<div class=\"headline\">";
        String html = head + news.getBody().replace(image, " ");
        mWebView.loadDataWithBaseURL(null, html, "text/html", "utf-8", null);
    }

    @Override
    public void onRefresh() {
        // loadDetailNews(mNewsId);
    }

    @Override
    public void onCompleted() {
        mSwipeRefreshLayout.setRefreshing(false);
        mSwipeRefreshLayout.setEnabled(false);
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

    public interface OnFragmentInteractionListener {
        void readLaterStatusChange(boolean added);
    }
}
