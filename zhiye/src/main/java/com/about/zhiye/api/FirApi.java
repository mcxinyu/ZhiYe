package com.about.zhiye.api;

import com.about.zhiye.model.VersionInfoFir;

import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by 跃峰 on 2017/5/6.
 * Contact Me : mcxinyu@foxmail.com
 */
public interface FirApi {

    /**
     * 获取最新版本信息
     *
     * @param appId    应用 ID
     * @param apiToken
     * @return 版本信息
     */
    @GET("apps/latest/{app_id}")
    Observable<VersionInfoFir> getLatestVersionInfo(@Path("app_id") String appId,
                                                    @Query("api_token") String apiToken);

}
