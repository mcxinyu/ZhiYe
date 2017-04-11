package com.about.zhiye.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.about.zhiye.R;
import com.squareup.timessquare.CalendarPickerView;

import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.about.zhiye.fragment.ZhihuFragment.ZHIHU_DAILY_BIRTHDAY;

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
        setContentView(R.layout.activity_pick_date);
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
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                calendar.add(Calendar.DAY_OF_YEAR, 1);

                Snackbar.make(mCalendarView, calendar.getTime().toString(), Snackbar.LENGTH_SHORT).show();
                // Intent intent = new Intent(getActivity(), NewsListFragment.newInstance());
                // intent.putExtra(Constants.BundleKeys.DATE,
                //         Constants.Dates.simpleDateFormat.format(calendar.getTime()));
                // startActivity(intent);
            }

            @Override
            public void onDateUnselected(Date date) {

            }
        });
        mCalendarView.setOnInvalidDateSelectedListener(new CalendarPickerView.OnInvalidDateSelectedListener() {
            @Override
            public void onInvalidDateSelected(Date date) {
                if (date.after(new Date())) {
                    Snackbar.make(mCalendarView, getString(R.string.not_coming), Snackbar.LENGTH_SHORT).show();
                } else {
                    Snackbar.make(mCalendarView, getString(R.string.not_born), Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initToolbar() {
        setSupportActionBar(mToolbar);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }
}
