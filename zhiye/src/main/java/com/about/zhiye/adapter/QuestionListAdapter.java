package com.about.zhiye.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.about.zhiye.R;
import com.about.zhiye.model.Question;
import com.about.zhiye.support.Constants;
import com.about.zhiye.util.QueryPreferences;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by huangyuefeng on 2017/6/20.
 * Contact me : mcxinyu@foxmail.com
 */
public class QuestionListAdapter extends RecyclerView.Adapter<QuestionListAdapter.CardViewHolder> {
    private Context mContext;
    private List<Question> mQuestions;

    public QuestionListAdapter(Context context, List<Question> questions) {
        mContext = context;
        mQuestions = questions;
    }

    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_zhihu_question_list, parent, false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CardViewHolder holder, int position) {
        holder.bindView(mQuestions.get(position));
    }

    @Override
    public int getItemCount() {
        return null == mQuestions ? 0 : mQuestions.size();
    }

    private void setNewses(List<Question> questions) {
        this.mQuestions = questions;
    }

    public void updateStories(List<Question> questions) {
        setNewses(questions);
        notifyDataSetChanged();
    }

    class CardViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.news_list_card_view)
        CardView mNewsListCardView;
        @BindView(R.id.daily_title)
        TextView mDailyTitle;

        public CardViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bindView(final Question question) {
            mDailyTitle.setText(question.getTitle());

            mNewsListCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (QueryPreferences.getOpenClientState(mContext)) {
                        openUsingZhihuClient(question.getUrl());
                    } else {
                        openUsingBrowser(question.getUrl());
                    }
                }
            });
        }

        private void openUsingBrowser(String url) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));

            if (mContext.getPackageManager().queryIntentActivities(intent, 0).size() > 0) {
                mContext.startActivity(intent);
            } else {
                Toast.makeText(mContext, mContext.getString(R.string.no_browser), Toast.LENGTH_SHORT).show();
            }
        }

        private void openUsingZhihuClient(String url) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));

            if (isZhihuClientInstalled()) {
                intent.setPackage(Constants.Information.ZHIHU_PACKAGE_ID);
                mContext.startActivity(intent);
            } else {
                Toast.makeText(mContext, mContext.getString(R.string.no_zhihu_client), Toast.LENGTH_SHORT).show();
            }
        }

        private boolean isZhihuClientInstalled() {
            try {
                return mContext.getPackageManager()
                        .getPackageInfo(Constants.Information.ZHIHU_PACKAGE_ID,
                                PackageManager.GET_ACTIVITIES) != null;
            } catch (PackageManager.NameNotFoundException ignored) {
                return false;
            }
        }
    }
}
