package com.about.zhiye.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.about.zhiye.db.DBScheme.DATABASE_NAME;
import static com.about.zhiye.db.DBScheme.DATABASE_VERSION;
import static com.about.zhiye.db.DBScheme.*;

/**
 * Created by huangyuefeng on 2017/3/28.
 * Contact me : mcxinyu@foxmail.com
 */
public class DBHelper extends SQLiteOpenHelper {

    private static final String READ_LATER_TABLE_CREATE_STATEMENT
            = "create table " + ReadLaterTable.TABLE_NAME + "(" +
            "_id integer primary key autoincrement," +
            ReadLaterTable.Columns.DATE + "," +
            ReadLaterTable.Columns.NEWS_ID + "," +
            ReadLaterTable.Columns.DELETED + "," +
            ReadLaterTable.Columns.DELETED_DATE +
            ");";

    private static final String HAVE_READ_TABLE_CREATE_STATEMENT
            = "create table " + HaveReadTable.TABLE_NAME + "(" +
            "_id integer primary key autoincrement," +
            HaveReadTable.Columns.NEWS_ID + "," +
            HaveReadTable.Columns.READ_DATE + "," +
            HaveReadTable.Columns.READ_LATER_READ_DATE +
            ");";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(READ_LATER_TABLE_CREATE_STATEMENT);
        db.execSQL(HAVE_READ_TABLE_CREATE_STATEMENT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        switch (oldVersion) {
            case 1:
                db.execSQL(HAVE_READ_TABLE_CREATE_STATEMENT);
            case 2:
                try {
                    db.execSQL("alter table " + HaveReadTable.TABLE_NAME +
                            " add column " + HaveReadTable.Columns.READ_LATER_READ_DATE);
                }catch (Exception e){
                    e.printStackTrace();
                }
            default:
                break;
        }
    }
}
