package com.about.zhiye.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
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
import android.widget.Toast;

import com.about.zhiye.R;
import com.about.zhiye.api.ApiFactory;
import com.about.zhiye.model.News;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;

import butterknife.BindView;
import butterknife.ButterKnife;
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
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(KEY_NEWS, mNews);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null){
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
        ButterKnife.bind(this, view);

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
                // TODO: 2017/3/27 滚动返回顶部
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_zhihu_web, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_share:
                shareNews(mNews.getTitle(), mNews.getShareUrl());
                return false;
            case R.id.action_read_later:
                // TODO: 2017/3/25
                return false;
            case R.id.action_browser:
                shareToBrowser(mNews.getShareUrl());
                return false;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void shareToBrowser(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));

        if (isIntentSafe(intent)) {
            startActivity(intent);
        } else {
            Toast.makeText(getContext(), getString(R.string.no_browser), Toast.LENGTH_SHORT).show();
        }
    }

    private void shareNews(String newsTitle, String newsUrl) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        //noinspection deprecation
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        intent.putExtra(Intent.EXTRA_TEXT,
                getString(R.string.the_left_brace) + newsTitle + getString(R.string.the_right_brace) + " " + newsUrl + " " + getString(R.string.share_from_zhihu));
        startActivity(Intent.createChooser(intent, getString(R.string.share_to)));
    }

    private boolean isIntentSafe(Intent intent) {
        return getActivity().getPackageManager().queryIntentActivities(intent, 0).size() > 0;
    }

    /*
    // 分享到客户端相关代码
    private void shareToZhihu() {
        final List<Question> questions = ZhihuHelper.getQuestions(mNews);
        String[] titlesArray = getQuestionTitlesAsStringArray(questions);

        if (titlesArray.length > 1){
            new AlertDialog.Builder(getContext(), R.style.dialog)
                    .setTitle("用知乎打开一个你感兴趣的问题")
                    .setItems(titlesArray, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            shareToZhihuClient(questions.get(which).getUrl());
                        }
                    })
                    .create()
                    .show();
        } else {
            shareToZhihuClient(questions.get(0).getUrl());
        }
    }

    private void shareToZhihuClient(String questionUrl) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(questionUrl));
        intent.setPackage(Constants.Information.ZHIHU_PACKAGE_ID);
        if (isZhihuClientInstalled()) {
            getActivity().startActivity(intent);
        } else {
            Toast.makeText(getContext(), getString(R.string.no_zhihu_client), Toast.LENGTH_SHORT).show();
        }
    }

    private String[] getQuestionTitlesAsStringArray(List<Question> questions) {
        String[] titles = new String[questions.size()];

        for (int i = 0; i < titles.length; i++) {
            titles[i] = questions.get(i).getTitle();
        }

        return titles;
    }

    private boolean isZhihuClientInstalled() {
        try {
            return getActivity()
                    .getPackageManager()
                    .getPackageInfo(Constants.Information.ZHIHU_PACKAGE_ID,
                            PackageManager.GET_ACTIVITIES) != null;
        } catch (PackageManager.NameNotFoundException ignored) {
            return false;
        }
    }
    */

    // @Override
    // public void setUserVisibleHint(boolean isVisibleToUser) {
    //     super.setUserVisibleHint(isVisibleToUser);
    //     loadDetailNews(mNewsId);
    // }

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
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
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
}
