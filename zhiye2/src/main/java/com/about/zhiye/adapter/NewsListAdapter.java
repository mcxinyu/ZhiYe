package com.about.zhiye.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.about.zhiye.R;
import com.about.zhiye.activity.ZhihuWebActivity;
import com.about.zhiye.api.ZhihuHelper;
import com.about.zhiye.db.DBLab;
import com.about.zhiye.model.Story;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by huangyuefeng on 2017/3/18.
 * Contact me : mcxinyu@foxmail.com
 */
public class NewsListAdapter extends RecyclerView.Adapter<NewsListAdapter.CardViewHolder> {
    private Context mContext;
    private List<Story> mStories;

    public NewsListAdapter(Context context, List<Story> stories) {
        mContext = context;
        mStories = stories;
    }

    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_zhihu_news_list, parent, false);

        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CardViewHolder holder, int position) {
        holder.BindView(mStories.get(position));
    }

    @Override
    public int getItemCount() {
        return null == mStories ? 0 : mStories.size();
    }

    private void setStories(List<Story> stories) {
        this.mStories = stories;
    }

    public void updateStories(List<Story> stories) {
        setStories(stories);
        notifyDataSetChanged();
    }

    class CardViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.thumbnail_image)
        ImageView mThumbnailImage;
        @BindView(R.id.question_title)
        TextView mQuestionTitle;
        @BindView(R.id.daily_title)
        TextView mDailyTitle;
        @BindView(R.id.news_list_card_view)
        CardView mNewsListCardView;
        @BindView(R.id.share_image_view)
        ImageView mShareImageView;
        @BindView(R.id.read_later_image_view)
        ImageView mReadLaterImageView;
        @BindView(R.id.browser_image_view)
        ImageView mBrowserImageView;

        public CardViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void BindView(final Story story) {
            if (TextUtils.isEmpty(story.getQuestionTitle())) {
                mQuestionTitle.setText(story.getTitle());
                mDailyTitle.setText(story.getTitle());
            } else {
                mQuestionTitle.setText(story.getQuestionTitle());
                mDailyTitle.setText(story.getTitle());
            }

            if (DBLab.get(mContext).queryReadLaterHave(story.getId())) {
                mReadLaterImageView.setImageResource(R.drawable.ic_action_read_later_selected_black);
            } else {
                mReadLaterImageView.setImageResource(R.drawable.ic_action_read_later_unselected_black);
            }

            if (null != story.getImages() && story.getImages().length > 0) {
                Glide.with(mContext)
                        .load(story.getImages()[0])
                        .crossFade()
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .centerCrop()
                        .into(mThumbnailImage);
            }

            mNewsListCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mContext.startActivity(ZhihuWebActivity.newIntent(mContext, story.getId()));
                }
            });
            mShareImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ZhihuHelper.shareNews(mContext, story.getTitle(), story.getShareUrl());
                }
            });
            mReadLaterImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DBLab dbLab = DBLab.get(mContext);
                    if (dbLab.queryReadLaterHave(story.getId())) {
                        dbLab.deleteReadLaterNews(story.getId());
                        ((ImageView) v).setImageResource(R.drawable.ic_action_read_later_unselected_black);
                    } else {
                        dbLab.insertReadLaterNews(story.getId());
                        ((ImageView) v).setImageResource(R.drawable.ic_action_read_later_selected_black);
                    }
                }
            });
            mBrowserImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ZhihuHelper.shareToBrowser(mContext, story.getShareUrl());
                }
            });
        }
    }
}
