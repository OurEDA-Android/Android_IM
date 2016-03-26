package com.example.qjm3662.bmobpushtext;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by qjm3662 on 2016/3/13 0013.
 */
public class Db extends SQLiteOpenHelper{
    //Cursor是数据库查询出来的结果对象（想象成指针）
    public Db(Context context) {
        super(context, "db2"/*数据库名*/, null, 3/*存储的数据库版本*/);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //创建数据库的内部结构
        db.execSQL("CREATE TABLE user("+"_id INTEGER PRIMARY KEY AUTOINCREMENT,"+
                //列名，类型，默认
                "username TEXT DEFAULT \"\","+
                "sex INTEGER DEFAULT 0,"+
                "age INTEGER DEFAULT 0,"+
                "person_note TEXT DEFAULT \"\")");
        db.execSQL("CREATE TABLE message("+"_id INTEGER PRIMARY KEY AUTOINCREMENT,"+
                //列名，类型，默认
                "username TEXT DEFAULT \"\","+
                "name TEXT DEFAULT \"\","+
                "flag INTEGER DEFAULT 0,"+
                "contain TEXT DEFAULT \"\")");
        db.execSQL("CREATE TABLE friend("+"_id INTEGER PRIMARY KEY AUTOINCREMENT,"+
                //列名，类型，默认
                "username TEXT DEFAULT \"\","+
                "name TEXT DEFAULT \"\","+
                "person_note TEXT DEFAULT \"\")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("CREATE TABLE message("+"_id INTEGER PRIMARY KEY AUTOINCREMENT,"+
                //列名，类型，默认
                "username TEXT DEFAULT \"\","+
                "name TEXT DEFAULT \"\","+
                "flag INTEGER DEFAULT 0,"+
                "contain TEXT DEFAULT \"\")");
    }
}
