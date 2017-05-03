package com.about.zhiye.db;

/**
 * Created by huangyuefeng on 2017/3/28.
 * Contact me : mcxinyu@foxmail.com
 */
public class DBScheme {
    public static final String DATABASE_NAME = "zhiye.db";
    public static final int DATABASE_VERSION = 3;

    /**
     * 稍后阅读表
     */
    public static final class ReadLaterTable {
        public static final String TABLE_NAME = "read_later";

        public static final class Columns {
            public static final String DATE = "date";
            public static final String NEWS_ID = "news_id";
            public static final String DELETED = "deleted";
            public static final String DELETED_DATE = "deleted_date";
        }
    }

    /**
     * 已读表，记录已读的新闻，包括稍后阅读中的已读时间
     */
    public static final class HaveReadTable {
        public static final String TABLE_NAME = "have_read";

        public static final class Columns {
            public static final String READ_DATE = "read_date";
            public static final String NEWS_ID = "news_id";
            public static final String READ_LATER_READ_DATE = "read_later_read_date";
        }
    }
}
