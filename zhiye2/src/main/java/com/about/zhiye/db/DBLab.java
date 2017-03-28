package com.about.zhiye.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.about.zhiye.db.DBScheme.ReadLaterTable;
import com.about.zhiye.db.DBScheme.ReadLaterTable.Columns;

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

    public static DBLab get(Context context){
        if (dbLab == null){
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

    public void insertReadLaterNews(String newsId) {
        try (Cursor cursor = queryReadLater(newsId)) {
            if (cursor.getCount() == 0) {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd HH:mm:ss", Locale.PRC);
                String currentDate = formatter.format(new Date(System.currentTimeMillis()));

                ContentValues values = new ContentValues();
                values.put(Columns.DATE, currentDate);
                values.put(Columns.NEWS_ID, newsId);
                values.put(Columns.DELETED, false);
                values.put(Columns.DELETED_DATE, "");

                mDatabase.insert(ReadLaterTable.TABLE_NAME, null, values);
            } else {
                updateReadLaterNews(newsId, false);
            }
        }
    }

    public void deleteReadLaterNews(String newsId) {
        updateReadLaterNews(newsId, true);
    }

    private void updateReadLaterNews(String newsId, boolean delete) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd HH:mm:ss", Locale.PRC);
        String currentDate = formatter.format(new Date(System.currentTimeMillis()));

        ContentValues values = new ContentValues();
        values.put(Columns.DELETED, delete);
        if (delete){
            // 要删除就要更新 删除时间，保留历史时间
            values.put(Columns.DELETED_DATE, currentDate);
        } else {
            // 不删除说明是重新添加的，要更新 添加时间
            values.put(Columns.DATE, currentDate);
            values.put(Columns.DELETED_DATE, "");
        }

        mDatabase.update(ReadLaterTable.TABLE_NAME, values, Columns.NEWS_ID + "=?", new String[]{newsId});
    }

    public List<String> queryAllReadLater() {
        try (Cursor cursor = mDatabase.query(ReadLaterTable.TABLE_NAME, null, null, null, null, null, null)) {
            if (cursor.getCount() == 0) {
                return null;
            }

            List<String> list = new ArrayList<>();
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                if (cursor.getInt(cursor.getColumnIndex(Columns.DELETED)) == 0) {
                    list.add(cursor.getString(cursor.getColumnIndex(Columns.NEWS_ID)));
                }
                cursor.moveToNext();
            }
            return list;
        }
    }

    private Cursor queryReadLater(String newsId) {
        return mDatabase.query(ReadLaterTable.TABLE_NAME, null,
                Columns.NEWS_ID + "=?",
                new String[]{newsId},
                null, null, null);
    }

    public boolean queryReadLaterHave(String newsId) {
        try (Cursor cursor = queryReadLater(newsId)) {
            if (cursor.getCount() == 0) {
                return false;
            }

            cursor.moveToFirst();
            return cursor.getInt(cursor.getColumnIndex(Columns.DELETED)) == 0;
        }
    }
}
