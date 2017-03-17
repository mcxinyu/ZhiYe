package com.about.zhiye.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.about.zhiye.ui.base.BaseFragment;

import java.util.List;

/**
 * Created by huangyuefeng on 2017/3/15.
 * Contact me : mcxinyu@foxmail.com
 */
public class ViewPagerFragmentAdapter extends FragmentPagerAdapter {
    private List<BaseFragment> mFragmentList;
    private String tag;

    public ViewPagerFragmentAdapter(FragmentManager supportFragmentManager, List<BaseFragment> fragmentList, String tag) {
        super(supportFragmentManager);
        mFragmentList = fragmentList;
        this.tag = tag;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        // super.destroyItem(container, position, object);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (tag.equals("main_view_pager")){
            switch (position){
                case 0:
                    return "知乎";
                case 1:
                    return "干货";
                case 2:
                    return "好奇心";
            }
        }
        // return super.getPageTitle(position);
        return null;
    }
}
