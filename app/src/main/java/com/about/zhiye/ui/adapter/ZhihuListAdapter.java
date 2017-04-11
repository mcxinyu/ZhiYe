package com.about.zhiye.ui.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.about.zhiye.R;
import com.about.zhiye.bean.zhihu.News;
import com.about.zhiye.bean.zhihu.NewsTimeLine;
import com.about.zhiye.bean.zhihu.Stories;
import com.about.zhiye.bean.zhihu.TopStories;
import com.about.zhiye.ui.widget.TopStoriesViewPager;
import com.about.zhiye.util.ScreenUtil;
import com.bumptech.glide.Glide;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static com.about.zhiye.ui.base.BasePresenter.ZHIHU_API;

/**
 * Created by huangyuefeng on 2017/3/14.
 * Contact me : mcxinyu@foxmail.com
 */
public class ZhihuListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_TOP = -1;
    private static final int TYPE_FOOTER = -2;

    public static final int LOAD_MORE = 0;
    public static final int LOAD_PULL_TO = 1;
    public static final int LOAD_NONE = 2;
    public static final int LOAD_END = 3;

    private Context mContext;
    private NewsTimeLine mNewsTimeLine;
    private News mNews;
    private int mStatus = 1;

    public ZhihuListAdapter(Context context, NewsTimeLine newsTimeLine) {
        mContext = context;
        mNewsTimeLine = newsTimeLine;
    }

    @Override
    public int getItemViewType(int position) {
        if (mNewsTimeLine.getTopStories() != null) {
            if (position == 0) {
                return TYPE_TOP;
            } else if (position + 1 == getItemCount()) {
                return TYPE_FOOTER;
            } else {
                return position;
            }
        } else if (position + 1 == getItemCount()) {
            return TYPE_FOOTER;
        } else {
            return position;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_TOP) {
            View view = View.inflate(parent.getContext(), R.layout.item_zhihu_top_stories, null);
            return new TopStoriesViewHolder(view);
        } else if (viewType == TYPE_FOOTER) {
            View view = View.inflate(parent.getContext(), R.layout.activity_view_footer, null);
            return new FooterViewHolder(view);
        } else {
            View view = View.inflate(parent.getContext(), R.layout.item_zhihu_stories, null);
            return new StoriesViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof FooterViewHolder) {
            FooterViewHolder footerViewHolder = (FooterViewHolder) holder;
            footerViewHolder.bindItem();
        } else if (holder instanceof TopStoriesViewHolder) {
            TopStoriesViewHolder topStoriesViewHolder = (TopStoriesViewHolder) holder;
            topStoriesViewHolder.bindItem(mNewsTimeLine.getTopStories());
        } else if (holder instanceof StoriesViewHolder) {
            StoriesViewHolder storiesViewHolder = (StoriesViewHolder) holder;
            storiesViewHolder.bindItem(mNewsTimeLine.getStories().get(position - 1));
        }
    }

    @Override
    public int getItemCount() {
        return mNewsTimeLine.getStories().size() + 2;
    }

    class FooterViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.progress_bar)
        ProgressBar mProgressBar;
        @BindView(R.id.loading_text_view)
        TextView mLoadingTextView;

        public FooterViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setLayoutParams(
                    new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ScreenUtil.instance(mContext).dip2px(40)));
        }

        public void bindItem() {
            switch (mStatus) {
                case LOAD_MORE:
                    mProgressBar.setVisibility(View.VISIBLE);
                    mLoadingTextView.setText("正在加载...");
                    itemView.setVisibility(View.VISIBLE);
                    break;
                case LOAD_PULL_TO:
                    mProgressBar.setVisibility(View.GONE);
                    mLoadingTextView.setText("上拉加载更多");
                    itemView.setVisibility(View.VISIBLE);
                    break;
                case LOAD_NONE:
                    mProgressBar.setVisibility(View.GONE);
                    mLoadingTextView.setText("无更多了");
                    break;
                case LOAD_END:
                    itemView.setVisibility(View.GONE);
                default:
                    break;
            }
        }
    }

    class TopStoriesViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.top_stories_view_pager)
        TopStoriesViewPager mTopStoriesViewPager;
        @BindView(R.id.top_title_text_view)
        TextView mTopTitleTextView;

        public TopStoriesViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }


        public void bindItem(List<TopStories> top_stories) {
            mTopStoriesViewPager.init(top_stories, mTopTitleTextView, new TopStoriesViewPager.ViewPagerClickListener() {
                @Override
                public void onClick(TopStories item) {
                    // mContext.startActivity();
                }
            });
        }
    }

    class StoriesViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.question_title_text_view)
        TextView mQuestionTitleTextView;
        @BindView(R.id.stories_title_text_view)
        TextView mStoriesTitleTextView;
        @BindView(R.id.stories_image_view)
        ImageView mStoriesImageView;
        @BindView(R.id.stories_card_view)
        CardView mStoriesCardView;

        public StoriesViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            ScreenUtil screenUtil = ScreenUtil.instance(mContext);
            int width = screenUtil.getScreenWidth();
            mStoriesCardView.setLayoutParams(new LinearLayout.LayoutParams(width, LinearLayout.LayoutParams.WRAP_CONTENT));
        }

        public void bindItem(Stories stories) {
            // setQuestionTitle(stories.getId());

            mStoriesTitleTextView.setText(stories.getTitle());
            String[] images = stories.getImages();
            Glide.with(mContext)
                    .load(images[0])
                    .centerCrop()
                    .into(mStoriesImageView);

            mStoriesCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // mContext.startActivity();
                }
            });
        }

        private void setQuestionTitle(String id){
            ZHIHU_API.getDetailNews(id)
                    .observeOn(Schedulers.io())
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<News>() {
                        @Override
                        public void call(News news) {
                            mQuestionTitleTextView.setText(news.getTitle());
                        }
                    });
        }
    }

    public void updateLoadStatus(int status){
        this.mStatus = status;
        notifyDataSetChanged();
    }
}
