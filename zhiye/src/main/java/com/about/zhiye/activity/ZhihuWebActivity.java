package com.about.zhiye.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.MenuItem;

import com.about.zhiye.db.DBLab;
import com.about.zhiye.fragment.ZhihuWebFragment;

public class ZhihuWebActivity extends BaseActivity
        implements ZhihuWebFragment.Callbacks {
    private static final String EXTRA_NEWS_ID = "news_id";
    private static final String EXTRA_TYPE = "type";

    private ZhihuWebFragment mFragment;

    private String mNewsId;
    private String mType;
    private boolean isReadLaterAdd;

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
}
