package com.about.zhiye.api;

import com.about.zhiye.bean.zhihu.News;
import com.about.zhiye.bean.zhihu.NewsTimeLine;
import com.about.zhiye.bean.zhihu.StartImage;
import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by 跃峰 on 2016/9/18.
 * Contact Me : mcxinyu@foxmail.com
 * 获取知乎资源
 */
public interface ZhihuApi {

    //启动界面图像获取
    @GET("start-image/1080*1920")
    Observable<StartImage> getStartImage();

    //最新消息
    @GET("news/latest")
    Observable<NewsTimeLine> getLatestNews();

    //过往消息
    @GET("news/before/{time}")
    Observable<NewsTimeLine> getBeforeNews(@Path("time") String time);

    //通过 id 获取消息
    @GET("news/{id}")
    Observable<News> getDetailNews(@Path("id") String id);
}
