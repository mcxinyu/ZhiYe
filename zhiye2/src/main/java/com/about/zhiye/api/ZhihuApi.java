package com.about.zhiye.api;

import com.about.zhiye.model.Comments;
import com.about.zhiye.model.News;
import com.about.zhiye.model.NewsTimeLine;
import com.about.zhiye.model.StartImage;
import com.about.zhiye.model.StoryExtra;

import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by 跃峰 on 2016/9/18.
 * Contact Me : mcxinyu@foxmail.com
 * 获取知乎资源
 */
public interface ZhihuApi {

    /**
     * 启动界面图像获取
     * @return 图片
     */
    @GET("api/7/prefetch-launch-images/1080*1920")
    Observable<StartImage> getStartImage();

    /**
     * 最新消息
     * @return 新闻
     */
    @GET("api/4/news/latest")
    Observable<NewsTimeLine> getLatestNews();

    /**
     * 过往消息，没有 TopStories
     * @param date 日期，例如 20170315 获取的是 20170312 的消息，日期大于等于当天获取的是当天的消息，
     *             （知乎日报的生日为 2013 年 5 月 19 日，若 before 后数字小于 20130520 ，只会接收到空消息。）
     * @return 新闻
     */
    @GET("api/4/news/before/{date}")
    Observable<NewsTimeLine> getBeforeNews(@Path("date") String date);

    /**
     * 通过 NewsTimeLine 的 id 获取消息内容
     * @param id 新闻的ID
     * @return 新闻
     */
    @GET("api/4/news/{id}")
    Observable<News> getDetailNews(@Path("id") String id);

    /**
     * 新闻额外信息
     * @param id 新闻的ID
     * @return 获取对应新闻的额外信息，如评论数量，所获的『赞』的数量。
     */
    @GET("api/4/story-extra/{id}")
    Observable<StoryExtra> getStoryExtra(@Path("id") String id);

    /**
     * 新闻对应长评论查看
     * @param id 新闻的ID
     * @return 评论
     */
    @GET("api/4/story/{id}/long-comments")
    Observable<Comments> getStoryExtraLongComments(@Path("id") String id);

    /**
     * 新闻对应短评论查看
     * @param id 新闻的ID
     * @return 评论
     */
    @GET("api/4/story/{id}/short-comments")
    Observable<Comments> getStoryExtraShortComments(@Path("id") String id);
}
