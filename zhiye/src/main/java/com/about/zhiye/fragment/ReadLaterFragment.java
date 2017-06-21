package com.about.zhiye.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.about.zhiye.R;
import com.about.zhiye.db.DBLab;
import com.arlib.floatingsearchview.FloatingSearchView;

/**
 * Created by huangyuefeng on 2017/3/29.
 * Contact me : mcxinyu@foxmail.com
 */
public class ReadLaterFragment extends SearchViewFragment {

    Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 1024:
                    int i = DBLab.get(getActivity())
                            .deleteAllReadLaterNews();
                    Toast.makeText(getActivity(),
                            getResources().getQuantityString(R.plurals.clear_count, i, i),
                            Toast.LENGTH_SHORT)
                            .show();
                    ((SingleZhihuNewsListFragment) mFragment).doRefresh(true);
                    break;
            }
            return false;
        }
    });

    private Fragment mFragment;
    private boolean isVisibleToUser;
    private boolean isPreloadFailure;

    public static ReadLaterFragment newInstance() {

        Bundle args = new Bundle();

        ReadLaterFragment fragment = new ReadLaterFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected Fragment createFragment() {
        return SingleZhihuNewsListFragment.newInstance(null, null);
    }

    @Override
    protected String setSearchViewTitle() {
        return getString(R.string.title_read_later);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mSearchView.inflateOverflowMenu(R.menu.menu_read_later);

        mSearchView.setOnMenuItemClickListener(new FloatingSearchView.OnMenuItemClickListener() {
            @Override
            public void onActionMenuItemSelected(MenuItem item) {
                if (item.getItemId() == R.id.action_clear) {
                    if (DBLab.get(getActivity()).queryAllReadLater().size() > 0) {
                        new AlertDialog.Builder(getActivity(), R.style.DialogStyle)
                                .setTitle(R.string.warning)
                                .setMessage(R.string.confirm_empty)
                                .setCancelable(true)
                                .setOnKeyListener(new DialogInterface.OnKeyListener() {
                                    @Override
                                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                                        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
                                            dialog.dismiss();
                                        }
                                        return false;
                                    }
                                })
                                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        mHandler.sendEmptyMessage(1024);
                                        dialog.dismiss();
                                    }
                                })
                                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .create()
                                .show();
                    } else {
                        Toast.makeText(getActivity(), R.string.no_more_records, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        mFragment = getFragment();

        if (isVisibleToUser && isPreloadFailure) {
            mFragment.setUserVisibleHint(isVisibleToUser);
            isPreloadFailure = false;
        }
    }

    @Override
    public boolean onActivityBackPress() {
        if (!isAppBarLayoutExpanded) {
            mAppBarLayout.setExpanded(true);
            ((SingleZhihuNewsListFragment) mFragment).scrollToTop();
            return true;
        } else if (mSearchView.isSearchBarFocused()) {
            mSearchView.setSearchFocused(false);
            return true;
        } else if (((SingleZhihuNewsListFragment) mFragment).getRecyclerScrollY() != 0) {
            ((SingleZhihuNewsListFragment) mFragment).scrollToTop();
            return true;
        }
        return false;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        this.isVisibleToUser = isVisibleToUser;
        if (isVisibleToUser) {
            if (mFragment == null) {
                // 加载的时候可能 mFragment 还没创建
                isPreloadFailure = true;
            } else if (mFragment instanceof SingleZhihuNewsListFragment) {
                // 每次 ReadLaterFragment 可见都去加载数据
                ((SingleZhihuNewsListFragment) mFragment).doRefresh(true);
            }
        }
    }
}
