package com.about.zhiye.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.about.zhiye.R;
import com.about.zhiye.model.News;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by huangyuefeng on 2017/3/20.
 * Contact me : mcxinyu@foxmail.com
 */
public class TopNewsFragment extends Fragment {
    private static final String ARG_NEWS = "top_news";

    @BindView(R.id.image_view)
    ImageView mImageView;
    @BindView(R.id.top_title_text_view)
    TextView mTopTitleTextView;
    private Unbinder unbinder;

    private News mNews;

    public static TopNewsFragment newInstance(News news) {

        Bundle args = new Bundle();
        args.putSerializable(ARG_NEWS, news);
        TopNewsFragment fragment = new TopNewsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mNews = (News) getArguments().getSerializable(ARG_NEWS);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.item_zhihu_top_story, container, false);
        unbinder = ButterKnife.bind(this, view);

        mTopTitleTextView.setText(mNews.getTitle());
        Glide.with(this)
                .load(mNews.getImage())
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(mImageView);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
