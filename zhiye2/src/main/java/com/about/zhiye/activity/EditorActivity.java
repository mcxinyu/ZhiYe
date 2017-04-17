package com.about.zhiye.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.about.zhiye.R;
import com.about.zhiye.fragment.EditorFragment;

public class EditorActivity extends SingleFragmentActivity {

    private static final String EXTRA_EDITOR_ID = "editor_id";

    private String mEditorId;

    public static Intent newIntent(Context context, String editorId) {

        Intent intent = new Intent(context, EditorActivity.class);
        intent.putExtra(EXTRA_EDITOR_ID, editorId);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        return EditorFragment.newInstance(mEditorId);
    }

    @Override
    protected boolean setHasToolbar() {
        mToolbar.setTitle(getString(R.string.title_editor));
        return true;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        mEditorId = getIntent().getStringExtra(EXTRA_EDITOR_ID);
        super.onCreate(savedInstanceState);
    }
}
