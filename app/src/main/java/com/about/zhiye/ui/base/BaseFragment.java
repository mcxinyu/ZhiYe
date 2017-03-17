package com.about.zhiye.ui.base;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.about.zhiye.R;
import com.about.zhiye.ui.iview.IZhihuFragmentView;

import butterknife.ButterKnife;

/**
 * Created by huangyuefeng on 2017/3/14.
 * Contact me : mcxinyu@foxmail.com
 */
public abstract class BaseFragment<V, T extends BasePresenter<V>> extends Fragment implements IZhihuFragmentView{
    protected T mPresenter;
    private SwipeRefreshLayout mRefreshLayout;
    private boolean mIsRequestDataRefresh;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = createPresenter();
        mPresenter.attachView((V) this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(createViewLayoutRes(), container, false);
        ButterKnife.bind(this, view);
        initView(view);
        if (isSetRefresh()){
            setupSwipeRefresh(view);
        }
        return view;
    }

    private void setupSwipeRefresh(View view) {
        mRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
        if(mRefreshLayout != null){
            // mRefreshLayout.setColorSchemeResources(R.color.refresh_progress_1,
            //         R.color.refresh_progress_2,R.color.refresh_progress_3);
            mRefreshLayout.setProgressViewOffset(true, 0, (int) TypedValue
                    .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24,getResources().getDisplayMetrics()));
            mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    requestDataRefresh();
                }
            });
        }
    }

    protected void requestDataRefresh() {
        mIsRequestDataRefresh = true;
    }

    private boolean isSetRefresh() {
        return true;
    }

    public void setRefresh(boolean requestDataRefresh) {
        if (mRefreshLayout == null) {
            return;
        }
        if (!requestDataRefresh) {
            mIsRequestDataRefresh = false;
            mRefreshLayout.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (mRefreshLayout != null) {
                        mRefreshLayout.setRefreshing(false);
                    }
                }
            }, 1000);
        } else {
            mRefreshLayout.setRefreshing(true);
        }
    }

    protected abstract void initView(View view);

    @LayoutRes
    protected abstract int createViewLayoutRes();

    protected abstract T createPresenter();
}
