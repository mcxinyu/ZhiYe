package com.about.zhiye;

import android.view.View;

/**
 * Created by huangyuefeng on 2017/6/30.
 * Contact me : mcxinyu@foxmail.com
 */
public interface BaseView<T> {
    void setPresenter(T presenter);

    void initView(View view);
}
