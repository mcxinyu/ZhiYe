package com.about.zhiye.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by 跃峰 on 2016/9/19.
 * Contact Me : mcxinyu@foxmail.com
 *
 * 启动界面图像获取
 *
 * text : 供显示的图片版权信息
 * img : 图像的 URL
 *
 */
public class StartImage implements Serializable{
    @SerializedName("text")
    private String text;
    @SerializedName("img")
    private String image;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "StartImage{text='"+ text + '\'' +",image='"+ image + '\'' +"}";
    }
}
