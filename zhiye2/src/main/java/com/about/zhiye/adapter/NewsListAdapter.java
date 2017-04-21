package com.about.zhiye.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.about.zhiye.R;
import com.about.zhiye.api.ZhihuHelper;
import com.about.zhiye.db.DBLab;
import com.about.zhiye.model.News;
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
    private List<News> mNewses;
    private Callbacks mCallbacks;

    public NewsListAdapter(Context context, List<News> newses, Callbacks callbacks) {
        mContext = context;
        mNewses = newses;
        mCallbacks = callbacks;
    }

    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_zhihu_news_list, parent, false);

        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CardViewHolder holder, int position) {
        holder.BindView(mNewses.get(position));
    }

    @Override
    public void onBindViewHolder(CardViewHolder holder, int position, List<Object> payloads) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position);
        } else {
            String id = mNewses.get(position).getId();
            if (DBLab.get(mContext).queryReadLaterExist(id)) {
                holder.mReadLaterImageView.setImageResource(R.drawable.ic_action_read_later_selected_black);
            } else {
                holder.mReadLaterImageView.setImageResource(R.drawable.ic_action_read_later_unselected_black);
            }
        }
    }

    @Override
    public int getItemCount() {
        return null == mNewses ? 0 : mNewses.size();
    }

    private void setNewses(List<News> newses) {
        this.mNewses = newses;
    }

    public void updateStories(List<News> newses) {
        setNewses(newses);
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
        @BindView(R.id.multi_picture_text_view)
        TextView mMultiPictureTextView;

        public CardViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void BindView(final News news) {
            if (TextUtils.isEmpty(news.getQuestions().get(0).getTitle())) {
                mQuestionTitle.setText(news.getTitle());
                mDailyTitle.setText(news.getTitle());
            } else {
                mQuestionTitle.setText(news.getQuestions().get(0).getTitle());
                mDailyTitle.setText(news.getTitle());
            }

            mQuestionTitle.setTypeface(null, Typeface.BOLD);
            if (mCallbacks.isReadLaterFragment()) {
                if (DBLab.get(mContext).queryHaveReadExistForReadLater(news.getId())) {
                    mQuestionTitle.setTypeface(null, Typeface.NORMAL);
                }
            } else {
                if (DBLab.get(mContext).queryHaveReadExist(news.getId())) {
                    mQuestionTitle.setTypeface(null, Typeface.NORMAL);
                }
            }

            if (DBLab.get(mContext).queryReadLaterExist(news.getId())) {
                mReadLaterImageView.setImageResource(R.drawable.ic_action_read_later_selected_black);
            } else {
                mReadLaterImageView.setImageResource(R.drawable.ic_action_read_later_unselected_black);
            }

            if (null != news.getMultiPic()) {
                mMultiPictureTextView.setVisibility(View.VISIBLE);
            } else {
                mMultiPictureTextView.setVisibility(View.INVISIBLE);
            }

            mThumbnailImage.setVisibility(View.VISIBLE);
            if (null != news.getThumbnail()) {
                // 性能优化，使用缩略图
                Glide.with(mContext)
                        .load(news.getThumbnail())
                        .crossFade()
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .centerCrop()
                        .into(mThumbnailImage);
            } else if (null != news.getImage()) {
                // 如果没有缩略图，可以使用大图
                Glide.with(mContext)
                        .load(news.getImage())
                        .crossFade()
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .centerCrop()
                        .into(mThumbnailImage);
            } else {
                // 如果连大图也没有，例如主题日报，那么不显示图片
                mThumbnailImage.setVisibility(View.GONE);
            }

            mNewsListCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mQuestionTitle.setTypeface(null, Typeface.NORMAL);
                    mCallbacks.startZhihuWebActivity(news.getId());
                    mCallbacks.hasRead(news.getId());
                }
            });
            mShareImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ZhihuHelper.shareNews(mContext, news.getTitle(), news.getShareUrl());
                }
            });
            mReadLaterImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DBLab dbLab = DBLab.get(mContext);
                    if (dbLab.queryReadLaterExist(news.getId())) {
                        dbLab.deleteReadLaterNews(news.getId());
                        ((ImageView) v).setImageResource(R.drawable.ic_action_read_later_unselected_black);
                        mCallbacks.addReadLater(news.getId(), false);
                    } else {
                        dbLab.insertReadLaterNews(news.getId());
                        ((ImageView) v).setImageResource(R.drawable.ic_action_read_later_selected_black);
                        mCallbacks.addReadLater(news.getId(), true);
                    }
                }
            });
            mBrowserImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ZhihuHelper.shareToBrowser(mContext, news.getShareUrl());
                }
            });
        }
    }

    public interface Callbacks {
        void startZhihuWebActivity(String newsId);

        void hasRead(String newsId);

        boolean isReadLaterFragment();

        void addReadLater(String newsId, boolean added);
    }
}
