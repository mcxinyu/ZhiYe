package com.about.zhiye.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.about.zhiye.db.DBScheme.HaveReadTable;
import com.about.zhiye.db.DBScheme.ReadLaterTable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by huangyuefeng on 2017/3/28.
 * Contact me : mcxinyu@foxmail.com
 */
public class DBLab {
    private static DBLab dbLab;
    private SQLiteDatabase mDatabase;
    private DBHelper mDBHelper;

    public static DBLab get(Context context) {
        if (dbLab == null) {
            dbLab = new DBLab(context);
        }
        return dbLab;
    }

    private DBLab(Context context) {
        mDBHelper = new DBHelper(context);
        open();
    }

    private void open() {
        mDatabase = mDBHelper.getWritableDatabase();
    }

    ////////////////////
    // Read Later Table
    ////////////////////
    public void insertReadLaterNews(String newsId) {
        try (Cursor cursor = queryReadLater(newsId)) {
            if (cursor.getCount() == 0) {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd HH:mm:ss", Locale.PRC);
                String currentDate = formatter.format(new Date(System.currentTimeMillis()));

                ContentValues values = new ContentValues();
                values.put(ReadLaterTable.Columns.DATE, currentDate);
                values.put(ReadLaterTable.Columns.NEWS_ID, newsId);
                values.put(ReadLaterTable.Columns.DELETED, false);
                values.put(ReadLaterTable.Columns.DELETED_DATE, "");

                mDatabase.insert(ReadLaterTable.TABLE_NAME, null, values);
            } else {
                updateReadLaterNews(newsId, false);
            }
        }
    }

    public void deleteReadLaterNews(String newsId) {
        updateReadLaterNews(newsId, true);
    }

    /**
     * 包括再次添加为稍后阅读
     *
     * @param newsId
     * @param delete 删除实际只是标记，并非真正删除，所有需要判断是不是又一次添加为稍后阅读
     */
    private void updateReadLaterNews(String newsId, boolean delete) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd HH:mm:ss", Locale.PRC);
        String currentDate = formatter.format(new Date(System.currentTimeMillis()));

        ContentValues values = new ContentValues();
        values.put(ReadLaterTable.Columns.DELETED, delete);
        if (delete) {
            // 要删除就要更新 删除时间，保留历史时间
            values.put(ReadLaterTable.Columns.DELETED_DATE, currentDate);
        } else {
            // 不删除说明是重新添加的，要更新 添加时间
            values.put(ReadLaterTable.Columns.DATE, currentDate);
            values.put(ReadLaterTable.Columns.DELETED_DATE, "");
        }

