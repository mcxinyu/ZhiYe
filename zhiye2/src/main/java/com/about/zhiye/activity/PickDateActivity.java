package com.about.zhiye.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.about.zhiye.R;
import com.squareup.timessquare.CalendarPickerView;

import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.about.zhiye.util.DateUtil.ZHIHU_DAILY_BIRTHDAY;

/**
 * Created by huangyuefeng on 2017/4/11.
 * Contact me : mcxinyu@foxmail.com
 */
public class PickDateActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.calendar_view)
    CalendarPickerView mCalendarView;

    public static Intent newIntent(Context context) {

        Intent intent = new Intent(context, PickDateActivity.class);
        // intent.putExtra();
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple);
        ButterKnife.bind(this);

        initToolbar();
        initView();
    }

    private void initView() {
        Calendar nextDay = Calendar.getInstance();
        nextDay.add(Calendar.DAY_OF_YEAR, 1);

        mCalendarView.init(ZHIHU_DAILY_BIRTHDAY, nextDay.getTime())
                .inMode(CalendarPickerView.SelectionMode.SINGLE)
                .withSelectedDate(Calendar.getInstance().getTime());

        mCalendarView.setOnDateSelectedListener(new CalendarPickerView.OnDateSelectedListener() {
            @Override
            public void onDateSelected(Date date) {
                startActivity(SingleNewsListActivity.newIntent(PickDateActivity.this, date));
            }

            @Override
            public void onDateUnselected(Date date) {

            }
        });
        mCalendarView.setOnInvalidDateSelectedListener(new CalendarPickerView.OnInvalidDateSelectedListener() {
            @Override
            public void onInvalidDateSelected(Date date) {
                if (date.after(new Date())) {
                    Snackbar.make(mCalendarView, getString(R.string.not_coming), Snackbar.LENGTH_SHORT)
                            .setAction(getString(R.string.ramdom_data), new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    startActivity(SingleNewsListActivity.newIntent(PickDateActivity.this, getRandomDate()));
                                }
                            })
                            .show();
                } else {
                    Snackbar.make(mCalendarView, getString(R.string.not_born), Snackbar.LENGTH_SHORT)
                            .setAction(getString(R.string.ramdom_data), new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    startActivity(SingleNewsListActivity.newIntent(PickDateActivity.this, getRandomDate()));
                                }
                            })
                            .show();
                }
            }
        });
    }

    private Date getRandomDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(ZHIHU_DAILY_BIRTHDAY);
        calendar.add(Calendar.DAY_OF_YEAR, 1 - (int) (Math.random() * getDaysBetween(new Date(), ZHIHU_DAILY_BIRTHDAY)));
        return calendar.getTime();
    }

    public int getDaysBetween(Date date1, Date date2) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date1);
        long time1 = calendar.getTimeInMillis();
        calendar.setTime(date2);
        long time2 = calendar.getTimeInMillis();
        long between_days = (time2 - time1) / (1000 * 3600 * 24);

        return Integer.parseInt(String.valueOf(between_days));
    }

    private void initToolbar() {
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
