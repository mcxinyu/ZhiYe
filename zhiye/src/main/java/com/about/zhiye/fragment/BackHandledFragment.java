package com.about.zhiye.fragment;

import android.content.Context;
import android.support.v4.app.Fragment;

/**
 * Created by huangyuefeng on 2017/7/6.
 * Contact me : mcxinyu@foxmail.com
 * <p>
 * 让 Fragment 有机会消费返回按钮点击事件
 */
public abstract class BackHandledFragment extends Fragment {
    protected BackHandledInterface mBackHandledInterface;

    public abstract boolean onBackPressed();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof BackHandledInterface) {
            mBackHandledInterface = (BackHandledInterface) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement BackHandledInterface");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mBackHandledInterface.setSelectedFragment(this);
    }

    public interface BackHandledInterface {
        public abstract void setSelectedFragment(BackHandledFragment selectedFragment);
    }
}
