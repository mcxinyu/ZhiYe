package com.about.zhiye.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.about.zhiye.R;
import com.about.zhiye.api.ApiRetrofit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by huangyuefeng on 2017/4/17.
 * Contact me : mcxinyu@foxmail.com
 */
public class EditorFragment extends Fragment {
    private static final String ARGS_EDITOR_ID = "editor_id";

    @BindView(R.id.web_view)
    WebView mWebView;
    @BindView(R.id.scroll_view)
    NestedScrollView mScrollView;
    Unbinder unbinder;

    private String mEditorId;

    public static EditorFragment newInstance(String editorId) {

        Bundle args = new Bundle();
        args.putString(ARGS_EDITOR_ID, editorId);
        EditorFragment fragment = new EditorFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mEditorId = getArguments().getString(ARGS_EDITOR_ID);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_editor, container, false);
        unbinder = ButterKnife.bind(this, view);

        initWebView();
        return view;
    }

    private void initWebView() {
        mWebView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        mWebView.loadUrl(ApiRetrofit.ZHIHU_BASE_URL + "api/4/editor/" + mEditorId + "/profile-page/android");
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return super.shouldOverrideUrlLoading(view, url);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
