package com.about.zhiye.ui.iview;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * Created by huangyuefeng on 2017/3/14.
 * Contact me : mcxinyu@foxmail.com
 */
public interface IZhihuFragmentView {
    void setDataRefresh(boolean refresh);
    RecyclerView getRecyclerView();
    LinearLayoutManager getLayoutManager();
}
