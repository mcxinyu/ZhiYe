package com.about.zhiye.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.MenuItem;

import com.about.zhiye.db.DBLab;
import com.about.zhiye.fragment.ZhihuWebFragment;

public class ZhihuWebActivity extends BaseActivity
        implements ZhihuWebFragment.Callbacks {
    private static final String EXTRA_NEWS_ID = "news_id";

    private String mNewsId;
    private boolean isReadLaterAdd;

    public static Intent newIntent(Context context, String newsId) {
        Intent intent = new Intent(context, ZhihuWebActivity.class);
        intent.putExtra(EXTRA_NEWS_ID, newsId);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        return ZhihuWebFragment.newInstance(mNewsId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mNewsId = getIntent().getStringExtra(EXTRA_NEWS_ID);
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
}
