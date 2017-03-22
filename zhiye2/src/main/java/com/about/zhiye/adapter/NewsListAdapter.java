package com.about.zhiye.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.about.zhiye.R;
import com.about.zhiye.activity.ZhihuWebActivity;
import com.about.zhiye.model.Story;
import com.bumptech.glide.Glide;

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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_news_list, parent, false);

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

    class CardViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.thumbnail_image)
        ImageView mThumbnailImage;
        @BindView(R.id.question_title)
        TextView mQuestionTitle;
        @BindView(R.id.daily_title)
        TextView mDailyTitle;
        @BindView(R.id.news_list_card_view)
        CardView mNewsListCardView;

        public CardViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void BindView(final Story story){
            mNewsListCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mContext.startActivity(ZhihuWebActivity.newIntent(mContext, story.getId()));
                }
            });

            if (TextUtils.isEmpty(story.getQuestionTitle())){
                mQuestionTitle.setText(story.getTitle());
                mDailyTitle.setText(story.getTitle());
            } else {
                mQuestionTitle.setText(story.getQuestionTitle());
                mDailyTitle.setText(story.getTitle());
            }

            if (null != story.getImages() && story.getImages().length > 0) {
                Glide.with(mContext)
                        .load(story.getImages()[0]).asBitmap().centerCrop()
                        .into(mThumbnailImage);
            }
        }


        private void openUsingBrowser(String url) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));

            if (mContext.getPackageManager().queryIntentActivities(browserIntent, 0).size() > 0) {
                mContext.startActivity(browserIntent);
            } else {
                Toast.makeText(mContext, mContext.getString(R.string.no_browser), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
