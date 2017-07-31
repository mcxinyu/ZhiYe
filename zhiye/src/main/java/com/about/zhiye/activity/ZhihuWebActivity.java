package com.about.zhiye.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.MenuItem;

import com.about.zhiye.ZhiYeApp;
import com.about.zhiye.db.DBLab;
import com.about.zhiye.fragment.BackHandledFragment;
import com.about.zhiye.fragment.ZhihuWebFragment;
import com.oubowu.slideback.SlideBackHelper;
import com.oubowu.slideback.SlideConfig;
import com.oubowu.slideback.widget.SlideBackLayout;

public class ZhihuWebActivity extends BaseActivity
        implements ZhihuWebFragment.Callbacks,
        BackHandledFragment.BackHandledInterface {
    private static final String EXTRA_NEWS_ID = "news_id";
    private static final String EXTRA_TYPE = "type";

    private ZhihuWebFragment mFragment;

    private String mNewsId;
    private String mType;
    private boolean isReadLaterAdd;
    private SlideBackLayout slideBackLayout;

    public static Intent newIntent(Context context, String newsId, String type) {
        Intent intent = new Intent(context, ZhihuWebActivity.class);
        intent.putExtra(EXTRA_NEWS_ID, newsId);
        intent.putExtra(EXTRA_TYPE, type);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        mFragment = ZhihuWebFragment.newInstance(mNewsId, mType);
        return mFragment;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mNewsId = getIntent().getStringExtra(EXTRA_NEWS_ID);
        mType = getIntent().getStringExtra(EXTRA_TYPE);
        isReadLaterAdd = DBLab.get(this).queryReadLaterExist(mNewsId);

        super.onCreate(savedInstanceState);

        slideBackLayout = SlideBackHelper.attach(this,
                ZhiYeApp.getActivityHelper(),
                new SlideConfig.Builder()
                        .rotateScreen(true)
                        .edgeOnly(false)
                        .lock(false)
                        .edgePercent(0.1f)
                        .slideOutPercent(0.5f)
                        .create(),
                null);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void readLaterStatusChange(boolean added) {
        Intent intent = new Intent();
        intent.putExtra("newsId", mNewsId);
        setResult(RESULT_OK, intent);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && mFragment != null && mFragment.getWebView().canGoBack()) {
            mFragment.getWebView().goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        if (mFragment == null || !((BackHandledFragment) mFragment).onBackPressed()) {
            super.onBackPressed();
            slideBackLayout.isComingToFinish();
        }
    }

    @Override
    public void setSelectedFragment(BackHandledFragment selectedFragment) {
        // mFragment = selectedFragment;
    }
}
