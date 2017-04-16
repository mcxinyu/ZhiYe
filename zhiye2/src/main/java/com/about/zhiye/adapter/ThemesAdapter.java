package com.about.zhiye.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.about.zhiye.R;
import com.about.zhiye.activity.ThemeActivity;
import com.about.zhiye.model.Themes;
import com.about.zhiye.util.StateUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by huangyuefeng on 2017/4/13.
 * Contact me : mcxinyu@foxmail.com
 */
public class ThemesAdapter extends RecyclerView.Adapter<ThemesAdapter.ThemeViewHolder> {
    private Context mContext;
    private List<Themes.OthersBean> mList;

    public ThemesAdapter(Context context, List<Themes.OthersBean> list) {
        mContext = context;
        mList = list;
    }

    @Override
    public ThemesAdapter.ThemeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(mContext).inflate(R.layout.item_zhihu_theme, parent, false);
        return new ThemeViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(ThemesAdapter.ThemeViewHolder holder, int position) {
        holder.BindView(mList.get(position));
    }

    @Override
    public int getItemCount() {
        return null == mList ? 0 : mList.size();
    }

    private void setNewses(List<Themes.OthersBean> list) {
        this.mList = list;
    }

    public void updateStories(List<Themes.OthersBean> list) {
        setNewses(list);
        notifyDataSetChanged();
    }

    class ThemeViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.thumbnail_view)
        ImageView mThumbnailView;
        @BindView(R.id.name_text_view)
        TextView mNameTextView;
        @BindView(R.id.description_text_view)
        TextView mDescriptionTextView;
        @BindView(R.id.theme_layout)
        RelativeLayout mThemeLayout;

        public ThemeViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void BindView(final Themes.OthersBean othersBean) {
            int screenWidth = StateUtils.getScreenWidth(mContext);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams((screenWidth / 2 - 16), (screenWidth / 2 - 16));
            mThemeLayout.setLayoutParams(params);

            Glide.with(mContext)
                    .load(othersBean.getThumbnail())
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .centerCrop()
                    .into(mThumbnailView);

            mNameTextView.setText(othersBean.getName());
            mDescriptionTextView.setText(othersBean.getDescription());

            mThemeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mContext.startActivity(ThemeActivity.newIntent(mContext, othersBean.getName(), othersBean.getId()));
                }
            });
        }
    }
}
