package com.about.zhiye.bean.zhihu;

import java.io.Serializable;
import java.util.List;

/**
 * Created by 跃峰 on 2016/9/19.
 * Contact Me : mcxinyu@foxmail.com
 * 最新消息
 */
public class NewsTimeLine implements Serializable{
    private String date;
    private List<Stories> stories;
    private List<TopStories> top_stories;

    public String getDate() {
        return date;
    }

    public List<Stories> getStories() {
        return stories;
    }

    public List<TopStories> getTop_stories() {
        return top_stories;
    }

    @Override
    public String toString() {
        return "NewsTimeLine{" +
                "date='" + date + '\'' +
                ", stories='" + stories + '\'' +
                ", top_stories='" + top_stories + '\'' +
                "}";
    }
}
