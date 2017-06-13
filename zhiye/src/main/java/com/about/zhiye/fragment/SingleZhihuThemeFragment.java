package com.about.zhiye.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.about.zhiye.R;
import com.about.zhiye.ZhiYeApp;
import com.about.zhiye.adapter.ThemeEditorAdapter;
import com.about.zhiye.adapter.ThemeStoryAdapter;
import com.about.zhiye.api.ZhihuHelper;
import com.about.zhiye.model.Theme;
import com.about.zhiye.util.QueryPreferences;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by huangyuefeng on 2017/4/16.
 * Contact me : mcxinyu@foxmail.com
 */
public class SingleZhihuThemeFragment extends Fragment
        implements Observer<Theme>, SwipeRefreshLayout.OnRefreshListener {

    private static final String ARGS_THEME_ID = "theme_id";
    private static final String ARGS_THEME_NAME = "theme_name";

    @BindView(R.id.image_view)
    ImageView mImageView;
    @BindView(R.id.image_source)
    TextView mImageSource;
    @BindView(R.id.description_text_view)
    TextView mDescriptionTextView;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.collapsing_layout)
    CollapsingToolbarLayout mCollapsingLayout;
    @BindView(R.id.editor_recycler_view)
    RecyclerView mEditorRecyclerView;
    @BindView(R.id.editor_layout)
    LinearLayout mEditorLayout;
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.coordinator_layout)
    CoordinatorLayout mCoordinatorLayout;
    @BindView(R.id.scroll_view)
    NestedScrollView mScrollView;
    @BindView(R.id.app_bar_layout)
    AppBarLayout mAppBarLayout;
    @BindView(R.id.editor_text_view)
    TextView mEditorTextView;
    private Unbinder unbinder;

    private int mThemeId;
    private String mThemeName;
    private Theme mTheme;
    private ThemeStoryAdapter mStoryAdapter;
    private ThemeEditorAdapter mEditorAdapter;
    private List<Theme.StoriesBean> mStoriesBeen;
    private List<Theme.EditorsBean> mEditorsBeen;
    private boolean isUserTouch = false;
    private Subscription mSubscribe;

    public static SingleZhihuThemeFragment newInstance(String themeName, int themeId) {

        Bundle args = new Bundle();
        args.putInt(ARGS_THEME_ID, themeId);
        args.putString(ARGS_THEME_NAME, themeName);
        SingleZhihuThemeFragment fragment = new SingleZhihuThemeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mThemeId = getArguments().getInt(ARGS_THEME_ID);
            mThemeName = getArguments().getString(ARGS_THEME_NAME);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_single_zhihu_theme, container, false);
        unbinder = ButterKnife.bind(this, view);

        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);

        LinearLayoutManager editorLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        mEditorRecyclerView.setLayoutManager(editorLayoutManager);
        mEditorAdapter = new ThemeEditorAdapter(getContext(), mEditorsBeen);
        mEditorRecyclerView.setAdapter(mEditorAdapter);

        LinearLayoutManager storyLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(storyLayoutManager);
        mStoryAdapter = new ThemeStoryAdapter(getContext(), mStoriesBeen);
        mRecyclerView.setAdapter(mStoryAdapter);

        // 为了让下拉刷新的时候不先打开 mCoordinatorLayout
        mScrollView.setVisibility(View.INVISIBLE);
        mScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                isUserTouch = scrollY > 0;
            }
        });

        if (UserWantsToRefreshAutomatically()) {
            loadTheme(mThemeId);
        }

        initToolbar();
        return view;
    }

    private boolean UserWantsToRefreshAutomatically() {
        return QueryPreferences.getAutoRefreshState(ZhiYeApp.getInstance());
    }

    private void loadTheme(int themeId) {
        mSubscribe = ZhihuHelper.getTheme(themeId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this);

        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setRefreshing(true);
        }
    }

    private void initToolbar() {

        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        mCollapsingLayout.setTitle(mThemeName);
        mCollapsingLayout.setExpandedTitleColor(Color.TRANSPARENT);
        mAppBarLayout.setExpanded(false);

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
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        if (mSubscribe != null && !mSubscribe.isUnsubscribed()) {
            mSubscribe.unsubscribe();
        }
    }

    private void setView(final Theme theme) {
        mDescriptionTextView.setText(theme.getDescription());

        Glide.with(getContext())
                .load(theme.getBackground()).centerCrop()
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(new GlideDrawableImageViewTarget(mImageView) {
                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> animation) {
                        super.onResourceReady(resource, animation);
                        mImageSource.setText(theme.getImageSource());
                        if (!isUserTouch) {
                            mAppBarLayout.setExpanded(true);
                        }
                    }
                });
    }

    @Override
    public void onCompleted() {
        mSwipeRefreshLayout.setRefreshing(false);
        mSwipeRefreshLayout.setEnabled(false);

        mEditorAdapter.updateEditors(mEditorsBeen);
        mEditorTextView.setVisibility(View.VISIBLE);
        mStoryAdapter.updateStories(mStoriesBeen);

        if (mStoriesBeen.size() > 0) {
             mScrollView.setVisibility(View.VISIBLE);
        }

        setView(mTheme);
    }

    @Override
    public void onError(Throwable e) {
        e.printStackTrace();
        mSwipeRefreshLayout.setRefreshing(false);
        mSwipeRefreshLayout.setEnabled(true);
        Snackbar.make(mCoordinatorLayout, getString(R.string.load_failure), Snackbar.LENGTH_SHORT)
                .setAction(getResources().getString(R.string.retry), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        loadTheme(mThemeId);
                    }
                })
                .show();
    }

    @Override
    public void onNext(Theme theme) {
        mTheme = theme;
        mStoriesBeen = theme.getStories();
        mEditorsBeen = theme.getEditors();
    }

    @Override
    public void onRefresh() {
        loadTheme(mThemeId);
    }
}
