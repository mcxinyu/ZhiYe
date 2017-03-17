package com.about.zhiye.ui.widget;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.about.zhiye.MyApp;
import com.about.zhiye.bean.zhihu.TopStories;
import com.about.zhiye.util.ScreenUtil;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by huangyuefeng on 2017/3/14.
 * Contact me : mcxinyu@foxmail.com
 */
public class TopStoriesViewPager extends RelativeLayout {
    private Context mContext;
    private LinearLayout mLinearLayout;
    private ViewPager mViewPager;
    private List<View> mViewList;
    private int currentItem = 0;
    private int oldItem = 0;
    private List<ImageView> mImageViewList;
    private ViewPagerClickListener mClickListener;

    private ScheduledExecutorService mScheduledExecutorService;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            mViewPager.setCurrentItem(currentItem);
        }
    };

    public TopStoriesViewPager(Context context) {
        this(context, null);
    }

    public TopStoriesViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        setView();
    }

    private void setView() {
        mViewPager = new ViewPager(mContext);
        mViewPager.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        mLinearLayout = new LinearLayout(mContext);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        params.setMargins(0, 0, 0, ScreenUtil.instance(mContext).dip2px(10));
        mLinearLayout.setLayoutParams(params);
        mLinearLayout.setGravity(CENTER_HORIZONTAL);

        addView(mViewPager);
        addView(mLinearLayout);
    }

    public void startAutoRun(){
        mScheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        mScheduledExecutorService.scheduleAtFixedRate(new ViewPagerTask(), 5, 5, TimeUnit.SECONDS);
    }

    public void stopAutoRun(){
        if (mScheduledExecutorService != null) {
            mScheduledExecutorService.shutdown();
        }
    }

    public void init(final List<TopStories> items, final TextView textView, final ViewPagerClickListener listener) {
        mClickListener = listener;
        mImageViewList = new ArrayList<>();
        mViewList = new ArrayList<>();

        for (int i = 0; i < items.size(); i++) {
            final TopStories topStories = items.get(i);
            ImageView imageView = new ImageView(mContext);
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            imageView.setLayoutParams(params);
            imageView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != listener){
                        listener.onClick(topStories);
                    }
                }
            });
            int width = ScreenUtil.instance(mContext).getScreenWidth();
            Glide.with(MyApp.mContext)
                    .load(topStories.getImage())
                    .centerCrop()
                    .into(imageView);
            mImageViewList.add(imageView);
        }

        mViewPager.setAdapter(new MyPagerAdapter(mImageViewList));
        textView.setText(items.get(0).getTitle());
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                textView.setText(items.get(position).getTitle());
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    private class ViewPagerTask implements Runnable {
        @Override
        public void run() {
            if (mImageViewList != null) {
                currentItem = (currentItem + 1) % mImageViewList.size();
                mHandler.obtainMessage().sendToTarget();
            }
        }
    }

    private class MyPagerAdapter extends PagerAdapter {

        private List<? extends View> mViews;
        public MyPagerAdapter(List<? extends View> views) {
            mViews = views;
        }

        @Override
        public int getCount() {
            return mViews.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            if ((mViews.size() > 0) && (mViews.get(position % mViews.size()).getParent() != null)){
                ((ViewPager)(mViews.get(position % mViews.size()).getParent()))
                        .removeView(mViews.get(position % mViews.size()));
            }
            container.addView(mViews.get(position % mViews.size()), 0);

            return mViews.get(position % mViews.size());
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(mViews.get(position % mViews.size()));
        }

    }

    public interface ViewPagerClickListener{
        void onClick(TopStories item);
    }
}
