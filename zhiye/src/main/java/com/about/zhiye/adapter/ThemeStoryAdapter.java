package com.about.zhiye.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.about.zhiye.R;
import com.about.zhiye.activity.ZhihuWebActivity;
import com.about.zhiye.db.DBLab;
import com.about.zhiye.model.Theme;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by huangyuefeng on 2017/4/16.
 * Contact me : mcxinyu@foxmail.com
 */
public class ThemeStoryAdapter extends RecyclerView.Adapter<ThemeStoryAdapter.ThemeStoryHolder> {
    private Context mContext;
    private List<Theme.StoriesBean> mStoriesBeanList;

    public ThemeStoryAdapter(Context context, List<Theme.StoriesBean> storiesBeanList) {
        mContext = context;
        mStoriesBeanList = storiesBeanList;
    }

    @Override
    public ThemeStoryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_theme_story, parent, false);
        return new ThemeStoryHolder(view);
    }

    @Override
    public void onBindViewHolder(ThemeStoryHolder holder, int position) {
        holder.bindView(mStoriesBeanList.get(position));
    }

    @Override
    public int getItemCount() {
        return null == mStoriesBeanList ? 0 : mStoriesBeanList.size();
    }

    private void setStories(List<Theme.StoriesBean> list) {
        mStoriesBeanList = list;
    }

    public void updateStories(List<Theme.StoriesBean> list) {
        setStories(list);
        notifyDataSetChanged();
    }

    class ThemeStoryHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.thumbnail_image)
        ImageView mThumbnailImage;
        @BindView(R.id.story_title)
        TextView mStoryTitle;
        @BindView(R.id.theme_story_list_card_view)
        CardView mThemeStoryListCardView;

        public ThemeStoryHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bindView(final Theme.StoriesBean storiesBean){
            mThumbnailImage.setVisibility(View.VISIBLE);
            if (storiesBean.getImages() != null){
                Glide.with(mContext)
                        .load(storiesBean.getImages().get(0))
                        .crossFade()
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .centerCrop()
                        .into(mThumbnailImage);
            } else {
                mThumbnailImage.setVisibility(View.GONE);
            }

            mStoryTitle.setTypeface(null, Typeface.BOLD);
            if (DBLab.get(mContext).queryHaveReadExist("" + storiesBean.getId())){
                mStoryTitle.setTypeface(null, Typeface.NORMAL);
            }
            mStoryTitle.setText(storiesBean.getTitle());

            mThemeStoryListCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mStoryTitle.setTypeface(null, Typeface.NORMAL);
                    DBLab.get(mContext).insertHaveReadNews("" + storiesBean.getId());
                    mContext.startActivity(ZhihuWebActivity.newIntent(mContext,
                            "" + storiesBean.getId(),
                            "" + storiesBean.getType()));
                }
            });
        }
    }

}
