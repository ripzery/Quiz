package com.example.ripzery.quiz;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by visit on 9/26/14 AD.
 */
public class MySQLiteHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "maprecord.db";
    public static final int DATABASE_VERSION = 1;
    public static final String TABLE_NAME = "direction" ;
    public static final String NAME = "record_name";
    public static final String LATLNG = "latlng";
    public static final String COLOR = "color";
    public static final String DATABASE_CREATE = "create table "+ TABLE_NAME + "(" + NAME + " text primary key, "+ LATLNG + " text not null, "+ COLOR + " text not null);";

    public MySQLiteHelper(Context context){
        super(context,DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
       sqLiteDatabase.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {

    }
}
