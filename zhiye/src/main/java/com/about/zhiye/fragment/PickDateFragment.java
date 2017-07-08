package com.about.zhiye.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.about.zhiye.R;
import com.about.zhiye.activity.SingleNewsListActivity;
import com.squareup.timessquare.CalendarPickerView;

import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.about.zhiye.util.DateUtil.ZHIHU_DAILY_BIRTHDAY;

/**
 * Created by huangyuefeng on 2017/4/13.
 * Contact me : mcxinyu@foxmail.com
 */
public class PickDateFragment extends Fragment {

    @BindView(R.id.calendar_view)
    CalendarPickerView mCalendarView;
    Unbinder unbinder;

    public static PickDateFragment newInstance() {

        Bundle args = new Bundle();

        PickDateFragment fragment = new PickDateFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pick_date, container, false);
        unbinder = ButterKnife.bind(this, view);
        initView();
        return view;
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
                startActivity(SingleNewsListActivity.newIntent(getContext(), date));
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
                            .setAction(getString(R.string.random_data), new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    startActivity(SingleNewsListActivity.newIntent(getContext(), getRandomDate()));
                                }
                            })
                            .show();
                } else {
                    Snackbar.make(mCalendarView, getString(R.string.not_born), Snackbar.LENGTH_SHORT)
                            .setAction(getString(R.string.random_data), new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    startActivity(SingleNewsListActivity.newIntent(getContext(), getRandomDate()));
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
