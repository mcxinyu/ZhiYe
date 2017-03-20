package com.about.zhiye.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by 跃峰 on 2016/9/19.
 * Contact Me : mcxinyu@foxmail.com
 *
 * 最新消息
 *
 * date : 日期
 * stories : 当日新闻
 * top_stories : 界面顶部 ViewPager 滚动显示的显示内容（子项格式同上）
 * （请注意区分此处的 image 属性与 stories 中的 images 属性）
 *
 */
public class NewsTimeLine implements Serializable{
    @SerializedName("date")
    private String date;
    @SerializedName("stories")
    private List<Story> stories;
    @SerializedName("top_stories")
    private List<TopStory> topStories;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<Story> getStories() {
        return stories;
    }

    public void setStories(List<Story> stories) {
        this.stories = stories;
    }

    public List<TopStory> getTopStories() {
        return topStories;
    }

    public void setTopStories(List<TopStory> topStories) {
        this.topStories = topStories;
    }

    @Override
    public String toString() {
        return "NewsTimeLine{" +
                "date='" + date + '\'' +
                ", stories='" + stories + '\'' +
                ", topStories='" + topStories + '\'' +
                "}";
    }
}
