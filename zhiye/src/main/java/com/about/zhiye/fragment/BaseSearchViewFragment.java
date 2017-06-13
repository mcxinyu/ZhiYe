package com.about.zhiye.fragment;

import android.content.Context;
import android.support.v4.app.Fragment;

import com.arlib.floatingsearchview.FloatingSearchView;

/**
 * Created by huangyuefeng on 2017/6/4.
 * Contact me : mcxinyu@foxmail.com
 */
public abstract class BaseSearchViewFragment extends Fragment {

    private Callbacks mCallbacks;

    public interface Callbacks {
        void onAttachSearchViewToDrawer(FloatingSearchView searchView);

        void onNestViewScroll(float verticalOffset);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Callbacks) {
            mCallbacks = (Callbacks) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement SearchViewCallbacks");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    protected void onNestViewScroll(float verticalOffset) {
        mCallbacks.onNestViewScroll(verticalOffset);
    }

    ;

    protected void attachSearchViewToActivityDrawer(FloatingSearchView searchView) {
        if (mCallbacks != null) {
            mCallbacks.onAttachSearchViewToDrawer(searchView);
        }
    }

    public abstract boolean onActivityBackPress();
}
