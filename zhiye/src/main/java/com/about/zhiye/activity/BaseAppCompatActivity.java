package com.about.zhiye.activity;

import android.support.v7.app.AppCompatActivity;

/**
 * Created by huangyuefeng on 2017/8/3.
 * Contact me : mcxinyu@foxmail.com
 */
public class BaseAppCompatActivity extends AppCompatActivity {

    @Override
    protected void onResume() {
        super.onResume();
        // if (QueryPreferences.getSettingShakeFeedbackState(this)) {
        //     PgyFeedbackShakeManager.setShakingThreshold(1000);
        //     PgyFeedbackShakeManager.register(this);
        // }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // PgyFeedbackShakeManager.unregister();
    }
}
