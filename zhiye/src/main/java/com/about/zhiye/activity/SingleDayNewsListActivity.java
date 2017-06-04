package com.about.zhiye.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import com.about.zhiye.fragment.SingleDayNewsListFragment;

import java.util.Calendar;
import java.util.Date;

import static com.about.zhiye.util.DateUtil.SIMPLE_DATE_FORMAT;

/**
 * Created by huangyuefeng on 2017/4/11.
 * Contact me : mcxinyu@foxmail.com
 */
public class SingleDayNewsListActivity extends SingleFragmentActivity implements SingleDayNewsListFragment.Callbacks {

    private static final String EXTRA_DATE = "date";

    private Date mDate;

    private SingleDayNewsListFragment mFragment;

    public static Intent newIntent(Context context, Date date) {
        Intent intent = new Intent(context, SingleDayNewsListActivity.class);
        intent.putExtra(EXTRA_DATE, date != null ? date : new Date());
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        mFragment = SingleDayNewsListFragment.newInstance(getNextDate());
        return mFragment;
    }

    @Override
    protected boolean setHasToolbar() {
        mToolbar.setTitle(SIMPLE_DATE_FORMAT.format(mDate));
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

        super.onCreate(savedInstanceState);

        mFragment.setUserVisibleHint(true);
    }

    private String getNextDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(mDate);
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        return SIMPLE_DATE_FORMAT.format(calendar.getTime());
    }

    @Override
    public void setBottomNavigationNotification(String title, int position) {

    }
}