        mDatabase.update(ReadLaterTable.TABLE_NAME, values, ReadLaterTable.Columns.NEWS_ID + "=?", new String[]{newsId});
    }

    /**
     * 查询列出所有稍后阅读新闻，不包括已删除的
     *
     * @return
     */
    public List<String> queryAllReadLater() {
        try (Cursor cursor = mDatabase.query(ReadLaterTable.TABLE_NAME, null, null, null, null, null,
                ReadLaterTable.Columns.DATE + " DESC")) {
            if (cursor.getCount() == 0) {
                return new ArrayList<>();
            }

            List<String> list = new ArrayList<>();
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                if (cursor.getInt(cursor.getColumnIndex(ReadLaterTable.Columns.DELETED)) == 0) {
                    list.add(cursor.getString(cursor.getColumnIndex(ReadLaterTable.Columns.NEWS_ID)));
                }
                cursor.moveToNext();
            }
            return list;
        }
    }

    private Cursor queryReadLater(String newsId) {
        return mDatabase.query(ReadLaterTable.TABLE_NAME, null,
                ReadLaterTable.Columns.NEWS_ID + "=?",
                new String[]{newsId},
                null, null, null);
    }

    public boolean queryReadLaterExist(String newsId) {
        try (Cursor cursor = queryReadLater(newsId)) {
            if (cursor.getCount() == 0) {
                return false;
            }

            cursor.moveToFirst();
            return cursor.getInt(cursor.getColumnIndex(ReadLaterTable.Columns.DELETED)) == 0;
        }
    }

    ////////////////////
    // Have Read Table
    ////////////////////
    public void insertHaveReadNews(String newsId) {
        if (queryHaveReadExist(newsId)) {
            updateHaveReadNews(newsId, false);
        } else {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd HH:mm:ss", Locale.PRC);
            String currentDate = formatter.format(new Date(System.currentTimeMillis()));

            ContentValues values = new ContentValues();
            values.put(HaveReadTable.Columns.NEWS_ID, newsId);
            values.put(HaveReadTable.Columns.READ_DATE, currentDate);

            mDatabase.insert(HaveReadTable.TABLE_NAME, null, values);
        }
    }

    public void insertHaveReadNewsForReadLater(String newsId) {
        if (queryHaveReadExist(newsId)) {
            updateHaveReadNews(newsId, true);
        } else {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd HH:mm:ss", Locale.PRC);
            String currentDate = formatter.format(new Date(System.currentTimeMillis()));

            ContentValues values = new ContentValues();
            values.put(HaveReadTable.Columns.NEWS_ID, newsId);
            values.put(HaveReadTable.Columns.READ_DATE, currentDate);
            values.put(HaveReadTable.Columns.READ_LATER_READ_DATE, currentDate);

            mDatabase.insert(HaveReadTable.TABLE_NAME, null, values);
        }
    }

    private void updateHaveReadNews(String newsId, boolean isReadLater) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd HH:mm:ss", Locale.PRC);
        String currentDate = formatter.format(new Date(System.currentTimeMillis()));

        ContentValues values = new ContentValues();
        values.put(HaveReadTable.Columns.READ_DATE, currentDate);
        if (isReadLater) {
            values.put(HaveReadTable.Columns.READ_LATER_READ_DATE, currentDate);
        }

        mDatabase.update(HaveReadTable.TABLE_NAME, values, HaveReadTable.Columns.NEWS_ID + "=?", new String[]{newsId});
    }

    public void deleteHaveReadNews(String newsId) {
        mDatabase.delete(HaveReadTable.TABLE_NAME, HaveReadTable.Columns.NEWS_ID + "=?", new String[]{newsId});
    }

    /**
     * 查询列出已读表中的所有新闻
     *
     * @return
     */
    public List<String> queryAllHaveRead() {
        try (Cursor cursor = mDatabase.query(HaveReadTable.TABLE_NAME, null, null, null, null, null, null)) {
            if (cursor.getCount() == 0) {
                return new ArrayList<>();
            }

            List<String> list = new ArrayList<>();
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                list.add(cursor.getString(cursor.getColumnIndex(HaveReadTable.Columns.NEWS_ID)));
                cursor.moveToNext();
            }
            return list;
        }
    }

    /**
     * 查询列出已读表中的某条新闻
     *
     * @param newsId
     * @return
     */
    private Cursor queryHaveRead(String newsId) {
        return mDatabase.query(HaveReadTable.TABLE_NAME, null,
                HaveReadTable.Columns.NEWS_ID + "=?",
                new String[]{newsId},
                null, null, null);
    }

    /**
     * 查询一条新闻是否已读
     *
     * @param newsId
     * @return
     */
    public boolean queryHaveReadExist(String newsId) {
        try (Cursor cursor = queryHaveRead(newsId)) {
            if (cursor.getCount() == 0) {
                return false;
            }

            cursor.moveToFirst();
            return !TextUtils.isEmpty(cursor.getString(cursor.getColumnIndex(HaveReadTable.Columns.READ_DATE)));
        }
    }

    /**
     * 查询一条稍后阅读的新闻是否已读
     *
     * @param newsId
     * @return
     */
    public boolean queryHaveReadExistForReadLater(String newsId) {
        try (Cursor cursor = queryHaveRead(newsId)) {
            if (cursor.getCount() == 0) {
                return false;
            }

            cursor.moveToFirst();
            return !TextUtils.isEmpty(cursor.getString(cursor.getColumnIndex(HaveReadTable.Columns.READ_LATER_READ_DATE)));
        }
    }

    /**
     * 查询所有稍后阅读中未读的新闻数量
     *
     * @return
     */
    public int queryAllUnHaveReadCountForReadLater() {
        List<String> list = queryAllReadLater();
        if (list.size() == 0) {
            return 0;
        }

        int count = 0;
        for (int i = 0; i < list.size(); i++) {
            if (!queryHaveReadExistForReadLater(list.get(i))) {
                count++;
            }
        }
        return count;
    }
}
