package com.about.zhiye.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by huangyuefeng on 2017/3/15.
 * Contact me : mcxinyu@foxmail.com
 *
 * comments : 长评论列表，形式为数组（请注意，其长度可能为 0）
 * author : 评论作者
 * content : 评论的内容
 * avatar : 用户头像图片的地址
 * id : 评论者的唯一标识符
 * likes : 评论所获『赞』的数量
 * time : 评论时间
 * reply_to : 所回复的消息
 *
 */
public class Comments {
    @SerializedName("comments")
    private List<CommentsBean> comments;

    public List<CommentsBean> getComments() {
        return comments;
    }

    public void setComments(List<CommentsBean> comments) {
        this.comments = comments;
    }

    public static class CommentsBean {
        /**
         * author : 巨型黑娃儿
         * content : 也不算逻辑问题。其实小时候刚刚听说这个玩意的时候我也奇...
         * avatar : http://pic3.zhimg.com/4131a3385c748c9e2d02ab80e29a0c52_im.jpg
         * time : 1479706360
         * reply_to : {"content":"第二个机灵抖的还是有逻辑问题，不该说忘了，应该说没喝过啊我也不知道","status":0,"id":27275308,"author":"2233155495"}
         * id : 27276057
         * likes : 2
         */

        @SerializedName("author")
        private String author;
        @SerializedName("content")
        private String content;
        @SerializedName("avatar")
        private String avatar;
        @SerializedName("time")
        private int time;
        @SerializedName("reply_to")
        private ReplyToBean replyTo;
        @SerializedName("id")
        private int id;
        @SerializedName("likes")
        private int likes;

        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public int getTime() {
            return time;
        }

        public void setTime(int time) {
            this.time = time;
        }

        public ReplyToBean getReplyTo() {
            return replyTo;
        }

        public void setReplyTo(ReplyToBean replyTo) {
            this.replyTo = replyTo;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getLikes() {
            return likes;
        }

        public void setLikes(int likes) {
            this.likes = likes;
        }

        public static class ReplyToBean {
            /**
             *
             * reply_to : 所回复的消息
             *
             * content : 原消息的内容
             * status : 消息状态，0为正常，非0为已被删除
             * id : 被回复者的唯一标识符
             * author : 被回复者
             * err_msg: 错误消息，仅当status非0时出现
             *
             */

            @SerializedName("content")
            private String content;
            @SerializedName("status")
            private int status;
            @SerializedName("id")
            private int id;
            @SerializedName("author")
            private String author;

            public String getContent() {
                return content;
            }

            public void setContent(String content) {
                this.content = content;
            }

            public int getStatus() {
                return status;
            }

            public void setStatus(int status) {
                this.status = status;
            }

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getAuthor() {
                return author;
            }

            public void setAuthor(String author) {
                this.author = author;
            }
        }
    }
}
