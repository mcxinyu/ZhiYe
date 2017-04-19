package com.about.zhiye.model;

import java.io.Serializable;

import static com.about.zhiye.support.Constants.Information.ZHIHU_QUESTION_LINK_PREFIX;

/**
 * Created by huangyuefeng on 2017/3/25.
 * Contact me : mcxinyu@foxmail.com
 */
public class Question implements Serializable {
    private String title;
    private String url;

    public Question(String title, String url) {
        this.title = title;
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isValidZhihuQuestion() {
        return url != null && url.startsWith(ZHIHU_QUESTION_LINK_PREFIX);
    }
}
