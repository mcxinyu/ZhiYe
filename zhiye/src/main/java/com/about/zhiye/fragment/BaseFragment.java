package com.about.zhiye.fragment;

import android.support.v4.app.Fragment;

/**
 * Created by huangyuefeng on 2017/7/1.
 * Contact me : mcxinyu@foxmail.com
 *
 * 该类是 底部导航栏中切换的 Fragment 的基类
 */
public abstract class BaseFragment extends Fragment {

    // public interface Callbacks {
    //     void onViewScroll(int verticalOffset);
    // }
    //
    // private Callbacks mCallbacks;
    //
    // @Override
    // public void onAttach(Context context) {
    //     super.onAttach(context);
    //     if (context instanceof Callbacks) {
    //         mCallbacks = (Callbacks) context;
    //     } else {
    //         throw new RuntimeException(context.toString()
    //                 + " must implement SearchViewCallbacks");
    //     }
    // }
    //
    // @Override
    // public void onDetach() {
    //     super.onDetach();
    //     mCallbacks = null;
    // }
    //
    // protected void onViewScroll(int verticalOffset) {
    //     mCallbacks.onViewScroll(verticalOffset);
    // }

    public abstract void scrollToTop();

    public abstract int getVerticalOffset();
}
