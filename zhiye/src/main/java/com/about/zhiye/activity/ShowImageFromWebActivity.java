package com.about.zhiye.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.about.zhiye.R;
import com.about.zhiye.api.ZhihuHelper;
import com.bm.library.PhotoView;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.about.zhiye.api.ZhihuHelper.ZHIHU_HELPER_PERMISSION_REQUEST_CODE;

/**
 * Created by huangyuefeng on 2017/7/3.
 * Contact me : mcxinyu@foxmail.com
 */
public class ShowImageFromWebActivity extends AppCompatActivity {
    private static final String TAG = "ShowImageFromWeb";
    private static final String EXTRA_IMAGE_URL_ARRAY = "extra_image_url_array";
    private static final String EXTRA_IMAGE_URL = "extra_image_url";

    @BindView(R.id.image_view_pager)
    ViewPager mImageViewPager;
    @BindView(R.id.image_index_text_view)
    TextView mImageIndexTextView;
    @BindView(R.id.share_button)
    Button mShareButton;
    @BindView(R.id.save_button)
    Button mSaveButton;

    private ImageBrowserAdapter mAdapter;
    private List<String> mImageUrls;
    private String mUrl;
    private int currentIndex;

    public static Intent newIntent(Context context, ArrayList<String> imageUrls, String imageUrl) {

        Intent intent = new Intent(context, ShowImageFromWebActivity.class);
        intent.putStringArrayListExtra(EXTRA_IMAGE_URL_ARRAY, imageUrls);
        intent.putExtra(EXTRA_IMAGE_URL, imageUrl);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        mImageUrls = getIntent().getStringArrayListExtra(EXTRA_IMAGE_URL_ARRAY);
        mUrl = getIntent().getStringExtra(EXTRA_IMAGE_URL);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_image_from_web);
        ButterKnife.bind(this);
        initListener();
        initData();
    }

    private void initData() {
        currentIndex = mImageUrls.indexOf(mUrl);
        final int size = mImageUrls.size();
        mAdapter = new ImageBrowserAdapter(this, mImageUrls);
        mImageViewPager.setPageMargin((int) (getResources().getDisplayMetrics().density * 15));
        mImageViewPager.setAdapter(mAdapter);

        if (size > 1) {
            mImageIndexTextView.setVisibility(View.VISIBLE);
            mImageIndexTextView.setText((currentIndex + 1) + "/" + size);
        } else {
            mImageIndexTextView.setVisibility(View.GONE);
        }

        mImageViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                currentIndex = position;
                int index = position % size;
                mImageIndexTextView.setText((index + 1) + "/" + size);
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mImageIndexTextView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showImageOptionDialog(mImageUrls.get(currentIndex));
                return false;
            }
        });

        mImageViewPager.setCurrentItem(currentIndex);
    }

    private void initListener() {
        mShareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 2017/7/3
                Toast.makeText(ShowImageFromWebActivity.this, "分享", Toast.LENGTH_SHORT).show();
            }
        });
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadImage(mImageUrls.get(currentIndex));
            }
        });
    }

    private void showImageOptionDialog(final String url) {
        String[] items = new String[]{getString(R.string.save_picture), getString(R.string.share_picture)};
        new AlertDialog.Builder(this)
                .setCancelable(true)
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                downloadImage(url);
                                break;
                            case 1:
                                // TODO: 2017/7/3
                                Toast.makeText(ShowImageFromWebActivity.this,
                                        "分享", Toast.LENGTH_SHORT)
                                        .show();
                                break;
                        }
                        dialog.dismiss();
                    }
                })
                .show();
    }

    /**
     * 开始下载图片
     */
    private void downloadImage(String url) {
        ZhihuHelper.downloadZhihuImageToAlbum(this, url);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        //如果用户同意所请求的权限
        if (permissions[0].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //所以在进行判断时,必须要结合这两个常量进行判断.
            if (requestCode == ZHIHU_HELPER_PERMISSION_REQUEST_CODE) {
                //进行下载操作
                downloadImage(mImageUrls.get(currentIndex));
            }
        } else {
            //用户不同意,提示用户,如下载失败,因为您拒绝了相关权限
            Toast.makeText(this, "无权限保存图片", Toast.LENGTH_SHORT).show();
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                Log.e(TAG, "false.请开启读写sd卡权限,不然无法正常工作");
            } else {
                Log.e(TAG, "true.请开启读写sd卡权限,不然无法正常工作");
            }
        }
    }

    class ImageBrowserAdapter extends PagerAdapter {
        private Context mContext;
        private List<String> imageUrls;

        public ImageBrowserAdapter(Context context, List<String> imageUrls) {
            mContext = context;
            this.imageUrls = imageUrls;
        }

        @Override
        public int getCount() {
            return imageUrls.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = View.inflate(mContext, R.layout.item_image_browser, null);

            PhotoView photoView = (PhotoView) view.findViewById(R.id.show_image_photo_view);
            photoView.enable();
            photoView.setScaleType(ImageView.ScaleType.FIT_CENTER);

            Glide.with(mContext)
                    .load(imageUrls.get(position))
                    .into(photoView);

            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }
}
