package com.about.zhiye.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.about.zhiye.R;
import com.about.zhiye.db.DBLab;
import com.about.zhiye.fragment.ZhihuWebFragment;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ZhihuWebActivity extends AppCompatActivity
        implements ZhihuWebFragment.Callbacks {
    private static final String EXTRA_NEWS_ID = "news_id";

    private String mNewsId;
    private Unbinder unbinder;
    private boolean isReadLaterAdd;

    public static Intent newIntent(Context context, String newsId) {

        Intent intent = new Intent(context, ZhihuWebActivity.class);
        intent.putExtra(EXTRA_NEWS_ID, newsId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zhihu_web);
        unbinder = ButterKnife.bind(this);

        mNewsId = getIntent().getStringExtra(EXTRA_NEWS_ID);
        isReadLaterAdd = DBLab.get(this).queryReadLaterExist(mNewsId);

        FragmentManager fragmentManager = getSupportFragmentManager();

        Fragment fragment = fragmentManager.findFragmentById(R.id.fragment_container);

        if (fragment == null) {
            fragment = ZhihuWebFragment.newInstance(mNewsId);
            fragmentManager.beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    .commit();
        }
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
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @Override
    public void readLaterStatusChange(boolean added) {
        Intent intent = new Intent();
        intent.putExtra("newsId", mNewsId);
        setResult(RESULT_OK, intent);
    }
}
