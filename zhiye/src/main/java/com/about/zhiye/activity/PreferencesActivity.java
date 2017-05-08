package com.about.zhiye.activity;

import android.Manifest;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.about.zhiye.R;
import com.about.zhiye.fragment.PreferencesFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.qiangxi.checkupdatelibrary.dialog.UpdateDialog.UPDATE_DIALOG_PERMISSION_REQUEST_CODE;

/**
 * Created by huangyuefeng on 2017/4/25.
 * Contact me : mcxinyu@foxmail.com
 */
public class PreferencesActivity extends AppCompatActivity {
    private static String TAG = "PreferencesActivity";

    private PreferencesFragment mFragment;

    public static Intent newIntent(Context context) {

        Intent intent = new Intent(context, PreferencesActivity.class);
        // intent.putExtra();
        return intent;
    }

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.app_bar_layout)
    AppBarLayout mAppBarLayout;
    @BindView(R.id.fragment_container)
    FrameLayout mFragmentContainer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_fragment);
        ButterKnife.bind(this);

        FragmentManager fragmentManager = getFragmentManager();
        mFragment = (PreferencesFragment) fragmentManager.findFragmentById(R.id.fragment_container);
        if (mFragment == null) {
            mFragment = PreferencesFragment.newInstance();
            fragmentManager.beginTransaction()
                    .add(R.id.fragment_container, mFragment)
                    .commit();
        }

        initToolbar();
    }

    private void initToolbar() {
        setSupportActionBar(mToolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        //如果用户同意所请求的权限
        if (permissions[0].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //所以在进行判断时,必须要结合这两个常量进行判断.
            //非强制更新对话框
            if (requestCode == UPDATE_DIALOG_PERMISSION_REQUEST_CODE) {
                //进行下载操作
                mFragment.getUpdateDialog().download();
            }
        } else {
            //用户不同意,提示用户,如下载失败,因为您拒绝了相关权限
            Toast.makeText(this, "程序将无法正常运行", Toast.LENGTH_SHORT).show();
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                Log.e(TAG, "false.请开启读写sd卡权限,不然无法正常工作");
            } else {
                Log.e(TAG, "true.请开启读写sd卡权限,不然无法正常工作");
            }
        }
    }
}
