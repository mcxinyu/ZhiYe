package com.about.zhiye.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.about.zhiye.R;
import com.about.zhiye.activity.EditorActivity;
import com.about.zhiye.model.Theme;
import com.about.zhiye.util.GlideCircleTransform;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by huangyuefeng on 2017/4/16.
 * Contact me : mcxinyu@foxmail.com
 */
public class ThemeEditorAdapter extends RecyclerView.Adapter<ThemeEditorAdapter.ThemeEditorHolder> {
    private Context mContext;
    private List<Theme.EditorsBean> mEditorsBeanList;

    public ThemeEditorAdapter(Context context, List<Theme.EditorsBean> editorsBeanList) {
        mContext = context;
        mEditorsBeanList = editorsBeanList;
    }

    @Override
    public ThemeEditorHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_theme_editor, parent, false);
        return new ThemeEditorHolder(view);
    }

    @Override
    public void onBindViewHolder(ThemeEditorHolder holder, int position) {
        holder.bindView(mEditorsBeanList.get(position));
    }

    @Override
    public int getItemCount() {
        return null == mEditorsBeanList ? 0 : mEditorsBeanList.size();
    }

    private void setEditors(List<Theme.EditorsBean> list) {
        mEditorsBeanList = list;
    }

    public void updateEditors(List<Theme.EditorsBean> list) {
        setEditors(list);
        notifyDataSetChanged();
    }

    class ThemeEditorHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.linear_layout)
        LinearLayout mLinearLayout;
        @BindView(R.id.image_view)
        ImageView mImageView;
        @BindView(R.id.text_view)
        TextView mTextView;

        public ThemeEditorHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bindView(final Theme.EditorsBean bean) {
            if (!TextUtils.isEmpty(bean.getAvatar())) {
                Glide.with(mContext)
                        .load(bean.getAvatar())
                        .crossFade()
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .centerCrop()
                        .bitmapTransform(new GlideCircleTransform(mContext))
                        .into(mImageView);
            }

            mTextView.setText(bean.getName());

            mLinearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mContext.startActivity(EditorActivity.newIntent(mContext, "" + bean.getId()));
                }
            });
        }
    }
}
