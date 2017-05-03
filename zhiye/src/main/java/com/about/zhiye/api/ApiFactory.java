package com.about.zhiye.api;

/**
 * Created by 跃峰 on 2016/9/18.
 * Contact Me : mcxinyu@foxmail.com
 * 使用单例模式
 */
public class ApiFactory {

    private static final Object monitor = new Object();
    private static ZhihuApi zhihuApiSingleton = null;

    public static ZhihuApi getZhihuApiSingleton() {
        synchronized (monitor) {
            if (zhihuApiSingleton == null) {
                zhihuApiSingleton = new ApiRetrofit().getZhihuApiService();
            }
            return zhihuApiSingleton;
        }
    }
}
