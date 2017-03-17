package com.about.zhiye.bean.zhihu;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * Created by 跃峰 on 2016/9/19.
 * Contact Me : mcxinyu@foxmail.com
 *
 * body : HTML 格式的新闻
 * image-source : 图片的内容提供方。为了避免被起诉非法使用图片，在显示图片时最好附上其版权信息。
 * title : 新闻标题
 * image : 获得的图片同 最新消息 获得的图片分辨率不同。这里获得的是在文章浏览界面中使用的大图。
 * share_url : 供在线查看内容与分享至 SNS 用的 URL
 * js : 供手机端的 WebView(UIWebView) 使用
 * recommenders : 这篇文章的推荐者
 * ga_prefix : 供 Google Analytics 使用
 * section : 栏目的信息
 * type : 新闻的类型
 * id : 新闻的 id
 * css : 供手机端的 WebView(UIWebView) 使用。可知，知乎日报的文章浏览界面利用 WebView(UIWebView) 实现
 *
 */
public class News implements Serializable{
    @SerializedName("body")
    private String body;
    @SerializedName("imageSource")
    private String imageSource;
    @SerializedName("title")
    private String title;
    @SerializedName("image")
    private String image;
    @SerializedName("shareUrl")
    private String shareUrl;
    @SerializedName("js")
    private String[] js;
    @SerializedName("recommenders")
    private List<Recommender> recommenders;
    @SerializedName("gaPrefix")
    private String gaPrefix;
    @SerializedName("section")
    private Section section;
    @SerializedName("type")
    private String type;
    @SerializedName("id")
    private String id;
    @SerializedName("css")
    private String[] css;

    public String getBody() {
        return body;
    }

    public String getImageSource() {
        return imageSource;
    }

    public String getTitle() {
        return title;
    }

    public String getImage() {
        return image;
    }

    public String getShareUrl() {
        return shareUrl;
    }

    public String getGaPrefix() {
        return gaPrefix;
    }

    public String getType() {
        return type;
    }

    public String getId() {
        return id;
    }

    public String[] getJs() {
        return js;
    }

    public String[] getCss() {
        return css;
    }

    public List<Recommender> getRecommenders() {
        return recommenders;
    }

    public Section getSection() {
        return section;
    }

    public class Recommender implements Serializable {
        private String avatar;

        public String getAvatar() {
            return avatar;
        }

        @Override
        public String toString() {
            return "Recommender{" +
                    "avatar='" + avatar + '\'' +
                    '}';
        }
    }

    /**
     * 栏目的信息
     *
     * thumbnail : 栏目的缩略图
     * id : 该栏目的 id
     * name : 该栏目的名称
     *
     */
    public class Section implements Serializable {
        private String thumbnail;
        private String id;
        private String name;

        public String getThumbnail() {
            return thumbnail;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return "Section{" +
                    "thumbnail='" + thumbnail + '\'' +
                    ", id='" + id + '\'' +
                    ", name='" + name + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "News{" +
                "body='" + body + '\'' +
                ", imageSource='" + imageSource + '\'' +
                ", title='" + title + '\'' +
                ", image='" + image + '\'' +
                ", shareUrl='" + shareUrl + '\'' +
                ", gaPrefix='" + gaPrefix + '\'' +
                ", type='" + type + '\'' +
                ", id='" + id + '\'' +
                ", js=" + Arrays.toString(js) +
                ", css=" + Arrays.toString(css) +
                ", recommenders=" + recommenders +
                ", section=" + section +
                '}';
    }
}
