package com.about.zhiye.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.about.zhiye.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by huangyuefeng on 2017/3/29.
 * Contact me : mcxinyu@foxmail.com
 */
public class ReadLaterFragment extends Fragment {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.fragment_container)
    FrameLayout mFragmentContainer;
    Unbinder unbinder;

    private NewsListFragment mNewsListFragment;
    private boolean isVisibleToUser;
    private boolean isPreloadFailure;

    public static ReadLaterFragment newInstance() {

        Bundle args = new Bundle();

        ReadLaterFragment fragment = new ReadLaterFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_read_later, container, false);
        unbinder = ButterKnife.bind(this, view);

        FragmentManager fragmentManager = getFragmentManager();
        mNewsListFragment = (NewsListFragment) fragmentManager.findFragmentById(R.id.fragment_container);

        if (mNewsListFragment == null) {
            mNewsListFragment = new NewsListFragment();
            fragmentManager.beginTransaction()
                    .add(R.id.fragment_container, mNewsListFragment)
                    .commit();
        }

        if (isVisibleToUser && isPreloadFailure) {
            mNewsListFragment.doRefresh(true);
            isPreloadFailure = false;
        }

        initToolbar();
        return view;
    }

    private void initToolbar() {
        mToolbar.setTitle(getString(R.string.title_read_later));
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        this.isVisibleToUser = isVisibleToUser;
        if (isVisibleToUser) {
            if (mNewsListFragment == null) {
                // 加载的时候可能 mNewsListFragment 还没创建
                isPreloadFailure = true;
            } else {
                // 每次 ReadLaterFragment 可见都去加载数据
                mNewsListFragment.doRefresh(true);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
