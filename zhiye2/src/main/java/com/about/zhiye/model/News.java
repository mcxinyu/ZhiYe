package com.about.zhiye.model;

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

    public void setBody(String body) {
        this.body = body;
    }

    public String getImageSource() {
        return imageSource;
    }

    public void setImageSource(String imageSource) {
        this.imageSource = imageSource;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getShareUrl() {
        return shareUrl;
    }

    public void setShareUrl(String shareUrl) {
        this.shareUrl = shareUrl;
    }

    public String[] getJs() {
        return js;
    }

    public void setJs(String[] js) {
        this.js = js;
    }

    public List<Recommender> getRecommenders() {
        return recommenders;
    }

    public void setRecommenders(List<Recommender> recommenders) {
        this.recommenders = recommenders;
    }

    public String getGaPrefix() {
        return gaPrefix;
    }

    public void setGaPrefix(String gaPrefix) {
        this.gaPrefix = gaPrefix;
    }

    public Section getSection() {
        return section;
    }

    public void setSection(Section section) {
        this.section = section;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String[] getCss() {
        return css;
    }

    public void setCss(String[] css) {
        this.css = css;
    }

    public class Recommender implements Serializable {
        private String avatar;

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
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

        public void setThumbnail(String thumbnail) {
            this.thumbnail = thumbnail;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
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
