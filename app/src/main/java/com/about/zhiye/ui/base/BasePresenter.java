package com.about.zhiye.ui.base;

import com.about.zhiye.api.ApiFactory;
import com.about.zhiye.api.ZhihuApi;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

/**
 * Created by huangyuefeng on 2017/3/13.
 * Contact me : mcxinyu@foxmail.com
 */
public abstract class BasePresenter<V> {
    protected Reference<V> mVIewReference;

    public static final ZhihuApi ZHIHU_API = ApiFactory.getZhihuApiSingleton();
    // public static final GankApi GANK_API = ApiFactory.getGankApiSingleton();
    // public static final DailyApi DAILY_API = ApiFactory.getDailyApiSingleton();

    public void attachView(V view){
        mVIewReference = new WeakReference<V>(view);
    }

    public V getVIew() {
        return mVIewReference.get();
    }

    public void detachView(){
        if (mVIewReference != null){
            mVIewReference.clear();
            mVIewReference = null;
        }
    }
}
