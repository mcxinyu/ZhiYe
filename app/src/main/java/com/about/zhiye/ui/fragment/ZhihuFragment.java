package com.about.zhiye.ui.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.about.zhiye.R;
import com.about.zhiye.ui.base.BaseFragment;
import com.about.zhiye.ui.iview.IZhihuFragmentView;
import com.about.zhiye.ui.presenter.ZhihuFragmentPresenter;

import butterknife.BindView;

/**
 * Created by huangyuefeng on 2017/3/14.
 * Contact Me : mcxinyu@foxmail.com
 */
public class ZhihuFragment extends BaseFragment<IZhihuFragmentView, ZhihuFragmentPresenter> {

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;

    @Override
    protected void initView(View view) {
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setDataRefresh(true);
        mPresenter.getLatestNews();
        mPresenter.scrollRecycleView();
    }

    @Override
    protected int createViewLayoutRes() {
        return R.layout.fragment_zhihu;
    }

    @Override
    protected ZhihuFragmentPresenter createPresenter() {
        return new ZhihuFragmentPresenter(getContext());
    }

    @Override
    protected void requestDataRefresh() {
        super.requestDataRefresh();
        setDataRefresh(true);
        mPresenter.getLatestNews();
    }

    @Override
    public void setDataRefresh(boolean refresh) {
        setRefresh(refresh);
    }

    @Override
    public RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    @Override
    public LinearLayoutManager getLayoutManager() {
        return mLayoutManager;
    }
}
