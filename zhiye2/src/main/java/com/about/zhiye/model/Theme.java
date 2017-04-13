package com.about.zhiye.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by huangyuefeng on 2017/4/13.
 * Contact me : mcxinyu@foxmail.com
 *
 * description : 该主题日报的介绍
 * background : 该主题日报的背景图片（大图）
 * color : 颜色，作用未知
 * name : 该主题日报的名称
 * image : 背景图片的小图版本
 * image_source : 图像的版权信息
 * stories : 该主题日报中的文章列表
 * editors : 该主题日报的编辑（『用户推荐日报』中此项的指是一个空数组，在 App 中的主编栏显示为『许多人』，点击后访问该主题日报的介绍页面，请留意）
 *
 */
public class Theme {

    @SerializedName("description")
    private String description;
    @SerializedName("background")
    private String background;
    @SerializedName("color")
    private int color;
    @SerializedName("name")
    private String name;
    @SerializedName("image")
    private String image;
    @SerializedName("image_source")
    private String imageSource;
    @SerializedName("stories")
    private List<StoriesBean> stories;
    @SerializedName("editors")
    private List<EditorsBean> editors;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getImageSource() {
        return imageSource;
    }

    public void setImageSource(String imageSource) {
        this.imageSource = imageSource;
    }

    public List<StoriesBean> getStories() {
        return stories;
    }

    public void setStories(List<StoriesBean> stories) {
        this.stories = stories;
    }

    public List<EditorsBean> getEditors() {
        return editors;
    }

    public void setEditors(List<EditorsBean> editors) {
        this.editors = editors;
    }

    /**
     * 该主题日报中的文章列表
     * type : 类型，作用未知
     * id : 文章 id
     * title : 消息的标题
     * multiPic : 多图片，建议 wifi 下观看
     * images : 图像地址（其类型为数组。请留意在代码中处理无该属性与数组长度为 0 的情况）
     */
    public static class StoriesBean {

        @SerializedName("type")
        private int type;
        @SerializedName("id")
        private int id;
        @SerializedName("title")
        private String title;
        @SerializedName("multiPic")
        private boolean multiPic;
        @SerializedName("images")
        private List<String> images;

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public boolean isMultiPic() {
            return multiPic;
        }

        public void setMultiPic(boolean multiPic) {
            this.multiPic = multiPic;
        }

        public List<String> getImages() {
            return images;
        }

        public void setImages(List<String> images) {
            this.images = images;
        }
    }

    /**
     * 该主题日报的编辑（『用户推荐日报』中此项的指是一个空数组，在 App 中的主编栏显示为『许多人』，点击后访问该主题日报的介绍页面，请留意）
     * url : 主编的知乎用户主页
     * bio : 主编的个人简介
     * id : 数据库中的唯一表示符
     * avatar : 主编的头像
     * name : 主编的姓名
     */
    public static class EditorsBean {

        @SerializedName("url")
        private String url;
        @SerializedName("bio")
        private String bio;
        @SerializedName("id")
        private int id;
        @SerializedName("avatar")
        private String avatar;
        @SerializedName("name")
        private String name;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getBio() {
            return bio;
        }

        public void setBio(String bio) {
            this.bio = bio;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
