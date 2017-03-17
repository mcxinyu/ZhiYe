package com.about.zhiye.bean.zhihu;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Created by 跃峰 on 2016/9/19.
 * Contact Me : mcxinyu@foxmail.com
 *
 * title : 新闻标题
 * images : 图像地址（官方 API 使用数组形式。目前暂未有使用多张图片的情形出现，曾见无 images 属性的情况，请在使用中注意 ）
 * ga_prefix : 供 Google Analytics 使用
 * type : 作用未知
 * id : url 与 share_url 中最后的数字（应为内容的 id）
 * multipic : 消息是否包含多张图片（仅出现在包含多图的新闻中）
 *
 */
public class Stories implements Serializable{
    @SerializedName("id")
    private String id;
    @SerializedName("title")
    private String title;
    @SerializedName("ga_prefix")
    private String gaPrefix;
    @SerializedName("multipic")
    private String multiPic;
    @SerializedName("type")
    private String type;
    @SerializedName("images")
    private String[] images;

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getGaPrefix() {
        return gaPrefix;
    }

    public String getMultiPic() {
        return multiPic;
    }

    public String getType() {
        return type;
    }

    public String[] getImages() {
        return images;
    }

    @Override
    public String toString() {
        return "Stories{" +
                "gaPrefix='" + gaPrefix + '\'' +
                ", id='" + id + '\'' +
                ", multiPic='" + multiPic + '\'' +
                ", title='" + title + '\'' +
                ", type='" + type + '\'' +
                ", images=" + Arrays.toString(images) +
                '}';
    }
}
