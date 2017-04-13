package com.about.zhiye.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.about.zhiye.fragment.NewsListFragment;

import java.util.Calendar;
import java.util.Date;

import static com.about.zhiye.util.DateUtil.SIMPLE_DATE_FORMAT;

/**
 * Created by huangyuefeng on 2017/4/11.
 * Contact me : mcxinyu@foxmail.com
 */
public class SingleNewsListActivity extends SingleFragmentActivity {

    private static final String EXTRA_DATE = "date";

    private Date mDate;

    public static Intent newIntent(Context context, Date date) {
        Intent intent = new Intent(context, SingleNewsListActivity.class);
        intent.putExtra(EXTRA_DATE, date != null ? date : new Date());
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        return NewsListFragment.newInstance(getNextDate());
    }

    @Override
    protected boolean setHaveToolbar() {
        mToolbar.setTitle(SIMPLE_DATE_FORMAT.format(mDate));
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mDate = (Date) getIntent().getSerializableExtra(EXTRA_DATE);

        super.onCreate(savedInstanceState);
    }

    private String getNextDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(mDate);
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        return SIMPLE_DATE_FORMAT.format(calendar.getTime());
    }
}
