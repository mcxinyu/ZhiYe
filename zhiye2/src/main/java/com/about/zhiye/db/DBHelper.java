package com.about.zhiye.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.about.zhiye.db.DBScheme.DATABASE_NAME;
import static com.about.zhiye.db.DBScheme.DATABASE_VERSION;
import static com.about.zhiye.db.DBScheme.ReadLaterTable.Columns.DATE;
import static com.about.zhiye.db.DBScheme.ReadLaterTable.Columns.DELETED_DATE;
import static com.about.zhiye.db.DBScheme.ReadLaterTable.Columns.NEWS_ID;
import static com.about.zhiye.db.DBScheme.ReadLaterTable.Columns.DELETED;
import static com.about.zhiye.db.DBScheme.ReadLaterTable.TABLE_NAME;

/**
 * Created by huangyuefeng on 2017/3/28.
 * Contact me : mcxinyu@foxmail.com
 */
public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_CREATE_STATEMENT
            = "create table " + TABLE_NAME + "(" +
            "_id integer primary key autoincrement," +
            DATE + "," +
            NEWS_ID + "," +
            DELETED + "," +
            DELETED_DATE +
            ");";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE_STATEMENT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
