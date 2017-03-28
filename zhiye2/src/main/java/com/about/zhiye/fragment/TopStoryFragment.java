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
import com.about.zhiye.model.TopStory;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by huangyuefeng on 2017/3/20.
 * Contact me : mcxinyu@foxmail.com
 */
public class TopStoryFragment extends Fragment {
    private static final String ARG_STORY = "top_story";

    @BindView(R.id.image_view)
    ImageView mImageView;
    @BindView(R.id.top_title_text_view)
    TextView mTopTitleTextView;

    private TopStory mTopStory;

    public static TopStoryFragment newInstance(TopStory topStory) {

        Bundle args = new Bundle();
        args.putSerializable(ARG_STORY, topStory);
        TopStoryFragment fragment = new TopStoryFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mTopStory = (TopStory) getArguments().getSerializable(ARG_STORY);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.item_zhihu_top_story, container, false);
        ButterKnife.bind(this, view);

        mTopTitleTextView.setText(mTopStory.getTitle());
        Glide.with(this)
                .load(mTopStory.getImage())
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(mImageView);

        return view;
    }
}
