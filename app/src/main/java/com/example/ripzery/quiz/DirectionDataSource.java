package com.example.ripzery.quiz;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.w3c.dom.Comment;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by visit on 9/26/14 AD.
 */
public class DirectionDataSource {
    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;
    private String[] allcolumns = {MySQLiteHelper.NAME, MySQLiteHelper.LATLNG, MySQLiteHelper.COLOR};

    public DirectionDataSource(Context context){
        dbHelper = new MySQLiteHelper(context);
    }

    public void open() throws SQLException{
        database = dbHelper.getWritableDatabase();
    }

    public void close(){
        dbHelper.close();
    }

    public Directions createRecord(String name,ArrayList<LatLng> latLngs, ArrayList<String> colors){
        String sLatLngs = "",sColors = "";

        for(LatLng latLng : latLngs){
            sLatLngs += String.valueOf(latLng.latitude) + "," + String.valueOf(latLng.longitude);
            if(!latLng.equals(latLngs.get(latLngs.size()-1))){
                sLatLngs += "!";
            }
        }

        int count = 0;
        for(String color : colors){
            sColors += color;
            count++;
            if(count != colors.size()){
                sColors += "!";
            }

        }

        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.NAME,name);
        values.put(MySQLiteHelper.LATLNG,sLatLngs);
        values.put(MySQLiteHelper.COLOR,sColors);
        Cursor cursor = database.query(MySQLiteHelper.TABLE_NAME,allcolumns,MySQLiteHelper.NAME + " = '" + name + "'", null,null,null,null);
        cursor.moveToFirst();
        Directions newDirection = cursorToDirection(cursor);
        cursor.close();
        return newDirection;
    }

    public ArrayList<Directions> getAllDirections(){
        ArrayList<Directions> directions = new ArrayList<Directions>();

        Cursor cursor = database.query(MySQLiteHelper.TABLE_NAME, allcolumns, null,null,null,null,null);

        cursor.moveToFirst();

        while (!cursor.isAfterLast()){
            Directions direction = cursorToDirection(cursor);
            directions.add(direction);
            cursor.moveToNext();
        }

        cursor.close();
        return directions;
    }

    public void deleteDirection(String recordName){
        database.delete(MySQLiteHelper.TABLE_NAME, MySQLiteHelper.NAME + " = '" + recordName + "'", null);
    }

    private Directions cursorToDirection(Cursor cursor){
        Directions directions = new Directions();
        directions.setName(cursor.getString(0));
        directions.setLatLngs(cursor.getString(1));
        directions.setColors(cursor.getString(2));
        return directions;
    }

}
