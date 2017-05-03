package com.about.zhiye.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by huangyuefeng on 2017/4/13.
 * Contact me : mcxinyu@foxmail.com
 * <p>
 * limit : 返回数目之限制（仅为猜测）
 * subscribed : 已订阅条目
 * others : 其他条目
 */
public class Themes implements Serializable {

    @SerializedName("limit")
    private int limit;
    @SerializedName("subscribed")
    private List<?> subscribed;
    @SerializedName("others")
    private List<OthersBean> others;

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public List<?> getSubscribed() {
        return subscribed;
    }

    public void setSubscribed(List<?> subscribed) {
        this.subscribed = subscribed;
    }

    public List<OthersBean> getOthers() {
        return others;
    }

    public void setOthers(List<OthersBean> others) {
        this.others = others;
    }

    /**
     * 其他条目
     * color : 颜色，作用未知
     * thumbnail : 供显示的图片地址
     * description : 主题日报的介绍
     * id : 该主题日报的编号
     * name : 供显示的主题日报名称
     */
    public static class OthersBean implements Serializable {

        @SerializedName("color")
        private int color;
        @SerializedName("thumbnail")
        private String thumbnail;
        @SerializedName("description")
        private String description;
        @SerializedName("id")
        private int id;
        @SerializedName("name")
        private String name;

        public int getColor() {
            return color;
        }

        public void setColor(int color) {
            this.color = color;
        }

        public String getThumbnail() {
            return thumbnail;
        }

        public void setThumbnail(String thumbnail) {
            this.thumbnail = thumbnail;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
