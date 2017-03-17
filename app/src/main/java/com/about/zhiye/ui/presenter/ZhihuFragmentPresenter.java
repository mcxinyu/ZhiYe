package com.about.zhiye.ui.presenter;

import android.content.Context;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.about.zhiye.bean.zhihu.NewsTimeLine;
import com.about.zhiye.ui.adapter.ZhihuListAdapter;
import com.about.zhiye.ui.base.BasePresenter;
import com.about.zhiye.ui.iview.IZhihuFragmentView;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by huangyuefeng on 2017/3/14.
 * Contact me : mcxinyu@foxmail.com
 */
public class ZhihuFragmentPresenter extends BasePresenter<IZhihuFragmentView> {
    private static final int GET_LATEST_NEWS = 0;
    private static final int GET_BEFORE_NEWS = 1;
    private static final int GET_DETAIL_NEWS = 2;

    private Context mContext;
    private IZhihuFragmentView mFragmentView;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private boolean isLoadedMore = false;
    private String time;
    private ZhihuListAdapter mAdapter;
    private NewsTimeLine mNewsTimeLine;
    private int lastVisibleItem;

    public ZhihuFragmentPresenter(Context context) {
        mContext = context;
    }

    public void getLatestNews() {
        mFragmentView = getVIew();
        if (mFragmentView != null) {
            mRecyclerView = mFragmentView.getRecyclerView();
            mLayoutManager = mFragmentView.getLayoutManager();

            // Observable<NewsTimeLine> latestNews = ZHIHU_API.getLatestNews();
            // latestNews.flatMap(new Func1<NewsTimeLine, Observable<News>>() {
            //     @Override
            //     public Observable<News> call(NewsTimeLine newsTimeLine) {
            //
            //         Observable<News> news = ZHIHU_API.setQuestionTitle(newsTimeLine.getStories().get(0).getId());
            //         return null;
            //     }
            // });

            ZHIHU_API.getLatestNews()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<NewsTimeLine>() {
                        @Override
                        public void call(NewsTimeLine newsTimeLine) {
                            displayZhihuList(newsTimeLine, mContext, mFragmentView, mRecyclerView);
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            loadError(throwable, GET_LATEST_NEWS);
                        }
                    });
        }
    }

    private void getBeforeNews(String time) {
        mFragmentView = getVIew();
        if (mFragmentView != null){
            mRecyclerView = mFragmentView.getRecyclerView();
            mLayoutManager = mFragmentView.getLayoutManager();

            ZHIHU_API.getBeforeNews(time)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<NewsTimeLine>() {
                        @Override
                        public void call(NewsTimeLine newsTimeLine) {
                            displayZhihuList(newsTimeLine, mContext, mFragmentView, mRecyclerView);
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            loadError(throwable, GET_BEFORE_NEWS);
                        }
                    });
        }
    }

    private void displayZhihuList(NewsTimeLine newsTimeLine,
                                  Context context,
                                  IZhihuFragmentView iZhihuFragmentView,
                                  RecyclerView recyclerView) {
        if (isLoadedMore){
            if (time == null){
                mAdapter.updateLoadStatus(ZhihuListAdapter.LOAD_MORE);
                iZhihuFragmentView.setDataRefresh(false);
                return;
            } else {
                mNewsTimeLine.getStories().addAll(newsTimeLine.getStories());
            }
            mAdapter.notifyDataSetChanged();
        } else {
            mNewsTimeLine = newsTimeLine;
            mAdapter = new ZhihuListAdapter(context, mNewsTimeLine);
            recyclerView.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
        }
        iZhihuFragmentView.setDataRefresh(false);
        time = newsTimeLine.getDate();
    }

    private void loadError(Throwable throwable, final int type) {
        throwable.printStackTrace();
        Snackbar.make(mRecyclerView, "网络不见了", Snackbar.LENGTH_LONG)
                .setAction("重试", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switch (type) {
                            case GET_LATEST_NEWS:
                                getLatestNews();
                                break;
                            case GET_BEFORE_NEWS:
                                getLatestNews();
                                break;
                            case GET_DETAIL_NEWS:
                                getLatestNews();
                                break;
                        }
                    }
                }).show();
    }

    public void scrollRecycleView() {
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE){
                    lastVisibleItem = mLayoutManager.findLastVisibleItemPosition();
                    if (mLayoutManager.getItemCount() == 1){
                        mAdapter.updateLoadStatus(ZhihuListAdapter.LOAD_NONE);
                        return;
                    }
                    if (lastVisibleItem + 1 == mLayoutManager.getItemCount()){
                        mAdapter.updateLoadStatus(ZhihuListAdapter.LOAD_PULL_TO);
                        isLoadedMore = true;
                        mAdapter.updateLoadStatus(ZhihuListAdapter.LOAD_MORE);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                getBeforeNews(time);
                            }
                        }, 1000);
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisibleItem = mLayoutManager.findLastVisibleItemPosition();
            }
        });
    }
}
