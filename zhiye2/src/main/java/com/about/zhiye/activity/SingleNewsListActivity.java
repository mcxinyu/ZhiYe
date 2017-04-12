package com.about.zhiye.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.about.zhiye.R;
import com.about.zhiye.fragment.NewsListFragment;

import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.about.zhiye.util.DateUtil.SIMPLE_DATE_FORMAT;

/**
 * Created by huangyuefeng on 2017/4/11.
 * Contact me : mcxinyu@foxmail.com
 */
public class SingleNewsListActivity extends AppCompatActivity {

    private static final String EXTRA_DATE = "date";
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.fragment_container)
    FrameLayout mFragmentContainer;

    private Date mDate;

    public static Intent newIntent(Context context, Date date) {
        Intent intent = new Intent(context, SingleNewsListActivity.class);
        intent.putExtra(EXTRA_DATE, date != null ? date : new Date());
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_news_list);
        ButterKnife.bind(this);

        mDate = (Date) getIntent().getSerializableExtra(EXTRA_DATE);

        initToolbar();
        initView();
    }

    private void initView() {
        FragmentManager fragmentManager = getSupportFragmentManager();

        Fragment fragment = fragmentManager.findFragmentById(R.id.fragment_container);
        if (fragment == null) {
            Fragment newFragment = NewsListFragment.newInstance(getNextDate());
            fragmentManager.beginTransaction()
                    .add(R.id.fragment_container, newFragment)
                    .commit();
        }
    }

    private String getNextDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(mDate);
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        return SIMPLE_DATE_FORMAT.format(calendar.getTime());
    }

    private void initToolbar() {
        mToolbar.setTitle(SIMPLE_DATE_FORMAT.format(mDate));
        setSupportActionBar(mToolbar);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
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
}
