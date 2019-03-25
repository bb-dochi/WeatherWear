package com.example.user.ww_test2;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by USER on 2017-10-20.
 */

public class DBManager extends SQLiteOpenHelper{
    SQLiteDatabase db;
    public DBManager(Context context){
        super(context,"db_w",null,1);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        //이미지,계절,날씨,온도지수(단계별로),성별,스타일
        String sql = "create table if not exists WW_3 (file text primary key, season text, temp text, gender text, style text)";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void InsertData(String file, String weather,String temp,String gender,String style){

    }
}
