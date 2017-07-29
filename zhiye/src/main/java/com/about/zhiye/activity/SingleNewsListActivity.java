package com.about.zhiye.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.View;

import com.about.zhiye.fragment.SingleZhihuNewsListFragment;

import java.util.Calendar;
import java.util.Date;

import static com.about.zhiye.util.DateUtil.SIMPLE_DATE_FORMAT;

/**
 * Created by huangyuefeng on 2017/4/11.
 * Contact me : mcxinyu@foxmail.com
 */
public class SingleNewsListActivity extends SingleFragmentActivity
        implements SingleZhihuNewsListFragment.Callbacks {

    private static final String EXTRA_DATE = "date";
    private static final String EXTRA_KEY_WORD = "key_word";

    private Date mDate;
    private String mKeyWord;

    private SingleZhihuNewsListFragment mFragment;

    public static Intent newIntent(Context context, @NonNull Calendar calendar) {
        Intent intent = new Intent(context, SingleNewsListActivity.class);
        intent.putExtra(EXTRA_DATE, calendar.getTime());
        return intent;
    }

    public static Intent newIntent(Context context, @NonNull Date data) {
        Intent intent = new Intent(context, SingleNewsListActivity.class);
        intent.putExtra(EXTRA_DATE, data);
        return intent;
    }

    public static Intent newIntent(Context context, @NonNull String keyWord) {
        Intent intent = new Intent(context, SingleNewsListActivity.class);
        intent.putExtra(EXTRA_KEY_WORD, keyWord);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        mFragment = SingleZhihuNewsListFragment.newInstance(getNextDate(), mKeyWord);
        return mFragment;
    }

    @Override
    protected boolean setHasToolbar() {
        mToolbar.setTitle(mDate != null ? SIMPLE_DATE_FORMAT.format(mDate) : mKeyWord);
        mToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFragment.scrollToTop();
            }
        });
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mDate = (Date) getIntent().getSerializableExtra(EXTRA_DATE);
        mKeyWord = getIntent().getStringExtra(EXTRA_KEY_WORD);

        super.onCreate(savedInstanceState);

        mFragment.setUserVisibleHint(true);
    }

    private String getNextDate() {
        if (mDate == null) {
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(mDate);
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        return SIMPLE_DATE_FORMAT.format(calendar.getTime());
    }

    @Override
    public void setBottomNavigationNotification(String title, int position) {

    }

    @Override
    public void onBackPressed() {
        if (!mFragment.isFirstItemOnTop()) {
            mAppBarLayout.setExpanded(true);
            mFragment.scrollToTop();
        } else {
            super.onBackPressed();
        }
    }
}
