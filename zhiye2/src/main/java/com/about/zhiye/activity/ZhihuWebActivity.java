package com.about.zhiye.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.about.zhiye.R;
import com.about.zhiye.fragment.ZhihuWebFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ZhihuWebActivity extends AppCompatActivity {
    private static final String EXTRA_NEWS_ID = "news_id";

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

    private String mNewsId;

    public static Intent newIntent(Context context, String newsId) {

        Intent intent = new Intent(context, ZhihuWebActivity.class);
        intent.putExtra(EXTRA_NEWS_ID, newsId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zhihu_web);
        ButterKnife.bind(this);

        mNewsId = getIntent().getStringExtra(EXTRA_NEWS_ID);

        FragmentManager fragmentManager = getSupportFragmentManager();

        Fragment fragment = fragmentManager.findFragmentById(R.id.fragment_container);

        if (fragment == null) {
            fragment = ZhihuWebFragment.newInstance(mNewsId);
            fragmentManager.beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    .commit();
        }
    }

}
