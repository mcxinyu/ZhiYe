package com.about.zhiye.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by huangyuefeng on 2017/3/15.
 * Contact me : mcxinyu@foxmail.com
 * <p>
 * 新闻额外信息
 * <p>
 * long_comments : 长评论总数
 * popularity : 点赞总数
 * short_comments : 短评论总数
 * comments : 评论总数
 */
public class StoryExtra implements Serializable {

    @SerializedName("long_comments")
    private int longComments;
    @SerializedName("popularity")
    private int popularity;
    @SerializedName("short_comments")
    private int shortComments;
    @SerializedName("comments")
    private int comments;

    public int getLongComments() {
        return longComments;
    }

    public void setLongComments(int longComments) {
        this.longComments = longComments;
    }

    public int getPopularity() {
        return popularity;
    }

    public void setPopularity(int popularity) {
        this.popularity = popularity;
    }

    public int getShortComments() {
        return shortComments;
    }

    public void setShortComments(int shortComments) {
        this.shortComments = shortComments;
    }

    public int getComments() {
        return comments;
    }

    public void setComments(int comments) {
        this.comments = comments;
    }
}
