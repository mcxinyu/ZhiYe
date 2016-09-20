package com.about.zhiye.bean.zhihu;

import java.io.Serializable;

/**
 * Created by 跃峰 on 2016/9/19.
 * Contact Me : mcxinyu@foxmail.com
 * 启动界面图像获取
 */
public class StartImage implements Serializable{
    //图片出处
    private String text;
    //图片地址
    private String img;

    public String getText() {
        return text;
    }

    public String getImg() {
        return img;
    }

    @Override
    public String toString() {
        return "StartImage{text='"+ text + '\'' +",img='"+ img + '\'' +"}";
    }
}
