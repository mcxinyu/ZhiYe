package com.about.zhiye.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.about.zhiye.R;
import com.about.zhiye.activity.ShowImageFromWebActivity;
import com.about.zhiye.api.ApiFactory;
import com.about.zhiye.api.ZhihuHelper;
import com.about.zhiye.db.DBLab;
import com.about.zhiye.model.News;
import com.bm.library.Info;
import com.bm.library.PhotoView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * A placeholder fragment containing a simple view.
 */
public class ZhihuWebFragment extends BackHandledFragment
        implements SwipeRefreshLayout.OnRefreshListener, Observer<News> {
    private static final String ARGS_NEWS_ID = "news_id";
    private static final String KEY_NEWS = "news_save";
    private static final String ARGS_TYPE = "type";

    @BindView(R.id.image_view)
    PhotoView mImageView;
    @BindView(R.id.image_view2)
    PhotoView mImageView2;
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
    @BindView(R.id.app_bar_layout)
    AppBarLayout mAppBarLayout;
    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;
    @BindView(R.id.share_button)
    Button mShareButton;
    @BindView(R.id.save_button)
    Button mSaveButton;
    private Unbinder unbinder;

    private String mNewsId;
    private String mType;
    private News mNews;
    private Callbacks mCallbacks;
    private boolean isUserTouch = false;
    private Subscription mSubscribe;
    private Info mRectF;

    public static ZhihuWebFragment newInstance(String newsId, String type) {
        Bundle args = new Bundle();
        args.putString(ARGS_NEWS_ID, newsId);
        args.putString(ARGS_TYPE, type);
        ZhihuWebFragment fragment = new ZhihuWebFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mNewsId = getArguments().getString(ARGS_NEWS_ID);
            mType = getArguments().getString(ARGS_TYPE);
        }
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_zhihu_web, container, false);
        unbinder = ButterKnife.bind(this, view);

        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);

        // 为了让下拉刷新的时候不先打开 mCoordinatorLayout
        mScrollView.setVisibility(View.INVISIBLE);
        mScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                isUserTouch = scrollY > 0;
            }
        });

        loadDetailNews(mNewsId);

        initToolbar();
        return view;
    }

    private void initToolbar() {
        mImageView.disenable();
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mImageView.setVisibility(View.GONE);
                mImageView2.setVisibility(View.VISIBLE);
                mSaveButton.setVisibility(View.VISIBLE);
                mSaveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        downloadImage(mNews.getImage());
                    }
                });

                mRectF = mImageView.getInfo();
                mImageView2.animaFrom(mRectF);
            }
        });

        mImageView2.enable();
        mImageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mImageView2.animaTo(mRectF, new Runnable() {
                    @Override
                    public void run() {
                        mImageView2.setVisibility(View.GONE);
                        mSaveButton.setVisibility(View.GONE);
                        mImageView.setVisibility(View.VISIBLE);
                    }
                });
            }
        });

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
    public boolean onBackPressed() {
        if (mImageView2.getVisibility() == View.VISIBLE) {
            mImageView2.animaTo(mRectF, new Runnable() {
                @Override
                public void run() {
                    mImageView2.setVisibility(View.GONE);
                    mImageView.setVisibility(View.VISIBLE);
                }
            });
            return true;
        } else {
            return false;
        }
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
        mCallbacks = null;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
        if (mSubscribe != null && !mSubscribe.isUnsubscribed()) {
            mSubscribe.unsubscribe();
        }
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
                if (mNews != null) {
                    ZhihuHelper.shareNews(getContext(), mNews.getTitle(), mNews.getShareUrl());
                }
                return false;
            case R.id.action_read_later:
                if (dbLab.queryReadLaterExist(mNewsId)) {
                    dbLab.deleteReadLaterNews(mNewsId);
                    item.setIcon(R.drawable.ic_action_read_later_unselected);
                    Snackbar.make(mSwipeRefreshLayout, getString(R.string.removed_read_later), Snackbar.LENGTH_SHORT)
                            .setAction(getString(R.string.undo), new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    DBLab.get(getContext()).insertReadLaterNews(mNewsId);
                                }
                            })
                            .show();
                    mCallbacks.readLaterStatusChange(false);
                } else {
                    dbLab.insertReadLaterNews(mNewsId);
                    item.setIcon(R.drawable.ic_action_read_later_selected);
                    Snackbar.make(mSwipeRefreshLayout, getString(R.string.added_read_later), Snackbar.LENGTH_SHORT)
                            .setAction(getString(R.string.undo), new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    DBLab.get(getContext()).deleteReadLaterNews(mNewsId);
                                }
                            })
                            .show();
                    mCallbacks.readLaterStatusChange(true);
                }
                return false;
            case R.id.action_browser:
                if (mNews != null) {
                    ZhihuHelper.shareToBrowser(getContext(), mNews.getShareUrl());
                }
                return false;
            case R.id.mark_as_unread:
                dbLab.deleteHaveReadNews(mNewsId);
                mCallbacks.readLaterStatusChange(true);
                Toast.makeText(getContext(), getString(R.string.action_marked_as_unread), Toast.LENGTH_SHORT).show();
                return false;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Callbacks) {
            mCallbacks = (Callbacks) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement SearchViewCallbacks");
        }
    }

    private void loadDetailNews(String id) {
        mSubscribe = ApiFactory.getZhihuApiSingleton().getDetailNews(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this);

        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setRefreshing(true);
        }
    }

    private void setWebView(final News news) {
        mTitleTextView.setText(news.getTitle());
        if (null == news.getImage()) {
            mScrollView.setNestedScrollingEnabled(false);
            AppBarLayout.LayoutParams params =
                    (AppBarLayout.LayoutParams) mAppBarLayout.getChildAt(0).getLayoutParams();
            params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL |
                    AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED |
                    AppBarLayout.LayoutParams.SCROLL_FLAG_SNAP);
        } else {
            Glide.with(this)
                    .load(news.getImage()).centerCrop()
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(new GlideDrawableImageViewTarget(mImageView) {
                        @Override
                        public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> animation) {
                            super.onResourceReady(resource, animation);
                            mImageSource.setText(news.getImageSource());
                            AppBarLayout.LayoutParams params =
                                    (AppBarLayout.LayoutParams) mAppBarLayout.getChildAt(0).getLayoutParams();
                            params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL |
                                    AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS |
                                    AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS_COLLAPSED);
                            if (!isUserTouch) {
                                mAppBarLayout.setExpanded(true);
                            }
                        }
                    });
            Glide.with(this)
                    .load(news.getImage())
                    .fitCenter()
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(mImageView2);
        }
        mScrollView.setVisibility(View.VISIBLE);
        setWebSettings(news);
    }

    private void setWebSettings(News news) {
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (mProgressBar != null) {
                    mProgressBar.setProgress(newProgress);
                }
                if (newProgress >= 100) {
                    if (mProgressBar != null) {
                        mProgressBar.setVisibility(View.INVISIBLE);
                    }
                }
                super.onProgressChanged(view, newProgress);
            }
        });
        mWebView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                responseWebLongClick(v);
                return false;
            }
        });

        switch (mType) {
            case "1":   // 1、无body，无图片；
                mWebView.loadUrl(news.getShareUrl());
                mWebView.setWebViewClient(new WebViewClient() {
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        if (mProgressBar != null) {
                            mProgressBar.setVisibility(View.VISIBLE);
                        }
                        view.loadUrl(url);
                        return super.shouldOverrideUrlLoading(view, url);
                    }
                });
                break;
            case "0":   // 0、有body，有图片；
            case "2":   // 2、有body，无图片；
                WebSettings settings = mWebView.getSettings();
                settings.setJavaScriptEnabled(true);
                settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);

                String head = "<head>\n\t<link rel=\"stylesheet\" href=\""
                        + news.getCss()[0] + "\"/>\n</head>";
                String topImage = "<div class=\"headline\">";
                String html = head + news.getBody().replace(topImage, " ");
                mWebView.addJavascriptInterface(new JavascriptInterface(getContext()), "ImageListener");
                mWebView.setWebViewClient(new WebViewClient() {
                    @Override
                    public void onPageStarted(WebView view, String url, Bitmap favicon) {
                        view.getSettings().setJavaScriptEnabled(true);
                        super.onPageStarted(view, url, favicon);
                    }

                    @Override
                    public void onPageFinished(WebView view, String url) {
                        view.getSettings().setJavaScriptEnabled(true);
                        super.onPageFinished(view, url);
                        addImageClickListener();
                    }
                });
                mWebView.loadDataWithBaseURL(null, html, "text/html", "utf-8", null);
                break;
        }
    }

    private ArrayList<String> getAllImageUrl(String body) {
        ArrayList<String> list = new ArrayList<>();
        Elements elements = Jsoup.parseBodyFragment(body)
                .body()
                .select(".content-image");
        // .getElementsByClass("content-image");
        for (Element element : elements) {
            list.add(element.attr("src"));
        }
        return list;
    }

    private void responseWebLongClick(View v) {
        if (v instanceof WebView) {
            WebView.HitTestResult result = ((WebView) v).getHitTestResult();
            if (result != null) {
                int type = result.getType();
                //判断点击类型如果是图片
                if (type == WebView.HitTestResult.IMAGE_TYPE || type == WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE) {
                    //弹出对话框
                    showImageOptionDialog(result.getExtra());
                }
            }
        }
    }

    private void showImageOptionDialog(final String url) {
        // String[] items = new String[]{getString(R.string.save_picture), getString(R.string.share_picture)};
        String[] items = new String[]{getString(R.string.save_picture)};
        new AlertDialog.Builder(getContext())
                .setCancelable(true)
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                downloadImage(url);
                                break;
                            case 1:
                                // TODO: 2017/7/3
                                Toast.makeText(getContext(), "分享", Toast.LENGTH_SHORT).show();
                                break;
                        }
                        dialog.dismiss();
                    }
                })
                .show();
    }

    private void addImageClickListener() {
        // 这段js函数的功能就是，遍历所有的img几点，
        // 并添加onclick函数，函数的功能是在图片点击的时候调用本地java接口并传递url过去
        mWebView.loadUrl("javascript:(function(){" +
                "var objects = document.getElementsByTagName(\"img\");" +
                "  for (var i=0;i<objects.length;i++) {" +
                "    if (objects[i].getAttribute(\"class\") == \"content-image\") {" +
                "      objects[i].onclick = function() {" +
                "        window.ImageListener.openImage(this.src);" +
                "      }" +
                "    }" +
                "  }" +
                "})()");
    }

    private class JavascriptInterface {
        private Context mContext;

        JavascriptInterface(Context context) {
            mContext = context;
        }

        @android.webkit.JavascriptInterface
        public void openImage(String img) {
            Intent intent = ShowImageFromWebActivity
                    .newIntent(getContext(), getAllImageUrl(mNews.getBody()), img);
            mContext.startActivity(intent);
        }
    }

    private void downloadImage(String url) {
        ZhihuHelper.downloadZhihuImageToAlbum(getContext(), url);
    }

    @Override
    public void onRefresh() {
        loadDetailNews(mNewsId);
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
        mSwipeRefreshLayout.setEnabled(true);
        Snackbar.make(mCoordinatorLayout, getString(R.string.load_failure), Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onNext(News news) {
        mNews = news;
    }

    public interface Callbacks {
        void readLaterStatusChange(boolean added);
    }

    public WebView getWebView() {
        return mWebView;
    }
}
