package com.about.zhiye.api;

import android.util.Log;

import com.about.zhiye.ZhiYeApp;
import com.about.zhiye.util.StateUtils;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by 跃峰 on 2016/9/18.
 * Contact Me : mcxinyu@foxmail.com
 */
public class ApiRetrofit {
    private static final String TAG = "ApiRetrofit";

    public ZhihuApi ZhihuApiService;

    public static final String ZHIHU_BASE_URL = "http://news-at.zhihu.com/";
    public static final String GANK_BASE_URL = "http://gank.io/api/";
    public static final String DAILY_BASE_URL = "http://app3.qdaily.com/app3/";

    public ZhihuApi getZhihuApiService() {
        return ZhihuApiService;
    }

    public ApiRetrofit() {
        // 缓存 url
        File httpCacheDirectory = new File(ZhiYeApp.getInstance().getCacheDir(), "cacheData");
        int cacheSize = 10 * 1024 * 1024; // 10 MiB
        Cache cache = new Cache(httpCacheDirectory, cacheSize);

        OkHttpClient client = new OkHttpClient.Builder()
                .retryOnConnectionFailure(true)
                .connectTimeout(15, TimeUnit.SECONDS)
                .addInterceptor(REWRITE_CACHE_CONTROL_INTERCEPTOR)
                .cache(cache)
                .build();

        Retrofit retrofitZhihu = new Retrofit.Builder()
                .baseUrl(ZHIHU_BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        Retrofit retrofitGank = new Retrofit.Builder()
                .baseUrl(GANK_BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        Retrofit retrofitDaily= new Retrofit.Builder()
                .baseUrl(DAILY_BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        ZhihuApiService = retrofitZhihu.create(ZhihuApi.class);
    }

    // 缓存
    private Interceptor REWRITE_CACHE_CONTROL_INTERCEPTOR = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {

            CacheControl.Builder cacheBuilder = new CacheControl.Builder();
            cacheBuilder.maxAge(0, TimeUnit.SECONDS);
            cacheBuilder.maxStale(7, TimeUnit.DAYS);
            CacheControl cacheControl = cacheBuilder.build();

            Request request = chain.request();
            if (!StateUtils.isNetworkAvailable(ZhiYeApp.getInstance())) {
                // 网络不可用时，读取缓存
                request = request.newBuilder()
                        .cacheControl(cacheControl)
                        .build();
                Log.d(TAG, "Cache Interceptor No Network");
            }

            Response originalResponse = chain.proceed(request);
            if (StateUtils.isNetworkAvailable(ZhiYeApp.getInstance())) {
                int maxAge = 0; // 这里设置为 0 就是说不进行缓存，我们也可以设置缓存时间
                return originalResponse.newBuilder()
                        .removeHeader("Pragma")
                        .removeHeader("Cache-Control")
                        .header("Cache-Control", "public ,max-age=" + maxAge)
                        .build();
            } else {
                int maxStale = 60 * 60 * 24 * 7; // tolerate 1-weeks stale
                return originalResponse.newBuilder()
                        .removeHeader("Pragma")
                        .removeHeader("Cache-Control")
                        .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                        .build();
            }
        }
    };
}
